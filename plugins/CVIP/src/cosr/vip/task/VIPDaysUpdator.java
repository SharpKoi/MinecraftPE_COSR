package cosr.vip.task;

import java.io.File;

import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.utils.Config;
import cosr.vip.CVIP;
import cosr.vip.VIP;

public class VIPDaysUpdator extends PluginTask<CVIP> {

	public VIPDaysUpdator(CVIP owner) {
		super(owner);
	}

	@Override
	public void onRun(int arg0) {
		VIP vip;
		for(String pn : CVIP.getPlayerVIPMap().keySet()) {
			vip = CVIP.getPlayerVIPMap().get(pn);
			vip.increaseDays();
			Config conf = new Config(new File(CVIP.getInstance().getDataFolder(), "players" + File.separator + pn+".yml"), Config.YAML);
			conf.set("days", vip.getDaysVia());
			conf.save();
		}
	}

}
