package cosr.vip;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cmen.essalg.CJEF;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import cosr.vip.VIP.VIPRank;
import cosr.vip.task.VIPDaysUpdator;

public class CVIP extends PluginBase {
	
	private static CVIP main;
	private static Config vipConfig;		//list-title, vip1, vip2, vip3, vip4, vip5, enable-days, white-list
	private static Map<String, VIP> playerVIPMap = new HashMap<String, VIP>();
	private static Map<String, VIP> grantVIPRequestions = new HashMap<String, VIP>();
	private static List<String> whiteList = new ArrayList<String>();

	public static final String INFO_TITLE = TextFormat.BOLD + (TextFormat.WHITE+"[" + TextFormat.DARK_AQUA+"CVIP" + TextFormat.WHITE+"]") + TextFormat.RESET;
	
	public static CVIP getInstance() {
		return main;
	}
	
	public static Map<String, VIP> getPlayerVIPMap() {
		return playerVIPMap;
	}
	
	public static Config getVipConfig() {
		return vipConfig;
	}

	public static Map<String, VIP> getGrantVIPRequestions() {
		return grantVIPRequestions;
	}
	
	public static List<String> getWhiteList() {
		return whiteList;
	}

	@Override
	public void onEnable() {
		main = this;
		vipConfig = new Config(new File(this.getDataFolder(), "vipConfig.yml"), Config.YAML);
		whiteList = vipConfig.getStringList("white-list");
		if(!whiteList.contains("CONSOLE")) {
			whiteList.add("CONSOLE");
		}
		File dir = new File(this.getDataFolder(), "players");
		if(!dir.exists()) {
			dir.mkdir();
		}
		for(File f : dir.listFiles()) {
			String ownerName = f.getName().replace(".yml", "");
			VIP vip = new VIP(ownerName);
			//重置
			vip.addDays(-1);
			playerVIPMap.put(ownerName, vip);
		}
		Server.getInstance().getScheduler().scheduleRepeatingTask(new VIPDaysUpdator(this), 1728000);
	}

	@Override
	public void onDisable() {
		Config conf;
		for(String pn : playerVIPMap.keySet()) {
			conf = new Config(new File(this.getDataFolder(), "players" + File.separator + pn+".yml"), Config.YAML);
			conf.setAll(playerVIPMap.get(pn).dataSection());
			conf.save();
		}
		conf = new Config(new File(this.getDataFolder(), "vipConfig.yml"), Config.YAML);
		conf.set("white-list", whiteList);
		conf.save();
		conf = null;
		playerVIPMap.clear();
		grantVIPRequestions.clear();
		System.gc();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equals("cvip")) {
			if(args.length < 1) return false;
			
			if(args[0].equals("help")) {
				sender.sendMessage(CVIP.INFO_TITLE + TextFormat.AQUA + "課金入魔女教!!");
			}else if(args[0].equals("my")) { 
				if(sender.isPlayer()) {
					Player p = (Player)sender;
					if(playerVIPMap.containsKey(p.getName())) {
						p.sendMessage(playerVIPMap.get(p.getName()).listData());
					}
				}else
					sender.sendMessage(TextFormat.RED + "請在遊戲中執行此指令!");
				
			}else if(args[0].equals("list")) {
				String list = "";
				String separator = TextFormat.RESET + "======================\n";
				VIP vip;
				VIPRank rank;
				for(String pn : playerVIPMap.keySet()) {
					vip = playerVIPMap.get(pn);
					rank = vip.getRank();
					if(vip.isEnable()) {
						list += TextFormat.BOLD + (TextFormat.WHITE + "-") + TextFormat.LIGHT_PURPLE + pn + ": " + 
								TextFormat.ITALIC + (rank.getColor() + rank.getName() + "\n" + TextFormat.RESET);
					}
				}
				sender.sendMessage(TextFormat.LIGHT_PURPLE + vipConfig.getString("list-title", "--- COSR魔女教成員 ---\n") + separator + list);
				vip = null;
				rank = null;
				System.gc();
			}else if(args[0].equals("grant")) {
				//plaerName rank
				if(sender.isOp()) {
					String playerName = "";
					VIPRank rank = null;
					if(args.length < 3) {
						if(args.length < 2) {
							if(sender.isPlayer()) {
								playerName = sender.getName();
								rank = VIPRank.VIP1;
							}else {
								sender.sendMessage(TextFormat.RED + "請在遊戲中執行此指令!");
								return true;
							}
						}else {
							playerName = args[1];
							rank = VIPRank.VIP1;
						}
					}else {
						playerName = args[1];
						rank = VIPRank.valueOf(args[2].toUpperCase());
					}
					
					if(!grantVIPRequestions.containsKey(sender.getName())) {
						if(sender.isPlayer()) {
							sender.sendMessage(CVIP.INFO_TITLE + TextFormat.BOLD + (TextFormat.RED + "您現在正在執行敏感操作, 請輸入密碼以確保您為可信對象"));
							FormWindowCustom window = new FormWindowCustom(CVIP.INFO_TITLE + "密碼確認");
							window.addElement(new ElementInput("請輸入密碼"));
							((Player)sender).showFormWindow(window);
							grantVIPRequestions.put(sender.getName(), new VIP(playerName, rank));
						}else {
							Calendar c = Calendar.getInstance();
							c.setTime(new Date());
							VIP vip = new VIP(playerName, rank, c);
							try {
								vip.grantTitle();
							}catch (FileNotFoundException e) {
								e.printStackTrace();
							}
							playerVIPMap.put(playerName, vip);
							sender.sendMessage(CVIP.INFO_TITLE + TextFormat.GREEN + "您已成功賦予玩家" + TextFormat.WHITE + playerName + 
									rank.getColor() + rank.getName() + TextFormat.GREEN + "的位階");
						}
					}
				}else {
					sender.sendMessage(TextFormat.RED + "查無此指令, 或您的權限不足");
				}
			}else if(args[0].equals("givexp")) {
				//plaerName rank
				if(sender.isOp()) {
					if(whiteList.contains(sender.getName())) {
						String playerName = "";
						int vipExp = 0;
						if(args.length < 3) {
							return false;
						}
						
						if(!CJEF.isInteger(args[2])) {
							sender.sendMessage(CVIP.INFO_TITLE + TextFormat.RED + "請輸入正確的正整數格式");
							return true;
						}
						
						playerName = args[1];
						vipExp = Integer.parseInt(args[2]);
						
						if(playerVIPMap.containsKey(playerName)) {
							playerVIPMap.get(playerName).addExp(vipExp);
							sender.sendMessage(CVIP.INFO_TITLE + TextFormat.GREEN + "您已成功給予玩家" + TextFormat.WHITE + playerName + 
									TextFormat.AQUA + vipExp + TextFormat.RESET + "VIP經驗值");
						}else {
							sender.sendMessage(CVIP.INFO_TITLE + TextFormat.RED + "該玩家尚未擁有VIP, 請賦予後再執行此指令");
						}
						return true;
					}
				}
				sender.sendMessage(TextFormat.RED + "查無此指令, 或您的權限不足");
			}else if(args[0].equals("enable")) {
				if(sender.isOp()) {
					if(whiteList.contains(sender.getName())) {
						String playerName = "";
						if(args.length < 2) {
							if(sender.isPlayer())
								playerName = sender.getName();
							else {
								sender.sendMessage(TextFormat.RED + "請在遊戲中執行此指令!");
								return true;
							}
						}else {
							playerName = args[1];
						}
						
						if(playerVIPMap.containsKey(playerName)) {
							VIP vip = playerVIPMap.get(playerName);
							vip.setDate(Calendar.getInstance());
							vip.start();
						}else {
							File file = new File(this.getDataFolder(), playerName);
							if(!file.exists()) {
								sender.sendMessage(CVIP.INFO_TITLE + TextFormat.RED + "找不到該玩家");
							}
							Config conf = new Config(file, Config.YAML);
							conf.set("date", new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
							conf.set("enable", true);
							conf.save();
						}
						sender.sendMessage(CVIP.INFO_TITLE + TextFormat.GREEN + "您已成功啟用玩家" + TextFormat.WHITE + playerName + 
								TextFormat.GREEN + "的" + TextFormat.LIGHT_PURPLE + "VIP");
						return true;
					}
				}
				sender.sendMessage(TextFormat.RED + "查無此指令, 或您的權限不足");
			}else if(args[0].equals("disable")) {
				if(sender.isOp()) {
					if(whiteList.contains(sender.getName())) {
						String playerName = "";
						if(args.length < 2) {
							if(sender.isPlayer())
								playerName = sender.getName();
							else {
								sender.sendMessage(TextFormat.RED + "請在遊戲中執行此指令!");
								return true;
							}
						}else {
							playerName = args[1];
						}
						
						if(playerVIPMap.containsKey(playerName)) {
							VIP vip = playerVIPMap.get(playerName);
							vip.setDaysVia(0);
							vip.stop();
						}else {
							File file = new File(this.getDataFolder(), playerName);
							if(!file.exists()) {
								sender.sendMessage(CVIP.INFO_TITLE + TextFormat.RED + "找不到該玩家");
							}
							Config conf = new Config(file, Config.YAML);
							conf.set("days", 0);
							conf.set("enable", false);
							conf.save();
						}
						sender.sendMessage(CVIP.INFO_TITLE + TextFormat.RED + "您已成功禁用玩家" + TextFormat.WHITE + playerName + 
								TextFormat.RED + "的" + TextFormat.LIGHT_PURPLE + "VIP");
						return true;
					}
				}
				sender.sendMessage(TextFormat.RED + "查無此指令, 或您的權限不足");
			}else if(args[0].equals("remove")) {
				
			}
		}
		return true;
	}
	
	public static boolean isVIP(String playerName) {
		if(CVIP.playerVIPMap.containsKey(playerName)) {
			return playerVIPMap.get(playerName).isEnable();
		}else {
			File file = new File(CVIP.getInstance().getDataFolder(), playerName);
			if(!file.exists()) {
				return false;
			}
			Config conf = new Config(file, Config.YAML);
			return conf.getBoolean("enable", false);
		}
	}
	
	public static Map<String, VIP> rerankAll() {
		Comparator<Map.Entry<String, VIP>> rankComparator = new Comparator<Map.Entry<String, VIP>>() {
			@Override
			public int compare(Entry<String, VIP> v1, Entry<String, VIP> v2) {
				return v2.getValue().getRank().getLevel() - v1.getValue().getRank().getLevel();
			}
		};
		
		List<Map.Entry<String, VIP>> list = new LinkedList<Map.Entry<String, VIP>>(CVIP.playerVIPMap.entrySet());
		Collections.sort(list,rankComparator);
		Map<String, VIP> map = new HashMap<String, VIP>();
		for(Map.Entry<String, VIP> entry : list) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}
}
