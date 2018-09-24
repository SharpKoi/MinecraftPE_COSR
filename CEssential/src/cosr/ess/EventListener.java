package cosr.ess;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerLoginEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerRespawnEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

public class EventListener implements Listener {
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onLogin(PlayerLoginEvent event) {
		event.getPlayer().setCheckMovement(false);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		Config conf = new Config(
				new File(Essential.getInstance().getDataFolder(), "home" + File.separator + p.getName() + ".yml"),
				Config.YAML);
		if (!Essential.getHomeMap().containsKey(p.getName())) {
			Essential.getHomeMap().put(p.getName(), new ArrayList<Position>());
		}
		List<String> list = conf.getStringList("home");
		List<Position> homeList = Essential.getHomeMap().get(p.getName());
		for (String home : list) {
			String[] posArray = home.split(", ");
			Position homePos = new Position(Integer.parseInt(posArray[1]), Integer.parseInt(posArray[2]),
					Integer.parseInt(posArray[3]), Server.getInstance().getLevelByName(posArray[0]));
			homeList.add(homePos);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		Config conf = new Config(
				new File(Essential.getInstance().getDataFolder(), "home" + File.separator + p.getName() + ".yml"),
				Config.YAML);
		if (!Essential.getHomeMap().containsKey(p.getName())) {
			Essential.getHomeMap().put(p.getName(), new ArrayList<Position>());
		}
		List<String> list = new ArrayList<String>();
		List<Position> homeList = Essential.getHomeMap().get(p.getName());
		for (Position home : homeList) {
			list.add(String.join(", ", new String[] { home.getLevel().getFolderName(), Integer.toString((int) home.x),
					Integer.toString((int) home.y), Integer.toString((int) home.z) }));
		}
		conf.set("home", list);
		conf.save();
	}
	
	@EventHandler
	public void onTouch(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Item item = event.getItem();
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(item.getId() == 339 && item.getName().equals(TextFormat.ITALIC + (TextFormat.LIGHT_PURPLE + "我的個人檔案"))) {
				DataWindow.showDataWindow(p);
			}
		}
		
	}

	@EventHandler
	public void PlayerDeath(PlayerDeathEvent event) {
		event.setKeepInventory(true);
	}

	@EventHandler
	public void PlayerRespawn(PlayerRespawnEvent e) {
		e.getPlayer().sendMessage(TextFormat.GOLD + "魔法精靈: " + TextFormat.YELLOW + "以保護了您的物品");
	}
}
