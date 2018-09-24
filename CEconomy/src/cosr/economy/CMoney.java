package cosr.economy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import cn.nukkit.utils.Config;

public class CMoney {
	
	public static final String FILEPATH = "CPoint" + File.separator;
	public static Config config = new Config(new File(CEconomy.getInstance().getDataFolder(), FILEPATH + "Config.yml"), Config.YAML);
	
	//每個玩家所持的金錢
	private static Map<String, Float> player_Money = new HashMap<String, Float>();
	
	public static String name() {
		return config.getString("name", "金錢");
	}
	
	public static Map<String, Float> getMoneyMap() {
		return player_Money;
	}
	
	public static float getMoney(String owner) throws FileNotFoundException {
		if(player_Money.containsKey(owner)) {
			return player_Money.get(owner);
		}else {
			File file = new File(CEconomy.getInstance().getDataFolder(), CEconomy.PDBPATH + owner+".yml");
			if(file.exists()) {
				Config conf = new Config(file, Config.YAML);
				return (float) conf.getDouble("Money");
			}else {
				throw new FileNotFoundException();
			}
		}
	}
	
	public static void giveMoney(String to, double money) throws FileNotFoundException {
		if(player_Money.containsKey(to)) {
			player_Money.put(to, (float)(player_Money.get(to) + money));
		}else {
			File file = new File(CEconomy.getInstance().getDataFolder(), CEconomy.PDBPATH + to+".yml");
			if(file.exists()) {
				Config conf = new Config(file, Config.YAML);
				conf.set("Money", conf.getDouble("Money")+money);
				conf.save();
			}else {
				throw new FileNotFoundException();
			}
		}
	}
	
	public static void takeMoney(String owner, double money) throws FileNotFoundException {
		if(player_Money.containsKey(owner)) {
			player_Money.put(owner, (float)(player_Money.get(owner) - money));
		}else {
			File file = new File(CEconomy.getInstance().getDataFolder(), CEconomy.PDBPATH + owner+".yml");
			if(file.exists()) {
				Config conf = new Config(file, Config.YAML);
				conf.set("Money", conf.getDouble("Money")-money);
				conf.save();
			}else {
				throw new FileNotFoundException();
			}
		}
	}
	
	public static void giveMoney(String from, String to, double money) throws FileNotFoundException {
		takeMoney(from, money);
		giveMoney(to, money);
	}
}
