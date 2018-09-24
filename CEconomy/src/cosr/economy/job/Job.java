package cosr.economy.job;

import cn.nukkit.utils.TextFormat;

public enum Job {
	LAMBERJACK("Lamberjack", "每當砍下64個原木則獲得30元", 64, 30.0),
	MINER("Miner", "每當挖掘64個鵝卵石則獲得15元", 64, 15.0),
	GARDENER("Gardener", "每當挖下64個樹葉方塊則獲得20元", 64, 20.0),
	DIGGER("Digger", "每當挖下64個泥土塊則獲得10元", 64, 10.0),
	NONE("None", "自由無業者，又或者是學生黨(無法藉由申請取得)", null, 0.0);
	
	private String name;
	private String description;
	private Object requirement;
	private double money;
	
	private Job(String name, String description, Object requirement, double money) {
		this.name = name;
		this.description = description;
		this.requirement = requirement;
		this.money = money;
	}
	
	public String getName() {
		return name;
	}
	
	public String chineseName() {
		switch(this) {
			case LAMBERJACK:
				return "伐木工";
			case MINER:
				return "礦工";
			case GARDENER:
				return "園丁";
			case DIGGER:
				return "挖掘工";
			case NONE:
				return "無業者";
			default:
				return "神秘怪客";
		}
	}
	
	public String getDescription() {
		return description;
	}
	
	public Object getRequirement() {
		return requirement;
	}
	
	public double getMoney() {
		return money;
	}

	public static Job getJob(String name) {
		if(name.equalsIgnoreCase("Lamberjack")) {
			return Job.LAMBERJACK;
		}
		if(name.equalsIgnoreCase("Miner")) {
			return Job.MINER;
		}
		if(name.equalsIgnoreCase("Gardener")) {
			return Job.GARDENER;
		}
		if(name.equalsIgnoreCase("Digger")) {
			return Job.DIGGER;
		}
		//else
		return Job.NONE;
	}
	
	public static String formList() {
		String title = TextFormat.RESET + (TextFormat.BOLD + (TextFormat.GREEN + "<<===== 工作列表 =====>>\n"));
		String jobList = "";
		for(Job job : Job.values()) {
			jobList += TextFormat.RESET + 
					(TextFormat.DARK_GREEN + job.getName()) + "(" + job.chineseName() + "): " + 
					TextFormat.RESET + job.getDescription() + "\n";
		}
		
		return title + jobList;
	}
	
	public void saveConfig() {
		//Only convenient for reading
	}
}