package cosr.newbie.chest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.nukkit.Server;
import cn.nukkit.block.BlockChest;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

public class NewbieChest {
	
	private BlockChest chest = null;
	private Map<Integer, Item> content = null;
	
	public NewbieChest() {
		this.chest = null;
		this.content = new HashMap<Integer, Item>();
	}
	
	public NewbieChest(BlockChest chest) {
		this.chest = chest;
		this.content = new HashMap<Integer, Item>();
	}
	
	public NewbieChest(BlockChest chest, Item... content) {
		this(chest);
		for(int i = 0; i < content.length; i++) {
			this.content.put(i, content[i]);
		}
		this.replenish();
	}
	
	public BlockChest getChest() {
		return chest;
	}
	
	public BlockEntityChest getChestEntity() {
		return chest == null? null : (BlockEntityChest) chest.getLevel().getBlockEntity(chest);
	}
	
	public void setChest(BlockChest chest) {
		this.chest = chest;
	}
	
	public Map<Integer, Item> getContent() {
		return content;
	}
	
	public NewbieChest addItem(int index, Item item) {
		content.put(index, item);
		return this;
	}
	
	public NewbieChest removeItem(int index) {
		content.remove(index);
		return this;
	}
	
	public void replenish() {
		if(chest != null) {
			BlockEntityChest chestEnt = (BlockEntityChest)chest.getLevel().getBlockEntity(chest);
			for(Integer index : content.keySet()) {
				chestEnt.getInventory().setItem(index, content.get(index));
				chestEnt.setItem(index, content.get(index));
			}
		}
	}
	
	public void createAt(Position pos, BlockFace face) {
		if(chest != null) {
			chest.place(chest.toItem(), pos.level.getBlock(pos), null, face, pos.x, pos.y, pos.z);
			BlockEntityChest chestEnt = (BlockEntityChest) pos.level.getBlockEntity(pos);
			if(chestEnt != null) {
				this.replenish();
			}
		}
	}
	
	public ConfigSection dataSection() {
		if(chest == null) return null;
		List<ConfigSection> dataList = new ArrayList<ConfigSection>();
		for(Integer index : content.keySet()) {
			ConfigSection itemData = new ConfigSection();
			Item item = content.get(index);
			itemData.set("index", index);
			itemData.set("id", item.getId());
			itemData.set("meta", item.getDamage());
			itemData.set("count", item.getCount());
			itemData.set("name", item.getName());
			itemData.set("lore", Arrays.asList(item.getLore()));
			List<String> enchList = new ArrayList<String>();
			for(Enchantment ench : item.getEnchantments()) {
				enchList.add(ench.getId()+", "+ench.getLevel());
			}
			itemData.set("enchantments", enchList);
			dataList.add(itemData);
		}
		ConfigSection allData = new ConfigSection();
		allData.set("chest", new ConfigSection() {
			private static final long serialVersionUID = 1L;
			{
				set("level", chest.getLevel().getFolderName());
				set("x", chest.floor().x);
				set("y", chest.floor().y);
				set("z", chest.floor().z);
			}
		});
		allData.set("content", dataList);
		return allData;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public NewbieChest loadFromConfig(Config conf) {
		if(conf.exists("chest")) {
			chest = (BlockChest) Server.getInstance().getLevelByName(conf.getString("chest.level", "")).
					getBlock(new Vector3(conf.getDouble("chest.x", 0), conf.getDouble("chest.y", 0), conf.getDouble("chest.z", 0)));
		}else {
			chest = null;
		}
		List<Map> itemList = conf.getMapList("content");
		for(Map<?, ?> itemData : itemList) {
			Item item = Item.get((int)itemData.get("id"), (int)itemData.get("meta"), (int)itemData.get("count"));
			String itemName = itemData.get("name").toString();
			if(!itemName.equals("") && !itemName.equals(item.getName())) item.setCustomName(itemName);
			String[] lore = ((List<String>)itemData.get("lore")).toArray(new String[0]);
			if(lore.length > 0) item.setLore(lore);
			List<Enchantment> enchList = new ArrayList<Enchantment>();
			List<String> enchDataList = (List<String>) itemData.get("enchantments");
			for(String enchData : enchDataList) {
				String[] dataArray = enchData.split(", ");
				Enchantment ench = Enchantment.get(Integer.parseInt(dataArray[0]));
				ench.setLevel(Integer.parseInt(dataArray[1]));
				enchList.add(ench);
			}
			if(enchList.size() > 0)
				item.addEnchantment(enchList.toArray(new Enchantment[] {}));
			this.content.put(Integer.parseInt(itemData.get("index").toString()), item);
		}
		return this;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public NewbieChest loadFromMap(Map data) {
		if(data.containsKey("chest")) {
			Map chestLoc = (Map) data.get("chest");
			chest = (BlockChest) Server.getInstance().getLevelByName(chestLoc.get("level").toString()).
					getBlock(new Vector3(Double.parseDouble(chestLoc.get("x").toString()), 
								Double.parseDouble(chestLoc.get("y").toString()), 
								Double.parseDouble(chestLoc.get("z").toString())));
		}else {
			chest = null;
		}
		List<Map> itemList = (List<Map>) data.get("content");
		for(Map<?, ?> itemData : itemList) {
			Item item = Item.get((int)itemData.get("id"), (int)itemData.get("meta"), (int)itemData.get("count"));
			String itemName = itemData.get("name").toString();
			if(!itemName.equals("") && !itemName.equals(item.getName())) item.setCustomName(itemName);
			String[] lore = ((List<String>)itemData.get("lore")).toArray(new String[0]);
			if(lore.length > 0) item.setLore(lore);
			List<Enchantment> enchList = new ArrayList<Enchantment>();
			List<String> enchDataList = (List<String>) itemData.get("enchantments");
			for(String enchData : enchDataList) {
				String[] dataArray = enchData.split(", ");
				Enchantment ench = Enchantment.get(Integer.parseInt(dataArray[0]));
				ench.setLevel(Integer.parseInt(dataArray[1]));
				enchList.add(ench);
			}
			if(enchList.size() > 0)
				item.addEnchantment(enchList.toArray(new Enchantment[] {}));
			this.content.put(Integer.parseInt(itemData.get("index").toString()), item);
		}
		return this;
	}
}
