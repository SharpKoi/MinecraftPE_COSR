package cosr.economy.job;

import java.io.File;
import java.io.FileNotFoundException;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import cosr.economy.CEconomy;
import cosr.economy.CMoney;

public class CJob {
	
	private JobRecorder recorder;
	private Job job;
	
	public CJob(Job job, JobRecorder recorder) {
		this.recorder = recorder;
		this.job = job;
	}
	
	public JobRecorder getRecorder() {
		return recorder;
	}

	public void setRecorder(JobRecorder recorder) {
		this.recorder = recorder;
	}

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}
	
	public void earnMoney(String player) {
		try {
			CMoney.giveMoney(player, this.job.getMoney());
		}catch (FileNotFoundException e) {
			//catch
		}
		this.recorder.setCount(0);
	}

	public static final String infoTitle() {
		return TextFormat.RESET + (TextFormat.BOLD + (TextFormat.WHITE + "[") + (TextFormat.AQUA + "CJob") + (TextFormat.WHITE + "]")) + TextFormat.RESET;
	}
	
	public String information() {
		return TextFormat.RESET + (TextFormat.GREEN + "您當前的工作為: ") + TextFormat.RESET + this.getJob().chineseName() + "\n" + 
				TextFormat.DARK_GREEN + "當前進度: " + TextFormat.RESET + this.getRecorder().getCount() + "/" + this.getJob().getRequirement().toString() +TextFormat.RESET + "\n" + 
				TextFormat.DARK_GREEN + "工作報酬: " + TextFormat.RESET + this.getJob().getMoney() + "\n";
	}
	
	public static CJob getCJobOfPlayer(String playerName) throws FileNotFoundException {
		if(CEconomy.getJobMap().containsKey(playerName)) {
			return CEconomy.getJobMap().get(playerName);
		}else {
			File file = new File(CEconomy.getInstance().getDataFolder(), CEconomy.PDBPATH + playerName+".yml");
			if(file.exists()) {
				Config conf = new Config(file, Config.YAML);
				return new CJob(Job.getJob(conf.getString("Job")), new JobRecorder());
			}else {
				throw new FileNotFoundException();
			}
		}
	}
}
