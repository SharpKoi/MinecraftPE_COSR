package cosr.cnpc.type;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;

public class CommandNPC extends NPCType {

	private CommandSender sender;
	private String command;
	
	public CommandNPC() {
		this("");
	}
	
	public CommandNPC(String command) {
		this(Server.getInstance().getConsoleSender(), command);
	}
	
	public CommandNPC(String senderName, String command) {
		this(Server.getInstance().getPlayer(senderName), command);
	}
	
	public CommandNPC(CommandSender sender, String command) {
		this.name = "CommandNPC";
		this.sender = sender;
		this.command = command;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public CommandSender getSender() {
		return sender;
	}

	public void setSender(CommandSender sender) {
		this.sender = sender;
	}

	@Override
	public boolean execute(Player trigger) {
		return Server.getInstance().dispatchCommand(sender, command);
	}
}
