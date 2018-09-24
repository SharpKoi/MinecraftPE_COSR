package cosr.economy.listener;

import java.io.File;
import java.util.Date;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.utils.*;
import cosr.economy.CEconomy;
import cosr.economy.CMoney;
import cosr.economy.CPoint;
import cosr.economy.job.CJob;
import cosr.economy.job.Job;
import cosr.economy.job.JobRecorder;

public class EventListener implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		
		File playerDataFile = new File(CEconomy.getInstance().getDataFolder(), CEconomy.PDBPATH+(player.getName()+".yml"));
		Config playerDataConfig = new Config(playerDataFile, Config.YAML);
		
		if(!playerDataConfig.exists("Money")) playerDataConfig.set("Money", 200.0);
		if(!playerDataConfig.exists("Point")) playerDataConfig.set("Point", 0.0);
		if(!playerDataConfig.exists("Job")) playerDataConfig.set("Job", "None");
		
		playerDataConfig.save();
		
		CMoney.getMoneyMap().put(player.getName(), (float)playerDataConfig.getDouble("Money"));
		CPoint.getPointMap().put(player.getName(), (float)playerDataConfig.getDouble("Point"));
		CEconomy.getJobMap().put(player.getName(), new CJob(Job.getJob(playerDataConfig.getString("Job", "None")), new JobRecorder()));
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		Config playerDataConfig = new Config(new File(CEconomy.getInstance().getDataFolder(), CEconomy.PDBPATH+(player.getName()+".yml")), Config.YAML);
		if(CMoney.getMoneyMap().containsKey(player.getName())) {
			playerDataConfig.set("Money", CMoney.getMoneyMap().get(player.getName()));
			CMoney.getMoneyMap().remove(player.getName());
		}
		
		if(CPoint.getPointMap().containsKey(player.getName())) {
			playerDataConfig.set("Point", CPoint.getPointMap().get(player.getName()));
			CPoint.getPointMap().remove(player.getName());
		}
			
		if(CEconomy.getJobMap().containsKey(player.getName())) {
			playerDataConfig.set("Job", (CEconomy.getJobMap().get(player.getName())).getJob().getName());
			CEconomy.getJobMap().remove(player.getName());
		}
		
		playerDataConfig.save();
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		
		if(player.isSurvival()) {
			CJob cjob = CEconomy.getJobMap().get(player.getName());
			JobRecorder jobRecorder = cjob.getRecorder();
			switch(cjob.getJob()) {
				case LAMBERJACK:
					if(block.getId() == 17 || block.getId() == 162) {
						jobRecorder.countIncreasing();
						if(jobRecorder.getCount() >= Integer.parseInt(Job.LAMBERJACK.getRequirement().toString())) {
							cjob.earnMoney(player.getName());
							player.sendMessage(CJob.infoTitle() + TextFormat.GREEN + "經過一番努力, 您獲得了" + cjob.getJob().getMoney() + "元的工作報酬!");
							if(jobRecorder.isKeepWorking(new Date())) {
								jobRecorder.timesIncreasing();
								jobRecorder.grandAchvTo(player.getName());
							}
							return;
						}
					}
					break;
				case MINER:
					if(block.getId() == 4 || block.getId() == 1) {
						cjob.getRecorder().countIncreasing();
						if(cjob.getRecorder().getCount() >= Integer.parseInt(Job.MINER.getRequirement().toString())) {
							cjob.earnMoney(player.getName());
							player.sendMessage(CJob.infoTitle() + TextFormat.GREEN + "經過一番努力, 您獲得了" + cjob.getJob().getMoney() + "元的工作報酬!");
							if(jobRecorder.isKeepWorking(new Date())) {
								jobRecorder.timesIncreasing();
								jobRecorder.grandAchvTo(player.getName());
							}
							return;
						}
					}
					break;
				case GARDENER: 
					if(block.getId() == 18) {
						cjob.getRecorder().countIncreasing();
						if(cjob.getRecorder().getCount() >= Integer.parseInt(Job.GARDENER.getRequirement().toString())) {
							cjob.earnMoney(player.getName());
							player.sendMessage(CJob.infoTitle() + TextFormat.GREEN + "經過一番努力, 您獲得了" + cjob.getJob().getMoney() + "元的工作報酬!");
							if(jobRecorder.isKeepWorking(new Date())) {
								jobRecorder.timesIncreasing();
								jobRecorder.grandAchvTo(player.getName());
							}
							return;
						}
					}
					break;
				case DIGGER:
					if(block.getId() == 2 || block.getId() == 3) {
						cjob.getRecorder().countIncreasing();
						if(cjob.getRecorder().getCount() >= Integer.parseInt(Job.DIGGER.getRequirement().toString())) {
							cjob.earnMoney(player.getName());
							player.sendMessage(CJob.infoTitle() + TextFormat.GREEN + "經過一番努力, 您獲得了" + cjob.getJob().getMoney() + "元的工作報酬!");
							if(jobRecorder.isKeepWorking(new Date())) {
								jobRecorder.timesIncreasing();
								jobRecorder.grandAchvTo(player.getName());
							}
							return;
						}
					}
					break;
				default:
					return;
			}
		}
	}
}
