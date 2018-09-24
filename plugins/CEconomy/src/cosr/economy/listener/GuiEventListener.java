package cosr.economy.listener;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.*;
import cn.nukkit.form.window.*;
import cn.nukkit.utils.TextFormat;
import cosr.economy.CEconomy;
import cosr.economy.CMoney;
import cosr.economy.EconomyGUI;
import cosr.economy.PluginInfo;
import cosr.economy.bank.CBank;
import cosr.economy.job.CJob;
import cosr.economy.job.Job;
import cosr.economy.job.JobRecorder;

public class GuiEventListener implements Listener {
	public static HashMap<String, Job> uiJobMap = new HashMap<String, Job>();
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onForm(PlayerFormRespondedEvent event) {
		Player p = event.getPlayer();
		FormWindow w = event.getWindow();
		FormResponse r = event.getResponse();
		
		if(r == null) {
			if(uiJobMap.containsKey(p.getName())) uiJobMap.remove(p.getName());
			return;
		}
		
		if(w instanceof FormWindowSimple) {
			//首頁、關於經濟CEconomy、我的錢包、市集、COSR商店市集、玩家商店市集、光顧商店(shop.getName())、我的商店列表、我的商店系統(title)、我的工作、取得其他工作、COSR銀行、我的銀行帳戶
			FormWindowSimple window = (FormWindowSimple) w;
			FormResponseSimple response = (FormResponseSimple) r;
			String btxt = response.getClickedButton().getText();
			if(window.getTitle().equals("經濟首頁")) {
				if(btxt.equals(TextFormat.BOLD + "關於世界經濟")) {
					p.showFormWindow(EconomyGUI.aboutWindow());
				}
				else if(btxt.equals(TextFormat.BOLD + "我的錢包")) {
					p.showFormWindow(EconomyGUI.moneySystemWindow(p.getName()));
				}
				else if(btxt.equals(TextFormat.BOLD + "我的工作")) {
					p.showFormWindow(EconomyGUI.myJobWindow(p.getName()));
				}
				else if(btxt.equals(TextFormat.BOLD + "COSR銀行")) {
					p.showFormWindow(EconomyGUI.bankSystemWindow());
				}
			}
			else if(window.getTitle().equals("關於經濟CEconomy")) {
				if(btxt.equals("")) {
					
				}
				else if(btxt.equals("")) {
					
				}
				else if(btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "關閉視窗"))) {
				}
			}
			else if(window.getTitle().equals("我的錢包")) {
				if(btxt.equals(TextFormat.BOLD + "給予玩家金幣")) {
					p.showFormWindow(EconomyGUI.giveMoneyWindow());
				}
				else if(btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "返回至首頁"))) {
					p.showFormWindow(EconomyGUI.homePage());
				}
			}
			else if(window.getTitle().equals("我的工作")) {
				if(btxt.startsWith(TextFormat.BOLD + "查看當前工作")) {
					p.showFormWindow(EconomyGUI.myJobInfoWindow(p.getName()));
				}
				else if(btxt.equals(TextFormat.BOLD + "取得其他工作")) {
					p.showFormWindow(EconomyGUI.allJobWindow());
				}
				else if(btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "離職"))) {
					p.showFormWindow(EconomyGUI.sureToQuitJobWindow(p.getName()));
				}
				else if(btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "返回至首頁"))) {
					p.showFormWindow(EconomyGUI.homePage());
				}
			}
			else if(window.getTitle().equals("取得其他工作")) {
				if(btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "返回"))) {
					p.showFormWindow(EconomyGUI.myJobWindow(p.getName()));
				}else {
					Job job = Job.getJob(btxt.split(" ")[1]);
					p.showFormWindow(EconomyGUI.sureToGetJobWindow(job));
					uiJobMap.put(p.getName(), job);
				}
			}
			else if(window.getTitle().equals("COSR銀行")) {
				if(btxt.startsWith(TextFormat.BOLD + "查看銀行當前狀態")) {
					p.showFormWindow(EconomyGUI.cBankInfoWindow());
				}
				else if(btxt.equals(TextFormat.BOLD + "我的銀行帳戶")) {
					p.showFormWindow(EconomyGUI.myBalanceWindow(p.getName()));
				}
				else if(btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "返回至首頁"))) {
					p.showFormWindow(EconomyGUI.homePage());
				}
			}
			else if(window.getTitle().equals("我的銀行帳戶")) {
				if(btxt.startsWith(TextFormat.BOLD + "查看詳細資料")) {
					p.showFormWindow(EconomyGUI.myBalanceInfoWindow(p.getName()));
				}
				else if(btxt.equals(TextFormat.BOLD + "存入現金")) {
					p.showFormWindow(EconomyGUI.storeMoneyWindow(p.getName()));
				}
				else if(btxt.equals(TextFormat.BOLD + "提取現金")) {
					p.showFormWindow(EconomyGUI.drawMoneyWindow(p.getName()));
				}
				else if(btxt.equals(TextFormat.BOLD + "申請貸款")) {
					p.showFormWindow(EconomyGUI.loanWindow(p.getName()));
				}
				else if(btxt.equals(TextFormat.BOLD + "繳還貸款")) {
					p.showFormWindow(EconomyGUI.payLoanWindow(p.getName()));
				}
				else if(btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "返回"))) {
					p.showFormWindow(EconomyGUI.bankSystemWindow());
				}
			}
		}
		else if(w instanceof FormWindowCustom) {
			//給予玩家金錢、給予玩家...金錢、創建個人商店、替...商店補貨、從...商店中提取物品、替...打廣告、將現金存入銀行、提取現金、申請貸款、繳還貸款
			FormWindowCustom window = (FormWindowCustom) w;
			FormResponseCustom response = (FormResponseCustom) r;
			
			if(window.getTitle().equals("給予玩家金錢")) {
				String to = response.getInputResponse(0);
				String moneyStr = response.getInputResponse(1);
				if(!isDigit(moneyStr)) {
					p.sendMessage(PluginInfo.moneyInfoTitle() + TextFormat.RED + "數字格式錯誤, 請重新輸入金錢數量");
					p.showFormWindow(EconomyGUI.giveMoneyWindow());
					return;
				}
				double money = Double.parseDouble(moneyStr);
				try {
					CMoney.giveMoney(p.getName(), to, (float)money);
				} catch (FileNotFoundException e) {
					p.sendMessage(PluginInfo.moneyInfoTitle() + TextFormat.RED + "找不到該玩家");
					e.printStackTrace();
				}
			}
			else if(window.getTitle().startsWith("給予玩家") && window.getTitle().endsWith("金錢")) {
				String playerName = window.getTitle().replace("給予玩家", "").replace("金錢", "").trim();
				String moneyStr = response.getInputResponse(0);
				if(!isDigit(moneyStr)) {
					p.sendMessage(PluginInfo.moneyInfoTitle() + TextFormat.RED + "數字格式錯誤, 請重新輸入金錢數量");
					p.showFormWindow(EconomyGUI.giveMoneyWindow());
					return;
				}
				double money = Double.parseDouble(moneyStr);
				try {
					CMoney.giveMoney(p.getName(), playerName, (float)money);
					p.sendMessage(TextFormat.GREEN + "成功給予玩家" + TextFormat.RESET + playerName + TextFormat.YELLOW
							+ (float)money + "元金錢");
				} catch (FileNotFoundException e) {
					p.sendMessage(PluginInfo.moneyInfoTitle() + TextFormat.RED + "找不到該玩家");
				}
			}
			else if(window.getTitle().equals("將現金存入銀行")) {
				String dollarStr = response.getInputResponse(1);
				if(!isDigit(dollarStr)) {
					p.sendMessage(PluginInfo.moneyInfoTitle() + TextFormat.RED + "數字格式錯誤, 請重新輸入金錢數量");
					p.showFormWindow(EconomyGUI.storeMoneyWindow(p.getName()));
					return;
				}
				double dollar = Double.parseDouble(dollarStr);
				CBank.addDeposit(p.getName(), (float) dollar);
				CMoney.getMoneyMap().put(p.getName(), (float)(CMoney.getMoneyMap().get(p.getName()) - dollar));
				p.sendMessage(CBank.infoTitle() + TextFormat.GREEN + "您成功存入了" + dollar + "元的現金!");
			}
			else if(window.getTitle().equals("提取現金")) {
				String dollarStr = response.getInputResponse(1);
				if(!isDigit(dollarStr)) {
					p.sendMessage(PluginInfo.moneyInfoTitle() + TextFormat.RED + "數字格式錯誤, 請重新輸入金錢數量");
					p.showFormWindow(EconomyGUI.storeMoneyWindow(p.getName()));
					return;
				}
				double dollar = Math.min(Double.parseDouble(dollarStr), CBank.getDepositMap().get(p.getName()));
				CBank.deDeposit(p.getName(), (float)dollar);
				CMoney.getMoneyMap().put(p.getName(), (float)(CMoney.getMoneyMap().get(p.getName()) + dollar));
				if(CBank.getDepositMap().get(p.getName()) <= 0) {
					CBank.getDepositMap().remove(p.getName());
				}
				p.sendMessage(CBank.infoTitle() + TextFormat.GREEN + "成功從您的銀行帳戶中拿取" + (float)dollar + "元金幣!");
			}
			else if(window.getTitle().equals("申請貸款")) {
				String loanStr = response.getInputResponse(1);
				if(!isDigit(loanStr)) {
					p.sendMessage(CBank.infoTitle() + TextFormat.RED + "請輸入正確的金錢數字格式!");
					p.showFormWindow(EconomyGUI.loanWindow(p.getName()));
					return;
				}
				double loan = Double.parseDouble(loanStr);
				
				if(loan <= CMoney.getMoneyMap().get(p.getName()) * 0.9) {
					if(CBank.getLoanMap().containsKey(p.getName())) {
						if(CBank.getLoanMap().get(p.getName()).getMoney() > 0) {
							p.sendMessage(CBank.infoTitle() + TextFormat.RED + "您這期的貸款尚未繳清，請先繳清後再申請下一期貸款!");
							return;
						}
					}
					CBank.putLoan(p.getName(), (float) loan);
					CMoney.getMoneyMap().put(p.getName(), (float)(CMoney.getMoneyMap().get(p.getName()) + loan));
					p.sendMessage(CBank.infoTitle() + TextFormat.GREEN + "成功向銀行貸款" +loan + "元金幣!");
				}else {
					p.sendMessage(CBank.infoTitle() + TextFormat.RED + "抱歉! 您不得申請超過您身上金錢90%的貸款\n" + 
							"您至多只能申請" + CMoney.getMoneyMap().get(p.getName()) * 0.90 + "元的貸款");
					p.showFormWindow(EconomyGUI.loanWindow(p.getName()));
					return;
				}
			}
			else if(window.getTitle().equals("繳還貸款")) {
				String loanStr = response.getInputResponse(1);
				if(!isDigit(loanStr)) {
					p.sendMessage(CBank.infoTitle() + TextFormat.RED + "請輸入正確的金錢數字格式!");
					p.showFormWindow(EconomyGUI.payLoanWindow(p.getName()));
					return;
				}
				double loan = Math.min(Double.parseDouble(loanStr), CBank.getLoanMap().get(p.getName()).getMoney());
				
				CBank.deLoan(p.getName(), (float) loan);
				CMoney.getMoneyMap().put(p.getName(), (float)(CMoney.getMoneyMap().get(p.getName()) - loan));
				
				if(CBank.getLoanMap().get(p.getName()).getMoney() <= 0) {
					p.sendMessage(CBank.infoTitle() + TextFormat.GREEN + "恭喜您已繳清這期的所有貸款!");
					CBank.getLoanMap().remove(p.getName());
				}else {
					p.sendMessage(CBank.infoTitle() + TextFormat.GREEN + "成功向銀行繳還" +loan + "元的貸款!");
				}
			}
		}
		else if(w instanceof FormWindowModal) {
			//商店訊息、交易成功(購買/出售)、我的商店資訊、確定刪除該商店?、我的工作資訊、確定取得該工作?、確定離開該工作?、COSR銀行當前狀態、我的帳戶狀態
			FormWindowModal window = (FormWindowModal) w;
			FormResponseModal response = (FormResponseModal) r;
			String btxt = response.getClickedButtonText();
			
			if(window.getTitle().equals("我的工作資訊")) {
				if(btxt.equals("返回")) {
					p.showFormWindow(EconomyGUI.myJobWindow(p.getName()));
				}
			}
			else if(window.getTitle().equals("確定取得該工作?")) {
				if(btxt.equals("確定")) {
					CEconomy.getJobMap().put(p.getName(), new CJob(uiJobMap.get(p.getName()), new JobRecorder()));
					if(CEconomy.getJobMap().containsKey(p.getName())) {
						if(!CEconomy.getJobMap().get(p.getName()).getJob().equals(Job.NONE)) {
							p.sendMessage(CJob.infoTitle() + TextFormat.GRAY + "已離開了您原先的工作: " + CEconomy.getJobMap().get(p.getName()).getJob().getName());
						}
					}
					p.sendMessage(CJob.infoTitle() + TextFormat.GREEN + "已成功申請工作: " + TextFormat.AQUA + uiJobMap.get(p.getName()).getName());
				}else if(btxt.equals("取消")){
					p.showFormWindow(EconomyGUI.allJobWindow());
				}
				uiJobMap.remove(p.getName());
			}
			else if(window.getTitle().equals("確定離開該工作?")) {
				if(btxt.equals("確定")) {
					if(CEconomy.getJobMap().containsKey(p.getName())) {
						p.sendMessage(CJob.infoTitle() + TextFormat.GRAY + "您已辭掉您當前的工作: " + CEconomy.getJobMap().get(p.getName()).getJob().getName());
						CEconomy.getJobMap().get(p.getName()).setJob(Job.NONE);
					}
					p.sendMessage(CJob.infoTitle() + TextFormat.GREEN + "已成功申請工作: " + TextFormat.AQUA + uiJobMap.get(p.getName()).getName());
				}else if(btxt.equals("取消")){
					p.showFormWindow(EconomyGUI.allJobWindow());
				}
				uiJobMap.remove(p.getName());
			}
			else if(window.getTitle().equals("COSR銀行當前狀態")) {
				if(btxt.equals("返回")) {
					p.showFormWindow(EconomyGUI.bankSystemWindow());
				}
			}
			else if(window.getTitle().equals("我的帳戶狀態")) {
				if(btxt.equals("返回")) {
					p.showFormWindow(EconomyGUI.myBalanceWindow(p.getName()));
				}
			}
			else if(window.getTitle().equals("錯誤")) {
				if(btxt.equals("首頁")) {
					p.showFormWindow(EconomyGUI.homePage());
				}
			}
		}
	}
	
	private static boolean isDigit(String s) {
		Pattern pattern = Pattern.compile("^(\\d*)(\\.\\d*)?$");
		Matcher matcher = pattern.matcher(s);
		return matcher.matches();
	}
}
