/*
 * 
 * 
 * 
 */

package cosr.roleplay;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import cosr.roleplay.database.PlayerDataBase;
import cosr.roleplay.gcollection.Achievement;
import cosr.roleplay.gcollection.Title;
import cosr.roleplay.gcollection.Title.Rarity;
import cosr.roleplay.listener.EventListener;

public class CRolePlay extends PluginBase implements Listener
{
	private static CRolePlay plugin = null;		//this
	
	private static HashMap<String, PlayerDataBase> onlinePDB= new HashMap<>();									//PlayerName-PlayerDataBase
	private static LinkedHashMap<String, Achievement> achvMap = new LinkedHashMap<String, Achievement>();		//head-achv(it will be the dictionary of avhievements)
	private static LinkedHashMap<String, Title> titleMap = new LinkedHashMap<String, Title>();					//head-title(it will be the dictionary of titles)
	
	private static Config pluginConfig;		//the basic configuration for this plugin
	
	private static final String achv_Usage = "------成就指令使用方法(1/1)------\n" + 
								"/achv help                       ----查看指令幫助\n" + 
								"/achv my                         ----列出自己擁有的所有成就\n" + 
								"/achv list                       ----列出所有成就項目";
	///achv grant [player] [achv_head] ----給予指定玩家一項成就
	
	private static final String title_Usage =  "------稱號指令使用方法(1/1)------\n" + 
								"/title help                       ----查看指令幫助\n" + 
								"/title my                         ----列出自己擁有的所有稱號\n" + 
								"/title list                       ----列出所有稱號項目\n" + 
								"/title pin [title_head]           ----在自己的名字上冠上稱號頭銜\n" + 
								"/title unpin                      ----在自己的名字上取下稱號頭銜\n";
	///title grant [player] [title_head] ----給予指定玩家一個稱號
	
	/*return this*/
	public static CRolePlay getInstance() {
		return CRolePlay.plugin;
	}
	
	/*return onlinePDB*/
	public static HashMap<String, PlayerDataBase> getOnlinePDB() {
		return onlinePDB;
	}

	/*return achvMap(the dictionary of achievement)*/
	public static LinkedHashMap<String, Achievement> getAchvMap() {
		return achvMap;
	}

	/*return achvMap(the dictionary of title)*/
	public static LinkedHashMap<String, Title> getTitleMap() {
		return titleMap;
	}
	
	/*return the plugin-configuration*/
	public static Config getPropertiesConfig() {
		return pluginConfig;
	}
	
	/*
	 * TODO: onEnable()
	 * #Tasks:
	 * 1. info in the logger
	 * 2. initial the plugin configuration
	 * 3. import the content of achievements from dictionary to achvMap
	 * 4. import the content of titles from dictionary to titleMap
	 * 5. if server reload, we also reload the PlayerDataBase for every online players
	 */
	public void onEnable() {
		CRolePlay.plugin = this;
		this.getLogger().info("" + this.getDescription().getVersion() + "");
		this.getServer().getPluginManager().registerEvents(new EventListener(),this);
		this.getDataFolder().mkdirs();
		
		pluginConfig = new Config(new File(this.getDataFolder(), "Config.yml"), Config.YAML);
		if(!pluginConfig.exists("player_level")) {
			pluginConfig.set("player_level", new ConfigSection() {
				private static final long serialVersionUID = 1L;
				{
					set("min_level", 1);
					set("max_level", 100);
					set("initial_level", 1);
				}
			});
		}
		
		Config achvConf = new Config(new File(this.getDataFolder(), Achievement.ACHV_FILE_NAME));
		for(String head : achvConf.getKeys(false)) {
			achvMap.put(head, new Achievement(head));
		}
		
		Config titleConf = new Config(new File(this.getDataFolder(), Title.TITLE_FILE_NAME));
		for(String head : titleConf.getKeys(false)) {
			titleMap.put(head, new Title(head));
		}
		
		if(this.getServer().getOnlinePlayers().size() > 0) {
			for(Player p : this.getServer().getOnlinePlayers().values()) {
				onlinePDB.put(p.getName(), new PlayerDataBase(p.getName()));
			}
		}
	}
	
	/*
	 * TODO: onDisable()
	 * #Tasks
	 * 1. save all the PlayerDataBase of every online players
	 * 2. save all the achievements
	 * 3. save all the titles
	 */
	public void onDisable() {
		for(String name : onlinePDB.keySet()) {
			onlinePDB.get(name).save();
		}
		
		Config achvConfig = new Config(new File(this.getDataFolder(), Achievement.ACHV_FILE_NAME));
		for(String head : achvMap.keySet()) {
			achvConfig.set(head, achvMap.get(head).dataSection());
		}
		achvConfig.save();
		
		Config titleConfig = new Config(new File(this.getDataFolder(), Title.TITLE_FILE_NAME));
		for(String head : titleMap.keySet()) {
			titleConfig.set(head, titleMap.get(head).dataSection());
		}
		titleConfig.save();
	}
	
	/*
	 * TODO: onCommand(CommandSender, Command, String, String[])
	 * #Task:
	 *  ##Title:
	 *   1. help info
	 *   2. title list
	 *   3. my title list
	 *   4. regist a new title {OP}
	 *   5. grant a title to someone {OP}
	 *   6. pin my title
	 *   7. unpin my title
	 *  ##Achievement:
	 *   1. help info
	 *   2. achv list
	 *   3. my achv list
	 *   4. regist a new achv {OP}
	 *   5. grant an achv to someone {OP}
	 *  ##PlayerLevel
	 *   1. help info
	 *   2. my level status
	 *   3. give exp to someone {OP}
	 *   4. set someone's level {OP}
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		/*稱號指令*/
		if(cmd.getName().equals("title")) {
			if(args.length < 1) return false;
			
			switch(args[0]) {
				case "help": 
					sender.sendMessage(title_Usage);
					break;
					
				case "list":
					if(sender.isPlayer()) {
						int page = 0;
						
						if(args.length < 2) {
							page = 1;
						}else {
							if(isInteger(args[1])) {
								page = Integer.parseInt(args[1]);
							}else {
								sender.sendMessage(TextFormat.RED + "請輸入正確的正整數格式");
								return true;
							}
						}
						
						Player p = (Player) sender;
						sender.sendMessage(PluginInfo.titleList(p, page));
					}
					break;
					
				case "my":
					if(sender.isPlayer()) {
						int page = 0;
						
						if(args.length < 2) {
							page = 1;
						}else {
							if(isInteger(args[1])) {
								page = Integer.parseInt(args[1]);
							}else {
								sender.sendMessage(TextFormat.RED + "請輸入正確的正整數格式");
								return true;
							}
						}
						
						Player p = (Player) sender;
						p.sendMessage(PluginInfo.personalTitleList(p, page));
					}else
						sender.sendMessage(TextFormat.RED + "請在遊戲中執行此指令");
					
					break;
					
				case "new":
					
					if(!sender.isOp()) {
						sender.sendMessage(TextFormat.RED + "您沒有足夠的權限使用此指令!");
						return true;
					}
					
					try {
						String head = (args.length >= 2)? args[1].toUpperCase() : "UNKNOWN";
						String name = (args.length >= 3)? args[2] : "";
						String description = (args.length >= 4)? args[3] : "";
						String requirement = (args.length >= 5)? args[4] : "";
						String reward = (args.length >= 6)? args[5] : "";
						Rarity rarity = (args.length >= 7)? Rarity.valueOf(args[6].toUpperCase()) : Rarity.NORMAL;
						titleMap.put(head, new Title(head, name, description, requirement, reward, rarity));
						break;
					}catch(IllegalArgumentException err) {
						sender.sendMessage(TextFormat.RED + "請輸入正確的稱號品級("
								+ Rarity.NORMAL.getColor() + "NORMAL" + TextFormat.RESET + ", "
								+ Rarity.ADVANCE.getColor() + "ADVANCE" + TextFormat.RESET + ", "
								+ Rarity.RARE.getColor() + "RARE" + TextFormat.RESET + ", "
								+ Rarity.LEGEND.getColor() + "LEGEND" + TextFormat.RESET + ", "
								+ Rarity.TABOO.getColor() + "TABOO" + TextFormat.RESET + ", "
								+ Rarity.COSR.getColor() + "COSR"
								+ TextFormat.RESET + ")");
					}
					
				case "grant": 
					if(args.length < 3) return false;
					
					if(!sender.isOp()) {
						sender.sendMessage(TextFormat.RED + "您沒有足夠的權限使用此指令!");
						return true;
					}
					
					if(!titleMap.containsKey(args[2].toUpperCase())) {
						sender.sendMessage(TextFormat.RED + "找不到您輸入的稱號, 請將稱號註冊後再賦予!");
						return true;
					}
					try {
						if(CRolePlay.titleMap.get(args[2].toUpperCase()).grantTo(args[1])) {
							sender.sendMessage(TextFormat.GREEN + "您成功賦予了" + (TextFormat.RESET + args[1]) + (TextFormat.GREEN + "一項稱號"));
						}else {
							sender.sendMessage(TextFormat.RED + "稱號賦予失敗! 請檢察您的輸入是否有誤");
						}
					}catch(FileNotFoundException err) {
						sender.sendMessage(TextFormat.RED + "找不到該玩家");
					}
					
					break;
					
				case "pin": 
					if(sender.isPlayer()) {
						Player p = (Player) sender;
						PlayerDataBase pdb = CRolePlay.getOnlinePDB().get(p.getName());
						if(pdb.getPlayerTitleMap().containsKey(args[1].toUpperCase())) {
							PlayerTitle pt = pdb.getPlayerTitleMap().get(args[1].toUpperCase());
							if(PlayerTitle.pinTag(p, args[1].toUpperCase())) {
								p.sendMessage(TextFormat.GREEN + "成功冠上 " + pt.nameTagForm() + " 的頭銜");
							}
						}else {
							p.sendMessage(TextFormat.RED + "您尚未擁有該稱號, 無法將此冠上頭銜!");
						}
					}else 
						sender.sendMessage(TextFormat.RED + "請在遊戲中執行此指令");
					
					break;
					
				case "unpin":
					if(sender.isPlayer()) {
						Player p = (Player) sender;
						if(PlayerTitle.unPinTag(p)) {
							p.sendMessage(TextFormat.GRAY + "成功卸下的頭銜");
						}
					}else 
						sender.sendMessage(TextFormat.RED + "請在遊戲中執行此指令");
					
					break;
				default:
					return false;
			}
		}
		
		/*成就指令*/
		if(cmd.getName().equalsIgnoreCase("achv")) {
			if(args.length < 1) return false;
			
			switch(args[0]) {
				case "help":
					sender.sendMessage(achv_Usage);
					break;
				
				case "list": 
					if(sender.isPlayer()) {
						int page = 0;
						
						if(args.length < 2) {
							page = 1;
						}else {
							if(!isInteger(args[1])) {
								sender.sendMessage(TextFormat.RED + "請輸入正確的正整數格式");
								return true;
							}
							page = Integer.parseInt(args[1]);
						}
						
						Player p = (Player) sender;
						sender.sendMessage(PluginInfo.achievementList(p, page));
					}
					break;
					
				case "my":
					if(sender.isPlayer()) {
						int page = 0;
						
						if(args.length < 2) {
							page = 1;
						}else {
							if(isInteger(args[1])) {
								page = Integer.parseInt(args[1]);
							}else {
								sender.sendMessage(TextFormat.RED + "請輸入正確的正整數格式");
								return true;
							}
						}
						
						Player p = (Player) sender;
						p.sendMessage(PluginInfo.personalAchvList(p, page));
					}else
						sender.sendMessage(TextFormat.RED + "請在遊戲中執行此指令");
					
					break;
			
				case "new":
					
					if(!sender.isOp()) {
						sender.sendMessage(TextFormat.RED + "您沒有足夠的權限使用此指令!");
						return true;
					}
					
					String head = (args.length >= 2)? args[1].toUpperCase() : "UNKNOWN";
					String name = (args.length >= 3)? args[2] : "";
					String description = (args.length >= 4)? args[3] : "";
					String requirement = (args.length >= 5)? args[4] : "";
					String reward = (args.length >= 6)? args[5] : "";
					
					achvMap.put(head, new Achievement(head, name, description, requirement, reward));
					break;
				
				case "grant":
					if(args.length < 3) return false;
					
					if(!sender.isOp()) {
						sender.sendMessage(TextFormat.RED + "您沒有足夠的權限使用此指令!");
						return true;
					}
					
					if(!achvMap.containsKey(args[2].toUpperCase())) {
						sender.sendMessage(TextFormat.RED + "找不到您輸入的成就, 請將成就註冊後再賦予!");
						return true;
					}
					try {
						if(CRolePlay.achvMap.get(args[2].toUpperCase()).grantTo(args[1])) {
							sender.sendMessage(TextFormat.GREEN + "您成功賦予了" + args[1] + "一項成就");
						}else {
							sender.sendMessage(TextFormat.RED + "成就賦予失敗! 請檢察您的輸入是否有誤");
						}
					}catch(FileNotFoundException err) {
						sender.sendMessage(TextFormat.RED + "找不到該玩家");
					}
					
					break;
				default:
					return false;
			}
		}
		
		/*等級指令*/
		if(cmd.getName().equalsIgnoreCase("lv")) {
			if(args.length < 1) return false;
			
			switch(args[0]) {
				case "help":
					break;
				case "my":
					if(sender.isPlayer()) {
						Player p = (Player) sender;
						p.sendMessage(PluginInfo.levelStatus(p));
					}else 
						sender.sendMessage(TextFormat.RED + "請在遊戲中執行此指令");
					
					break;
				case "give":
					if(args.length < 3) return false;
					
					if(!sender.isOp()) {
						sender.sendMessage(TextFormat.RED + "您沒有足夠的權限使用此指令!");
						return true;
					}
					
					if(!isInteger(args[2])) {
						sender.sendMessage(TextFormat.RED + "請輸入正確的正整數格式");
						return true;
					}
					try {
						giveExp(args[1], Integer.parseInt(args[2]));
						sender.sendMessage(TextFormat.GREEN + "成功給予" + (TextFormat.RESET + args[1]) + (TextFormat.GREEN + "玩家") + 
								TextFormat.AQUA + args[2] + "經驗值");
						Player p = this.getServer().getPlayer(args[1]);
						if(p != null) {
							p.sendMessage(TextFormat.AQUA + "您獲得了" + args[2] + "經驗值!");
						}
					}catch(FileNotFoundException err) {
						sender.sendMessage(TextFormat.RED + "找不到該玩家");
					}
					
					break;
				case "set":
					if(!isInteger(args[2])) {
						sender.sendMessage(TextFormat.RED + "請輸入正確的正整數格式");
						return true;
					}
					
					if(!sender.isOp()) {
						sender.sendMessage(TextFormat.RED + "您沒有足夠的權限使用此指令!");
						return true;
					}
					
					try {
						int lv = Integer.parseInt(args[2]);
						int minLv = pluginConfig.getInt("player_level.min_level", 1);
						int maxLv = pluginConfig.getInt("player_level.max_level", 100);
						if(lv < minLv) lv = minLv;						
						if(lv > maxLv) lv = maxLv;
						setPlayerLv(args[1], lv);
						sender.sendMessage(TextFormat.GREEN + "成功將玩家" + args[1] + "的等級設為" + lv);
					}catch(FileNotFoundException err) {
						sender.sendMessage(TextFormat.RED + "找不到該玩家");
					}
					
					break;
				default:
					return false;
			}
		}
		return true;
	}
	
	private static boolean isInteger(String s) {
		Pattern pattern = Pattern.compile("^([1-9]?)(\\d*)$");
		Matcher matcher = pattern.matcher(s);
		return matcher.matches();
	}
	
	
	
	public static Title getPinnedTitle(String playerName) {
		if(onlinePDB.get(playerName) == null) {
			File file = new File(CRolePlay.getInstance().getDataFolder(), PlayerDataBase.PDBPATH + playerName + ".yml");
			if(!file.exists()) return null;
			Config conf = new Config(file, Config.YAML);
			Set<String> headSet = conf.getSection("Titles").getKeys();
			for(String head : headSet) {
				if(conf.getBoolean("Titles."+head+".IsTag")) return Title.get(head);
			}
		}else {
			for(PlayerTitle pt : onlinePDB.get(playerName).getPlayerTitleMap().values()) {
				if(pt.isTag()) {
					return pt.getTitle();
				}
			}
		}
		return null;
	}
	
	/*[API] get PlayerLevel of someone*/
	public static PlayerLevel getPlv(String playerName) throws FileNotFoundException {
		Player p = CRolePlay.getInstance().getServer().getPlayer(playerName);
		if(onlinePDB.containsKey(playerName) && p != null) {
			return onlinePDB.get(playerName).plv;
		}else {
			File pdbFile = new File(CRolePlay.getInstance().getDataFolder(), PlayerDataBase.PDBPATH + playerName + ".yml");
			if(!pdbFile.exists()) {
				throw new FileNotFoundException();
			}
			PlayerLevel plv = new PlayerLevel();
			Config conf = new Config(pdbFile);
			plv.loadData(conf);
			return plv;
		}
	}
	
	/*[API] give someone exp*/
	public static void giveExp(String playerName, int exp) throws FileNotFoundException {
		int lv;
		Player p = CRolePlay.getInstance().getServer().getPlayer(playerName);
		if(onlinePDB.containsKey(p.getName()) && p != null) {
			lv = onlinePDB.get(p.getName()).plv.getLv();
			onlinePDB.get(p.getName()).plv.update(exp);
			//p.sendMessage(TextFormat.AQUA + "您獲得了" + exp + "經驗值!");
			checkGrant(playerName);
			if(lv < onlinePDB.get(p.getName()).plv.getLv()) {
				p.setTitleAnimationTimes(10, 20, 10);
				p.sendTitle(TextFormat.YELLOW + "您的等級升至" + onlinePDB.get(p.getName()).plv.getLv() + "了!");
			}
			
		}else {
			File pdbFile = new File(CRolePlay.getInstance().getDataFolder(), PlayerDataBase.PDBPATH + playerName + ".yml");
			if(!pdbFile.exists()) {
				throw new FileNotFoundException();
			}
			PlayerLevel plv = new PlayerLevel();
			Config conf = new Config(pdbFile);
			plv.loadData(conf);
			plv.update(exp);
			checkGrant(playerName);
			conf.set(PlayerLevel.PARENTKEY, plv.dataSection());
			conf.save();
		}
	}
	
	/*[API] set someone's lv*/
	public static void setPlayerLv(String playerName, int lv) throws FileNotFoundException {
		Player p = CRolePlay.getInstance().getServer().getPlayer(playerName);
		if(onlinePDB.containsKey(playerName) && p != null) {
			PlayerDataBase pdb = onlinePDB.get(playerName);
			pdb.plv.setLv(lv);
			pdb.plv.updateLevelUpExp();
			pdb.plv.setExp(0);
			checkGrant(playerName);
			p.sendTitle(TextFormat.YELLOW + "您的等級被調整為" + lv + "了!");
		}else {
			File pdbFile = new File(CRolePlay.getInstance().getDataFolder(), PlayerDataBase.PDBPATH + playerName + ".yml");
			if(!pdbFile.exists()) {
				throw new FileNotFoundException();
			}
			PlayerLevel plv = new PlayerLevel();
			Config conf = new Config(pdbFile);
			plv.loadData(conf);
			plv.setLv((lv));
			plv.updateLevelUpExp();
			plv.setExp(0);
			checkGrant(playerName);
			conf.set(PlayerLevel.PARENTKEY, plv.dataSection());
			conf.save();
		}
	}
	
	/*[API] grant a title to someone according to player's level*/
	public static void checkGrant(String targetName) {
		PlayerDataBase pdb;
		if(onlinePDB.containsKey(targetName)) {
			pdb = onlinePDB.get(targetName);
		}else {
			pdb = new PlayerDataBase(targetName);
		}
		try {
			if(pdb.getPlayerLevel().getLv() >= 10) {
				if(!pdb.getPlayerTitleMap().containsKey("NOVICE"))
					CRolePlay.getTitleMap().get("NOVICE").grantTo(targetName);
			}
			if(pdb.getPlayerLevel().getLv() >= 20) {
				if(!pdb.getPlayerTitleMap().containsKey("ADVANCEDNOVICE"))
					CRolePlay.getTitleMap().get("ADVANCEDNOVICE").grantTo(targetName);
			}
			if(pdb.getPlayerLevel().getLv() >= 30) {
				if(!pdb.getPlayerTitleMap().containsKey("CONFIRMEDNOVICE"))
					CRolePlay.getTitleMap().get("CONFIRMEDNOVICE").grantTo(targetName);
			}
			if(pdb.getPlayerLevel().getLv() >= 40) {
				if(!pdb.getPlayerTitleMap().containsKey("PLAYER"))
					CRolePlay.getTitleMap().get("PLAYER").grantTo(targetName);
			}
			if(pdb.getPlayerLevel().getLv() >= 50) {
				if(!pdb.getPlayerTitleMap().containsKey("ADVANCEDPLAYER"))
					CRolePlay.getTitleMap().get("ADVANCEDPLAYER").grantTo(targetName);
			}
			if(pdb.getPlayerLevel().getLv() >= 60) {
				if(!pdb.getPlayerTitleMap().containsKey("CONFIRMEDPLAYER"))
					CRolePlay.getTitleMap().get("CONFIRMEDPLAYER").grantTo(targetName);
			}
			if(pdb.getPlayerLevel().getLv() >= 70) {
				if(!pdb.getPlayerTitleMap().containsKey("MAGICPRO"))
					CRolePlay.getTitleMap().get("MAGICPRO").grantTo(targetName);
			}
			if(pdb.getPlayerLevel().getLv() >= 80) {
				if(!pdb.getPlayerTitleMap().containsKey("ELFMAGICIAN"))
					CRolePlay.getTitleMap().get("ELFMAGICIAN").grantTo(targetName);
			}
			if(pdb.getPlayerLevel().getLv() >= 90) {
				if(!pdb.getPlayerTitleMap().containsKey("TIMEANDSPACE"))
					CRolePlay.getTitleMap().get("TIMEANDSPACE").grantTo(targetName);
			}
			if(pdb.getPlayerLevel().getLv() >= 100) {
				if(!pdb.getPlayerTitleMap().containsKey("ETERNALSOUL"))
					CRolePlay.getTitleMap().get("ETERNALSOUL").grantTo(targetName);
			}
		}catch(FileNotFoundException err) {
			return;
		}
	}
}
