package cosr.newbie.event;

import cmen.essalg.CJEF;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.ConfigSection;
import nukkitcoders.mobplugin.MobPlugin;
import nukkitcoders.mobplugin.entities.monster.Monster;

public class SpawnZone {
	
	String entityType;
	Position spawnPos;
	int amount;
	
	public SpawnZone(Position spawnPos) {
		this("Unknown", spawnPos, 1);
	}
	
	public SpawnZone(String entityType, Position spawnPos, int amount) {
		this.entityType = entityType;
		this.spawnPos = spawnPos;
		this.amount = amount;
	}
	
	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public Position getPos() {
		return spawnPos;
	}

	public void setPos(Position spawnPos) {
		this.spawnPos = spawnPos;
	}

	public int getSpawnAmount() {
		return amount;
	}
	
	public void setSpawnAmount(int amount) {
		this.amount = amount;
	}
	
	public void spawn() {
		Entity ent;
		for(int i = 1; i <= amount; i++) {
			if ((ent = MobPlugin.create(entityType, spawnPos)) != null) {
				if(ent instanceof Monster) {
					spawnPos.level.setTime(Level.TIME_MIDNIGHT);
				}
				ent.spawnToAll();
			}
		}
	}
	
	public ConfigSection dataSection() {
		return new ConfigSection() {
			private static final long serialVersionUID = 1L;
			{
				set("entity_type", entityType);
				set("amount", amount);
				set("x", CJEF.getD2(spawnPos.x));
				set("y", CJEF.getD2(spawnPos.y));
				set("z", CJEF.getD2(spawnPos.z));
			}
		};
	}
}
