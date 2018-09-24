package cosr.ess;

import java.io.FileNotFoundException;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.utils.TextFormat;
import cosr.economy.CEconomy;
import cosr.economy.CMoney;
import cosr.economy.CPoint;
import cosr.roleplay.CRolePlay;
import cosr.roleplay.PlayerLevel;

public class CEssButtom extends PluginTask<Essential> {

	public CEssButtom(Essential plugin) {
		super(plugin);
	}

	@Override
	public void onRun(int ticks) {
		Essential.getInstance().getServer().getOnlinePlayers().values().forEach(this::poping);
	}

	public void poping(Player p) {
		if(p == null) return;
		if(p.getName() == null) return;
		try {
			PlayerLevel plv = CRolePlay.getPlv(p.getName());
			String levelName = p.getLevel().getFolderName();
			p.sendPopup(TextFormat.WHITE + "等級: " + (plv != null? plv.getLv() : 0) + space(7) + TextFormat.RESET + 
						TextFormat.AQUA + "經驗: " + (plv != null? plv.getExp() : 0) + "/" + (plv != null? plv.getLevelUpExp() : 0) + "\n" + 
						TextFormat.YELLOW + "金錢: " + 
							(CMoney.getMoneyMap().get(p.getName()) != null? CEconomy.getD2(CMoney.getMoneyMap().get(p.getName())) : 0) + space(7) + 
						TextFormat.LIGHT_PURPLE + CPoint.config.getString("name", "點券") + ": " + 
							(CPoint.getPointMap().get(p.getName()) != null? CEconomy.getD2(CPoint.getPointMap().get(p.getName())) : 0) + space(7) + 
						TextFormat.GREEN + "手持: " + p.getInventory().getItemInHand().getId() + ":" + p.getInventory().getItemInHand().getDamage() + "\n" + 
						TextFormat.GRAY + "所在世界: " + (p.isOp()? levelName : cosr.multiworld.Main.getWorldsConfig().get(levelName)) + space(7) + 
						TextFormat.GOLD + "伺服器狀態: " + Server.getInstance().getTicksPerSecondAverage() + space(7) + 
						TextFormat.RED + "在線人數: " + Server.getInstance().getOnlinePlayers().size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	
	private static String space(int amount) {
		String endl = "";
		for(int i = 0; i < amount; i++) {
			endl += " ";
		}
		return endl;
	}
}
