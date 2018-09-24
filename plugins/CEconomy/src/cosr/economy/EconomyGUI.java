package cosr.economy;

import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;
import cosr.economy.bank.CBank;
import cosr.economy.bank.Loan;
import cosr.economy.job.CJob;
import cosr.economy.job.Job;
public class EconomyGUI {
	
	/**首頁 TODO:Simple*/
	public static FormWindowSimple homePage() {
		FormWindowSimple window = new FormWindowSimple("經濟首頁", "請選擇功能");
		window.addButton(new ElementButton(TextFormat.BOLD + "關於世界經濟"));
		window.addButton(new ElementButton(TextFormat.BOLD + "我的錢包"));
		window.addButton(new ElementButton(TextFormat.BOLD + "我的工作"));
		window.addButton(new ElementButton(TextFormat.BOLD + "COSR銀行"));
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "關閉視窗")));
		return window;
	}
	
	/*關於經濟CEconomy TODO:Simple*/
	public static FormWindowSimple aboutWindow() {
		FormWindowSimple window = new FormWindowSimple("關於經濟CEconomy", "");
		window.addButton(new ElementButton(TextFormat.BOLD + "關於插件"));
		window.addButton(new ElementButton(TextFormat.BOLD + "關於金錢"));
		window.addButton(new ElementButton(TextFormat.BOLD + "關於商店"));
		window.addButton(new ElementButton(TextFormat.BOLD + "關於工作"));
		window.addButton(new ElementButton(TextFormat.BOLD + "關於銀行"));
		window.addButton(new ElementButton(TextFormat.BOLD + "關於作者"));
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "返回至首頁")));
		return window;
	}
	
	//.....這一堆About真的不太想寫
	//嘛，寫寫心得 意思意思一下也好
	
	/*我的錢包 TODO:Simple*/
	public static FormWindowSimple moneySystemWindow(String owner) {
		FormWindowSimple window = new FormWindowSimple("我的錢包", "您還有"+CMoney.getMoneyMap().get(owner)+"元的金錢");
		window.addButton(new ElementButton(TextFormat.BOLD + "給予玩家金幣"));							//giveMoneyWindow
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "返回至首頁")));			//homePage
		return window;
	}
	
	//給予玩家金錢 TODO:Custom
	public static FormWindowCustom giveMoneyWindow() {
		FormWindowCustom window = new FormWindowCustom("給予玩家金錢");
		window.addElement(new ElementInput("請輸入對象"));
		window.addElement(new ElementInput("請輸入金錢數量"));
		
		return window;
	}
	
	//給予玩家...金錢 TODO:Custom
	public static FormWindowCustom giveMoneyWindow(String playerName) {
		FormWindowCustom window = new FormWindowCustom("給予玩家" + playerName + "金錢");
		window.addElement(new ElementInput("請輸入金錢數量"));
		
		return window;
	}
	
	//我的工作 (DONE) TODO:Simple
	public static FormWindowSimple myJobWindow(String owner) {
		Job job = CEconomy.getJobMap().get(owner).getJob();
		FormWindowSimple window = new FormWindowSimple("我的工作", "您現在的工作為" + TextFormat.DARK_GREEN + job.chineseName());
		window.addButton(new ElementButton(TextFormat.BOLD + "查看當前工作"+job.getName()));
		window.addButton(new ElementButton(TextFormat.BOLD + "取得其他工作"));
		if(!job.getName().equals("None")) {
			window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "離職")));
		}
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "返回至首頁")));
		return window;
	}
	
	//我的工作資訊 TODO:Modal
	public static FormWindowModal myJobInfoWindow(String playerName) {
		CJob cjob = CEconomy.getJobMap().get(playerName);
		FormWindowModal window = new FormWindowModal("我的工作資訊", "", "確認", "返回");
		window.setContent(TextFormat.DARK_GREEN + "工作名稱: " + TextFormat.RESET + cjob.getJob().chineseName() + "(" + cjob.getJob().getName() + ")\n" + 
							TextFormat.DARK_GREEN + "工作介紹: " + TextFormat.RESET + cjob.getJob().getDescription() + "\n" + 
							TextFormat.DARK_GREEN + "當前進度: " + TextFormat.RESET + cjob.getRecorder().getCount() + "/" + cjob.getJob().getRequirement());
		return window;
	}
	
	//取得其他工作 TODO:Simple
	public static FormWindowSimple allJobWindow() {
		FormWindowSimple window = new FormWindowSimple("取得其他工作", "請選擇您所想要的工作");
		for(Job j : Job.values()) {
			window.addButton(new ElementButton(j.chineseName() + " " + j.getName()));
		}
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "返回")));
		return window;
	}
	
	//確定取得該工作? TODO:Modal
	public static FormWindowModal sureToGetJobWindow(Job job) {
		FormWindowModal window = new FormWindowModal("確定取得該工作?", "", "確定", "取消");
		window.setContent(TextFormat.DARK_GREEN + "工作名稱: " + TextFormat.RESET + job.chineseName() + "(" + job.getName() + ")\n" + 
				TextFormat.DARK_GREEN + "工作介紹: " + TextFormat.RESET + job.getDescription());
		return window;
	}
	
	//確定離開該工作? TODO:Modal
	public static FormWindowModal sureToQuitJobWindow(String playerName) {
		CJob cjob = CEconomy.getJobMap().get(playerName);
		FormWindowModal window = new FormWindowModal("確定離開該工作?", "", "確定", "取消");
		window.setContent(TextFormat.DARK_GREEN + "工作名稱: " + TextFormat.RESET + cjob.getJob().chineseName() + "(" + cjob.getJob().getName() + ")\n" + 
				TextFormat.DARK_GREEN + "工作介紹: " + TextFormat.RESET + cjob.getJob().getDescription() + "\n" + 
				TextFormat.DARK_GREEN + "當前進度: " + TextFormat.RESET + cjob.getRecorder().getCount() + "/" + cjob.getJob().getRequirement());
		return window;
	}
	
	/*COSR銀行 (DONE) TODO:Simple*/
	public static FormWindowSimple bankSystemWindow() {
		FormWindowSimple window = new FormWindowSimple("COSR銀行", "");
		window.addButton(new ElementButton(TextFormat.BOLD + "查看銀行當前狀態"));
		window.addButton(new ElementButton(TextFormat.BOLD + "我的銀行帳戶"));
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "返回至首頁")));
		return window;
	}
	
	//COSR銀行當前狀態 TODO:Modal
	public static FormWindowModal cBankInfoWindow() {
		FormWindowModal window = new FormWindowModal("COSR銀行當前狀態", "", "確認", "返回");
		window.setContent(TextFormat.DARK_GREEN + "銀行當前利率: " + TextFormat.RESET + (float)CBank.getInterestRate() + "\n" + 
							TextFormat.DARK_GREEN + "上次更新時間: " + TextFormat.RESET + CBank.dateForm());
		return window;
	}
	
	//我的銀行帳戶 TODO:Simple
	public static FormWindowSimple myBalanceWindow(String playerName) {
		FormWindowSimple window = new FormWindowSimple("我的銀行帳戶", "");
		window.addButton(new ElementButton(TextFormat.BOLD + "查看詳細資料"));
		window.addButton(new ElementButton(TextFormat.BOLD + "存入現金"));
		if(CBank.getDepositMap().containsKey(playerName)) {
			if(CBank.getDepositMap().get(playerName) > 0)
				window.addButton(new ElementButton(TextFormat.BOLD + "提取現金"));
		}
		window.addButton(new ElementButton(TextFormat.BOLD + "申請貸款"));
		if(CBank.getLoanMap().containsKey(playerName)) {
			if(CBank.getLoanMap().get(playerName).getMoney() > 0)
				window.addButton(new ElementButton(TextFormat.BOLD + "繳還貸款"));
		}
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "返回")));
		return window;
	}
	
	//我的帳戶狀態 TODO:Modal
	public static FormWindowModal myBalanceInfoWindow(String playerName) {
		FormWindowModal window = new FormWindowModal("我的帳戶狀態", "", "確認", "返回");
		Loan loan = CBank.getLoanMap().get(playerName);
		window.setContent(TextFormat.DARK_GREEN + "當前存款: " + TextFormat.RESET + CBank.getDepositMap().getOrDefault(playerName, (float)0.0) + "\n" + 
							TextFormat.DARK_GREEN + "當前貸款: " + TextFormat.RESET + (loan == null? "0.0" : loan.getMoney()) + "\n" + 
							TextFormat.DARK_GREEN + "本期貸款已過天數: " + TextFormat.RESET + (loan == null? "0" : loan.getDays()) + "\n" + 
							TextFormat.DARK_GREEN + "銀行當前利率: " + TextFormat.RESET + CBank.getInterestRate() + "\n" + 
							TextFormat.DARK_GREEN + "上次更新日期: " + TextFormat.RESET + CBank.dateForm());
		return window;
	}
	
	//將現金存入銀行 TODO:Custom
	public static FormWindowCustom storeMoneyWindow(String playerName) {
		FormWindowCustom window = new FormWindowCustom("將現金存入銀行");
		String info = TextFormat.DARK_GREEN + "當前存款: " + TextFormat.RESET + CBank.getDepositMap().getOrDefault(playerName, (float)0.0) + "\n" + 
						TextFormat.DARK_GREEN + "銀行當前利率: " + TextFormat.RESET + CBank.getInterestRate() + "\n" + 
						TextFormat.DARK_GREEN + "上次更新時間: " + TextFormat.RESET + CBank.dateForm();
		window.addElement(new ElementLabel(info));
		window.addElement(new ElementInput("請輸入存入現金數量"));
		
		return window;
	}
	
	//提取現金 TODO:Custom
	public static FormWindowCustom drawMoneyWindow(String playerName) {
		FormWindowCustom window = new FormWindowCustom("提取現金");
		String info = TextFormat.DARK_GREEN + "當前存款: " + TextFormat.RESET + CBank.getDepositMap().getOrDefault(playerName, (float)0.0) + "\n" + 
						TextFormat.DARK_GREEN + "銀行當前利率: " + TextFormat.RESET + CBank.getInterestRate() + "\n" + 
						TextFormat.DARK_GREEN + "上次更新時間: " + TextFormat.RESET + CBank.dateForm();
		window.addElement(new ElementLabel(info));
		window.addElement(new ElementInput("請輸入提取現金數量"));
		
		return window;
	}
	
	//申請貸款 TODO:Custom
	public static FormWindowCustom loanWindow(String playerName) {
		FormWindowCustom window = new FormWindowCustom("申請貸款");
		String info = TextFormat.ITALIC + (TextFormat.YELLOW + "注意: 一次借貸最多只能申請您身上金錢的90%, 並請於七日內繳清, 否則將直接從您身上的金錢扣除");
		window.addElement(new ElementLabel(info));
		window.addElement(new ElementInput("請輸入申貸現金數量"));
		
		return window;
	}
	
	//繳還貸款 TODO:Custom
	public static FormWindowCustom payLoanWindow(String playerName) {
		FormWindowCustom window = new FormWindowCustom("繳還貸款");
		String info = TextFormat.ITALIC + (TextFormat.YELLOW + "注意: 請於七日內繳清這期貸款, 否則將直接從您身上的金錢扣除");
		window.addElement(new ElementLabel(info));
		window.addElement(new ElementInput("請輸入繳還現金數量"));
		
		return window;
	}
}
