package cosr.cnpc.type;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;

public class TeleportNPC extends NPCType {
	
	private Level to;
	private Vector3 target;
	
	public TeleportNPC() {
		this.name = "TeleportNPC";
		to = null;
		target = null;
	}
	
	public TeleportNPC(Position targetPos) {
		this(targetPos.getLevel(), new Vector3(targetPos.x, targetPos.y, targetPos.z));
	}
	
	public TeleportNPC(String levelName, Vector3 target) {
		this(Server.getInstance().getLevelByName(levelName), target);
	}
	
	public TeleportNPC(Level to, Vector3 target) {
		this.name = "TeleportNPC";
		this.to = to;
		if(target == null) 
			target = to.getSpawnLocation();
		else 
			this.target = target;
	}
	
	public Level getTo() {
		return to;
	}

	public void setTo(Level to) {
		this.to = to;
	}

	public Vector3 getTarget() {
		return target;
	}

	public void setTarget(Vector3 target) {
		this.target = target;
	}

	@Override
	public boolean execute(Player trigger) {
		return trigger.teleport(new Position(target.getX(), target.getY(), target.getZ(), to));
	}

}
