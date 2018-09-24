  /* 此class主要是建立一個線上玩家的線上database，用以儲存玩家的各項遊戲紀錄
 * 方便在事件監聽時直接利用線上的DataBase裡的紀錄來做判斷玩家是否取得該成就
 * 在玩家離線後再將該玩家的DataBase儲存至檔案裏面，方便玩家上線時再讀取該檔
 */


package cosr.roleplay.database;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import cosr.roleplay.*;
public class PlayerDataBase {
	
	public static final String PDBPATH = "players" + File.separator;
	
	public boolean isNewPlayer = false;
	public String name = "";
	public String ip = "";
	public PlayerLevel plv;
	public int killcount = 0;
	public String foename = "";
	public Calendar loginMoment = Calendar.getInstance();
	private LinkedHashMap<String, Boolean> playerAchvMap;		//head-beenSeen
	private LinkedHashMap<String, PlayerTitle> playerTitleMap;	//head-playerTitle
	//TODO: add more data.
	
	//private Main plugin = Main.getInstance();
	
	public PlayerDataBase(String name) {
		File file = new File(CRolePlay.getInstance().getDataFolder(), PDBPATH + name + ".yml");
		this.playerAchvMap = new LinkedHashMap<>();
		this.playerTitleMap = new LinkedHashMap<>();
		this.plv = new PlayerLevel();
		this.name = name;
		if(!file.exists()) {
			this.isNewPlayer = true;
			return;
		}else {
			Config conf = new Config(file, Config.YAML);
			this.ip = conf.getString("IP");
			this.plv.loadData(conf);
			this.killcount = conf.getInt("KillCount");
			
			try {
				if(conf.exists("Last_login"))
					this.loginMoment.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(conf.getString("Last_login")));
				else
					this.loginMoment.set(2018, 0, 1, 0, 0);
			} catch (ParseException e) {
				CRolePlay.getInstance().getLogger().info(TextFormat.RED + "日期格式錯誤! 請檢察玩家檔案");
			}
			
			ConfigSection achvSection = conf.getSection("Achievements");
			for(String head : achvSection.keySet()) {
				playerAchvMap.put(head, achvSection.getBoolean(head));
			}
			
			ConfigSection titleSection = conf.getSection("Titles");
			for(String head : titleSection.getKeys(false)) {
				PlayerTitle pt = new PlayerTitle();
				pt.loadData(conf, head);
				playerTitleMap.put(head, pt);
			}
			//...
		}
	}
	
	public LinkedHashMap<String, Boolean> getPlayerAchvMap() {
		return playerAchvMap;
	}

	public void setPlayerAchvList(LinkedHashMap<String, Boolean> playerAchvMap) {
		this.playerAchvMap = playerAchvMap;
	}

	public LinkedHashMap<String, PlayerTitle> getPlayerTitleMap() {
		return playerTitleMap;
	}

	public void setPlayerTitleMap(LinkedHashMap<String, PlayerTitle> playerTitleMap) {
		this.playerTitleMap = playerTitleMap;
	}
	
	public PlayerLevel getPlayerLevel() {
		return this.plv;
	}

	private ConfigSection putToSection() {
		ConfigSection section = new ConfigSection();
		section.set("Name", this.name);
		section.set("IP", this.ip);
		section.set(PlayerLevel.PARENTKEY, plv.dataSection());
		
		section.set("KillCount", this.killcount);
		section.set("Last_login", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(loginMoment.getTime()));
		section.set("Achievements", this.playerAchvMap);
		
		ConfigSection allPlayerTitle = new ConfigSection();
		for(String head : this.playerTitleMap.keySet()) {
			allPlayerTitle.set(head, playerTitleMap.get(head).dataSection());
		}
		section.set("Titles", allPlayerTitle);
		
		return section;
	}
	
	
	public void save() {
		Config config = new Config(new File(CRolePlay.getInstance().getDataFolder(), PDBPATH + name + ".yml"),Config.YAML);
		config.setAll(putToSection());
		
		config.save();
	}
	
	public void titleTagReset() {
		for(PlayerTitle pt : this.playerTitleMap.values()) {
			pt.unTag();
		}
	}
}