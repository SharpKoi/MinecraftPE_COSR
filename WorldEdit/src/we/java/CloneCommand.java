package we.java;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class CloneCommand extends Command {

	public CloneCommand() {
		super("clone", "clone blocks by using this command", "/clone", new String[] {"copy", "cln"});
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		
		
		return true;
	}
}
