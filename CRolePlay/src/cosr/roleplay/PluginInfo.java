package cosr.roleplay;

import java.util.LinkedHashMap;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import cosr.roleplay.gcollection.Achievement;
import cosr.roleplay.gcollection.Title;

public class PluginInfo {
	
	public static String personalAchvList(Player p, int page) {
		LinkedHashMap<String, Boolean> playerAchvMap = CRolePlay.getOnlinePDB().get(p.getName()).getPlayerAchvMap();
		int maxIndex = playerAchvMap.size();
		int maxPage = maxIndex/4 + ((maxIndex%4 == 0)? 0:1);
		int realPage = (page > maxPage)? maxPage : page;
		if(realPage <= 0) realPage = 1;
		
		int startIndex = (realPage-1)*4;
		int endIndex = realPage*4;
		
		String title = TextFormat.RESET + (TextFormat.GREEN + "我的成就列表(page " + realPage + "/" + maxPage + ")\n");
		String separator = TextFormat.RESET + "=========================\n";
		
		String achvInfo = "";
		
		int i = 0;
		for(String head : playerAchvMap.keySet()) {
			//從1開始
			i++;
			if(i > startIndex && i <= endIndex) {
				Achievement achv = new Achievement(head);
				if(achv != null) {
					achvInfo += TextFormat.RESET + (TextFormat.BOLD + (TextFormat.GREEN + achv.getHead())) + "\n" + 
							TextFormat.RESET + (TextFormat.DARK_GREEN + "成就名稱: ") + TextFormat.RESET + achv.getName() + "\n" + 
							TextFormat.RESET + (TextFormat.DARK_GREEN + "成就信息: ") + TextFormat.RESET + achv.getDescription() + "\n" + 
							TextFormat.RESET + (TextFormat.DARK_GREEN + "達成條件: ") + TextFormat.RESET + achv.getRequirement() + "\n" + 
							TextFormat.RESET + (TextFormat.DARK_GREEN + "達成獎勵: ") + TextFormat.RESET + achv.getReward() + "\n" + 
							separator;
				}
			}
		}
		
		return title + separator + achvInfo + "\n";
	}
	
	public static String personalTitleList(Player p, int page) {
		LinkedHashMap<String, PlayerTitle> playerTitleMap = CRolePlay.getOnlinePDB().get(p.getName()).getPlayerTitleMap();
		int maxIndex = playerTitleMap.size();
		int maxPage = maxIndex/4 + ((maxIndex%4 == 0)? 0:1);
		int realPage = (page > maxPage)? maxPage : page;
		if(realPage <= 0) realPage = 1;
		
		int startIndex = (realPage-1)*4;
		int endIndex = realPage*4;
		
		String title = TextFormat.RESET + (TextFormat.GREEN + "我的稱號列表(page " + realPage + "/" + maxPage + ")\n");
		String separator = TextFormat.RESET + "=========================\n";
		
		String titleInfo = "";
		
		int i = 0;
		for(String head : playerTitleMap.keySet()) {
			//從1開始
			i++;
			if(i > startIndex && i <= endIndex) {
				PlayerTitle pt = playerTitleMap.get(head);
				Title _title = pt.getTitle();
				titleInfo += TextFormat.RESET + (TextFormat.BOLD + (_title.getRarity().getColor() + _title.getHead())) + 
						TextFormat.GRAY + ((pt.isTag())? "(當前頭銜)" : "") + "\n" + 
						TextFormat.RESET + (TextFormat.DARK_GREEN + "稱號名稱: ") + TextFormat.RESET + _title.getName() + "\n" + 
						TextFormat.RESET + (TextFormat.DARK_GREEN + "稱號品級: ") + TextFormat.RESET + 
						(_title.getRarity().getColor() + _title.getRarity().getName()) + "\n" + 
						TextFormat.RESET + (TextFormat.DARK_GREEN + "稱號信息: ") + TextFormat.RESET + _title.getDescription() + "\n" + 
						TextFormat.RESET + (TextFormat.DARK_GREEN + "達成條件: ") + TextFormat.RESET + _title.getRequirement() + "\n" + 
						TextFormat.RESET + (TextFormat.DARK_GREEN + "達成獎勵: ") + TextFormat.RESET + _title.getReward() + "\n" + 
						separator;
			}
		}
		
		return title + separator + titleInfo + "\n";
	}

	public static String achievementList(Player p, int page) {
		
		int maxIndex = CRolePlay.getAchvMap().size();
		int maxPage = maxIndex/4 + ((maxIndex%4 == 0)? 0:1);
		int realPage = (page > maxPage)? maxPage : page;
		if(realPage <= 0) realPage = 1;
		
		int startIndex = (realPage-1)*4;
		int endIndex = realPage*4;
		
		String title = TextFormat.RESET + (TextFormat.GREEN + "伺服器成就列表(page " + realPage + "/" + maxPage + ")\n");
		String separator = TextFormat.RESET + "=========================\n";
		
		String achvInfo = "";
		
		int i = 0;
		for(String head : CRolePlay.getAchvMap().keySet()) {
			//從1開始
			i++;
			if(i > startIndex && i <= endIndex) {
				Achievement achv = CRolePlay.getAchvMap().get(head);
				achvInfo += TextFormat.RESET + (TextFormat.BOLD + (TextFormat.GREEN + achv.getHead())) + "\n" + 
							TextFormat.RESET + (TextFormat.DARK_GREEN + "成就名稱: ") + 
							((CRolePlay.getOnlinePDB().get(p.getName()).getPlayerAchvMap().containsKey(achv.getHead()))?
								TextFormat.RESET + achv.getName() : "??????") + "\n" + 
							TextFormat.RESET + (TextFormat.DARK_GREEN + "成就信息: ") + 
							((CRolePlay.getOnlinePDB().get(p.getName()).getPlayerAchvMap().containsKey(achv.getHead()))?
									TextFormat.RESET + achv.getDescription() : "??????") + "\n" + 
							TextFormat.RESET + (TextFormat.DARK_GREEN + "達成條件: ") + 
							((CRolePlay.getOnlinePDB().get(p.getName()).getPlayerAchvMap().containsKey(achv.getHead()))?
									TextFormat.RESET + achv.getRequirement() : "??????") + "\n" + 
							TextFormat.RESET + (TextFormat.DARK_GREEN + "達成獎勵: ") +
							((CRolePlay.getOnlinePDB().get(p.getName()).getPlayerAchvMap().containsKey(achv.getHead()))?
									TextFormat.RESET + achv.getReward() : "??????") + "\n" + 
							separator;
			}
		}
		
		return title + separator + achvInfo + "\n";
	}
	
	public static String titleList(Player p, int page) {
		
		int maxIndex = CRolePlay.getAchvMap().size();
		int maxPage = maxIndex/4 + ((maxIndex%4 == 0)? 0:1);
		int realPage = (page > maxPage)? maxPage : page;
		if(realPage <= 0) realPage = 1;
		
		int startIndex = (realPage-1)*4;
		int endIndex = realPage*4;
		
		String title = TextFormat.RESET + (TextFormat.GREEN + "伺服器稱號列表(page " + realPage + "/" + maxPage + ")\n");
		String separator = TextFormat.RESET + "=========================\n";
		
		String titleInfo = "";
		
		int i = 0;
		for(String head : CRolePlay.getTitleMap().keySet()) {
			//從1開始
			i++;
			if(i > startIndex && i <= endIndex) {
				Title _title = CRolePlay.getTitleMap().get(head);
				titleInfo += TextFormat.RESET + (TextFormat.BOLD + (_title.getRarity().getColor() + _title.getHead())) + "\n" + 
							TextFormat.RESET + (TextFormat.DARK_GREEN + "稱號名稱: ") + 
							((CRolePlay.getOnlinePDB().get(p.getName()).getPlayerTitleMap().containsKey(_title.getHead()))?
								TextFormat.RESET + _title.getName() : "??????") + "\n" + 
							
							TextFormat.RESET + (TextFormat.DARK_GREEN + "稱號品級: ") + 
								TextFormat.RESET + _title.getRarity().getColor() + _title.getRarity().getName() + "\n" + 
							
							TextFormat.RESET + (TextFormat.DARK_GREEN + "稱號信息: ") + 
							((CRolePlay.getOnlinePDB().get(p.getName()).getPlayerTitleMap().containsKey(_title.getHead()))?
									TextFormat.RESET + _title.getDescription() : "??????") + "\n" + 
							
							TextFormat.RESET + (TextFormat.DARK_GREEN + "達成條件: ") + 
							((CRolePlay.getOnlinePDB().get(p.getName()).getPlayerTitleMap().containsKey(_title.getHead()))?
									TextFormat.RESET + _title.getRequirement() : "??????") + "\n" + 
							
							TextFormat.RESET + (TextFormat.DARK_GREEN + "達成獎勵: ") +
							((CRolePlay.getOnlinePDB().get(p.getName()).getPlayerTitleMap().containsKey(_title.getHead()))?
									TextFormat.RESET + _title.getReward() : "??????") + "\n" + 
							separator;
			}
		}
		
		return title + separator + titleInfo + "\n";
	}
	
	public static String levelStatus(Player p) {
		//TODO: 玩家等級信息
		PlayerLevel plv = CRolePlay.getOnlinePDB().get(p.getName()).plv;
		return TextFormat.RESET + 
				(TextFormat.DARK_AQUA + "您當前的等級為: ") + TextFormat.RESET + plv.getLv() + "\n" + 
				(TextFormat.DARK_AQUA + "您當前獲得的經驗: ") + TextFormat.RESET + plv.getExp() + "/" + plv.getLevelUpExp() + "\n" + 
				(TextFormat.DARK_AQUA + "距下次升級還需: ") + TextFormat.RESET + (plv.getLevelUpExp() - plv.getExp()) + "\n";
	}
}
