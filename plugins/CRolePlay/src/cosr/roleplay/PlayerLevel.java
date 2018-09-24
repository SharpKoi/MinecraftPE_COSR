package cosr.roleplay;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

public class PlayerLevel {
	//等級、所得經驗、升等門檻
	
	public static final String PARENTKEY = "LvSystem";
	
	private int lv;
	private ExpCalculator expCalculator;
	private int exp;
	private int lvUpExp;
	
	public PlayerLevel() {
		this.lv = CRolePlay.getPropertiesConfig().getInt("player_level.initial_level", 1);
		this.expCalculator = new ExpCalculator();
		this.exp = 0;
		this.lvUpExp = getLevelUpExp(lv);
	}
	
	public PlayerLevel(int lv) {
		this(lv, 0, getLevelUpExp(lv));
		this.expCalculator = new ExpCalculator();
	}
	
	public PlayerLevel(int lv, int exp, int lvUpExp) {
		this.lv = lv;
		this.expCalculator = new ExpCalculator();
		this.exp = exp;
		this.lvUpExp = lvUpExp;
	}
	
	public int getLv() {
		return lv;
	}

	public void setLv(int lv) {
		this.lv = lv;
	}
	
	public ExpCalculator getExpCalculator() {
		return expCalculator;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getLevelUpExp() {
		return lvUpExp;
	}
	
	public static int getLevelUpExp(int lv) {
		return (int) (2*((3.8*(Math.log10(lv) / Math.log10(2))+1320) + (10.8*Math.pow(lv, 2) + 11*lv + 100)) - 2000);
	}

	public void setLevelUpExp(int lvUpExp) {
		this.lvUpExp = lvUpExp;
	}
	
	public void expUp(int exp) {
		this.exp += exp;
	}
	
	public void levelUp() {
		this.lv += 1;
	}
	
	public void levelUp(int lv) {
		this.lv += lv;
	}

	public void updateLevelUpExp() {
		this.lvUpExp = getLevelUpExp(this.lv);
	}
	
	public void checkLevelUp() {
		updateLevelUpExp();
		if(this.exp >= lvUpExp) {
			if(this.lv < CRolePlay.getPropertiesConfig().getInt("player_level.max_level", 100)) {
				this.exp -= lvUpExp;
				this.lv += 1;
				this.updateLevelUpExp();
				checkLevelUp();
			}else {
				this.exp = this.lvUpExp - 1;
				return;
			}
		}
	}
	
	public void update(int upExp) {
		expUp(upExp);
		checkLevelUp();
	}
	
	public void mineExpUpdate() {
		expCalculator.addMineCount();
		update(expCalculator.getMineExp());
	}
	
	public void mobKillExpUpdate() {
		expCalculator.addMobKillCount();
		update(expCalculator.getMobKillExp());
	}
	
	public ConfigSection dataSection() {
		return new ConfigSection() {
			private static final long serialVersionUID = 1L;	//控制版本是否兼容
			{
				set("Lv", lv);
				set("Exp", exp);
				set("Max_Exp", lvUpExp);
			}
		};
	}
	
	public void loadData(Config config) {
		this.lv = config.getInt(PARENTKEY + ".Lv");
		this.exp = config.getInt(PARENTKEY + ".Exp");
		this.lvUpExp = config.getInt(PARENTKEY + ".Max_Exp");
	}
}
