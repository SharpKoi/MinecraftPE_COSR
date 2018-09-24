package cosr.newbie.event;

import java.util.List;
import java.util.Map;

import cn.nukkit.Server;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

public class BuffSetter extends SimpleEvent {

	private int effectId;
	private int duration;
	private int amplifier;
	
	public BuffSetter(Level triggerField, String triggerType) {
		super(triggerField, triggerType);
		this.effectId = 0;
	}
	
	public BuffSetter(Level triggerField, String triggerType, int id, int seconds) {
		this(triggerField, triggerType, id, seconds, 0);
	}
	
	public BuffSetter(Level triggerField, String triggerType, int id, int seconds, int amplifier) {
		super(triggerField, triggerType);
		this.effectId = id;
		this.duration = seconds*20;
		this.amplifier = amplifier;
	}
	
	public Effect getEffect() {
		if(effectId != 0) return Effect.getEffect(effectId).setDuration(duration).setAmplifier(amplifier);
		return null;
	}
	
	public void setEffect(Effect effect) {
		this.effectId = effect.getId();
		this.duration = effect.getDuration();
		this.amplifier = effect.getAmplifier();
	}
	
	public void setEffect(int id, int seconds) {
		setEffect(id, seconds, 0);
	}
	
	public void setEffect(int id, int seconds, int amplifier) {
		this.effectId = id;
		this.duration = seconds*20;
		this.amplifier = amplifier;
	}
	
	public void apply(EntityCreature target) {
		if(effectId <= 0) {
			target.removeAllEffects();
			return;
		}
		Effect effect = Effect.getEffect(effectId).setDuration(duration).setAmplifier(amplifier);
		if(effect.getDuration() <= 0) effect.setDuration(10);
		target.addEffect(effect);
	}
	
	public ConfigSection dataSection() {
		ConfigSection effectData = new ConfigSection();
		effectData.set("id", effectId);
		effectData.set("duration", duration);
		effectData.set("amplifier", amplifier);
		return new ConfigSection() {
			private static final long serialVersionUID = 1L;
			{
				set("only_once", onlyOnce);
				set("trigger_field", triggerField.getFolderName());
				set("trigger_type", triggerType);
				set("detect_zones", getDetectZoneData());
				set("effect", effectData);
				set("played_list", played.toArray());
			}
		};
	}
	
	@SuppressWarnings("rawtypes")
	public BuffSetter loadFromConfig(Config conf) {
		onlyOnce = conf.getBoolean("only_once");
		triggerField = Server.getInstance().getLevelByName(conf.getString("trigger_field"));
		triggerType = conf.getString("trigger_type");
		List<Map> dzList = conf.getMapList("detect_zones");
		for(Map map : dzList) {
			detectZone.add(new Position((double)map.get("x"), (double)map.get("y"), (double)map.get("z"), triggerField));
		}
		effectId = conf.getInt("effect.id");
		duration = conf.getInt("effect.duration");
		amplifier = conf.getInt("effect.amplifier");
		for(String playerName : conf.getStringList("played_list")) {
			played.add(playerName);
		}
		return this;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public BuffSetter loadFromMap(Map data) {
		onlyOnce = Boolean.parseBoolean(data.get("only_once").toString());
		triggerField = Server.getInstance().getLevelByName(data.get("trigger_field").toString());
		triggerType = data.get("trigger_type").toString();
		List<Map> dzList = (List<Map>) data.get("detect_zones");
		for(Map map : dzList) {
			detectZone.add(new Position((double)map.get("x"), (double)map.get("y"), (double)map.get("z"), triggerField));
		}
		Map<?, ?> effData = (Map<?, ?>) data.get("effect");
		effectId = Integer.parseInt(effData.get("id").toString());
		duration = Integer.parseInt(effData.get("duration").toString());
		amplifier = Integer.parseInt(effData.get("amplifier").toString());
		for(String playerName : (List<String>)data.get("played_list")) {
			played.add(playerName);
		}
		return this;
	}
}
