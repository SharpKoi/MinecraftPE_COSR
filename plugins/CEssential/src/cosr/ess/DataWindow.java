package cosr.ess;

import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.utils.TextFormat;
import cosr.economy.CEconomy;
import cosr.economy.job.CJob;
import cosr.roleplay.CRolePlay;
import cosr.roleplay.gcollection.Title;

public class DataWindow {

	public static void showDataWindow(Player p) {
		FormWindowModal window = new FormWindowModal("我的基本資訊", "", "確認", "取消");
		Title pinned = CRolePlay.getPinnedTitle(p.getName());
		CJob cjob = CEconomy.getJobMap().get(p.getName());
		String levelName = p.getLevel().getFolderName();
		window.setContent(TextFormat.DARK_AQUA + "關於我: \n" + 
				TextFormat.DARK_AQUA + "稱號: " + (pinned != null? pinned.getRarity().getColor() + pinned.getName() : "None") + "\n" + 
				TextFormat.DARK_AQUA + "工作: " + TextFormat.WHITE + (cjob != null? cjob.getJob().chineseName() : "None") + "\n" + 
				TextFormat.DARK_AQUA + "所在世界: " + TextFormat.WHITE + (p.isOp()? levelName : cosr.multiworld.Main.getWorldsConfig().get(levelName)) + "\n" + 
				"\n" + TextFormat.RESET + 
				TextFormat.DARK_GREEN + "關於伺服器: \n" + 
				TextFormat.DARK_GREEN+ "在線人數: " + TextFormat.WHITE + Essential.getInstance().getServer().getOnlinePlayers().size() + "\n");
		
		p.showFormWindow(window);
	}
	
}
