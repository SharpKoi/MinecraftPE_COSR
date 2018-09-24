package cosr.ess;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cmen.essalg.CJEF;
import cn.nukkit.AdventureSettings.Type;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.event.Listener;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import cosr.vip.CVIP;

public class Essential extends PluginBase implements Listener {
	
	private static Essential main;
	private static CEssButtom buttomTask;
	private static Config pluginConfig;
	private static Map<String, List<Position>> homeMap = new HashMap<String, List<Position>>();
	
	private static CCleaner cleaner = null;
	
	public static Essential getInstance() {
		return main;
	}
	
	public static Map<String, List<Position>> getHomeMap() {
		return homeMap;
	}
	
	public static List<Position> getHomeList(String playerName) {
		return homeMap.get(playerName);
	}
	
	public void onEnable() {
		main = this;
		pluginConfig = new Config(new File(this.getDataFolder(), "propertise.yml"), Config.YAML);
		buttomTask = new CEssButtom(this);
		cleaner = new CCleaner(this);
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		this.getServer().getScheduler().scheduleRepeatingTask(buttomTask, 20);
		this.getServer().getScheduler().scheduleRepeatingTask(cleaner, 200);
	}
	
	public void onDisable() {
		Config conf = null;
		for(String pn : homeMap.keySet()) {
			List<String> list = new ArrayList<String>();
			for(Position home : homeMap.get(pn)) {
				list.add(String.join(", ", new String[] 
						{home.getLevel().getFolderName(), Integer.toString((int)home.x), Integer.toString((int)home.y), Integer.toString((int)home.z)}));
			}
			conf = new Config(new File(this.getDataFolder(), "home" + File.separator + pn+".yml"), Config.YAML);
			conf.set("home", list);
			conf.save();
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equals("help")) {
			if(pluginConfig.getBoolean("enable_help", true) == false) {
				
			}
		}
		else if(cmd.getName().equals("getpf")) {
			Item item = Item.get(339);
			item.setCustomName(TextFormat.ITALIC + (TextFormat.LIGHT_PURPLE + "我的個人檔案"));
			if(sender.isPlayer()) {
				Player p = (Player)sender;
				if(p.getInventory().canAddItem(item)) {
					p.getInventory().addItem(item);
					p.sendMessage(TextFormat.GREEN + "你獲得了" + TextFormat.RESET + item.getName() + TextFormat.RESET + "x1");
				}
			}
		}
		else if(cmd.getName().equals("clc")) {
			String endl = "";
			for(int i = 1; i <= 20; i++) endl+="\n";
			sender.sendMessage(endl);
		}
		else if(cmd.getName().equals("burn")) {
			if(args.length < 1) return false;
			
			int time = 0;
			if(args.length < 2) time = 5;
			
			if(args.length >= 2) {
				if(!CJEF.isDigit(args[1])) {
					sender.sendMessage(TextFormat.RED + "請輸入正確的秒數格式!");
					return true;
				}
				time = Integer.parseInt(args[1]);
			}
			
			Player target = Server.getInstance().getPlayer(args[0]);
			if(target != null) {
				target.setOnFire(time);
			}else {
				sender.sendMessage(TextFormat.RED + "目標不存在!");
			}
		}
		else if(cmd.getName().equals("full")) {
			Player target = null;
			if(args.length < 1) {
				if(sender.isPlayer()) {
					target = (Player) sender;
					target.getFoodData().setLevel(target.getFoodData().getMaxLevel());
				}else {
					sender.sendMessage(TextFormat.RED + "請指定玩家");
				}
			}else {
				target = Server.getInstance().getPlayer(args[0]);
				target.getFoodData().setLevel(target.getFoodData().getMaxLevel());
			}
		}
		else if(cmd.getName().equals("cure")) {
			Player target = null;
			int level = 0;
			if(args.length < 1) {
				if(sender.isPlayer()) {
					target = (Player) sender;
					level = target.getMaxHealth();
				}else {
					sender.sendMessage(TextFormat.RED + "請指定玩家");
					return false;
				}
			}else {
				target = Server.getInstance().getPlayer(args[0]);
				if(args.length < 2) {
					level = target.getMaxHealth();
				}else {
					if(!CJEF.isDigit(args[1])) {
						sender.sendMessage(TextFormat.RED + "請輸入正確的數字格式!");
						return true;
					}
					level = Integer.parseInt(args[1]);
				}
			}
			target.setHealth(level);
		}
		else if(cmd.getName().equals("oplist")) {
			String list = TextFormat.LIGHT_PURPLE + "伺服器管理員名單: \n";
			for(String opName : Server.getInstance().getOps().getKeys(false)) {
				list += opName+"\n";
			}
			sender.sendMessage(list);
		}
		else if(cmd.getName().equals("clean")) {
			cleaner.clean();
		}
		else if(cmd.getName().equals("summon")) {
			Player target = null;
			if(args.length < 1) {
				if(sender.isPlayer()) {
					target = (Player)sender;
				}else {
					sender.sendMessage(TextFormat.RED + "請指定玩家");
					return false;
				}
			}else {
				target = Server.getInstance().getPlayer(args[0]);
			}
			if(target != null) {
				CompoundTag nbt = new CompoundTag();
				nbt.putList(new ListTag<DoubleTag>("Pos")
						.add(new DoubleTag("", target.x))
						.add(new DoubleTag("", target.y))
						.add(new DoubleTag("", target.z)))
					.putList(new ListTag<DoubleTag>("Motion")
						.add(new DoubleTag("", 0))
						.add(new DoubleTag("", 0))
						.add(new DoubleTag("", 0)))
					.putList(new ListTag<FloatTag>("Rotation")
						.add(new FloatTag("", 0))
						.add(new FloatTag("", 0)))
					.putFloat("Scale", 3)
					.putBoolean("OnGround", true);
				new EntityLightning(target.getLevel().getChunk((int)target.x >> 4, (int)target.z >> 4), nbt).spawnTo(target);
			}
		}
		else if(cmd.getName().equals("fly")) {
			String playerName = "";
			Player target = null;
			if(args.length < 1) {
				if(sender.isPlayer()) {
					Player p = (Player)sender;
					target = p;
				}else
					sender.sendMessage(TextFormat.RED + "請指定玩家");
			}else {
				playerName = args[1];
				target = Server.getInstance().getPlayer(playerName);
			}
			if(CVIP.isVIP(target.getName())) {
				if(!target.getAdventureSettings().get(Type.ALLOW_FLIGHT)) {
					target.getAdventureSettings().set(Type.ALLOW_FLIGHT, true);
					target.getDataProperties().putBoolean(Entity.DATA_FLAG_CAN_FLY, true);
					target.sendMessage(TextFormat.GREEN + "飛行模式啟動!");
				}else {
					target.getAdventureSettings().set(Type.ALLOW_FLIGHT, false);
					target.getDataProperties().putBoolean(Entity.DATA_FLAG_CAN_FLY, false);
					target.sendMessage(TextFormat.GRAY + "飛行模式關閉!");
				}
				target.getAdventureSettings().update();
			}else
				target.sendMessage(TextFormat.RED + "您沒有權限使用此功能");
		}
		else if(cmd.getName().equals("home")) {
			if(sender.isPlayer()) {
				Player p = (Player)sender;
				if(args.length < 1) {
					if(homeMap.containsKey(p.getName())) {
						if(homeMap.get(p.getName()).size() > 0) {
							p.sendTitle(TextFormat.GRAY + "正在準備進行傳送...");
							Position home = homeMap.get(p.getName()).get(0);
							p.teleport(home);
							p.sendTitle(TextFormat.GREEN + "傳送至Home", TextFormat.WHITE + 
									home.getLevel().getFolderName() + "("+(int)home.x+", "+(int)home.y+", "+(int)home.z+")", 1, 1, 1);
							return true;
						}
					}
					p.sendMessage(TextFormat.RED + "您當前還沒有設置home點, 請輸入/home set來設置");
					return true;
				}
				else {
					if(args[0].equalsIgnoreCase("set")) {
						if(!homeMap.containsKey(p.getName())) {
							homeMap.put(p.getName(), new ArrayList<Position>());
						}
						if(homeMap.get(p.getName()).size() <= 5) {
							homeMap.get(p.getName()).add(p.getLocation());
							p.sendMessage(TextFormat.GREEN + "成功設置" + 
									TextFormat.WHITE + p.getLevel().getFolderName()+"("+(int)p.x+", "+(int)p.y+", "+(int)p.z+")" + 
									TextFormat.GREEN + "為您的home點");
						}else {
							p.sendMessage(TextFormat.RED + "一人最多只能設置5個home點");
							return true;
						}
					}
					else if(args[0].equalsIgnoreCase("my")) {
						if(!homeMap.containsKey(p.getName())) {
							homeMap.put(p.getName(), new ArrayList<Position>());
						}
						String homeList = TextFormat.ITALIC + (TextFormat.YELLOW + "--- 我的home點 ---\n");
						String separator = TextFormat.RESET + "====================\n";
						homeList += separator;
						List<Position> posList = homeMap.get(p.getName());
						if(posList.size() > 0) {
							for(int i = 0; i < posList.size(); i++) {
								Position home = posList.get(i);
								homeList += TextFormat.RESET + (i+"-> ") + TextFormat.YELLOW + 
										home.getLevel().getFolderName() + "("+(int)home.x+", "+(int)home.y+", "+(int)home.z+")\n";
							}
						}
						p.sendMessage(homeList);
					}
					else if(args[0].equalsIgnoreCase("del")) {
						if(!homeMap.containsKey(p.getName())) {
							homeMap.put(p.getName(), new ArrayList<Position>());
						}
						if(args.length < 2) {
							p.sendMessage(TextFormat.RED + "/home del <index>");
							return true;
						}
						if(!CJEF.isInteger(args[1])) {
							p.sendMessage(TextFormat.RED + "/home del <index>");
							return true;
						}
						int index = Integer.parseInt(args[1]);
						if(homeMap.get(p.getName()).size() > index) {
							Position home = homeMap.get(p.getName()).get(index);
							homeMap.get(p.getName()).remove(index);
							p.sendMessage(TextFormat.GREEN + "已成功刪除home點" + 
									TextFormat.YELLOW + home.getLevel().getFolderName() + "("+(int)home.x+", "+(int)home.y+", "+(int)home.z+")");
						}else
							p.sendMessage(TextFormat.RED + "該home點不存在");
					}else if(CJEF.isInteger(args[0])) {
						p.sendTitle(TextFormat.GRAY + "正在準備進行傳送...");
						int index = Integer.parseInt(args[0]);
						Position home = homeMap.get(p.getName()).get(index);
						p.teleport(home);
						p.sendTitle(TextFormat.GREEN + "傳送至Home", TextFormat.WHITE + 
								home.getLevel().getFolderName() + "("+(int)home.x+", "+(int)home.y+", "+(int)home.z+")", 1, 1, 1);
					}
					else return false;
				}
			}else
				sender.sendMessage(TextFormat.RED + "請在遊戲中執行此指令");
		}
		else if(cmd.getName().equalsIgnoreCase("inv-empty")) {
			if(sender.isPlayer()) {
				Player p = (Player) sender;
				if(p.isOp()) {
					p.getInventory().clearAll();
				}
			}else
				sender.sendMessage(TextFormat.RED + "請在遊戲中執行此指令");
		}
		else if(cmd.getName().equals("unload")) {
			if(args.length < 1) return false;
			String pluginName = args[0];
			Server.getInstance().getPluginManager().disablePlugin(Server.getInstance().getPluginManager().getPlugin(pluginName));
			Server.getInstance().getLogger().info("unload" + pluginName + "successfully");
		}
		return true;
	}
	
	
}
