package cosr.we.tool;

import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import we.java.Main;

public class QuickBuildTool {
	private Main plugin;
	public Level world;
	public Vector3 pos1;
	public Vector3 pos2;
	public int step;
	public Feature feature;
	public enum Feature {
		BUILD,
		MEASURE;
	}
	
	public QuickBuildTool(Level world, Vector3 pos1, Vector3 pos2, Feature feature) {
		plugin = Main.getInstance();
		this.world = world;
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.feature = feature;
		switch(feature) {
			case BUILD:
				this.step = 4;
				break;
			case MEASURE:
				this.step = 3;
				break;
			default:
				this.step = 0;
				break;
		}
	}
	
	public void setPos1(double x1, double y1, double z1) {
		this.pos1 = new Vector3(x1, y1, z1);
	}
	
	public void setPos2(double x2, double y2, double z2) {
		this.pos2 = new Vector3(x2, y2, z2);
	}
	
	public QuickBuildTool(Level world, Feature feature) {
		this(world, null, null, feature);
	}
	
	public QuickBuildTool() {
		this(null, null, null, null);
	}
	
	public void make(Block b) {
		for(int x = (int) Math.min(pos1.x, pos2.x); x <= Math.max(pos1.x, pos2.x); x++)
			for(int y = (int) Math.min(pos1.y, pos2.y); y <= Math.max(pos1.y, pos2.y); y++)
				for(int z = (int) Math.min(pos1.z, pos2.z); z <= Math.max(pos1.z, pos2.z); z++)
					world.setBlock(new Vector3(x, y, z), b);
	}
	
	public void buildAssist() {
		int xdist = (int) Math.abs(pos1.x - pos2.x);
		int ydist = (int) Math.abs(pos1.y - pos2.y);
		int zdist = (int) Math.abs(pos1.z - pos2.z);
		int[] arr = {xdist, ydist, zdist};
		
		Block b = Block.get(152);
		int min = unsignedMin(arr);
		if((xdist != 0 && ydist != 0) || (xdist != 0 && zdist != 0) || (ydist != 0 && zdist != 0)) {
			if(min == xdist) world.setBlock(new Vector3(pos1.x, pos2.y, pos2.z), b);
			else if(min == ydist) world.setBlock(new Vector3(pos2.x, pos1.y, pos2.z), b);
			else if(min == zdist) world.setBlock(new Vector3(pos2.x, pos2.y, pos1.z), b);
			else plugin.getServer().getLogger().info("No match!");
		}
	}
	
	private int unsignedMin(int[] arr) {
		int min = arr[0];
		for(int i = 0; i < arr.length; i++) if(arr[i] > 0 && arr[i] < min) min = arr[i];
		return min;
	}
}
