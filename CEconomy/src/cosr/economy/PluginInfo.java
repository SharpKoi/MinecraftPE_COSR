package cosr.economy;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import cosr.economy.bank.CBank;
import cosr.economy.bank.Loan;

public class PluginInfo {
	
	public enum Role {
		CONSOLE,
		OP,
		PLAYER;
	}
	
	public enum Economy {
		MONEY("Money"),
		SHOP("Shop"),
		BANK("Bank"),
		JOB("Job");
		
		private String name;
		
		private Economy(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}
	
	public static final String COINNAME = "金幣";
	
	public static final String pluginInfoTitle() {
		return TextFormat.RESET + (TextFormat.BOLD + (TextFormat.WHITE + "[") + (TextFormat.AQUA + "CEconomy") + (TextFormat.WHITE + "]")) + TextFormat.RESET;
	}
	
	public static final String moneyInfoTitle() {
		return TextFormat.RESET + (TextFormat.BOLD + (TextFormat.WHITE + "[") + (TextFormat.AQUA + "CMoney") + (TextFormat.WHITE + "]")) + TextFormat.RESET;
	}
	
	public static final String pluginInfo(Economy type) {
		String title = TextFormat.RESET + (TextFormat.BOLD + (TextFormat.AQUA + "--- CEconomy 經濟核心 v1.0 - " + type.getName() + " ---")) + "\n";
		String separator = TextFormat.RESET + "==========================================================\n";
		
		String authorLine = TextFormat.RESET + (TextFormat.BOLD + (TextFormat.YELLOW + "@author: ")) 
				+ (TextFormat.RESET + (TextFormat.WHITE + "Cmen")) + "\n";
		String teamLine = TextFormat.RESET + (TextFormat.BOLD + (TextFormat.YELLOW + "@team: ")) 
				+ (TextFormat.RESET + (TextFormat.WHITE + "COSR")) + "\n";
		String aboutUsLine = TextFormat.RESET + (TextFormat.BOLD + (TextFormat.YELLOW + "@about us: "))
				+ (TextFormat.RESET + (TextFormat.WHITE + "http://cosr.ddns.net")) + "\n";
		String contactLine = TextFormat.RESET + (TextFormat.BOLD + (TextFormat.YELLOW + "@contact: "))
				+ (TextFormat.RESET + (TextFormat.WHITE + "storm319117@gmail.com")) + "\n";
		
		return separator + title + separator + authorLine + "\n" + teamLine + "\n" + aboutUsLine + "\n" + contactLine + separator;
	}
	
	public static final String moneyHelp() {
		
		String firstPart = TextFormat.RESET + 
				"一 關於金錢: \n" + 
				"1. 擁有足夠的金錢，您可以在遊戲中以金錢購買任何物品\n" + 
				"2. 您可以隨時在任何場合給予其他玩家金錢\n" + 
				"3. 您可以透過特定方式獲取金錢\n" + 
				"4. 主指令為 /c$\n";
		
		String secondPart = TextFormat.RESET + 
				"二 如何獲得金錢: \n" + 
				"1. 您可以在遊戲中接工作以獲得金錢 (詳情請輸入/cjob help查看)\n" + 
				"2. 您可以利用個人商店將身上多餘的東西售出給有需要的玩家以獲得金錢 (詳情請輸入/cshop help查看)\n" + 
				"3. 您可以透過伺服器商店將身上多餘的東西售出以獲得金錢 (詳情請輸入/cshop help查看)\n" + 
				"4. 您可以將金錢存入銀行以賺取更多利息 (詳情請輸入/cbank help查看)\n"; 
		
		String thirdPart = TextFormat.RESET + 
				"三 相關指令: \n" + 
				"1. /c$ help  查看金錢指令幫助\n" + 
				"2. /c$ give [player] [dollar]  給予其他玩家金錢\n" + 
				"3. /c$ wallet  查看自己的錢包\n";
		
		String morePart = TextFormat.RESET + 
				"四 更多: \n" + 
				"1. /c$ help 查看金錢相關幫助\n" + 
				"2. /cshop help 查看商店相關幫助\n" + 
				"3. /cbank help 查看銀行相關幫助\n" + 
				"4. /cjob help 查看工作相關幫助\n" + 
				"5. 若有任何問題請聯繫COSR團隊以及作者Cmen\n";
		
		return pluginInfo(Economy.MONEY) + firstPart + "\n" + secondPart + "\n" + thirdPart + "\n" + morePart;
	}
	
	public static final String shopHelp() {
		String warning = TextFormat.RESET + (TextFormat.BOLD + (TextFormat.RED + "由於商店功能較為複雜, 請務必仔細看完!!\n"));
		
		String firstPart = TextFormat.RESET + 
				(TextFormat.BOLD + (TextFormat.GREEN + "一 關於商店: \n")) + TextFormat.RESET + 
				"1. 商店的交易類型分為" + (TextFormat.YELLOW + "SELL(賣)") + TextFormat.RESET + "以及" + (TextFormat.YELLOW + "BUY(買)\n") + TextFormat.RESET + 
				"2. 您可以點擊已設置的商店告示牌, 來完成物品的買賣\n" + 
				"3. 您可以使用指令建構屬於您的告示牌商店, 詳情請見" + TextFormat.YELLOW + "(二 三)\n" + TextFormat.RESET + 
				"4. 商店告示牌上的文字" +TextFormat.YELLOW + "第一列為商店名稱, 第二列為擁有者資訊, 第三列為交易資訊, 第四列為交易價格\n" + TextFormat.RESET + 
				"5. 當您不想讓您的商店繼續交易, 可使用指令將指定編號的商店移除\n" + 
				"6. 若要查看商店編號, 請輸入 " + (TextFormat.YELLOW + "/cshop list") + TextFormat.RESET + "便可查看您所有商店的編號以及詳細資訊\n" + TextFormat.RESET + 
				"7. 點擊您的商店將會跳出商店信息\n" + 
				"8. 主指令為 /cshop\n";
		
		String secondPart = TextFormat.RESET + 
				(TextFormat.BOLD + (TextFormat.GREEN + "二 關於售出類型的商店: \n")) + TextFormat.RESET + 
				"1. 告示牌上第三列文字標頭符號為" + TextFormat.YELLOW + "S\n" + TextFormat.RESET + 
				"2. 當您點擊該類型商店, 且有足夠的金錢時, " + (TextFormat.YELLOW + "您會收到擁有者的所販賣的物品, 同時您的金錢也將可能減少\n") + TextFormat.RESET + 
				"3. 您可以透過" + (TextFormat.YELLOW + "指令") +TextFormat.RESET + "建構該類型的商店, 以賣出您不需要的東西\n" + TextFormat.RESET + 
				"4. 建構指令為: " + (TextFormat.YELLOW + "/cshop create [商店名稱] s [物品ID] [交易數量] [價錢]\n") + TextFormat.RESET + 
				"5. 指令執行完後點擊您所想設置的告示牌以完成建構, 但注意, 設置前此告示牌上不能有文字紀錄\n" + TextFormat.RESET + 
				"6. 建構完成後, " + (TextFormat.YELLOW + "您身上的該物品將會全數放入您的商店") + (TextFormat.RESET + ", 以作為") + (TextFormat.YELLOW + "庫存\n") + TextFormat.RESET + 
				"7. 您可以使用指令提取該類型商店儲存的物品, 用法: " + (TextFormat.YELLOW + "/cshop extract [商店ID] [數量]\n") + TextFormat.RESET + 
				"8. 您可以使用指令替您該類型的商店補貨, 用法: " + (TextFormat.YELLOW + "/cshop replenish [商店ID] [數量]\n") + TextFormat.RESET + 
				"9. 您亦可" + (TextFormat.YELLOW + "手持您的商店所販賣的物品, 點擊您的告示牌, 每點擊一次將會放入一個物品\n") + TextFormat.RESET + 
				"10. 您可以在您的商店列表中看到此類型的商店, 用法為 " + TextFormat.YELLOW + "/cshop list [頁數]\n"; 
		
		String thirdPart = TextFormat.RESET + 
				(TextFormat.BOLD + (TextFormat.GREEN + "三 關於買進類型的商店: \n")) + TextFormat.RESET + 
				"1. 告示牌上第三列文字標頭符號為" + TextFormat.YELLOW + "B\n" + TextFormat.RESET + 
				"2. 當您點擊該類型商店, 且有足夠的物品數量時, " + (TextFormat.YELLOW + "您可能會失去一定數量的該物品, 同時您可能會獲得一定的金錢報酬\n") + TextFormat.RESET + 
				"3. 您可以透過" + (TextFormat.YELLOW + "指令") +TextFormat.RESET + "建構該類型的商店, 以買進您所需的物品\n" + TextFormat.RESET + 
				"4. 建構指令為: " + (TextFormat.YELLOW + "/cshop create [商店名稱] b [物品ID] [交易數量] [價錢])\n") + TextFormat.RESET + 
				"5. 指令執行完後點擊您所想設置的告示牌以完成建構, 但注意, 設置前此告示牌上不能有文字紀錄\n" + TextFormat.RESET + 
				"6. 您可以使用指令提取該類型商店儲存的物品, 用法: " + (TextFormat.YELLOW + "/cshop extract [商店ID] [數量]\n") + TextFormat.RESET + 
				"7. 您無法將身上的物品放入該類型的商店\n" + TextFormat.RESET + 
				"8. 您可以在您的商店列表中看到此類型的商店, 用法為 " + TextFormat.YELLOW + "/cshop list [頁數]\n";
		
		String forthPart = TextFormat.RESET + 
				(TextFormat.BOLD + (TextFormat.GREEN + "四 關於伺服器商店與個人商店: \n")) + TextFormat.RESET + 
				"1. 建構權限: 伺服器商店" + (TextFormat.DARK_AQUA + "[OP]") + (TextFormat.RESET + " / 個人商店") + (TextFormat.DARK_AQUA + "[PLAYER]\n") + TextFormat.RESET + 
				"2. 查看權限: 伺服器商店" + (TextFormat.DARK_AQUA + "[OP]") + (TextFormat.RESET + " / 個人商店") + (TextFormat.DARK_AQUA + "[擁有者]\n") + TextFormat.RESET + 
				"3. 刪除權限: 伺服器商店" + (TextFormat.DARK_AQUA + "[OP]") + (TextFormat.RESET + " / 個人商店") + (TextFormat.DARK_AQUA + "[擁有者]\n") + TextFormat.RESET + 
				"4. 商店形式: 伺服器商店" + (TextFormat.DARK_AQUA + "[告示牌]") + (TextFormat.RESET + " / 個人商店") + (TextFormat.DARK_AQUA + "[告示牌]\n") + TextFormat.RESET + 
				"5. 交易類型: 伺服器商店" + (TextFormat.DARK_AQUA + "[買 or 賣]") + (TextFormat.RESET + " / 個人商店") + (TextFormat.DARK_AQUA + "[買 or 賣]\n") + TextFormat.RESET + 
				"6. 庫存量: 伺服器商店" + (TextFormat.DARK_AQUA + "[無限]") + (TextFormat.RESET + " / 個人商店") + (TextFormat.DARK_AQUA + "[有限]\n");
				
		
		String fifthPart = TextFormat.RESET + 
				(TextFormat.BOLD + (TextFormat.GREEN + "五: 關於指令: \n")) + TextFormat.RESET + 
				"1. " + TextFormat.DARK_GREEN + "/cshop help  " + TextFormat.RESET + ">查看金錢指令幫助\n" + 
				"2. " + TextFormat.DARK_GREEN + "/cshop list  " + TextFormat.RESET + ">查看您所有商店的列表\n" + 
				"3. " + TextFormat.DARK_GREEN + "/cshop create [商店名稱] [交易類型b/s] [物品ID] [交易數量] [價錢]  " + TextFormat.RESET + ">創建個人商店\n" + 
				"4. " + TextFormat.DARK_GREEN + "/cshop delete [商店ID]  " + TextFormat.RESET + ">刪除指定編號的個人商店\n" + 
				"5. " + TextFormat.DARK_GREEN + "/cshop check [商店ID]  " + TextFormat.RESET + ">查看該編號商店的詳細資訊\n" + 
				"6. " + TextFormat.DARK_GREEN + "/cshop replenish [商店ID] [物品數量]  " + TextFormat.RESET + ">替指定編號的商店補貨\n" + 
				"7. " + TextFormat.DARK_GREEN + "/cshop extract [商店ID] [物品數量]  " + TextFormat.RESET + ">從指定編號的商店中取出貨物\n" + 
				"8. " + TextFormat.DARK_GREEN + "/cshop ad [商店ID] [廣告內容] [廣播次數]  " + TextFormat.RESET + ">幫指定編號的商店打廣告\n" + 
				"9. " + TextFormat.DARK_GREEN + "/cshop ui  " + TextFormat.RESET + ">打開商店管理介面\n";
		
		String morePart = TextFormat.RESET + 
				(TextFormat.BOLD + (TextFormat.GREEN + "六 更多: \n")) + TextFormat.RESET + 
				"1. /c$ help 查看金錢相關幫助\n" + 
				"2. /cshop help 查看商店相關幫助\n" + 
				"3. /cbank help 查看銀行相關幫助\n" + 
				"4. /cjob help 查看工作相關幫助\n" + 
				"5. 若有任何問題請聯繫COSR團隊以及作者Cmen\n";
		
		
		return pluginInfo(Economy.SHOP) + warning + "\n" + firstPart + "\n" + secondPart + "\n" + thirdPart + "\n" + forthPart + "\n" + fifthPart + "\n" + morePart;
	}
	
	public static final String bankHelp() {
		
		String firstPart = TextFormat.RESET + 
				"一 關於銀行: \n" + 
				"1. 您可以將身上擁有的金錢存入銀行, 以賺取更多的利息\n" + 
				"2. 您可以向銀行申請貸款, 以滿足您的需求(最高可貸您現有金額的90%)\n" + 
				"3. 貸款請於7日內繳清, 否則將直接扣除您身上的金錢\n" + 
				"4. 每天銀行的利率將隨機變更\n" + 
				"5. 每過一天, 銀行會以當前利率重新計算您已儲存的金錢\n" + 
				"6. 主指令為 /cbank\n";
		
		String secondPart = TextFormat.RESET + 
				"二 相關指令: \n" + 
				"1. /cbank  查看當前銀行狀態及資訊\n" + 
				"2. /cbank help  查看銀行指令幫助\n" + 
				"3. /cbank store [dollar]  將指定數量的金錢存入銀行\n" + 
				"4. /cbank draw [dollar]  將指定數量的金錢從銀行中取出\n" + 
				"5. /cbank loan [dollar]  向銀行貸款指定數量的金錢\n" + 
				"6. /cbank pb [dollar]  將指定數量的貸款繳還給銀行\n";
		
		String morePart = TextFormat.RESET + 
				"三 更多: \n" + 
				"1. /c$ help 查看金錢相關幫助\n" + 
				"2. /cshop help 查看商店相關幫助\n" + 
				"3. /cbank help 查看銀行相關幫助\n" + 
				"4. /cjob help 查看工作相關幫助\n" + 
				"5. 若有任何問題請聯繫COSR團隊以及作者Cmen\n";
				
		return pluginInfo(Economy.BANK) + firstPart + "\n" + secondPart + "\n" + morePart;
	}
	
	public static final String jobHelp() {
		
		String firstPart = TextFormat.RESET + 
				"一 關於工作: \n" + 
				"1. 您可以使用 /cjob list來查看當前伺服器裡所有的工作\n" + 
				"2. 您可以申請工作以獲得金錢\n" + 
				"3. 一個玩家只能選擇一項工作\n" + 
				"4. 當您身上的金錢為負值時, 將無法順利申請工作\n" + 
				"5. 若您成功的達成工作的需求, 將會予以該工作的報酬\n" + 
				"6. 當您不想繼續做目前的工作, 可以選擇辭職\n" + 
				"7. 若您當前有工作, 又申請其他工作, 新工作將會覆蓋舊工作\n" + 
				"8. 主指令為 /cjob\n";
		
		String secondPart = TextFormat.RESET + 
				"二 相關指令: \n" + 
				"1. /cjob help  >查看工作指令幫助\n" + 
				"2. /cjob list  >查看當前伺服器中所有的工作\n" + 
				"3. /cjob get [工作名稱]  >申請該工作\\n" + 
				"4. /cjob quit  >辭掉當前工作\n";
		
		String morePart = TextFormat.RESET + 
				"三 更多: \n" + 
				"1. /c$ help 查看金錢相關幫助\n" + 
				"2. /cshop help 查看商店相關幫助\n" + 
				"3. /cbank help 查看銀行相關幫助\n" + 
				"4. /cjob help 查看工作相關幫助\n" + 
				"5. 若有任何問題請聯繫COSR團隊以及作者Cmen\n";
				
		return pluginInfo(Economy.BANK) + firstPart + "\n" + secondPart + "\n" + morePart;
	}
	
	/*
	public static final String serverShopListInfo(CommandSender sender, int page) {
		
		int maxIndex = CEconomy.getServerShopList().size();
		int maxPage = maxIndex/4 + 1;
		int realPage = (page > maxPage)? maxPage : page;
		if(realPage <= 0) realPage = 1;
		
		String title = TextFormat.RESET + (TextFormat.GREEN + "伺服器商店列表(page " + realPage + "/" + maxPage + ")\n");
		String separator = TextFormat.RESET + "=========================\n";
		
		String shopInfo = "";
		for(int i = (realPage-1)*4; i < realPage*4; i++) {
			if(i < maxIndex) {
				ServerShop svShop = CEconomy.getServerShopList().get(i);
				String num = TextFormat.RESET + (TextFormat.BOLD + "#"+i+": \n");
				
				shopInfo += num + svShop.Information(sender.getName()) + separator;
			}else {
				break;
			}
		}
		
		return title + separator + shopInfo;
		/*
		 * --- 伺服器商店列表(page 1/?) ---
		 * ================================
		 * #1:
		 * 商店名稱: COSR_Shop
		 * 交易類型: 賣(SELL)
		 * 物品: 
		 * 物品ID: 
		 * 位於: 
		 * ================================
		 *
	}
	*/
	
	/*
	public static final String personalShopListInfo(Player sender, String owner, int page) {
		
		if(!CEconomy.getShopMap().containsKey(owner)) return "No Shop!";
		
		int maxIndex = CEconomy.getShopMap().get(owner).size();
		int maxPage = maxIndex/4 + 1;
		int realPage = (page > maxPage)? maxPage : page;
		
		String title = TextFormat.RESET + (TextFormat.GREEN + "您所有的商店列表(page " + realPage + "/" + maxPage + ")\n");
		String separator = TextFormat.RESET + "=========================\n";
		
		String shopInfo = "";
		for(int i = (realPage-1)*4; i < realPage*4; i++) {
			if(i < maxIndex) {
				PersonalShop pShop = CEconomy.getShopMap().get(owner).get(i);
				String num = TextFormat.RESET + (TextFormat.BOLD + "#"+i+": \n");
				
				shopInfo += num + pShop.Information(sender.getName()) + separator;
			}else {
				break;
			}
		}
		
		return title + separator + shopInfo;
		/*
		 * --- 您所有的商店列表(page 1/?) ---
		 * ==================================
		 * #1:
		 * 商店名稱: MyShop
		 * 交易類型: 買(BUY)
		 * 物品: 
		 * 物品ID: 
		 * 一次交易數量: 
		 * 庫存量: 
		 * 位於:
		 * ===================================
		 *
	}
	*/
	
	public static final String bankStatus(Player player) {
		String title = TextFormat.RESET + (TextFormat.GREEN + "--- COSR銀行當前狀態 --- \n");
		String separator = TextFormat.RESET + "=======================\n";
		String depositLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "您目前儲存的金額: " + 
				TextFormat.RESET + CEconomy.getD2(CBank.getDepositMap().getOrDefault(player.getName(), (float)0.0)) + "\n");
		String loanLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "您目前貸款的金額: " + 
				TextFormat.RESET + CEconomy.getD2(CBank.getLoanMap().getOrDefault(player.getName(), new Loan()).getMoney()) + "\n");
		String loanDaysLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "貸款已過天數: " + 
				TextFormat.RESET + CBank.getLoanMap().getOrDefault(player.getName(), new Loan()).getDays() + "\n");
		String IRLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "當前銀行利率: " + 
				TextFormat.RESET + CEconomy.getD2(CBank.getInterestRate())*100 + "%" + "\n");
		String lastDate = TextFormat.RESET + (TextFormat.DARK_GREEN + "上次更新時間: " + 
				TextFormat.RESET + CBank.dateForm() + "\n");
		
		return title + separator + depositLine + loanLine + loanDaysLine + IRLine + lastDate;
		
		/*
		 * --- COSR銀行當前狀態 --- 
		 * =======================
		 * 您目前儲存的金額: 
		 * 您目前貸款的金額: 
		 * 貸款已過天數: 
		 * 銀行利率: 
		 * 上次利率更新日期: 
		 */
	}
}
