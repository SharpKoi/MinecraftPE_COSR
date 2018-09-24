package cosr.shop.task;

import java.util.LinkedList;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.PluginTask;
import cosr.shop.CShopMain;
import cosr.shop.utils.CAdvertisement;

public class AdvertisingTask extends PluginTask<PluginBase> {

	LinkedList<CAdvertisement> adList = new LinkedList<CAdvertisement>();
	
	public AdvertisingTask(CShopMain plugin) {
		super(plugin);
	}
	
	public LinkedList<CAdvertisement> getAdList() {
		return adList;
	}
	
	@Override
	public void onRun(int arg0) {
		for(int i = 0; i < adList.size(); i++) {
			adList.get(i).advertise();
			if(adList.get(i).getTimes() <= 0) {
				adList.remove(i);
				i--;
			}
		}
	}
}
