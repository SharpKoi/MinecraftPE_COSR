package cosr.economy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cmen.essalg.CJEF;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import cosr.economy.bank.CBank;
import cosr.economy.job.CJob;
import cosr.economy.job.Job;
import cosr.economy.job.JobRecorder;
import cosr.economy.listener.EventListener;
import cosr.economy.listener.GuiEventListener;
import cosr.economy.task.BankUpdator;

public class CEconomy extends PluginBase {
	
	public static final String PDBPATH = "PlayerDataBase" + File.separator;
	
	//每個玩家的工作
	private static Map<String, CJob> player_Job = new HashMap<String, CJob>();
	
	private static CEconomy main = null;
	
	public static CEconomy getInstance() {
		return main;
	}

	public static Map<String, CJob> getJobMap() {
		return player_Job;
	}

	//TODO: onEnable()
	@Override
	public void onEnable() {
		main = this;
		
		CBank.loadData();
		
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		this.getServer().getPluginManager().registerEvents(new GuiEventListener(), this);
		
		//防止reload讀取不到已在線玩家的金錢、點券、工作
		if(this.getServer().getOnlinePlayers().size() > 0) {
			//TODO: 金錢載入寫成一個method
			for(Player player : this.getServer().getOnlinePlayers().values()) {
				File playerDataFile = new File(CEconomy.getInstance().getDataFolder(), CEconomy.PDBPATH+(player.getName()+".yml"));
				Config playerDataConfig = new Config(playerDataFile, Config.YAML);
				
				if(!playerDataConfig.exists("Money")) playerDataConfig.set("Money", 200.0);
				if(!playerDataConfig.exists("Point")) playerDataConfig.set("Point", 0.0);
				if(!playerDataConfig.exists("Job")) playerDataConfig.set("Job", "None");
				
				playerDataConfig.save();
				
				CMoney.getMoneyMap().put(player.getName(), (float)playerDataConfig.getDouble("Money"));
				CPoint.getPointMap().put(player.getName(), (float)playerDataConfig.getDouble("Point"));
				player_Job.put(player.getName(), new CJob(Job.getJob(playerDataConfig.getString("Job", "None")), new JobRecorder()));
			}
		}
		
		//初始化CPoint白名單
		Config wlConf = new Config(new File(this.getDataFolder(), CPoint.FILEPATH + "whitelist.yml"), Config.YAML);
		for(String member : wlConf.getStringList("whitelist")) {
			CPoint.registWL(member);
		}
		wlConf = null;
		
		//銀行每日更新
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());
		int dateDiff = CJEF.getDateDifferent(now, CBank.getLastUpdateDate());
		this.getServer().getScheduler().scheduleDelayedRepeatingTask(new BankUpdator(this), 1728000, 1728000);
		if(dateDiff != 0) {
			for(int i = 1; i <= dateDiff; i++) {
				for(String p : CBank.getDepositMap().keySet()) {
					CBank.getDepositMap().put(p, CEconomy.getD2((CBank.getDepositMap().get(p) * (1 + CBank.getInterestRate()))));
				}
				for(String p : CBank.getLoanMap().keySet()) {
					CBank.getLoanMap().get(p).dayIncreasing();
				}
			}
		}
	}

	//TODO: onDisable()
	/*
	 * 伺服器關閉時將儲存所有資料至檔案中</br>
	 * 
	 * @see cn.nukkit.plugin.PluginBase#onDisable()
	 */
	@Override
	public void onDisable() {
		//儲存玩家金錢
		for(String p : CMoney.getMoneyMap().keySet()) {
			Config conf = new Config(new File(this.getDataFolder(), PDBPATH + p+".yml"), Config.YAML);
			conf.set("Money", CMoney.getMoneyMap().get(p));
			
			conf.save();
		}
		
		//儲存玩家點券
		for(String p : CPoint.getPointMap().keySet()) {
			Config conf = new Config(new File(this.getDataFolder(), PDBPATH + p+".yml"), Config.YAML);
			conf.set("Point", CPoint.getPointMap().get(p));
			
			conf.save();
		}
		
		//儲存玩家工作
		for(String p : player_Job.keySet()) {
			Config conf = new Config(new File(this.getDataFolder(), PDBPATH + p+".yml"), Config.YAML);
			conf.set("Job", player_Job.get(p).getJob().getName());
			
			conf.save();
		}
		
		//儲存銀行狀態
		CBank.saveData();
		
		//儲存CPoint白名單
		Config wlConf = new Config(new File(this.getDataFolder(), CPoint.FILEPATH + "whitelist.yml"), Config.YAML);
		wlConf.set("whitelist", CPoint.whiteList);
		wlConf.save();
		wlConf = null;
	}

	//TODO: onCommand()
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch(cmd.getName()) {
			case "cec":
				if(args.length < 1) {
					return false;
				}
				
				if(args[0].equals("c$")) {
					
				}
				else if(args[0].equals("cshop")) {
					
				}
				else if(args[0].equals("cbank")) {
					
				}
				else if(args[0].equals("cjob")) {
					
				}
				else if(args[0].equals("ui")) {
					if(sender.isPlayer()) {
						Player p = (Player)sender;
						p.showFormWindow(EconomyGUI.homePage());
					}
				}
				else {
					return false;
				}
		
			case "c$":
				if(args.length < 1) return false;
				
				if(args[0].equals("help") || args[0].equals("h")) {
					sender.sendMessage(PluginInfo.moneyHelp());
				}
				else if(args[0].equals("give")) {
					if(args.length >= 3) {
						if(!isDigit(args[2])) {
							sender.sendMessage(PluginInfo.moneyInfoTitle() + TextFormat.RED + "數字格式錯誤! 請檢查後重新輸入!");
							return true;
						}
						try {
							float money = (float) Double.parseDouble(args[2]);
							if(sender.isPlayer()) {
								CMoney.giveMoney(sender.getName(), args[1], money);
							}else {
								CMoney.giveMoney(args[1], money);
							}
							sender.sendMessage(PluginInfo.moneyInfoTitle() + TextFormat.GREEN + "成功給予" + args[1] + "玩家" + args[2] + "元金幣!");
						}catch(FileNotFoundException err) {
							sender.sendMessage(PluginInfo.moneyInfoTitle() + TextFormat.RED + "找不到該玩家");
						}
					}else 
						sender.sendMessage(PluginInfo.moneyInfoTitle() + TextFormat.RED + "請指定玩家及金錢");
				}
				else if(args[0].equals("wallet") || args[0].equals("w") || args[0].equals("my")){
					sender.sendMessage(PluginInfo.moneyInfoTitle() + TextFormat.YELLOW + "您身上還有" + CMoney.getMoneyMap().get(sender.getName()) + "元");
				}else
					return false;
				
				break;
			case "cp$": 
				if(args.length < 1) return false;
				
				if(args[0].equals("help") || args[0].equals("h")) {
					
				}
				else if(args[0].equals("give")) {
					if(args.length >= 3) {
						if(!CPoint.isWL(sender.getName())) {
							sender.sendMessage(CPoint.infoTitle() + TextFormat.RED + "您沒有足夠的權限可以執行此指令!");
							return true;
						}
						if(!isDigit(args[2])) {
							sender.sendMessage(PluginInfo.moneyInfoTitle() + TextFormat.RED + "數字格式錯誤! 請檢查後重新輸入!");
							return true;
						}
						try {
							float point = (float) Double.parseDouble(args[2]);
							if(sender.isPlayer()) {
								CPoint.givePoint(sender.getName(), args[1], point);
							}else {
								CPoint.givePoint(args[1], point);
							}
							sender.sendMessage(PluginInfo.moneyInfoTitle() + TextFormat.GREEN + 
									"成功給予" + args[1] + "玩家" + args[2] + "元" + CPoint.config.getString("name", "點券"));
						}catch(FileNotFoundException err) {
							sender.sendMessage(PluginInfo.moneyInfoTitle() + TextFormat.RED + "找不到該玩家");
						}
					}else 
						sender.sendMessage(PluginInfo.moneyInfoTitle() + TextFormat.RED + "請指定玩家及金錢");
				}
				else if(args[0].equals("wallet") || args[0].equals("w") || args[0].equals("my")){
					sender.sendMessage(PluginInfo.moneyInfoTitle() + TextFormat.YELLOW + "您身上還有" + 
							CMoney.getMoneyMap().get(sender.getName()) + "元" + CPoint.config.getString("name", "點券"));
				}else
					return false;
				
				break;
			case "cbank":
				if(args.length < 1) {
					if(sender.isPlayer()) {
						sender.sendMessage(PluginInfo.bankStatus((Player) sender));
					}else {
						sender.sendMessage(CBank.infoTitle() + TextFormat.RED + "請在遊戲中執行此指令");
					}
					return true;
				}
				if(args[0].equals("help") || args[0].equals("h")) {
					sender.sendMessage(PluginInfo.bankHelp());
				}
				else if(args[0].equals("store")) {
					//args[1] dollar
					if(sender.isPlayer()) {
						if(args.length < 2) {
							sender.sendMessage(CBank.infoTitle() + TextFormat.RED + "請指定金錢數量");
							return true;
						}
						if(!isDigit(args[1])) {
							sender.sendMessage(CBank.infoTitle() + TextFormat.RED + "請輸入正確的金錢數字格式!");
							return true;
						}
						Player p = (Player) sender;
						double money = Double.parseDouble(args[1]);
						if(CBank.getDepositMap().containsKey(p.getName())) {
							CBank.addDeposit(p.getName(), (float) money);
						}else {
							CBank.putDeposit(p.getName(), (float) money);
						}
						CMoney.getMoneyMap().put(p.getName(), (float)(CMoney.getMoneyMap().get(p.getName()) - money));
						p.sendMessage(CBank.infoTitle() + TextFormat.GREEN + "成功儲存" + money + "元金幣至銀行!");
					}else
						sender.sendMessage(CBank.infoTitle() + TextFormat.RED + "請在遊戲中執行此指令");
				}
				else if(args[0].equals("draw")) {
					//args[1] dollar
					if(sender.isPlayer()) {
						if(args.length < 2) {
							sender.sendMessage(CBank.infoTitle() + TextFormat.RED + "請指定金錢數量");
							return true;
						}
						if(!isDigit(args[1])) {
							sender.sendMessage(CBank.infoTitle() + TextFormat.RED + "請輸入正確的金錢數字格式!");
							return true;
						}
						Player p = (Player) sender;
						if(!CBank.getDepositMap().containsKey(p.getName())) {
							p.sendMessage(CBank.infoTitle() + TextFormat.GRAY + "您沒有在銀行儲存任何的現金!");
							return true;
						}else {
							if(CBank.getDepositMap().get(p.getName()) <= 0) {
								p.sendMessage(CBank.infoTitle() + TextFormat.GRAY + "您沒有在銀行儲存任何的現金!");
								return true;
							}
						}
						double money = Math.min(Double.parseDouble(args[1]), CBank.getDepositMap().get(p.getName()));
						
						CBank.deDeposit(p.getName(), (float) money);
						CMoney.getMoneyMap().put(p.getName(), (float)(CMoney.getMoneyMap().get(p.getName()) + money));
						if(CBank.getDepositMap().get(p.getName()) <= 0) {
							CBank.getDepositMap().remove(p.getName());
						}
						p.sendMessage(CBank.infoTitle() + TextFormat.GREEN + "成功從您的銀行帳戶中拿取" + money + "元金幣!");
					}else
						sender.sendMessage(CBank.infoTitle() + TextFormat.RED + "請在遊戲中執行此指令");
				}
				else if(args[0].equals("loan")) {
					//args[1] dollar
					if(sender.isPlayer()) {
						if(args.length < 2) {
							sender.sendMessage(CBank.infoTitle() + TextFormat.RED + "請指定金錢數量");
							return true;
						}
						if(!isDigit(args[1])) {
							sender.sendMessage(CBank.infoTitle() + TextFormat.RED + "請輸入正確的金錢數字格式!");
							return true;
						}
						
						Player p = (Player) sender;
						double loan = Double.parseDouble(args[1]);
						
						if(loan <= CMoney.getMoneyMap().get(p.getName()) * 0.9) {
							if(CBank.getLoanMap().containsKey(p.getName())) {
								if(CBank.getLoanMap().get(p.getName()).getMoney() > 0) {
									p.sendMessage(CBank.infoTitle() + TextFormat.RED + "您這期的貸款尚未繳清，請先繳清後再申請下一期貸款!");
									return true;
								}
							}
							CBank.putLoan(p.getName(), (float) loan);
							CMoney.getMoneyMap().put(p.getName(), (float)(CMoney.getMoneyMap().get(p.getName()) + loan));
							p.sendMessage(CBank.infoTitle() + TextFormat.GREEN + "成功向銀行貸款" +loan + "元金幣!");
						}else {
							p.sendMessage(CBank.infoTitle() + TextFormat.RED + "抱歉! 您不得申請超過您身上金錢90%的貸款\n" + 
									"您至多只能申請" + CMoney.getMoneyMap().get(p.getName()) * 0.90 + "元的貸款");
						}
					}else
						sender.sendMessage(CBank.infoTitle() + TextFormat.RED + "請在遊戲中執行此指令");
				}
				else if(args[0].equals("pb")) {
					//args[1] dollar
					if(sender.isPlayer()) {
						if(args.length < 2) {
							sender.sendMessage(CBank.infoTitle() + TextFormat.RED + "請指定金錢數量");
							return true;
						}
						if(!isDigit(args[1])) {
							sender.sendMessage(CBank.infoTitle() + TextFormat.RED + "請輸入正確的金錢數字格式!");
							return true;
						}
						
						Player p = (Player) sender;
						if(!CBank.getLoanMap().containsKey(p.getName())) {
							p.sendMessage(CBank.infoTitle() + TextFormat.GRAY + "恭喜! 您沒有任何貸款，無須執行此指令");
							return true;
						}
						double loan = Math.min(Double.parseDouble(args[1]), CBank.getLoanMap().get(p.getName()).getMoney());
						
						CBank.deLoan(p.getName(), (float) loan);
						CMoney.getMoneyMap().put(p.getName(), (float)(CMoney.getMoneyMap().get(p.getName()) - loan));
						
						if(CBank.getLoanMap().get(p.getName()).getMoney() <= 0) {
							p.sendMessage(CBank.infoTitle() + TextFormat.GREEN + "恭喜您已繳清這期的所有貸款!");
							CBank.getLoanMap().remove(p.getName());
						}else {
							p.sendMessage(CBank.infoTitle() + TextFormat.GREEN + "成功向銀行繳還" +loan + "元的貸款!");
						}
					}else
						sender.sendMessage(CBank.infoTitle() + TextFormat.RED + "請在遊戲中執行此指令");
				}
				else if(args[0].equals("stat")) {
					sender.sendMessage(CBank.infoTitle() + TextFormat.YELLOW + "銀行當前利率為: " + CBank.getInterestRate() * 100 + "%");
					sender.sendMessage(CBank.infoTitle() + TextFormat.YELLOW + "上次更新時間為: " + CBank.dateForm());
				}else 
					return false;
				break;
		
			case "cjob":
				if(args.length < 1) return false;
				
				if(args[0].equals("help")) {
					sender.sendMessage(PluginInfo.jobHelp());
				}
				else if(args[0].equals("list")) {
					sender.sendMessage(Job.formList());
				}
				else if(args[0].equals("my")) {
					if(sender.isPlayer()) {
						Player p = (Player) sender;
						CJob myjob = player_Job.get(p.getName());
						p.sendMessage(myjob.information());
					}else {
						sender.sendMessage(CJob.infoTitle() + TextFormat.RED + "請在遊戲中執行此指令");
					}
				}
				else if(args[0].equals("get")) {
					if(sender.isPlayer()) {
						//args[1] Job
						if(args.length < 2) {
							sender.sendMessage(CJob.infoTitle() + TextFormat.RED + "請輸入正確的指令格式");
							return false;
						}
						Player p = (Player) sender;
						
						if(Job.getJob(args[1]).equals(Job.NONE)) {
							p.sendMessage(CJob.infoTitle() + TextFormat.RED + "無法取得該工作，請輸入 /cjob list查看當前工作列表");
						}else {
							if(!player_Job.get(p.getName()).getJob().equals(Job.NONE)) {
								p.sendMessage(CJob.infoTitle() + TextFormat.GRAY + "已離開了您原先的工作: " + player_Job.get(p.getName()).getJob().getName());
							}
							player_Job.put(p.getName(), new CJob(Job.getJob(args[1]), new JobRecorder()));
							p.sendMessage(CJob.infoTitle() + TextFormat.GREEN + "已成功申請工作: " + TextFormat.AQUA + Job.getJob(args[1]).getName());
						}
					}
				}
				else if(args[0].equals("quit")) {
					if(sender.isPlayer()) {
						Player p = (Player) sender;
						if(player_Job.get(p.getName()).getJob().equals(Job.NONE)) {
							p.sendMessage(CJob.infoTitle() + TextFormat.GRAY + "您當前無任何工作");
						}else {
							p.sendMessage(CJob.infoTitle() + TextFormat.GRAY + "您已辭掉您當前的工作: " + player_Job.get(p.getName()).getJob().getName());
							player_Job.get(p.getName()).setJob(Job.NONE);
						}
					}else
						sender.sendMessage(CJob.infoTitle() + TextFormat.RED + "請在遊戲中執行此指令");
				}else
					return false;
				
				break;
		}
		return true;
	}
	
	public static float getD2(double value) {
		return (float)Math.round(value*100)/100;
	}
	
	private static boolean isDigit(String s) {
		Pattern pattern = Pattern.compile("^(\\d*)(\\.\\d*)?$");
		Matcher matcher = pattern.matcher(s);
		return matcher.matches();
	}
	
	public void saveAll() {
		
	}
}
