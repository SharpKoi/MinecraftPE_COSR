/*
 * mate: 
 * friends:
 * - 
 * - 
 * - 
 * friend_requests:
 * - 
 * - 
 * - 
 */

package cosr.friend;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import cosr.friend.api.CSocialAPI;
import cosr.mcpemail.Mail;
import cosr.roleplay.CRolePlay;
import cosr.roleplay.database.PlayerDataBase;

public class SocialMain extends PluginBase {
	
	public static final String infoTitle = TextFormat.RESET + 
			(TextFormat.BOLD + (TextFormat.WHITE + "[") + (TextFormat.DARK_AQUA + "CSocial") + (TextFormat.WHITE + "]"));
	
	public static Map<String, ArrayList<String>> FPOOL = new HashMap<String, ArrayList<String>>();					//每個玩家的朋友群
	public static Map<String, String> MPOOL = new HashMap<String, String>();										//每個玩家的配偶
	public static Map<String, ArrayList<String>> friendRequestPool = new HashMap<String, ArrayList<String>>();		//每個玩家收到的好友邀請
	public static Map<String, String> proposingPool = new HashMap<String, String>();								//每個玩家的求婚請求
	public static Set<String> breakingSet = new HashSet<String>();													//每個玩家的離婚請求
	public static Map<String, String> msgMap;																		//每個玩家的心情小語
	
	private static SocialMain plugin;
	
	public static SocialMain getInstance() {
		return SocialMain.plugin;
	}
	
	public static ArrayList<String> getFriendRequests(String playerName) {
		return friendRequestPool.get(playerName);
	}
	
	public void onEnable() {
		plugin = this;
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		msgMap = new HashMap<String, String>();
		
		File dataFolder = this.getDataFolder();
		for(File playerFile : dataFolder.listFiles()) {
			Config conf = new Config(playerFile);
			String playerName = playerFile.getName().replace(".yml", "");
			FPOOL.put(playerName, new ArrayList<String>(conf.getStringList("friends")));
			friendRequestPool.put(playerName, new ArrayList<String>(conf.getStringList("friend_requests")));
		}
	}
	
	public void onDisable() {
		Config conf;
		for(String playerName : MPOOL.keySet()) {
			conf = new Config(new File(this.getDataFolder(), playerName + ".yml"));
			conf.set("mate", MPOOL.get(playerName));
			conf.save();
			conf = new Config(new File(this.getDataFolder(), MPOOL.get(playerName) + ".yml"));
			conf.set("mate", playerName);
			conf.save();
		}
		for(String playerName : FPOOL.keySet()) {
			conf = new Config(new File(this.getDataFolder(), playerName + ".yml"));
			conf.set("friends", FPOOL.get(playerName));
			conf.save();
		}
		for(String playerName : friendRequestPool.keySet()) {
			conf = new Config(new File(this.getDataFolder(), playerName + ".yml"));
			conf.set("friend_requests", friendRequestPool.get(playerName));
			conf.save();
		}
		for(String playerName : msgMap.keySet()) {
			conf = new Config(new File(this.getDataFolder(), playerName + ".yml"));
			conf.set("mood-msg", msgMap.get(playerName));
			conf.save();
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equals("csocial")) {
			if(args.length < 1) return false;
			
			if(args[0].equals("save-all")) {
				if(!sender.isOp()) return false;
				Config conf;
				for(String playerName : MPOOL.keySet()) {
					conf = new Config(new File(this.getDataFolder(), playerName + ".yml"));
					conf.set("mate", MPOOL.get(playerName));
					conf.save();
					conf = new Config(new File(this.getDataFolder(), MPOOL.get(playerName) + ".yml"));
					conf.set("mate", playerName);
					conf.save();
				}
				for(String playerName : FPOOL.keySet()) {
					conf = new Config(new File(this.getDataFolder(), playerName + ".yml"));
					conf.set("friends", FPOOL.get(playerName));
					conf.save();
				}
				for(String playerName : friendRequestPool.keySet()) {
					conf = new Config(new File(this.getDataFolder(), playerName + ".yml"));
					conf.set("friend_requests", friendRequestPool.get(playerName));
					conf.save();
				}
			}
			else if(args[0].equals("motto")) {
				if(args[1].equals("set")) {
					if(args.length < 3) return false;
					
					if(sender.isPlayer()) {
						msgMap.put(sender.getName(), args[2]);
						sender.sendMessage(TextFormat.GREEN + "心情小語設定成功!");
					}else
						sender.sendMessage(TextFormat.RED + "請在遊戲中執行此指令");
				}
			}
			else if(args[0].equals("ui")) {
				if(sender.isPlayer()) {
					Player p = (Player) sender;
					p.showFormWindow(SocialGUI.homePage());
				}else
					sender.sendMessage(TextFormat.RED + "請在遊戲中執行此指令");
			}
			else if(args[0].equals("check")) {
				if(!sender.isOp()) return false;
				String mlist = "";
				for(String playerName : MPOOL.keySet()) {
					mlist += playerName + "---" + MPOOL.get(playerName) + "\n";
				}
				sender.sendMessage("MPOOL: \n" + mlist);
			}
			else if(args[0].equalsIgnoreCase("doll")) {
				if(sender.isPlayer()) {
					Player p = (Player) sender;
					Item doll = CSocialAPI.socialItem();
					if (p.getInventory().canAddItem(doll)) {
						p.getInventory().addItem(doll);
						p.sendMessage(TextFormat.GREEN + "您獲得了" + TextFormat.RESET + doll.getName() + TextFormat.RESET + "x1");
					}
				}
			}
 		}
		if(cmd.getName().equals("cfriend")) {
			if(!sender.isPlayer()) {
				sender.sendMessage(TextFormat.RED + "請在遊戲中執行此指令");
				return true;
			}
			if(args.length < 1) return false;
			
			Player p = (Player) sender;
			if(args[0].equals("help")) {
				
			}
			else if(args[0].equals("my")) {
				String friendList = "";
				for(String friendName : FPOOL.get(p.getName())) {
					friendList += friendName + "\n";
				}
				
				p.sendMessage("--- 您的好友列表 ---\n" + friendList);
			}else if(args[0].equals("requests")) {
				String requestList = "";
				if(!friendRequestPool.containsKey(p.getName())) {
					friendRequestPool.put(p.getName(), new ArrayList<String>());
				}
				for(String request : friendRequestPool.get(p.getName())) {
					requestList += request + "\n";
				}
				p.sendMessage("所有未處理的好友邀請: \n" + requestList);
			}
			else if(args[0].equals("add")) {
				if(args.length < 2) return false;
				
				if(FPOOL.get(p.getName()).contains(args[1])) {
					p.sendMessage(TextFormat.GRAY + "該玩家已經是您的好友囉");
					return true;
				}
				
				if(args[1].equals(p.getName())) {
					p.sendMessage(TextFormat.RED + "無法將自己加為好友");
					return true;
				}
				
				Player target = this.getServer().getPlayer(args[1]);
				if(target != null) {
					if(!friendRequestPool.containsKey(args[1])) {
						friendRequestPool.put(args[1], new ArrayList<String>());
					}
					friendRequestPool.get(args[1]).add(p.getName());
					target.sendMessage(TextFormat.ITALIC + (TextFormat.YELLOW + "玩家" + p.getName() + "向您提出了好友邀請\n")
							+ TextFormat.RESET + "輸入/cfriend accept " + p.getName() + "  接受\n"
							+ TextFormat.RESET + "輸入/cfriend deny " + p.getName() + "  拒絕");
				}else {
					File file = new File(this.getDataFolder(), args[1] + ".yml");
					if(!file.exists()) {
						p.sendMessage(TextFormat.RED + "找不到該玩家");
						return true;
					}
					
					Config conf = new Config(file);
					if(!conf.exists("friend_requests")) {
						conf.set("friend_requests", new ArrayList<String>());
					}
					conf.getStringList("friend_requests").add(p.getName());
					conf.save();
				}
			}
			else if(args[0].equals("retract")) {
				if(args.length < 2) return false;
				
				if(friendRequestPool.containsKey(args[1])) {
					if(friendRequestPool.get(args[1]).contains(p.getName())) {
						friendRequestPool.get(args[1]).remove(p.getName());
						p.sendMessage(TextFormat.GRAY + "已成功撤回好友邀請");
						return true;
					}
				}
				//若上述兩條件皆無達成責執行此
				p.sendMessage(TextFormat.GRAY + "您尚未對該玩家送出任何好友邀請");
			}
			else if(args[0].equals("accept")) {
				if(args.length < 2) return false;
				
				if(friendRequestPool.containsKey(p.getName())) {
					if(friendRequestPool.get(p.getName()).contains(args[1])) {
						p.sendMessage(TextFormat.GREEN + "成功接受" + args[1] + "的好友邀請");
						if(!FPOOL.containsKey(p.getName())) {
							FPOOL.put(p.getName(), new ArrayList<String>());
						}
						FPOOL.get(p.getName()).add(args[1]);
						
						Player requestOne = this.getServer().getPlayer(args[1]);
						if(requestOne != null) {
							requestOne.sendMessage(TextFormat.GREEN + "玩家" + p.getName() + "接受了您的好友邀請!");
							FPOOL.get(args[1]).add(p.getName());
						}else {
							File file = new File(this.getDataFolder(), args[1] + ".yml");
							Config conf = new Config(file);
							conf.getStringList("friends").add(p.getName());
							conf.save();
						}
						
						friendRequestPool.get(p.getName()).remove(args[1]);
						if(friendRequestPool.containsKey(args[1])) {
							if(friendRequestPool.get(args[1]).contains(p.getName()))
								friendRequestPool.get(args[1]).remove(p.getName());
						}
						return true;
					}
				}
				p.sendMessage(TextFormat.GRAY + "無該玩家的好友邀請");
			}
			else if(args[0].equals("deny")) {
				if(args.length < 2) return false;
				
				if(friendRequestPool.containsKey(p.getName())) {
					if(friendRequestPool.get(p.getName()).contains(args[1])) {
						p.sendMessage(TextFormat.GRAY + "您拒絕了" + args[1] + "的好友邀請");
						friendRequestPool.get(p.getName()).remove(args[1]);
						return true;
					}
				}
				p.sendMessage(TextFormat.GRAY + "無該玩家的好友邀請");
			}
			else if(args[0].equals("remove")) {
				if(args.length < 2) return false;
				
				ArrayList<String> friendList = FPOOL.get(p.getName());
				if(friendList.contains(args[1])) {
					friendList.remove(args[1]);
					
					if(FPOOL.containsKey(args[1])) {
						FPOOL.get(args[1]).remove(p.getName());
					}else {
						File file = new File(this.getDataFolder(), args[1] + ".yml");
						if(!file.exists()) {
							p.sendMessage(TextFormat.RED + "找不到該玩家");
							return true;
						}
						
						Config conf = new Config(file);
						if(!conf.exists("friends")) {
							conf.set("friends", new ArrayList<String>());
						}
						if(!conf.getStringList("friends").contains(p.getName())) {
							p.sendMessage(TextFormat.RED + "您未在該玩家的好友名單內");
							return true;
						}
						conf.getStringList("friends").remove(p.getName());
						conf.save();
					}
					p.sendMessage(TextFormat.GRAY + "已將玩家" + args[1] + "從您的好友名單中移除");
					
				}else {
					p.sendMessage(TextFormat.RED + "該玩家未在您的好友名單內");
				}
			}
			else
				return false;
		}
		else if(cmd.getName().equals("cmarry")) {
			if(!sender.isPlayer()) {
				sender.sendMessage(TextFormat.RED + "請在遊戲中執行此指令");
				return true;
			}
			if(args.length < 1) return false;
			
			Player p = (Player) sender;
			if(args[0].equals("help")) {
				
			}
			else if(args[0].equals("my")) {
				String mate = getMate(p.getName());
				if((mate != null)) {
					p.sendMessage(TextFormat.AQUA + "您的伴侶是: " + TextFormat.RESET + mate + "\n" + 
							((this.getServer().getPlayer(mate) != null)? 
							TextFormat.GREEN + "現在正在線上, 趕快去找TA吧!" : TextFormat.GRAY + "現在不再線上, 趕快找TA來玩吧!"));
					
				}else {
					p.sendMessage(TextFormat.GRAY + "您當前還沒有伴侶, 趕快去找一個吧(#");
				}
			}
			else if(args[0].equals("propose")) {
				if(args.length < 2) return false;
				
					Player target = this.getServer().getPlayer(args[1]);
					
					if(args[1].equals(p.getName())) {
						p.sendMessage(TextFormat.RED + "無法向自己告白www");
						return true;
					}
					
					if(proposingPool.containsValue(p.getName())) {
						p.sendMessage(TextFormat.GRAY + "抱歉! 一人無法同時向多個人求婚呦");
						p.sendMessage(TextFormat.YELLOW + "別再三心二意了, 趕緊向TA表示些什麼吧!");
						return true;
					}
					
					if(MPOOL.containsValue(args[1]) || MPOOL.containsKey(args[1])) {
						p.sendMessage(TextFormat.GRAY + "該對象已經有配偶囉, 無法向TA求婚惹......");
						return true;
					}
					if(proposingPool.containsKey(args[1])) {
						p.sendMessage(TextFormat.GRAY + "該對象已經有其他人正在求婚囉, 還請稍後呢!");
						p.sendMessage(TextFormat.YELLOW + "(小提示: 寄封信給他, 讓他知道你的心意吧)");
						return true;
					}
					
					if(target != null) {
						proposingPool.put(args[1], p.getName());
						target.sendMessage(TextFormat.ITALIC + (TextFormat.YELLOW + "玩家" + p.getName() + "向您求婚了!! >///<\n")
								+ TextFormat.RESET + "輸入/cmarry accept 接受TA的心意><\n"
								+ TextFormat.RESET + "輸入/cmarry deny  婉拒TA的心意QQ");
					}else {
						p.sendMessage(TextFormat.GRAY + "該對象似乎不在線上, 等TA上線了再向他表達心意吧!");
					}
			}
			else if(args[0].equals("retract")) {
					if(proposingPool.containsValue(p.getName())) {
						proposingPool.remove(getKeyByValue(proposingPool, p.getName()));
						p.sendMessage(TextFormat.GRAY + "已成功撤回結婚請求");
					}else
						p.sendMessage(TextFormat.GRAY + "您當前沒有送出任何結婚請求!");
				
			}
			else if(args[0].equals("accept")) {
				try {
					if(proposingPool.containsKey(p.getName())) {
						MPOOL.put(proposingPool.get(p.getName()), p.getName());
						p.sendTitle(TextFormat.RED + "恭喜!", "您接受了" + proposingPool.get(p.getName()) + "玩家的求婚, 正式結為夫妻 >///<");
						String ta_name = proposingPool.get(p.getName());
						Player ta = this.getServer().getPlayer(ta_name);
						
						if(ta != null) ta.sendTitle(TextFormat.RED + "恭喜!", "玩家" + p.getName() + "接受了您的求婚, 正式結為夫妻 >///<");
						
						new Mail(TextFormat.RED + "GM", ta_name, TextFormat.RED + "系統道賀信件", 
								"恭喜! 玩家"+p.getName()+"接受了您的求婚!\n" + 
								"我們相信這是一種緣分, 希望你們能夠好好經營這份良緣, \n" + 
								"我們也將舉辦更多活動, 以維持這份感動\n\n" + 
								TextFormat.RESET + "COSR團隊 敬上").sendOut();
						
						new Mail(TextFormat.RED + "GM", p.getName(), TextFormat.RED + "系統道賀信件", 
								"恭喜! 您與玩家" +ta_name+"正式結為夫妻!\n" + 
								"我們相信這是一種緣分, 希望你們能夠好好經營這份良緣, \n" + 
								"我們也將舉辦更多活動, 以維持這份感動\n\n" + 
								TextFormat.RESET + "COSR團隊 敬上").sendOut();
						
						CRolePlay.getAchvMap().get("HEARTANDSOUL").grantTo(p.getName());
						CRolePlay.getAchvMap().get("HEARTANDSOUL").grantTo(ta_name);
						Server.getInstance().broadcastMessage(TextFormat.ITALIC + (TextFormat.BOLD + (TextFormat.YELLOW + "恭喜!! " + 
																TextFormat.AQUA + "玩家" + TextFormat.WHITE + ta_name + 
																TextFormat.AQUA + "與玩家" + TextFormat.WHITE + p.getName() + 
																TextFormat.RED + "結為夫妻" + TextFormat.AQUA + "了!!\n" + 
																TextFormat.GOLD + "請大家為他們獻上最好的祝福吧!")));
						
						proposingPool.remove(p.getName());
						
					}else {
						p.sendMessage(TextFormat.GRAY + "您當前沒有收到任何結婚請求, 不如再等一段時間看看吧!");
					}
				}catch(FileNotFoundException err) {
					//catch
				}
					
			}
			else if(args[0].equals("deny")) {
					if(proposingPool.containsKey(p.getName())) {
						p.sendMessage(TextFormat.GRAY + "您拒絕了玩家"+proposingPool.get(p.getName())+"的請求!");
						Player ta = this.getServer().getPlayer(proposingPool.get(p.getName()));
						
						if(ta != null) {
							ta.sendMessage(TextFormat.RED + "玩家"+p.getName()+"拒絕了您的求婚......\n" + 
											TextFormat.GRAY + "或許是心意沒有傳達到, 不過沒關係, 準備好後下一次會更好!");
						}else {
							new Mail(TextFormat.RED + "GM", proposingPool.get(p.getName()), TextFormat.RED + "系統信件", 
									TextFormat.RED + "玩家"+p.getName()+"拒絕了您的求婚......\n" + 
									TextFormat.GRAY + "或許是心意沒有傳達到, 不過沒關係, 準備好後下一次會更好!\n\n" + 
									TextFormat.RESET + "COSR團隊 敬上").sendOut();
						}
						
						proposingPool.remove(p.getName());
						
					}else {
						p.sendMessage(TextFormat.GRAY + "您當前沒有收到任何結婚請求, 不如再等一段時間看看吧!");
					}
			}
			else if(args[0].equals("tp")) {
				String mateName = getMate(p.getName());
				if(mateName != null) {
					Player mate = this.getServer().getPlayer(mateName);
					if(mate != null) {
						p.teleport(mate);
						mate.sendMessage(TextFormat.GOLD + "您的伴侶來找您了!");
					}else
						p.sendMessage(TextFormat.GRAY + "您的伴侶目前不在線上, 趕快找他來玩吧!");
				}else
					p.sendMessage(TextFormat.GRAY + "您當前還沒有伴侶, 趕快去找一個吧(#");
			}
			else if(args[0].equals("break")) {
				String mateName = getMate(p.getName());
				if(mateName != null) {
					breakingSet.add(p.getName());
					p.sendMessage(TextFormat.RED + "您真的要離開TA了嗎...?");
					Player mate = this.getServer().getPlayer(mateName);
					
					if(mate != null) {
						p.sendMessage(TextFormat.GRAY + "已送出離婚請求, 等待對方回覆......");
						mate.sendMessage(TextFormat.RED + "您的伴侶"+p.getName()+"向您提出了離婚請求......\n" + 
											"再聊天室輸入@Y以表示同意\n" + 
											"再聊天室輸入@N表示拒絕");
					}else {
						if(isLongTimeNoLogin(mateName)) {
							p.sendMessage(TextFormat.GRAY + "對方未上線時隔已久, 無條件離婚成功");
							
							new Mail(TextFormat.RED + "GM", mateName, TextFormat.RED + "系統信件", 
									TextFormat.RED + "玩家"+p.getName()+"提出了離婚要求\n" + 
									TextFormat.GRAY + "由於您太久沒有上線, 為了維護玩家權利, 已無條件自動離婚\n\n" + 
									  				  "我們對此感到抱歉, 若您有任何問題或異議, 歡迎聯繫我們\n\n" + 
									TextFormat.RESET + "COSR團隊 敬上").sendOut();
							
							breakingSet.remove(p.getName());
							MPOOL.remove(p.getName());
						}else {
							p.sendMessage(TextFormat.GRAY + "您的伴侶不在線上, 離婚須兩人同意!");
						}
					}
				}else
					p.sendMessage(TextFormat.GRAY + "您當前還沒有伴侶, 趕快去找一個吧(#");
			}
			else
				return false;
		}
		return true;
	}
	
	public static Object getKeyByValue(Map<? extends Object, ? extends Object> map, Object value) {
		for(Object o : map.keySet()) {
			if(map.get(o).equals(value)) {
				return o;
			}
		}
		return null;
	}
	
	public static String getMate(String playerName) {
		if(MPOOL.containsKey(playerName)) {
			return MPOOL.get(playerName);
		}else {
			String mateName = (String) getKeyByValue(MPOOL, playerName);
			if(mateName != null) {
				return mateName;
			}else {
				Config conf = new Config(new File(SocialMain.getInstance().getDataFolder(), playerName+".yml"), Config.YAML);
				if(conf.exists("mate")) {
					return conf.getString("mate");
				}else
					return null;
			}
		}
	}
	
	public static String getMoodMsg(String playerName) {
		if(msgMap.containsKey(playerName)) {
			return msgMap.get(playerName);
		}else {
			Config conf = new Config(new File(SocialMain.getInstance().getDataFolder(), playerName + ".yml"), Config.YAML);
			if(conf.exists("mood-msg")) {
				return conf.getString("mood-msg");
			}else {
				return null;
			}
		}
	}
	
	//TODO: 移至CRolePlay的PlayerDataBase
	public static boolean isLongTimeNoLogin(String playerName) {
		PlayerDataBase pdb = new PlayerDataBase(playerName);
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		
		if(now.get(Calendar.YEAR) > pdb.loginMoment.get(Calendar.YEAR) || 
				now.get(Calendar.MONTH) > pdb.loginMoment.get(Calendar.MONTH) ||
				now.get(Calendar.DAY_OF_MONTH) - pdb.loginMoment.get(Calendar.DAY_OF_MONTH) >= 14) {
			return true;
		}else
			return false;
	}
}
