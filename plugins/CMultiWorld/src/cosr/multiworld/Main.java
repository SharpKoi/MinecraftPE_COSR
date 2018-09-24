package cosr.multiworld;

import java.io.File;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

public class Main extends PluginBase{
	private static Main main;
	private static Config worldConf;
	private TPGUI ui;
	private static File worlds;
	
	public static Main getInstance() {
		return main;
	}
	
	public void onEnable() {
		main = this;
		ui = new TPGUI();
		this.getDataFolder().mkdirs();
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		worldConf = new Config(new File(this.getDataFolder(), "WorldList.yml"), Config.YAML);
		worlds = new File(this.getDataFolder().getParentFile().getParentFile(), "worlds");
		
		File[] files = worlds.listFiles();
		for(File file : files) {
			String name = file.getName();
			this.getServer().loadLevel(name);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if(cmd.getName().equals("cworld") || cmd.getName().equals("cw")) {
				if(args[0].equals("tpui") || args[0].equals("u")) {
					if(sender.isPlayer()) {
						ui.showMapsWindow((Player) sender);
						return true;
					}else sender.sendMessage(TextFormat.RED + "請在遊戲中使用此指令!");
				}
				else if(args[0].equals("new") || args[0].equals("n")) {
					switch(args[3]) {
					case "flat":
						registLevel(args[1], args[2], Generator.TYPE_FLAT);
						break;
					case "survive":
						registLevel(args[1], args[2], Generator.TYPE_INFINITE);
						break;
					case "nether":
						registLevel(args[1], args[2], Generator.TYPE_NETHER);
						break;
					default:
						registLevel(args[1], args[2], Generator.TYPE_FLAT);
						break;
					}
					sender.sendMessage(TextFormat.GREEN + "地圖註冊成功!");
					return true;
				}
				else if(args[0].equals("remove") || args[0].equals("del") || args[0].equals("d")) {
					if(sender.isOp()) {
						if(worldConf.exists(args[1])) {
							worldConf.remove(args[1]);
							worldConf.save();
						}
						if(this.getServer().getLevelByName(args[1]) != null) this.getServer().getLevelByName(args[1]).unload();
						if(removeLevel(args[1])) {
							sender.sendMessage(TextFormat.GRAY + "成功移除地圖" + args[1]);
							return true;
						}else sender.sendMessage(TextFormat.RED + "地圖" + args[1] + "不存在!");
					}else {
						sender.sendMessage(TextFormat.RED + "不明指令" + cmd.getName() + "\n或您未有權限執行此指令");
					}
				}
				else if(args[0].equalsIgnoreCase("tp")) {
					if(sender.isPlayer()) {
						Player p = (Player) sender;
						Level target = null;
						if(args.length < 2) {
							target = Server.getInstance().getDefaultLevel();
						}else {
							target = Server.getInstance().getLevelByName(args[1]);
						}
						
						if(target != null) {
							String targetName = target.getFolderName();
							if(Server.getInstance().isLevelLoaded(targetName)) {
								p.sendTitle(TextFormat.GRAY + "正在傳送至", worldConf.getString(targetName, targetName));
								p.teleport(target.getSafeSpawn());
								p.sendTitle(TextFormat.GREEN + "您已成功傳送至", worldConf.getString(targetName, targetName), 1, 1, 1);
								return true;
							}
						}else {
							p.sendMessage(TextFormat.GRAY + "此地圖尚未準備好，無法進行傳送");
						}
					}
				}
				else if(args[0].equals("gettp")) {
					if(sender.isPlayer()) {
						Player p = (Player)sender;
						Item tper = Item.get(345);
						tper.setCustomName(TextFormat.ITALIC + (TextFormat.LIGHT_PURPLE + "世界傳送儀"));
						tper.setLore(TextFormat.YELLOW + "這是前朝斯卡圖部落所流傳下來的遺物, ", TextFormat.YELLOW + "似乎能夠利用她前往各個世界呢");
						if(p.getInventory().canAddItem(tper)) {
							p.getInventory().addItem(tper);
							p.sendMessage(TextFormat.GREEN + "您已獲得了" + tper.getName());
							return true;
						}else {
							p.sendMessage(TextFormat.RED + "拰的背包已滿, 無法獲得此物品");
						}
					}
				}
			}
		}catch(ArrayIndexOutOfBoundsException err) {
			sender.sendMessage(TextFormat.RED + "請輸入正確的指令格式!");
		}
		return false;
	}
	
	/*刪除資料夾，資料夾中可能還有資料夾，所以要用遞迴把它處理掉*/
	public static boolean removeLevel(String levelname) {
		if(worlds.exists()) {
			if(worlds.isDirectory()) {
				File levelFile = new File(worlds, levelname);
				if(levelFile.isDirectory()) {
					File[] files = levelFile.listFiles();
					for(File f : files) {
						if(f.isDirectory()) removeLevel(levelname + File.separator + f.getName());
						f.delete();
					}
					levelFile.delete();
				}
			}
			return true;
		}
		return false;
	}
	
	public void registLevel(String name, String title, int type) {
		Server server = this.getServer();
		worldConf.set(name, title);
		if(!server.getLevels().containsValue(server.getLevelByName(name))) {
			this.getServer().generateLevel(name, 0, Generator.getGenerator(type));
		}
		worldConf.save();
	}
	
	public static Config getWorldsConfig() {
		return worldConf;
	}
	
	public static File getWorlds() {
		return worlds;
	}
}
