package cosr.ess;

import java.io.File;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.level.Level;
import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

public class CCleaner extends PluginTask<Essential> {

	private Config config;
	private String cleanerName = "清除者";
	private String msg1;
	private String msg2;
	private String msg;
	public int step = 0;

	public CCleaner(Essential plugin) {
		super(plugin);
		config = new Config(new File(Essential.getInstance().getDataFolder(), "cleaner.yml"), Config.YAML);
		cleanerName = config.getString("name", "清除者");
		msg1 = config.getString("message1", "將在20秒後清除所有掉落物以及生物");
		msg2 = config.getString("message2", "將在10秒後清除所有掉落物以及生物");
		msg = config.getString("message", "默默地清除了@i個掉落物以及@c個生物");
	}

	@Override
	public void onRun(int ticks) {
		step++;
		if(step == 58) Server.getInstance().broadcastMessage("[" + cleanerName + TextFormat.RESET + "]" + TextFormat.GRAY + msg1);
		else if(step == 59) Server.getInstance().broadcastMessage("[" + cleanerName + TextFormat.RESET + "]" + TextFormat.GRAY + msg2);
		else if(step == 60) {
			step = 0;
			clean();
		}
	}

	public void clean() {
		int itemAmount = 0;
		int creatureAmount = 0;
		int count = 0;
		for (Level l : Essential.getInstance().getServer().getLevels().values()) {
			int amount = l.getEntities().length;
			for (Entity e : l.getEntities()) {
				if (count >= amount) {
					count = 0;
					break;
				}
				if (!(e instanceof EntityHuman)) {
					if (e instanceof EntityItem) {
						if (config.getBoolean("dropped_item", true)) {
							e.close();
							itemAmount++;
						}
					} else if (e instanceof EntityCreature) {
						if (config.getBoolean("creature", false)) {
							e.close();
							creatureAmount++;
						}
					}
					count++;
				}
			}
		}
		Essential.getInstance().getServer().broadcastMessage("[" + cleanerName + "]" + TextFormat.GRAY
				+ msg.replace("@i", Integer.toString(itemAmount)).replace("@c", Integer.toString(creatureAmount)));
	}
}
