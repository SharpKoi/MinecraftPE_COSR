package cosr.we;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.EntityHumanType;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.item.Item;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import cosr.we.tool.QuickBuildTool;
import cosr.we.tool.QuickBuildTool.Feature;

public class Main extends PluginBase implements Listener
{
	public Map<String, QuickBuildTool> excPool;
	public List<String> whiteList;
	
	private static Main main;
	
	public static Main getInstance() {
		return main;
	}
	
	public void onEnable() {
		main = this;
		excPool = new HashMap<String, QuickBuildTool>();
		whiteList = new Config(new File(this.getDataFolder(), "whitelist.yml"), Config.YAML).getStringList("whitelist");
		this.getServer().getPluginManager().registerEvents(this,this);
		this.getDataFolder().mkdirs();
	}
	
	public void onDisable() {
		excPool.clear();
		
		Config conf = new Config(new File(this.getDataFolder(), "whitelist.yml"));
		conf.set("whitelist", whiteList);
		conf.save();
		whiteList.clear();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		try {
			if(cmd.getName().equals("pos")) {
				if(args[0].equals("start")) {
					if(!sender.isPlayer()) {
						sender.sendMessage(TextFormat.RED + "請在遊戲裡使用此指令");
						return false;
					}
					
					Player sp = (Player) sender;
					if(!whiteList.contains(sp.getName())) {
						sp.sendMessage(TextFormat.RED + "您並非白名單中的成員, 無法使用此功能");
						return true;
					}
					
					QuickBuildTool bt = new QuickBuildTool(sp.getLevel(), Feature.BUILD);
					if(!excPool.containsKey(sp.getName())) {
						excPool.put(sp.getName(), bt);
						sender.sendMessage("請點擊第一點");
						((EntityHumanType) sender).getInventory().setItem(sp.getInventory().getHeldItemIndex(), new Item(0,0,0));
					}else 
						sender.sendMessage(TextFormat.RED + "您正在執行此指令! 若要中斷操作請輸入指令 /pos stop");
				}
				else if(args[0].equals("measure")) {
					if(!sender.isPlayer()) {
						sender.sendMessage(TextFormat.RED + "請在遊戲裡使用此指令");
						return false;
					}
					
					Player sp = (Player) sender;
					if(!whiteList.contains(sp.getName())) {
						sp.sendMessage(TextFormat.RED + "您並非白名單中的成員, 無法使用此功能");
						return true;
					}
					
					QuickBuildTool bt = new QuickBuildTool(sp.getLevel(), Feature.MEASURE);
					if(!excPool.containsKey(sp.getName())) {
						excPool.put(sp.getName(), bt);
						sender.sendMessage("請點擊第一點");
					}else 
						sender.sendMessage(TextFormat.RED + "您正在執行此指令! 若要中斷操作請輸入指令 /pos stop");
				}
				else if(args[0].equals("stop")) {
					if(!sender.isPlayer()) {
						sender.sendMessage(TextFormat.RED + "請在遊戲裡使用此指令");
						return false;
					}
					
					Player sp = (Player) sender;
					if(!whiteList.contains(sp.getName())) {
						sp.sendMessage(TextFormat.RED + "您並非白名單中的成員, 無法使用此功能");
						return true;
					}
					
					if(excPool.containsKey(sp.getName())) {
						excPool.remove(sp.getName());
						sp.sendMessage(TextFormat.DARK_GRAY + "您已成功停止當前動作");
					}else 
						sp.sendMessage(TextFormat.RED + "您尚未執行WorldEdit中任何指令");
				}
				else if(args[0].equals("wl")) {
					if(!sender.isOp()) {
						sender.sendMessage(TextFormat.RED + "您沒有權限使用此指令!");
						return true;
					}
					if(args[1].equals("add")) {
						if(args.length >= 3) {
							this.whiteList.add(args[2]);
						}else {
							if(sender.isPlayer()) {
								Player p = (Player) sender;
								this.whiteList.add(p.getName());
							}else return false;
						}
						sender.sendMessage(TextFormat.GREEN + "成功將玩家" + TextFormat.RESET + args[2] + TextFormat.GREEN + "加入小木斧白名單!");
					}
					else if(args[1].equals("del")) {
						this.whiteList.remove(args[2]);
						sender.sendMessage(TextFormat.GREEN + "成功將玩家" + TextFormat.RESET + args[2] + TextFormat.GREEN + "從小木斧白名單中移除!");
					}
					else if(args[1].equals("show")) {
						String list = "--- 小木斧白名單 ---\n";
						for(String player : this.whiteList) {
							list += (player + "\n");
						}
						sender.sendMessage(list);
					}
					else if(args[1].equals("save")) {
						Config conf = new Config(new File(this.getDataFolder(), "whitelist.yml"));
						conf.set("whitelist", whiteList);
						conf.save();
					}
					else 
						return false;
				}
				else 
					return false;
			}
		}catch(ArrayIndexOutOfBoundsException err) {
			sender.sendMessage(TextFormat.RED + "請輸入正確的指令格式");
			return false;
		}
		
		return true;
	}
	
	@EventHandler
	public void onTouch(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		String n = p.getName();
	    int x = (int)e.getBlock().getX();
	    int y = (int)e.getBlock().getY();
	    int z = (int)e.getBlock().getZ();
	    
		if(excPool.containsKey(n) && e.getAction().toString().equals("RIGHT_CLICK_BLOCK")) {
			QuickBuildTool bt = excPool.get(n);
			bt.step--;
			if(bt.feature.equals(Feature.BUILD)) {
				switch(bt.step){ 
					case 3:
						if(p.getInventory().getItemInHand().getId() == 0) {
							if(p.getLevel().equals(bt.world)) bt.setPos1(x, y, z);
							p.sendMessage("請點擊第二點");
						}else {
							p.sendMessage("給我把東西放下來再點喔(# ﾟДﾟ)");
							bt.step++;
						}
						break;
					case 2:
						if(p.getInventory().getItemInHand().getId() == 0 ) {
							if(p.getLevel().equals(bt.world)) bt.setPos2(x, y, z);
							p.sendMessage("手上請拿要鋪路的物品，然後點及地板");
						}else {
							p.sendMessage("給我把東西放下來再點喔(# ﾟДﾟ)");
							bt.step++;
						}
						break;
					case 1:
						p.sendMessage("所有步驟完成,地形生成中");
						int id = p.getInventory().getItemInHand().getId();
						int damage = p.getInventory().getItemInHand().getDamage();
						Block b = Block.get(id, damage);
						bt.make(b);
						excPool.remove(n);
						break;
				}
			}
			if(bt.feature.equals(Feature.MEASURE)) {
				switch(bt.step) {
					case 2:
						if(e.getPlayer().getInventory().getItemInHand().getId() == 0) {
							if(p.getLevel().equals(bt.world)) bt.setPos1(x, y, z);
							p.sendMessage("請點擊第二點");
						}else {
							p.sendMessage("給我把東西放下來再點喔(# ﾟДﾟ)");
							bt.step++;
						}
						break;
					case 1:
						if(e.getPlayer().getInventory().getItemInHand().getId() == 0) {
							if(p.getLevel().equals(bt.world)) bt.setPos2(x, y, z);
						}else {
							e.getPlayer().sendMessage("給我把東西放下來再點喔(# ﾟДﾟ)");
							bt.step++;
						}
						//All done
						p.sendMessage(TextFormat.YELLOW + "第一個方塊的座標為" + TextFormat.AQUA + "("+bt.pos1.x+", "+bt.pos1.y+", "+bt.pos1.z+")");
						p.sendMessage(TextFormat.YELLOW + "第二個方塊的座標為" + TextFormat.AQUA + "("+bt.pos2.x+", "+bt.pos2.y+", "+bt.pos2.z+")");
						p.sendMessage(TextFormat.YELLOW + "是否為直線: " + TextFormat.AQUA + isStraight((int)bt.pos1.x, (int)bt.pos1.y, (int)bt.pos1.z, (int)bt.pos2.x, (int)bt.pos2.y, (int)bt.pos2.z));
						p.sendMessage(TextFormat.BLUE + "也許您想要操作這個方塊? (已用紅石磚標誌)" + TextFormat.RESET);
						bt.buildAssist();
						excPool.remove(n);
						break;
				}
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		String name = event.getPlayer().getName();
		if(excPool.containsKey(name)) excPool.remove(name);
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		String name = event.getPlayer().getName();
		if(excPool.containsKey(name)) excPool.remove(name);
	}
	
	public int getLength(int x1 ,int y1,int z1,int x2,int y2,int z2) {				//多加的方法(取得方塊間長度) -Cmen
		int length;
		int xlength = Math.abs(x2-x1);
		int ylength = Math.abs(y2-y1);
		int zlength = Math.abs(z2-z1);
		length = (int) Math.pow(Math.pow(xlength, 2)+Math.pow(ylength, 2)+Math.pow(zlength, 2), 0.5);
		if(isStraight(x1, y1, z1, x2, y2, z2)) {
			return length-1;
		}else {
			return length;
		}
	}
	
	public boolean isStraight(int x1 ,int y1,int z1,int x2,int y2,int z2) {			//多加的方法(判斷直線或斜線) -Cmen
		if((x1 == x2&&y1 == y2) || (y1 == y2&&z1 == z2) || (x1 == x2&&z1 == z2)) {
			return true;
		}else {
			return false;
		}
	}
}
			
		
		
	


