package cosr.economy.math;

import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;

public class CMathLib {
	
	public static double toRadians(double angle) {
		return (angle * Math.PI / 180);
	}
	
	public static double toAngle(double radians) {
		return (radians * 180 / Math.PI);
	}
	
	public static Vector3 getUnitVector(double yaw) {
		if(Math.tan(toRadians(yaw)) <= 1 && Math.tan(toRadians(yaw)) >= -1) {
			return (yaw <= 45 || yaw >= 315)? new Vector3(0, 0, -1) : new Vector3(0, 0, 1);
		}else {
			return (yaw >= 45 && yaw <= 135)? new Vector3(1, 0, 0) : new Vector3(-1, 0, 0);
		}
	}
	
	public static BlockFace getShopSignDirection(double playerYaw) {
		if(Math.tan(toRadians(playerYaw)) <= 1 && Math.tan(toRadians(playerYaw)) >= -1) {
			return (playerYaw <= 45 || playerYaw >= 315)? BlockFace.SOUTH.getOpposite() : BlockFace.SOUTH;
		}else {
			return (playerYaw >= 45 && playerYaw <= 135)? BlockFace.WEST.getOpposite() : BlockFace.WEST;
		}
	}
}
