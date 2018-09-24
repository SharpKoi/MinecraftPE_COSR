package cosr.vip;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import cosr.roleplay.CRolePlay;
import cosr.roleplay.gcollection.Title;

public class VIP {
	
	public enum VIPRank {
		VIP1(CVIP.getVipConfig().getString("vip1", "魔女"), 1, 100, "WITCH", TextFormat.AQUA),
		VIP2(CVIP.getVipConfig().getString("vip2", "魔女教傳教士"), 2, 200, "MISSIONARY", TextFormat.YELLOW),
		VIP3(CVIP.getVipConfig().getString("vip3", "魔女教樞機"), 3, 400, "CARDO", TextFormat.LIGHT_PURPLE),
		VIP4(CVIP.getVipConfig().getString("vip4", "魔女教司教"), 4, 800, "BISHOP", TextFormat.GOLD),
		VIPMAX(CVIP.getVipConfig().getString("vip5", "魔女教教主"), 5, 1600, "ARCHBISHOP", TextFormat.RED);
		
		private String name;
		private String rewardTitle;
		private final int level;
		private TextFormat color;

		private int levelUpExp;

		private VIPRank() {
			this("Unknown", 0, 9999, "UNKNOWN", TextFormat.GRAY);
		}
		
		private VIPRank(String name, int level, int levelUpExp, String rewardTitle, TextFormat color) {
			this.name = name;
			this.level = level;
			this.levelUpExp = levelUpExp;
			this.rewardTitle = rewardTitle;
			this.color = color;
		}

		public String getName() {
			return name;
		}
		
		public int getLevel() {
			return level;
		}

		public int getLevelUpExp() {
			return levelUpExp;
		}

		public String getRewardTitleHead() {
			return rewardTitle;
		}
		
		public TextFormat getColor() {
			return color;
		}
		
		public VIPRank getNext() {
			switch(this) {
				case VIP1: 
					return VIP2;
				case VIP2:
					return VIP3;
				case VIP3:
					return VIP4;
				case VIP4:
					return VIPMAX;
				case VIPMAX:
					return null;
				default:
					return VIP1;
			}
		}
	}
	
	private String owner;
	private VIPRank rank;
	private int exp;
	private Calendar date;
	private Calendar lastLoginDate;
	private int daysVia;
	private boolean enable;
	
	public VIP() {
		this("None", Calendar.getInstance());
		this.lastLoginDate = Calendar.getInstance();
	}
	
	public VIP(String owner) {
		this.owner = owner;
		this.date = Calendar.getInstance();
		this.lastLoginDate = Calendar.getInstance();
		this.loadFor(owner);
	}
	
	public VIP(String owner, VIPRank rank) {
		this(owner, rank, Calendar.getInstance());
	}
	
	public VIP(String owner, Calendar date) {
		this(owner, VIPRank.VIP1, date);
	}
	
	public VIP(String owner, VIPRank rank, Calendar date) {
		this(owner, rank, 0, date, 0, true);
	}
	
	public VIP(String owner, VIPRank rank, Calendar date, int daysVia) {
		this(owner, rank, 0, date, daysVia, true);
	}
	
	public VIP(String owner, VIPRank rank, int exp, Calendar date, int daysVia, boolean enable) {
		this.date = Calendar.getInstance();
		lastLoginDate = Calendar.getInstance();
		this.owner = owner;
		this.rank = rank;
		this.exp = exp;
		this.date = date;
		this.daysVia = daysVia;
		this.enable = enable;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public VIPRank getRank() {
		return rank;
	}

	public void setRank(VIPRank rank) {
		this.rank = rank;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public void rankUp() {
		this.rank = this.rank.getNext();
	}
	
	public void checkRankUp() {
		if(this.exp >= rank.getLevelUpExp()) {
			if(this.rank.getNext() != null) {
				this.exp -= this.rank.getLevelUpExp();
				rankUp();
				try {
					grantTitle();
				}catch(FileNotFoundException e) {
					e.printStackTrace();
				}
				checkRankUp();
			}else {
				this.exp = this.rank.getLevelUpExp() - 1;
				return;
			}
		}
	}
	
	public void addExp(int exp) {
		this.exp += exp;
		checkRankUp();
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}
	
	public int getDaysVia() {
		return daysVia;
	}

	public void setDaysVia(int daysVia) {
		this.daysVia = daysVia;
	}
	
	public void addDays(int days) {
		this.daysVia += days;
	}
	
	public void increaseDays() {
		this.daysVia++;
	}
	
	public void start() {
		this.enable = true;
	}
	
	public void stop() {
		this.enable = false;
	}
	
	public boolean isEnable() {
		return enable;
	}

	public Calendar getLastLoginDate() {
		return lastLoginDate;
	}
	
	public void grantTitle() throws FileNotFoundException {
		if(CRolePlay.getTitleMap().containsKey(rank.getRewardTitleHead()))
			CRolePlay.getTitleMap().get(rank.getRewardTitleHead()).grantTo(owner);
		else 
			new Title(rank.getRewardTitleHead()).grantTo(owner);
	}

	public ConfigSection dataSection() {
		return new ConfigSection() {
			private static final long serialVersionUID = 1L;
			{
				set("rank", rank.name());
				set("exp", exp);
				set("date", new SimpleDateFormat("yyyy-MM-dd").format(date.getTime()));
				set("days", daysVia);
				set("last-login", new SimpleDateFormat("yyyy-MM-dd").format(lastLoginDate.getTime()));
				set("enable", enable);
			}
		};
	}
	
	public void loadFor(String owner) {
		File file = new File(CVIP.getInstance().getDataFolder(), "players" + File.separator + owner+".yml");
		if(file.exists()) {
			Config conf = new Config(file, Config.YAML);
			this.owner = owner;
			this.rank = VIPRank.valueOf(conf.getString("rank"));
			this.exp = conf.getInt("exp");
			try {
				if(conf.exists("date"))
					this.date.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(conf.getString("date")));
				else 
					this.date.setTime(new Date());
				if(conf.exists("last-login"))
					this.lastLoginDate.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(conf.getString("last-login")));
				else 
					this.lastLoginDate.setTime(new Date());
			} catch (ParseException e) {
				this.date = Calendar.getInstance();
			}
			this.daysVia = conf.getInt("days");
			this.enable = conf.getBoolean("enable");
		}
	}
	
	public String listData() {
		Calendar deadline = Calendar.getInstance();
		deadline.setTime(date.getTime());
		int deadDate = deadline.get(Calendar.DATE) + CVIP.getVipConfig().getInt("enable-days", 30);
		deadline.set(Calendar.DATE, deadDate);
		//if(deadDate >= )
		
		return TextFormat.RESET + "--- 我的VIP --- \n"
				+ TextFormat.RESET + "================\n"
				+ TextFormat.RESET + "目前等級: " + rank.getColor() + rank.getName() + "("+rank.name()+")\n"
				+ TextFormat.RESET + "目前經驗: " + exp + "/" + rank.getLevelUpExp() + "\n"
				+ TextFormat.RESET + "經過天數: " + daysVia + "\n"
				+ TextFormat.RESET + "有效期限: " + new SimpleDateFormat("yyyy-MM-dd").format(deadline.getTime());
	}
}
