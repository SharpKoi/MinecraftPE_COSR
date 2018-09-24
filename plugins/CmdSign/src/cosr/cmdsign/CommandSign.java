package cosr.cmdsign;

import java.io.File;

import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.command.CommandSender;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

public class CommandSign {
	
	public enum CmdExecutor {
		CONSOLE,
		PLAYER;
	}
	
	private static Main plugin;
	
	private String title;
	private String description;
	private String[] command;
	private String levelname;
	private String executor;
	private Vector3 coordinate;
	private Config csConf;
	
	public CommandSign() {
		this("None", "None", new String[]{"help"}, null, null, CmdExecutor.PLAYER);
	}
	
	public CommandSign(String title, String description, String[] cmd, CmdExecutor type) {
		this(title, description, cmd, null, null, type);
	}
	
	public CommandSign(String title, String description, String[] cmd, String levelname, Vector3 coordinate, CmdExecutor type) {
		plugin = Main.getInstance();
		this.csConf = new Config(new File(plugin.getDataFolder(), title+".yml"), Config.YAML);
		this.title = title;
		this.description = description;
		this.command = cmd;
		this.levelname = levelname;
		this.coordinate = coordinate;
		this.executor = (type.equals(CmdExecutor.CONSOLE))? "CONSOLE" : "PLAYER";
	}
	
	public static CommandSign loadFrom(String fileName) {
		plugin = Main.getInstance();
		if(fileName.endsWith(".yml")) {
			Config conf = new Config(new File(plugin.getDataFolder(), fileName), Config.YAML);
			CommandSign cmdsign = new CommandSign(fileName.replace(".yml", ""), conf.getString("Description"), conf.getString("Command").split("-"), 
					conf.getString("Level"), new Vector3(conf.getDouble("X"), conf.getDouble("Y"), conf.getDouble("Z")), 
					(conf.getString("Type").equals("CONSOLE"))? CmdExecutor.CONSOLE : CmdExecutor.PLAYER);

			return cmdsign;
		}
		else return null;
	}
	
	public void saveConfig() {
		String compound = "";
		csConf.set("Type", executor);
		csConf.set("Description", this.description);
		
		for(int i = 0; i < command.length; i++) {
			compound += this.command[i];
			if(i != command.length-1) compound += "-";
		}
		csConf.set("Command", compound);
		
		csConf.set("Level", this.levelname);
		csConf.set("X", this.coordinate.getX());
		csConf.set("Y", this.coordinate.getY());
		csConf.set("Z", this.coordinate.getZ());
		csConf.save();
	}
	
	public void keyOntoSign(BlockEntitySign sign) {
		String head, title, content;
		head = TextFormat.YELLOW + "[" + this.executor + "]";
		title = TextFormat.BOLD + this.title;
		content = this.description;
		
		sign.setText(head, title, content);
	}
	
	public void execute(CommandSender sender) {
		String cmdline = "";
		for(int i = 0; i < this.command.length; i++) {
			if(i == this.command.length-1) {
				cmdline += this.command[i];
				break;
			}
			cmdline += this.command[i] + " ";
		}
		plugin.getServer().dispatchCommand(sender, cmdline);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String[] getCommand() {
		return command;
	}

	public void setCommand(String[] command) {
		this.command = command;
	}

	public String getLevelname() {
		return levelname;
	}

	public void setLevelname(String levelname) {
		this.levelname = levelname;
	}
	
	public Vector3 getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Vector3 coordinate) {
		this.coordinate = coordinate;
	}
	
	public String getExecutorType() {
		return executor;
	}
	
	public void setExecutorType(CmdExecutor type) {
		switch(type) {
			case CONSOLE:
				this.executor = "CONSOLE";
				break;
			case PLAYER:
				this.executor = "PLAYER";
				break;
		}
	}
}
