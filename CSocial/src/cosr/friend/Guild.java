/**/

package cosr.friend;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

public class Guild {
	private String name, description;
	private String leader, viceleader;
	private ArrayList<String> members;
	private Vector3 startp, endp;
	private int lv, ctb, max_ctb;									//公會等級、當前貢獻度、升下一級所需貢獻度
	
	private SocialMain plugin;
	
	public Guild(String name, String description, String leader) {
		this(name, leader, description, null, null);
	}
	
	public Guild(String name, String description, String leader, Vector3 startp, Vector3 endp) {
		this.name = name;
		this.description = description;
		this.leader = leader;
		this.members = new ArrayList<String>();
		this.startp = startp;
		this.endp = endp;
		this.lv = 0;
		this.ctb = 0;
		this.max_ctb = maxCBT();
	}
	
	public ConfigSection getherSection() {
		ConfigSection section = new ConfigSection();
		section.put("Name", this.name);
		section.put("Description", this.description);
		section.put("Leader", this.leader);
		section.put("Vice-leader", this.viceleader);
		section.put("Level", this.lv);
		section.put("Contribution", this.ctb);
		section.put("Max-Contribution", this.max_ctb);
		section.put("Land.start-point", this.startp.toString());
		section.put("Land.end-point", this.endp.toString());
		
		Iterator<String> itr = this.members.iterator();
		if(itr.hasNext()) {
			String member = itr.next();
			section.put("Members."+members.indexOf(member), member);
		}
		
		return section;
	}
	
	public void saveConfig() {
		File file = new File(plugin.getDataFolder() + "/Guilds", this.name + ".yml");
		Config conf = new Config(file, Config.YAML);
		
		conf.setAll(getherSection());
	}
	
	public int maxCBT() {
		return (int) (0.1515 * Math.pow(this.lv, Math.E) + 100);
	}
}
