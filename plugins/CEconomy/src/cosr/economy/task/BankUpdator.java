package cosr.economy.task;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.PluginTask;
import cosr.economy.CEconomy;
import cosr.economy.bank.CBank;

public class BankUpdator extends PluginTask<PluginBase> {
	
	public BankUpdator(CEconomy plugin) {
		super(plugin);
	}

	@Override
	public void onRun(int arg0) {
		CBank.update();
	}
}
