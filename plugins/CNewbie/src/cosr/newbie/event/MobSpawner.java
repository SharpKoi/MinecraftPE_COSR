package cosr.newbie.event;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

public class MobSpawner extends SimpleEvent {

	private Set<SpawnZone> spawnZonePool = new HashSet<SpawnZone>();
	
	public MobSpawner(Level triggerField, String triggerType) {
		this(triggerField, triggerType, true);
	}
	
	public MobSpawner(Level triggerField, String triggerType, boolean once) {
		super(triggerField, triggerType);
		this.onlyOnce = once;
	}
	
	public Set<SpawnZone> getSpawnZonePool() {
		return spawnZonePool;
	}
	
	public void putSpawnZone(SpawnZone sz) {
		this.spawnZonePool.add(sz);
	}
	
	public void spawnAllMobs() {
		for(SpawnZone sz : spawnZonePool) {
			sz.spawn();
		}
	}
	
	public ConfigSection dataSection() {
		List<ConfigSection> spawnZoneData = new ArrayList<ConfigSection>();
		for(SpawnZone sz : spawnZonePool) {
			spawnZoneData.add(sz.dataSection());
		}
		return new ConfigSection() {
			private static final long serialVersionUID = 1L;
			{
				set("only_once", onlyOnce);
				set("trigger_field", triggerField.getFolderName());
				set("trigger_type", triggerType);
				set("detect_zones", getDetectZoneData());
				set("spawn_zones", spawnZoneData);
				set("played_list", played.toArray());
			}
		};
	}
	
	@SuppressWarnings("rawtypes")
	public MobSpawner loadFromConfig(Config conf) {
		onlyOnce = conf.getBoolean("only_once");
		triggerField = Server.getInstance().getLevelByName(conf.getString("trigger_field"));
		triggerType = conf.getString("trigger_type");
		List<Map> dzList = conf.getMapList("detect_zones");
		for(Map map : dzList) {
			detectZone.add(new Position((double)map.get("x"), (double)map.get("y"), (double)map.get("z"), triggerField));
		}
		List<Map> szList = conf.getMapList("spawn_zones");
		for(Map map : szList) {
			spawnZonePool.add(new SpawnZone(map.get("entity_type").toString(), 
					new Position((double)map.get("x"), (double)map.get("y"), (double)map.get("z"), triggerField), 
					(int) map.get("amount")));
		}
		for(String playerName : conf.getStringList("played_list")) {
			played.add(playerName);
		}
		return this;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public MobSpawner loadFromMap(Map data) {
		onlyOnce = Boolean.parseBoolean(data.get("only_once").toString());
		triggerField = Server.getInstance().getLevelByName(data.get("trigger_field").toString());
		triggerType = data.get("trigger_type").toString();
		List<Map> dzList = (List<Map>) data.get("detect_zones");
		for(Map map : dzList) {
			detectZone.add(new Position((double)map.get("x"), (double)map.get("y"), (double)map.get("z"), triggerField));
		}
		List<Map> szList = (List<Map>) data.get("spawn_zones");
		for(Map map : szList) {
			spawnZonePool.add(new SpawnZone(map.get("entity_type").toString(), 
					new Position((double)map.get("x"), (double)map.get("y"), (double)map.get("z"), triggerField), 
					(int) map.get("amount")));
		}
		for(String playerName : (List<String>)data.get("played_list")) {
			played.add(playerName);
		}
		return this;
	}
}
