package cosr.economy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.nukkit.Server;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

public class CPoint {
	
	public static final String FILEPATH = "CPoint" + File.separator;
	
	public static Config config = new Config(new File(CEconomy.getInstance().getDataFolder(), FILEPATH + "Config.yml"), Config.YAML);
	private static Map<String, Float> player_Point = new HashMap<String, Float>();
	public static List<String> whiteList = new LinkedList<String>() {
		private static final long serialVersionUID = 1L;
		{
			if(!this.contains(Server.getInstance().getConsoleSender().getName()))
				add(Server.getInstance().getConsoleSender().getName());
		}
	};
	
	public static String name() {
		return config.getString("name", "ÂI¨é");
	}
	
	public static String infoTitle() {
		return TextFormat.RESET + (TextFormat.BOLD + (TextFormat.WHITE + "[") + (TextFormat.LIGHT_PURPLE + "CPoint")) + TextFormat.WHITE + "]" + TextFormat.RESET;
	}
	
	public static Map<String, Float> getPointMap() {
		return player_Point;
	}
	
	public static void registWL(String opName) {
		if(opName.equals(Server.getInstance().getConsoleSender().getName())) return;
		if(Server.getInstance().isOp(opName))
			whiteList.add(opName);
	}
	
	public static void removeWL(String opName) {
		if(whiteList.contains(opName)) 
			whiteList.remove(opName);
	}
	
	public static boolean isWL(String opName) {
		return whiteList.contains(opName);
	}

	public static float getPoint(String owner) throws FileNotFoundException {
		if(player_Point.containsKey(owner)) {
			return player_Point.get(owner);
		}else {
			File file = new File(CEconomy.getInstance().getDataFolder(), CEconomy.PDBPATH + owner+".yml");
			if(file.exists()) {
				Config conf = new Config(file, Config.YAML);
				return (float) conf.getDouble("Point");
			}else {
				throw new FileNotFoundException();
			}
		}
	}
	
	public static void givePoint(String to, double point) throws FileNotFoundException {
		if(player_Point.containsKey(to)) {
			player_Point.put(to, (float)(player_Point.get(to) + point));
		}else {
			File file = new File(CEconomy.getInstance().getDataFolder(), CEconomy.PDBPATH + to+".yml");
			if(file.exists()) {
				Config conf = new Config(file, Config.YAML);
				conf.set("Point", conf.getDouble("Point")+point);
				conf.save();
			}else {
				throw new FileNotFoundException();
			}
		}
	}
	
	public static void takePoint(String owner, double point) throws FileNotFoundException {
		if(player_Point.containsKey(owner)) {
			player_Point.put(owner, (float)(player_Point.get(owner) - point));
		}else {
			File file = new File(CEconomy.getInstance().getDataFolder(), CEconomy.PDBPATH + owner+".yml");
			if(file.exists()) {
				Config conf = new Config(file, Config.YAML);
				conf.set("Point", conf.getDouble("Point")-point);
				conf.save();
			}else {
				throw new FileNotFoundException();
			}
		}
	}
	
	public static void givePoint(String from, String to, double point) throws FileNotFoundException {
		takePoint(from, point);
		givePoint(to, point);
	}
	
}
