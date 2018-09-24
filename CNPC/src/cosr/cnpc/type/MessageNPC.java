package cosr.cnpc.type;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;

public class MessageNPC extends NPCType {
	
	private String message;
	private String npcName;

	public MessageNPC() {
		this("hello", "");
	}
	
	public MessageNPC(String message) {
		this(message, "");
	}
	
	public MessageNPC(String message, String npcName) {
		this.name = "MessageNPC";
		this.message = message;
		this.npcName = npcName;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getNpcName() {
		return npcName;
	}

	@Override
	public boolean execute(Player trigger) {
		trigger.sendMessage((TextFormat.YELLOW+"[") + (TextFormat.AQUA+npcName) + (TextFormat.YELLOW+"]") + TextFormat.RESET+message);
		return true;
	}

}
