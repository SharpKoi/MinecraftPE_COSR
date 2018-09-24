
package cosr.shop.shops;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSignPost;
import cn.nukkit.block.BlockWallSign;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import cosr.shop.CShopMain;

public abstract class CShop {
	//player_aliase, server_aliase, svfair_name
	private static Config config = new Config(new File(CShopMain.getInstance().getDataFolder(), "config.yml"), Config.YAML);
	
	protected OwnerType type;
	protected String ownerName;
	protected String name;
	protected String levelName;
	protected Vector3 signPos;
	protected Vector3 itemPos;
	protected boolean isItemShown;
	protected boolean renaming = false;
	protected boolean costEditting = false;
	
	public enum OwnerType {
		PLAYER("Player", CShop.getShopConfig().getString("player_alias", "Player")),
		SERVER("Server", CShop.getShopConfig().getString("server_alias", "Server")),
		NONE("NONE", "None");
		
		private String typeName;
		private String alias;

		private OwnerType(String typeName, String alias) {
			this.typeName = typeName;
			this.alias = alias;
		}
		
		public String getName() {
			return typeName;
		}
		
		public String getAlias() {
			return alias;
		}
		
		public char getSymbol() {
			return getName().charAt(0);
		}
		
		public static OwnerType getType(String typeName) {
			switch(typeName.toLowerCase()) {
				case "player": return OwnerType.PLAYER;
				
				case "server": return OwnerType.SERVER;
				
				default: return OwnerType.NONE;
			}
		}
	}
	
	public static Config getShopConfig() {
		return config;
	}
	
	public void buildUp(Vector3 touchPoint, Player builder) {
		if(levelName != null) {
			Vector3 bodyPos = new Vector3(touchPoint.x, touchPoint.y+1, touchPoint.z);
			this.itemPos = new Vector3(touchPoint.x+0.5, touchPoint.y+2, touchPoint.z+0.5);
			BlockFace signFace = builder.getDirection().getOpposite();
			this.signPos = bodyPos.add(builder != null? signFace.getUnitVector() : new Vector3(-1, 0, 0));
			
			Level level = Server.getInstance().getLevelByName(this.levelName);
			if(level != null) {
				level.setBlock(bodyPos, Block.get(155));
				level.setBlock(itemPos.floor(), Block.get(20));
				this.placeSign(level, signPos, new BlockWallSign(signFace.getIndex()));
				BlockEntitySign signEntity = (BlockEntitySign) level.getBlockEntity(signPos);
				this.keyOntoSign(signEntity);
			}
		}
	}
	
	public void destroy() {
		if(levelName != null) {
			Level level = Server.getInstance().getLevelByName(this.levelName);
			if(level != null) {
				hidePiece();
				level.setBlock(signPos, Block.get(Block.AIR));
				level.setBlock(itemPos.subtract(new Vector3(0, 1, 0)), Block.get(Block.AIR));
				level.setBlock(itemPos, Block.get(Block.AIR));
			}
		}
	}
	
	private void placeSign(Level level, Vector3 pos, BlockSignPost sign) {
		//直接用setBlock設置告示牌會被判斷為非實體，因此必須利用自訂義方法告訴伺服器此木牌為實體
		CompoundTag nbt = new CompoundTag()
				.putString("id", BlockEntity.SIGN)
                .putInt("x", (int) pos.x)
                .putInt("y", (int) pos.y)
                .putInt("z", (int) pos.z)
                .putString("Text1", "")
                .putString("Text2", "")
                .putString("Text3", "")
                .putString("Text4", "");
		
		level.setBlock(pos, sign, true);
		//x座標*16 且 y座標*16
		new BlockEntitySign(level.getChunk((int) pos.x >> 4, (int) pos.z >> 4), nbt);
	}
	
	public abstract void keyOntoSign(BlockEntitySign sign);
	
	public abstract String Information(String viewer);
	
	public static String infoTitle() {
		return TextFormat.RESET + (TextFormat.BOLD + (TextFormat.WHITE + "[") + (TextFormat.AQUA + "CShop")) + TextFormat.WHITE + "]" + TextFormat.RESET;
	}
	
	//TODO: move to each shop
	protected boolean deItem(Player p, Item target, int count) {
		int remain = 0;
		
		if(!p.getInventory().contains(target)) return false;
		
		List<Integer> emptySlots = new ArrayList<Integer>();
		for(int i = 0; i < p.getInventory().getSize(); i++) {
			Item item = p.getInventory().getItem(i);
			if(item.equals(target)) {
				if((remain + item.count) < count) {
					remain += item.count;
					emptySlots.add(i);
				}else {
					item.count = (item.count + remain) - count;
					p.getInventory().setItem(i, item);
					
					for(int slot : emptySlots) {
						p.getInventory().setItem(slot, Item.get(0));
					}
					return true;
				}
			}
		}
		return false;
	}
	
	protected boolean deItem(Player p, Item target) {
		int remain = 0;
		
		if(!p.getInventory().contains(target)) return false;
		
		List<Integer> emptySlots = new ArrayList<Integer>();
		for(int i = 0; i < p.getInventory().getSize(); i++) {
			Item item = p.getInventory().getItem(i);
			if(item.equals(target)) {
				if((remain + item.count) < target.getCount()) {
					remain += item.count;
					emptySlots.add(i);
				}else {
					item.count = (item.count + remain) - target.getCount();
					p.getInventory().setItem(i, item);
					
					for(int slot : emptySlots) {
						p.getInventory().setItem(slot, Item.get(0));
					}
					return true;
				}
			}
		}
		return false;
	}

	public OwnerType getType() {
		return type;
	}

	public void setType(OwnerType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		if(levelName == null || signPos == null) {
			return;
		}
		renaming = true;
		BlockEntitySign sign = (BlockEntitySign) Server.getInstance().getLevelByName(levelName).getBlockEntity(signPos);
		if(sign != null) {
			sign.setText(name, sign.getText()[1], sign.getText()[2], sign.getText()[3]);
		}
		renaming = false;
	}
	
	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		if(type.equals(OwnerType.SERVER)) return;
		this.ownerName = ownerName;
	}
	/*
	public int getItemId() {
		return itemId;
	}
	
	public int getItemMeta() {
		return itemMeta;
	}
	
	public Item getItem() {
		return Item.get(itemId, itemMeta, count);
	}
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = (float)cost;
	}
	*/

	public String getLevelName() {
		return levelName;
	}
	
	public Level getLevel() {
		return Server.getInstance().getLevelByName(levelName);
	}

	public void setLevel(String levelName) {
		this.levelName = levelName;
	}

	public Vector3 getSignPos() {
		return signPos;
	}

	public void setSignPos(Vector3 pos) {
		this.signPos = pos;
	}
	
	public double getX() {
		return signPos.x;
	}
	
	public double getY() {
		return signPos.y;
	}
	
	public double getZ() {
		return signPos.z;
	}
	
	public Position getPosition() {
		return new Position(signPos.x, signPos.y, signPos.z, Server.getInstance().getLevelByName(levelName));
	}

	public Vector3 getItemPos() {
		return itemPos;
	}

	public void setItemPos(Vector3 itemPos) {
		this.itemPos = itemPos;
	}

	public boolean isItemShown() {
		return isItemShown;
	}

	public void setItemVisible() {
		this.isItemShown = true;
	}
	
	public void setItemDisappear() {
		this.isItemShown = false;
	}
	
	public boolean isRenaming() {
		return renaming;
	}

	public void setRenaming(boolean renaming) {
		this.renaming = renaming;
	}

	public boolean isCostEditting() {
		return costEditting;
	}

	public void setCostEditting(boolean costEditting) {
		this.costEditting = costEditting;
	}

	public abstract boolean isCompleted();
	
	public void hidePiece() {
		for(Entity entity : Server.getInstance().getLevelByName(this.levelName).getEntities()) {
			if(entity instanceof EntityItem) {
				if(entity.getX() == itemPos.x && entity.getY() == itemPos.y && entity.getZ() == itemPos.z) {
					setItemDisappear();
					Server.getInstance().getLevelByName(this.levelName).removeEntity(entity);
					break;
				}
			}
		}
	}
	
	public void showPiece(Item piece) {
		Server.getInstance().getLevelByName(this.levelName)
			.dropItem(itemPos, piece, new Vector3(0,0,0), false, Integer.MAX_VALUE);
			
		setItemVisible();
	}
}
