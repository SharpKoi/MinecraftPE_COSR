package cosr.newbie.event;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cmen.essalg.CJEF;
import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.ConfigSection;

public abstract class SimpleEvent {
	
	protected String id;
	
	protected boolean onlyOnce;
	protected Set<String> played = null;
	protected Level triggerField;
	protected String triggerType;
	protected Set<Position> detectZone = new HashSet<Position>();
	
	public SimpleEvent(String id) {
		this.id = id;
	}
	
	public SimpleEvent(Level triggerField, String triggerType) {
		this(triggerField, triggerType, true);
	}
	
	public SimpleEvent(Level triggerField, String triggerType, boolean onlyOnce) {
		this.triggerField = triggerField;
		this.triggerType = triggerType;
		if(onlyOnce) {
			played = new HashSet<String>();
		}
	}
	
	public Level getField() {
		return triggerField;
	}

	public void setField(Level triggerField) {
		this.triggerField = triggerField;
	}

	public String getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}

	public Set<String> getPlayed() {
		return played;
	}

	public Set<Position> getDetectZone() {
		return detectZone;
	}
	
	public boolean isOnlyOnce() {
		return onlyOnce;
	}
	
	public void savePlayedOne(Player p) {
		played.add(p.getName());
	}
	
	public boolean checkTrigger(Entity trigger) {
		if(!trigger.getLevel().equals(triggerField) || (played.contains(trigger.getName()) && onlyOnce)) {
			return false;
		}
		if(trigger instanceof Player) {
			if(triggerType.equalsIgnoreCase("Player")) {
				for(Position pos : detectZone) {
					if(trigger.getLocation().floor().equals(pos.floor())) return true;
				}
			}
		}else {
			if(trigger.getClass().getSimpleName().equalsIgnoreCase(triggerType)) {
				return true;
			}
		}
		return false;
	}
	
	public List<ConfigSection> getDetectZoneData() {
		List<ConfigSection> sectionList = new ArrayList<ConfigSection>();
		for(Position pos : detectZone) {
			ConfigSection section = new ConfigSection();
			section.set("x", CJEF.getD2(pos.x));
			section.set("y", CJEF.getD2(pos.y));
			section.set("z", CJEF.getD2(pos.z));
			sectionList.add(section);
		}
		return sectionList;
	}
}
