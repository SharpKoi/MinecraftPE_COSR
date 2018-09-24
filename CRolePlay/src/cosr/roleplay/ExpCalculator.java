package cosr.roleplay;

public class ExpCalculator {

	private int mineCount;
	private int mobKillCount;
	
	public ExpCalculator() {
		this.mineCount = 0;
		this.mobKillCount = 0;
	}
	
	public ExpCalculator(int mineCount, int mobKillCount) {
		this.mineCount = mineCount;
		this.mobKillCount = mobKillCount;
	}

	public int getMineCount() {
		return mineCount;
	}

	public void setMineCount(int mineCount) {
		this.mineCount = mineCount;
	}

	public int getMobKillCount() {
		return mobKillCount;
	}

	public void setMobKillCount(int mobKillCount) {
		this.mobKillCount = mobKillCount;
	}
	
	public void addMineCount() {
		this.mineCount += 1;
	}
	
	public void addMineCount(int count) {
		this.mineCount += count;
	}
	
	public void addMobKillCount() {
		this.mobKillCount += 1;
	}
	
	public void addMobKillCount(int count) {
		this.mobKillCount += count;
	}
	
	public int getMineExp() {
		int exp = (int) Math.ceil((Math.PI/13.89) * Math.pow(1.0009, this.mineCount) + 3);
		int maxExp = CRolePlay.getPropertiesConfig().getInt("player_level.max_mine_exp", 80);
		return (exp > maxExp)? maxExp : exp;
	}
	
	public int getMobKillExp() {
		int exp = (int) Math.ceil(15 + Math.pow(1.0015, this.mobKillCount));
		int maxExp = CRolePlay.getPropertiesConfig().getInt("player_level.max_mob_exp", 100);
		return (exp > maxExp)? maxExp : exp;
	}
}
