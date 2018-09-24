package cosr.economy.bank;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import cosr.economy.CEconomy;

public class CBank {
	
	public static final String BANKPATH = "CBank" + File.separator + "CBank.yml";
	
	private static Calendar date = Calendar.getInstance();
	private static HashMap<String, Loan> player_Loan = new HashMap<String, Loan>();			//玩家-貸款的錢
	private static HashMap<String, Float> player_Deposit = new HashMap<String, Float>();		//玩家-儲存的金錢
	private static float interest_rate;
	
	public static final String infoTitle() {
		return TextFormat.RESET + (TextFormat.BOLD + (TextFormat.WHITE + "[") + (TextFormat.AQUA + "CBank") + (TextFormat.WHITE + "]")) + TextFormat.RESET;
	}

	public static float getInterestRate() {
		return interest_rate;
	}
	
	public static Calendar getLastUpdateDate() {
		return date;
	}
	
	public static String dateForm() {
		return (new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date.getTime()));
	}

	public static void setInterestRate(float interest_rate) {
		CBank.interest_rate = interest_rate;
	}

	public static HashMap<String, Loan> getLoanMap() {
		return player_Loan;
	}

	public static HashMap<String, Float> getDepositMap() {
		return player_Deposit;
	}
	
	public static void putLoan(String player, float loan) {
		player_Loan.put(player, new Loan(loan));
	}
	
	public static void addLoan(String player, float value) {
		if(player_Loan.containsKey(player)) {
			player_Loan.get(player).addLoan(value);
		}else {
			player_Loan.put(player, new Loan(value));
		}
	}
	
	public static void deLoan(String player, float value) {
		if(player_Loan.containsKey(player)) {
			player_Loan.get(player).deLoan(value);
		}
	}
	
	public static void putDeposit(String player, float deposit) {
		player_Deposit.put(player, deposit);
	}
	
	public static void addDeposit(String player, float value) {
		if(player_Deposit.containsKey(player)) {
			player_Deposit.put(player, player_Deposit.get(player) + value);
		}else
			player_Deposit.put(player, value);
	}
	
	public static void deDeposit(String player, float value) {
		if(player_Deposit.containsKey(player))
			player_Deposit.put(player, player_Deposit.get(player) - value);
	}
	
	public static void update() {
		for(String p : player_Deposit.keySet()) {
			player_Deposit.put(p, CEconomy.getD2((player_Deposit.get(p) * (1 + interest_rate))));
		}
		for(String p : player_Loan.keySet()) {
			player_Loan.get(p).dayIncreasing();
		}
		saveData();
		
		float rate = (float) (Math.random() * 0.01);
		CBank.setInterestRate(rate);
	}
	
	public static void saveData() {
		date.setTime(new Date());
		Config conf = new Config(new File(CEconomy.getInstance().getDataFolder(), BANKPATH));
		
		conf.set("Date", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date.getTime()));
		conf.set("IR", CEconomy.getD2(interest_rate));
		
		conf.set("Deposit", new LinkedHashMap<String, Object>(player_Deposit));
		
		for(String pn : player_Loan.keySet()) {
			conf.set("Loan."+pn+".Value", player_Loan.get(pn).getMoney());
			conf.set("Loan."+pn+".Days", player_Loan.get(pn).getDays());
		}
		
		conf.save();
	}
	
	public static void loadData() {
		Config conf = new Config(new File(CEconomy.getInstance().getDataFolder(), BANKPATH));
		interest_rate = (float)conf.getDouble("IR", 0.01);
		
		try {
			Date lastDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(conf.getString("Date", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())));
			date.setTime(lastDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		ConfigSection section = (ConfigSection) conf.get("Deposit");
		if(section != null) {
			for(String pn : section.getAllMap().keySet()) {
				player_Deposit.put(pn, (float)section.getDouble(pn));
			}
		}
		
		section = (ConfigSection) conf.get("Loan");
		if(section != null) {
			for(String pn : section.getAllMap().keySet()) {
				player_Loan.put(pn, new Loan(section.getDouble(pn + ".Value"), section.getInt(pn + ".Days")));
			}
		}
	}
}
