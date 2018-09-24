package cosr.vip.listener;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import cosr.vip.CVIP;
import cosr.vip.VIP;

public class EventListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		
		if(new File(CVIP.getInstance().getDataFolder(), "players" + File.separator + p.getName()+".yml").exists()) {
			VIP vip = new VIP(p.getName());
			if(vip.isEnable()) {
				Calendar current = Calendar.getInstance();
				current.setTime(new Date());
				if(vip.getLastLoginDate().get(Calendar.YEAR) == current.get(Calendar.YEAR) &&
					vip.getLastLoginDate().get(Calendar.MONTH) == current.get(Calendar.MONTH) &&
					vip.getLastLoginDate().get(Calendar.DATE)+1 == current.get(Calendar.DATE))
						vip.addExp(CVIP.getVipConfig().getInt("daily-exp", 5));
				
				CVIP.getPlayerVIPMap().put(p.getName(), vip);
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if(CVIP.getPlayerVIPMap().containsKey(p.getName())) {
			Config conf = new Config(new File(CVIP.getInstance().getDataFolder(), "players" + File.separator + p.getName()+".yml"));
			conf.setAll(CVIP.getPlayerVIPMap().get(p.getName()).dataSection());
			conf.save();
		}
	}
	
	@EventHandler
	public void onForm(PlayerFormRespondedEvent event) {
		Player p = event.getPlayer();
		FormWindow w = event.getWindow();
		FormResponse r = event.getResponse();
		
		if(r == null) return;
		
		if(w instanceof FormWindowCustom) {
			if(((FormWindowCustom) w).getTitle().equals(CVIP.INFO_TITLE + "密碼確認")) {
				FormResponseCustom response = (FormResponseCustom) r;
				String password = response.getInputResponse(0);
				if(password.equals(CVIP.getVipConfig().getString("password", "COSRVIP"))) {
					if(CVIP.getGrantVIPRequestions().containsKey(p.getName())) {
						VIP vip = CVIP.getGrantVIPRequestions().get(p.getName());
						Calendar c = Calendar.getInstance();
						c.setTime(new Date());
						CVIP.getPlayerVIPMap().put(vip.getOwner(), vip);
						p.sendMessage(CVIP.INFO_TITLE + TextFormat.GREEN + "您已成功賦予玩家" + TextFormat.WHITE + vip.getOwner() + 
								vip.getRank().getColor() + vip.getRank().getName() + TextFormat.RESET + "的位階");
						CVIP.getGrantVIPRequestions().remove(p.getName());
					}
				}
			}
		}
	}
	
	
}
