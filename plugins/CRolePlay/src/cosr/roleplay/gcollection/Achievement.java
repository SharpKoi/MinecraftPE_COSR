package cosr.roleplay.gcollection;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import cosr.roleplay.CRolePlay;
import cosr.roleplay.database.PlayerDataBase;

public class Achievement extends GameCollection {
	
	public static final String ACHV_FILE_NAME = "Achievements.yml";
	
	public Achievement() {
		this("UNKNOWN", "未知", "Unknown", "Unknown", "Unknown");
	}
	
	public Achievement(String head) {
		this(head, new File(CRolePlay.getInstance().getDataFolder(), ACHV_FILE_NAME));
	}
	
	public Achievement(String head, File configFile) {
		this.head = head;
		this.loadFromConfig(new Config(configFile));
	}
	
	public Achievement(String head, String name, String description, String requirement, String reward) {
		this.head = head;
		this.name = name;
		this.description = description;
		this.requirement = requirement;
		this.reward = reward;
	}
	
	public String msgTitleForm() {
		return TextFormat.RESET + (TextFormat.BOLD + (TextFormat.WHITE + "[" + TextFormat.DARK_GREEN + this.getName() + TextFormat.WHITE + "]")) + TextFormat.RESET;
	}
	
	public String getterMessage() {
		String achv_msg;
		achv_msg = "恭喜你達成成就: \n"
					+ TextFormat.BOLD + TextFormat.GREEN + this.msgTitleForm() + " "
					+ TextFormat.RESET + TextFormat.GRAY + this.getDescription();
		return achv_msg;
	}
	
	//player_name
	public boolean grantTo(String targetName) throws FileNotFoundException {
		if(CRolePlay.getOnlinePDB().containsKey(targetName)) {
			Map<String, Boolean> achvMap = CRolePlay.getOnlinePDB().get(targetName).getPlayerAchvMap();
			achvMap.put(this.head, true);
			Player target = CRolePlay.getInstance().getServer().getPlayer(targetName);
			if(target != null) {
				target.sendMessage(getterMessage());
				return true;
			}
		}else {
			File file = new File(CRolePlay.getInstance().getDataFolder(), PlayerDataBase.PDBPATH + targetName + ".yml");
			if(file.exists()) {
				Config targetConf = new Config(file, Config.YAML);
				ConfigSection achvSection = targetConf.getSection("Achievements");
				achvSection.set(this.head, false);
				targetConf.save();
				return true;
			}else {
				throw new FileNotFoundException();
			}
		}
		
		return false;
	}
}
