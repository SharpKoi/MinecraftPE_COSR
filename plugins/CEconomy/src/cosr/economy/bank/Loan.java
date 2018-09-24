package cosr.economy.bank;

public class Loan {
	
	private double money;
	private int days;
	
	public Loan() {
		this(0.0, 0);
	}
	
	public Loan(double money) {
		this(money, 0);
	}
	
	public Loan(double money, int days) {
		this.money = money;
		this.days = days;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}
	
	public void addLoan(double loan) {
		this.money += loan;
	}
	
	public void deLoan(double loan) {
		this.money -= loan;
	}
	
	public void dayIncreasing() {
		this.days++;
	}
}
