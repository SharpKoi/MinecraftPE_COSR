package cosr.economy.job;

import java.util.Calendar;
import java.util.Date;

public class JobRecorder {
	
	private Calendar c;
	private int times;
	private int count;
	
	public JobRecorder() {
		this(0);
	}
	
	public JobRecorder(int times) {
		this(Calendar.getInstance(), times);
	}
	
	public JobRecorder(Calendar c, int times) {
		this.c = c;
		this.times = times;
	}
	
	public int getTimes() {
		return times;
	}
	
	public void setTimes(int times) {
		this.times = times;
	}
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public Calendar getCalendar() {
		return c;
	}
	
	public void setCalendar(Date date) {
		this.c.setTime(date);
	}
	
	public void grandAchvTo(String player) {
		if(times == 20) {
			//TODO: grand HARDWORKER
		}
		else if(times == 50) {
			//TODO: grand WORKMADMAN
		}
		else if(times == 100) {
			//TODO: grand EXTREMEWORKMAD
		}
		else if(times == 200) {
			//TODO: grand LEGENDWORKMAD
		}
	}

	public boolean isKeepWorking(Date currentTime) {
		Calendar current = Calendar.getInstance();
		current.setTime(currentTime);
		
		if(c.get(Calendar.YEAR) == current.get(Calendar.YEAR) && c.get(Calendar.MONTH) == current.get(Calendar.MONTH) &&
				c.get(Calendar.DATE) == current.get(Calendar.DATE) && c.get(Calendar.HOUR) == current.get(Calendar.HOUR)) {
			
			return (current.get(Calendar.MINUTE) - c.get(Calendar.MINUTE) <= 10);
		}else
			return false;
	}
	
	public void timesIncreasing() {
		times += 1;
	}
	
	public void countIncreasing() {
		count += 1;
	}
}
