package cosr.cnpc.type;

import cn.nukkit.Player;

public abstract class NPCType {
	
	protected String name;
	
	public String getName() {
		return name;
	}

	public abstract boolean execute(Player trigger);
	
	public static NPCType forName(String name) {
		if(name.equalsIgnoreCase("CommandNPC")) {
			return new CommandNPC();
		}else if(name.equalsIgnoreCase("MessageNPC")) {
			return new MessageNPC();
		}else if(name.equalsIgnoreCase("TeleportNPC")) {
			return new TeleportNPC();
		}else
			return null;
	}
}
