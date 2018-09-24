package cosr.newbie;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import cosr.newbie.chest.NBChestReplenisher;
import cosr.newbie.chest.NewbieChest;
import cosr.newbie.command.NBChestCommand;
import cosr.newbie.command.NewbieCommand;
import cosr.newbie.event.BuffSetter;
import cosr.newbie.event.MobSpawner;
import cosr.newbie.listener.EventListener;

public class CNewbie extends PluginBase {
	public static final String TITLE = TextFormat.RESET + "[" + TextFormat.DARK_AQUA + "CNewbie" + TextFormat.RESET + "]";
	private static CNewbie main;
	
	public static Level NBVillage = null;
	public static Position endPoint = null;
	private static List<NewbieChest> nbChestList = new ArrayList<NewbieChest>();
	private static Set<String> nbChestSettingPool = new HashSet<>();
	private static Set<String> nbChestContentingPool = new HashSet<>();
	private static List<String> passedList = new ArrayList<String>();
	
	private static Map<String, BuildTool> buildingMap = new HashMap<String, BuildTool>();
	private static Set<String> endSettingPool = new HashSet<String>();
	
	public static List<MobSpawner> MSPool = new ArrayList<>();
	public static List<BuffSetter> BSPool = new ArrayList<>();
	
	public static CNewbie getInstance() {
		return main;
	}
	
	public static List<NewbieChest> getNBChestList() {
		return nbChestList;
	}
	
	public static Set<String> getNbChestSettingPool() {
		return nbChestSettingPool;
	}

	public static Set<String> getNbChestContentingPool() {
		return nbChestContentingPool;
	}

	public static Map<String, BuildTool> getBuildingMap() {
		return buildingMap;
	}

	public static Set<String> getEndSettingPool() {
		return endSettingPool;
	}

	@SuppressWarnings("rawtypes")
	public void onEnable() {
		main = this;
		//register all needed
		Server.getInstance().getPluginManager().registerEvents(new EventListener(), this);
		Server.getInstance().getCommandMap().register("Newbie", new NewbieCommand());
		Server.getInstance().getCommandMap().register("NewbieChest", new NBChestCommand());
		Server.getInstance().getScheduler().scheduleRepeatingTask(new NBChestReplenisher(this), 2400);
		
		//load newbie village
		File nbvFile = new File(this.getDataFolder(), "NBVillage.yml");
		if(nbvFile.exists()) {
			Config nbvConf = new Config(nbvFile, Config.YAML);
			NBVillage = Server.getInstance().getLevelByName(nbvConf.getString("world", ""));
			endPoint = new Position(nbvConf.getDouble("end-point.x"), nbvConf.getDouble("end-point.y"), nbvConf.getDouble("end-point.z"), NBVillage);
		}
		if(NBVillage == null) {
			Server.getInstance().getLogger().info(CNewbie.TITLE + TextFormat.RED + "新手村尚未設定, 請輸入/nb v <levelName>設置");
		}else {
			//load mobspawner
			Config msConf = new Config(new File(this.getDataFolder(), "MobSpawner.yml"), Config.YAML);
			List<Map> msDataList = msConf.getMapList("MOB-SPAWNER");
			for(Map<?, ?> msData : msDataList) {
				MSPool.add(new MobSpawner(NBVillage, "Player", true).loadFromMap(msData));
			}
			//load buffsetter
			Config bsConf = new Config(new File(this.getDataFolder(), "BuffSetter.yml"), Config.YAML);
			List<Map> bsDataList = bsConf.getMapList("BUFF-SETTER");
			for(Map<?, ?> bsData : bsDataList) {
				BSPool.add(new BuffSetter(NBVillage, "Player").loadFromMap(bsData));
			}
			//load newbie chest
			Config nbcConf = new Config(new File(this.getDataFolder(), "NewbieChest.yml"), Config.YAML);
			List<Map> nbcDataList = nbcConf.getMapList("NEWBIE-CHEST");
			for(Map<?, ?> nbcData : nbcDataList) {
				nbChestList.add(new NewbieChest().loadFromMap(nbcData));
			}
		}
		
		Config passedConf = new Config(new File(this.getDataFolder(), "passed-list.yml"), Config.YAML);
		if(passedConf.exists("Passed")) {
			passedList = passedConf.getStringList("Passed");
		}
	}
	
	public void onDisable() {
		//save newbie village
		if(NBVillage != null) {
			Config nbvConf = new Config(new File(this.getDataFolder(), "NBVillage.yml"), Config.YAML);
			nbvConf.set("world", NBVillage.getFolderName());
			if(endPoint != null) {
				ConfigSection epData = new ConfigSection() {
					private static final long serialVersionUID = 1L;
					{
						set("x", endPoint.getFloorX());
						set("y", endPoint.getFloorY());
						set("z", endPoint.getFloorZ());
					}
				};
				nbvConf.set("end-point", epData);
			}
			nbvConf.save();
			
			//save mobspawner
			Config msConf = new Config(new File(this.getDataFolder(), "MobSpawner.yml"), Config.YAML);
			List<ConfigSection> allMSData = new ArrayList<ConfigSection>();
			for(MobSpawner ms : MSPool) {
				allMSData.add(ms.dataSection());
			}
			msConf.set("MOB-SPAWNER", allMSData);
			msConf.save();
			
			//save buffsetter
			Config bsConf = new Config(new File(this.getDataFolder(), "BuffSetter.yml"), Config.YAML);
			List<ConfigSection> allBSData = new ArrayList<ConfigSection>();
			for(BuffSetter bs : BSPool) {
				allBSData.add(bs.dataSection());
			}
			bsConf.set("BUFF-SETTER", allBSData);
			bsConf.save();
			
			//save newbie chest
			Config nbcConf = new Config(new File(this.getDataFolder(), "NewbieChest.yml"), Config.YAML);
			List<ConfigSection> allNBCData = new ArrayList<ConfigSection>();
			for(NewbieChest nbc : nbChestList) {
				allNBCData.add(nbc.dataSection());
			}
			nbcConf.set("NEWBIE-CHEST", allNBCData);
			nbcConf.save();
			
			//save passed-list
			Config passedConf = new Config(new File(this.getDataFolder(), "passed-list.yml"), Config.YAML);
			passedConf.set("Passed", passedList);
			passedConf.save();
		}
	}
	
	//Turn to api
	public static boolean allDone() {
		return (NBVillage != null && endPoint != null);
	}
	
	public static boolean checkPass(Player p) {
		return (p.getPosition().floor().equals(endPoint.floor()) && !passedList.contains(p.getName()));
	}
	
	public static void pass(Player p) {
		if(!passedList.contains(p.getName())) {
			p.sendMessage(CNewbie.TITLE + TextFormat.GREEN + "恭喜你完成新手教學!!");
			passedList.add(p.getName());
		}
	}
	
	public static boolean isPassed(Player p) {
		return isPassed(p.getName());
	}
	
	public static boolean isPassed(String playerName) {
		return passedList.contains(playerName);
	}
}
