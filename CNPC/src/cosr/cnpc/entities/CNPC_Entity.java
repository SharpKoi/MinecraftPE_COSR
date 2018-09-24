
package cosr.cnpc.entities;

import cn.nukkit.entity.Entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.data.FloatEntityData;
import cn.nukkit.entity.data.Vector3fEntityData;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.network.protocol.AddEntityPacket;
import cosr.cnpc.type.*;
import cosr.cnpc.type.NPCType;

public abstract class CNPC_Entity extends Entity{
    
	public float height = 1;

    public CNPC_Entity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.setDataProperty(new FloatEntityData(DATA_SCALE, this.namedTag.getFloat("Scale")));
    }

	@Override
    public void spawnTo(Player player){
    	AddEntityPacket pk = new AddEntityPacket();
    	pk.entityRuntimeId = this.getId();
    	pk.entityUniqueId = this.getId();
    	pk.type = this.getNetworkId();
    	pk.x = (float) this.x;
    	pk.y = (float) this.y;
    	pk.z = (float) this.z;
    	pk.speedX = pk.speedY = pk.speedZ = 0;
    	pk.yaw = (float) this.yaw;
    	pk.pitch = (float) this.pitch;
    	pk.metadata = this.dataProperties;
    	player.dataPacket(pk);
    	super.spawnTo(player);
    }
	
	public static CompoundTag createNBT(Player creator, String entityClass, NPCType type, String name) {
		CompoundTag nbt =  new CompoundTag()
				.putList(new ListTag<DoubleTag>("Pos")
							.add(new DoubleTag("X", creator.x))
							.add(new DoubleTag("Y", creator.y))
							.add(new DoubleTag("Z", creator.z)))
				.putList(new ListTag<DoubleTag>("Motion")
							.add(new DoubleTag("MotionX", 0))
							.add(new DoubleTag("MotionY", 0))
							.add(new DoubleTag("MotionZ", 0)))
				.putList(new ListTag<FloatTag>("Rotation")
							.add(new FloatTag("Yaw", (float) creator.getYaw()))
							.add(new FloatTag("Pitch", (float) creator.getPitch())))
				.putBoolean("Invulnerable", true);
		if(entityClass.contains("NPC")) {
			ListTag<StringTag> typeList = new ListTag<StringTag>("NPCType");
			typeList.add(new StringTag("Type", type.getName()));
			switch(type.getName()) {
				case "MessageNPC":
					String msg = ((MessageNPC)type).getMessage();
					typeList.add(new StringTag("Message", (msg == null? "hello" : msg)));
					break;
				case "CommandNPC":
					String command = ((CommandNPC)type).getCommand();
					CommandSender sender = ((CommandNPC)type).getSender();
					typeList.add(new StringTag("Sender", (sender == null? Server.getInstance().getConsoleSender().getName() : sender.getName())));
					typeList.add(new StringTag("Command", (command == null? "" : command)));
					break;
				case "TeleportNPC":
					Level to = ((TeleportNPC)type).getTo();
					Vector3 tg = ((TeleportNPC)type).getTarget();
					typeList.add(new StringTag("TpLevel", (to == null? "" : to.getFolderName())))
							.add(new StringTag("TpX", tg == null? "" : Double.toString(tg.getX())))
							.add(new StringTag("TpY", tg == null? "" : Double.toString(tg.getY())))
							.add(new StringTag("TpZ", tg == null? "" : Double.toString(tg.getZ())));
					break;
			}
			nbt.putString("NameTag", name)
				.putBoolean("Npc", true)
				.putList(typeList)
				.putFloat("Scale", 1);
			if(entityClass.contains("Human")) {
				nbt.putCompound("Skin", new CompoundTag()
						.putBoolean("Transparent", false)
						.putByteArray("Data", creator.getSkin().getData())
						.putString("ModelId", creator.getSkin().getModel()))
				.putBoolean("IsHuman", true)
				.putString("Item", creator.getInventory().getItemInHand().getName())
				.putString("Helmet", creator.getInventory().getHelmet().getName())
				.putString("Chestplate", creator.getInventory().getChestplate().getName())
				.putString("Leggings", creator.getInventory().getLeggings().getName())
		        .putString("Boots", creator.getInventory().getBoots().getName());
			}
			return nbt;
		}
		return nbt;
	}
}
	