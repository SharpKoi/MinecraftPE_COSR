package cosr.roleplay.listener;

import java.io.FileNotFoundException;
import java.util.Date;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cosr.roleplay.*;
import cosr.roleplay.database.*;

public class EventListener implements Listener {
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onLogin(PlayerLoginEvent event) {
		CRolePlay.getInstance().getServer().addOnlinePlayer(event.getPlayer());
	}

	
	@EventHandler(priority=EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent event) {
		try {
			Player player = event.getPlayer();
			PlayerDataBase playerdata = new PlayerDataBase(player.getName());
			
			//initialize some informations of PlayerDataBase
			playerdata.ip = player.getAddress();
			playerdata.loginMoment.setTime(new Date());
			CRolePlay.getOnlinePDB().put(player.getName(), playerdata);
			if(playerdata.isNewPlayer == true) {
				CRolePlay.getAchvMap().get("FIRST_LOGIN").grantTo(player.getName());
			}
			
			for(String head : playerdata.getPlayerTitleMap().keySet()) {
				PlayerTitle pt = playerdata.getPlayerTitleMap().get(head);
				if(!pt.hasBeenSeen()) {
					player.sendMessage(pt.getTitle().getterMessage());
					pt.setBeenSeen(true);
				}
				if(pt.isTag()) {
					PlayerTitle.pinTag(player, head);
				}
			}
		}catch(FileNotFoundException err) {
			//catch
		}
	}
	
	@SuppressWarnings("unused")
	@EventHandler
	public void onKill(PlayerDeathEvent event) {
		try {
			Player deathPlayer = event.getEntity();
			if(event.getEntity().getKiller() instanceof Player) {
				Player killer = (Player)event.getEntity().getKiller();
				PlayerDataBase playerdata = CRolePlay.getOnlinePDB().get(killer.getName());
				playerdata.killcount++;
				if(playerdata.killcount == 1) {
					CRolePlay.getAchvMap().get("FIRST_BLOOD").grantTo(killer.getName());
				}
				CRolePlay.giveExp(killer.getName(), CRolePlay.getPropertiesConfig().getInt("player_level.exp.player_killed", 40));
			}
		}catch(FileNotFoundException err) {
			//catch
		}
	}
	
	public void onMobKilled(EntityDeathEvent event) {
		try {
			Entity deathEntity = event.getEntity();
			if(deathEntity instanceof EntityMob) {
				EntityDamageEvent cause = ((EntityMob)deathEntity).getLastDamageCause();
				if(cause instanceof EntityDamageByEntityEvent) {
					Entity killer = ((EntityDamageByEntityEvent) cause).getDamager();
					if(killer instanceof Player) {
						Player player = (Player) killer;
						PlayerLevel plv = CRolePlay.getOnlinePDB().get(player.getName()).getPlayerLevel();
						plv.getExpCalculator().addMobKillCount();
						CRolePlay.giveExp(player.getName(), plv.getExpCalculator().getMobKillExp());
					}
				}
			}
		}catch(Exception err) {
			err.printStackTrace();
		}
	}
	
	@EventHandler
	public void OnChat(PlayerChatEvent event) {
		//CRACKER
		Player player = event.getPlayer();
		
		try {
			if(event.getMessage().equals("@Test")) {
				CRolePlay.getAchvMap().get("TEST_1").grantTo(player.getName());
			}
			
			if(event.getMessage().equals("@tEst")) {
				CRolePlay.getAchvMap().get("TEST_2").grantTo(player.getName());
			}
			
			if(event.getMessage().equals("@teSt")) {
				CRolePlay.getAchvMap().get("TEST_3").grantTo(player.getName());
			}
			
			if(event.getMessage().equals("@tesT")) {
				CRolePlay.getAchvMap().get("TEST_4").grantTo(player.getName());
			}
		}catch(FileNotFoundException err) {
			//catch
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if(event.getPlayer().isCreative()) return;
		
		Player player = event.getPlayer();
		Block block = event.getBlock();
		try {
			if(block.getId() == 1 || block.getId() == 4) {
				PlayerLevel plv = CRolePlay.getOnlinePDB().get(player.getName()).getPlayerLevel();
				plv.getExpCalculator().addMineCount();
				CRolePlay.giveExp(player.getName(), plv.getExpCalculator().getMineExp());
			}
			else if(block.getId() == 16) {
				CRolePlay.giveExp(player.getName(), CRolePlay.getPropertiesConfig().getInt("player_level.exp.ore.coal", 15));
			}
			else if(block.getId() == 15) {
				CRolePlay.giveExp(player.getName(), CRolePlay.getPropertiesConfig().getInt("player_level.exp.ore.iron", 40));
			}
			else if(block.getId() == 14) {
				CRolePlay.giveExp(player.getName(), CRolePlay.getPropertiesConfig().getInt("player_level.exp.ore.gold", 60));
			}
			else if(block.getId() == 56) {
				CRolePlay.giveExp(player.getName(), CRolePlay.getPropertiesConfig().getInt("player_level.exp.ore.diamond", 120));
			}
			else if(block.getId() == 129) {
				CRolePlay.giveExp(player.getName(), CRolePlay.getPropertiesConfig().getInt("player_level.exp.ore.emerald", 120));
			}
			else if(block.getId() == 21) {
				CRolePlay.giveExp(player.getName(), CRolePlay.getPropertiesConfig().getInt("player_level.exp.ore.lapis", 50));
			}
			else if(block.getId() == 73 || block.getId() == 74) {
				CRolePlay.giveExp(player.getName(), CRolePlay.getPropertiesConfig().getInt("player_level.exp.ore.redstone", 50));
			}
		}catch(FileNotFoundException err) {
			//catch
		}
	}

	//多增加幾個Event
	@EventHandler
	public void OnQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if(CRolePlay.getOnlinePDB().containsKey(player.getName())) {
			PlayerDataBase playerdata = CRolePlay.getOnlinePDB().get(player.getName());
			playerdata.save();
		}
		CRolePlay.getOnlinePDB().remove(player.getName());
	}
}
