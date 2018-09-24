package cosr.roleplay.gcollection;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

public class GameCollection {

	protected String head;
	protected String name;
	protected String description;
	protected String requirement;
	protected String reward;

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRequirement() {
		return requirement;
	}

	public void setRequirement(String requirement) {
		this.requirement = requirement;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

	public ConfigSection dataSection() {
		return new ConfigSection() {
			private static final long serialVersionUID = 1L;
			{
				set("Name", name);
				set("Quote", description);
				set("Require", requirement);
				set("Reward", reward);
			}
		};
	}

	public ConfigSection toSection() {
		return new ConfigSection(this.head, dataSection());
	}

	public void loadFromConfig(Config config) {
		if(this.head != null) {
			if (config.exists(head.toUpperCase())) {
				this.name = config.getString(head + ".Name");
				this.description = config.getString(head + ".Quote");
				this.requirement = config.getString(head + ".Require");
				this.reward = config.getString(head + ".Reward");
				
				return;
			}
		}
		//若上述兩個條件皆未達成，則全設為未知
		this.head = "UNKNOWN";
		this.name = "未知";
		this.description = "Unknown";
		this.requirement = "Unknown";
		this.reward = "Unknown";
	}
}
