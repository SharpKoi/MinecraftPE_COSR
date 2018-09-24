


package cosr.shop.shops;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import cosr.economy.CEconomy;
import cosr.economy.CMoney;
import cosr.shop.CShopMain;
import cosr.shop.ItemSingle;
import cosr.shop.MoneyCostable;
import cosr.shop.Sellable;

public class SoldShop extends CShop implements ItemSingle, MoneyCostable, Sellable {
	
	public static final String CONFIGPATH = "SoldShop" + File.separator;
	
	private Item item;
	private int stock;
	private float cost;
	
	public SoldShop() {
		this(OwnerType.NONE, "Unknown", "Unknown", Item.get(Item.AIR), 0, 0, 0);
	}
	
	public SoldShop(OwnerType type, String name, String ownerName, Item item, int count, int stock, double cost) {
		this(type, name, ownerName, item, count, stock, cost, null, null, null, true);
	}
	
	public SoldShop(OwnerType type, String name, String ownerName, int itemId, int itemMeta, int count, int stock, double cost) {
		this(type, name, ownerName, Item.get(itemId, itemMeta), count, stock, cost, null, null, null, true);
	}
	
	public SoldShop(OwnerType type, String name, String ownerName, Item item, int count, int stock, double cost, Position signPos, Position itemPos) {
		this(type, name, ownerName, item, count, stock, cost, 
				signPos.getLevel().getFolderName(), 
				new Vector3(signPos.getX(), signPos.getY(), signPos.getZ()), new Vector3(itemPos.getX(), itemPos.getY(), itemPos.getZ()), 
				true);
	}
	
	public SoldShop(OwnerType type, String name, String ownerName, Item item, int count, int stock, double cost, 
						String levelName, Vector3 signPos, Vector3 itemPos, boolean isItemShown) {
		this.type = type;
		this.name = name;
		this.ownerName = type.equals(OwnerType.PLAYER)? ownerName : OwnerType.SERVER.getName();
		this.item = item;
		this.stock = stock;
		this.cost = (float)cost;
		this.levelName = levelName;
		this.signPos = signPos;
		this.itemPos = itemPos;
		this.isItemShown = isItemShown;
	}

	public SoldShop(OwnerType type, String name, String ownerName, int itemId, int itemMeta, int count, int stock, double cost, 
			String levelName, Vector3 signPos, Vector3 itemPos) {
		this(type, name, ownerName, Item.get(itemId, itemMeta), count, stock, cost, levelName, signPos, itemPos, true);
	}

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	public int getStock() {
		return stock;
	}

	@Override
	public void setStock(int stock) {
		this.stock = stock;
	}

	@Override
	public float getCost() {
		return cost;
	}

	@Override
	public void setCost(float cost) {
		this.cost = cost;
	}

	@Override
	public void keyOntoSign(BlockEntitySign sign) {
		if(sign == null) return;
		String line2 = TextFormat.BOLD + ((TextFormat.GREEN + "擁有者: ") + (TextFormat.WHITE + ownerName));
		String line3 = TextFormat.BOLD + (TextFormat.AQUA + "賣" + TextFormat.WHITE + ">") + 
				TextFormat.RESET + (TextFormat.ITALIC + (TextFormat.YELLOW + item.getName()) + 
				(TextFormat.GRAY + " x" + TextFormat.GOLD + item.getCount()));
		String line4 = TextFormat.BOLD + (TextFormat.RED + "$" + cost);
		sign.setText(this.name, line2, line3, line4);
	}
	
	@Override
	public void buildUp(Vector3 touchPoint, Player builder) {
		super.buildUp(touchPoint, builder);
		Level level = Server.getInstance().getLevelByName(this.levelName);
		if(level != null) {
			Item piece = item.clone();
			piece.setCount(1);
			level.dropItem(itemPos, piece, new Vector3(0, 0, 0), false, Integer.MAX_VALUE);
		}
	}

	public ConfigSection dataSection() {
		
		ConfigSection dataSection = new ConfigSection();
		
		dataSection.set("Type", this.getClass().getSimpleName());
		dataSection.set("Name", name);
		dataSection.set("Cost", cost);
		dataSection.set("Stock", stock);
		dataSection.set("World", levelName);
		dataSection.set("X", signPos.getX());
		dataSection.set("Y", signPos.getY());
		dataSection.set("Z", signPos.getZ());
		
		dataSection.set("Showpiece", new ConfigSection() {
			private static final long serialVersionUID = 1L;
			{
				set("IsItemShown", isItemShown);
				set("X", itemPos.getX());
				set("Y", itemPos.getY());
				set("Z", itemPos.getZ());
			}
		});
		
		dataSection.set("ItemData", new ConfigSection() {
			private static final long serialVersionUID = 1L;
			{
				set("ItemId", item.getId());
				set("ItemMeta", item.getDamage());
				set("Count", item.getCount());
				set("Name", item.getCustomName());
				set("Lore", Arrays.asList(item.getLore()));
				List<String> enchList = new ArrayList<String>();
				for(Enchantment ench : item.getEnchantments()) {
					enchList.add(ench.getId()+", "+ench.getLevel());
				}
				set("Enchantments", enchList);
			}
		});
		
		return dataSection;
	}
	
	public SoldShop loadFor(String ownerName, int number) {
		Config conf;
		if(this.getType().equals(OwnerType.SERVER)) {
			conf = new Config(new File(CShopMain.getInstance().getDataFolder(), CONFIGPATH+("ServerShop.yml")), Config.YAML);
		}else if(this.getType().equals(OwnerType.PLAYER)) {
			conf = new Config(new File(CShopMain.getInstance().getDataFolder(), CONFIGPATH+(ownerName+".yml")), Config.YAML);
		}else {
			if(ownerName.replace("Shop", "").equalsIgnoreCase("Server")) {
				conf = new Config(new File(CShopMain.getInstance().getDataFolder(), CONFIGPATH+("ServerShop.yml")), Config.YAML);
				this.setType(OwnerType.SERVER);
			}else {
				conf = new Config(new File(CShopMain.getInstance().getDataFolder(), CONFIGPATH+(ownerName+".yml")), Config.YAML);
				this.setType(OwnerType.PLAYER);
			}
		}
		String numStr = Integer.toString(number);
		this.name = conf.getString(numStr+".Name");
		this.ownerName = ownerName;
		this.stock = conf.getInt(numStr+".Stock");
		this.cost = (float) conf.getDouble(numStr+".Cost");
		this.levelName = conf.getString(numStr+".World");
		this.signPos = new Vector3(conf.getDouble(numStr+".X"), conf.getDouble(numStr+".Y"), conf.getDouble(numStr+".Z"));
		this.itemPos = new Vector3(conf.getDouble(numStr+".Showpiece.X"), conf.getDouble(numStr+".Showpiece.Y"), conf.getDouble(numStr+".Showpiece.Z"));
		this.isItemShown = conf.getBoolean(numStr+".Showpiece.IsItemShown");
		Item _item = Item.get(conf.getInt(numStr+".ItemData.ItemId"), conf.getInt(numStr+".ItemData.ItemMeta"), conf.getInt(numStr+".ItemData.Count"));
		String itemName = conf.getString(numStr+".ItemData.Name");
		if(!itemName.equals("")) _item.setCustomName(itemName);
		String[] lore = conf.getStringList(numStr+".ItemData.Lore").toArray(new String[]{});
		if(lore.length > 0) _item.setLore(lore);
		List<Enchantment> enchList = new ArrayList<Enchantment>();
		List<String> enchDataList = conf.getStringList(numStr+".ItemData.Enchantments");
		for(String enchData : enchDataList) {
			String[] dataArray = enchData.split(", ");
			Enchantment ench = Enchantment.get(Integer.parseInt(dataArray[0]));
			ench.setLevel(Integer.parseInt(dataArray[1]));
			enchList.add(ench);
		}
		if(enchList.size() > 0)
			_item.addEnchantment(enchList.toArray(new Enchantment[] {}));
		this.item = _item.clone();
		
		return this;
	}

	@Override
	public void sellTo(Player buyer) {
		if(buyer == null) return;
		if(type.equals(OwnerType.PLAYER) && stock < item.count) {
			buyer.sendMessage(infoTitle() + TextFormat.RED + "抱歉! 此商店當前庫存不足，無法購買此商品!");
			return;
		}
		
		double money = CMoney.getMoneyMap().get(buyer.getName());
		if(money >= cost) {
			if(!buyer.getInventory().canAddItem(item.clone())) {
				buyer.sendMessage(infoTitle() + TextFormat.RED + "抱歉! 您的背包滿了, 請清空後再購買商品!");
				return;
			}
			buyer.getInventory().addItem(item.clone());
			
			CMoney.getMoneyMap().put(buyer.getName(), (float)(money-cost));
			if(type.equals(OwnerType.PLAYER)) {
				this.stock -= item.count;
				if(CMoney.getMoneyMap().containsKey(ownerName)) {
					CMoney.getMoneyMap().put(ownerName, (float)(money+cost));
				}else {
					Config conf = new Config(new File(CEconomy.getInstance().getDataFolder(), CEconomy.PDBPATH+(ownerName+".yml")));
					conf.set("Money", money+cost);
					conf.save();
				}
			}
			
			buyer.sendMessage(infoTitle() + TextFormat.GREEN + "購買成功!");
		}else {
			buyer.sendMessage(infoTitle() + TextFormat.RED + "抱歉! 您未有足夠的金錢, 無法購買此商品!");
			return;
		}
	}
	
	@Override
	public boolean isCompleted() {
		return (type.equals(OwnerType.SERVER) || (stock > item.getCount()));
	}
	
	public void replenish(int count) {
		if(type.equals(OwnerType.SERVER)) return;
		Player player = CEconomy.getInstance().getServer().getPlayer(ownerName);
		if(player != null) {
			if(this.deItem(player, this.item.clone(), count)) {
				this.stock += count;
				player.sendMessage(CShop.infoTitle() + TextFormat.GREEN + "物品添加成功");
			}else {
				player.sendMessage(CShop.infoTitle() + TextFormat.RED + "您身上沒有足夠的物品");
			}
		}
	}
	
	public void replenishAll() {
		if(type.equals(OwnerType.SERVER)) return;
		Player player = CEconomy.getInstance().getServer().getPlayer(ownerName);
		if(player != null) {
			for(Item item : player.getInventory().slots.values()) {
				if(item.equals(this.item)) {
					this.stock += item.getCount();
					item.count = 0;
				}
			}
			player.getInventory().remove(this.item.clone());
		}
	}
	
	public void replenishAll(Player player) {
		if(type.equals(OwnerType.SERVER)) return;
		if(player != null) {
			for(Item item : player.getInventory().slots.values()) {
				if(item.equals(this.item.clone())) {
					this.stock += item.getCount();
					item.count = 0;
				}
			}
			player.getInventory().remove(this.item.clone());
		}
	}
	
	public void extract(int amount) {
		if(type.equals(OwnerType.SERVER)) return;
		Player owner = CEconomy.getInstance().getServer().getPlayer(ownerName);
		if(owner == null) {
			CEconomy.getInstance().getServer().getLevelByName(this.levelName).dropItem(this.itemPos, item.clone());
			return;
		}
		if(stock < amount) {
			if(owner.getInventory().canAddItem(item.clone())) {
				owner.sendMessage(infoTitle() + TextFormat.RED + "您的背包已滿，請清空後再執行此動作");
				return;
			}
			owner.sendMessage(infoTitle() + TextFormat.GRAY + "您的商店存貨不足，已將所有存貨退還給您");
			owner.getInventory().addItem(item.clone());
			this.stock = 0;
		}else {
			if(!owner.getInventory().canAddItem(item.clone())) {
				owner.sendMessage(infoTitle() + TextFormat.RED + "您的背包已滿，請清空後再執行此動作");
				return;
			}
			this.stock -= amount;
			owner.getInventory().addItem(item.clone());
			owner.sendMessage(infoTitle() + TextFormat.GREEN + "已成功將" + amount + "個" + this.item.getCustomName() + "退還給您");
		}
	}
	
	public void returnAllGoods() {
		if(type.equals(OwnerType.SERVER)) return;
		this.extract(this.stock);
	}
	
	@Override
	public String Information(String viewer) {
		String shopInfo = "";
		
		CEconomy plugin = CEconomy.getInstance();
		String shopNameLine = TextFormat.RESET + (TextFormat.GREEN + "商店名稱: ") + (TextFormat.RESET + this.getName()) + "\n";
		String ownerNameLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "擁有者: " + (TextFormat.RESET + this.ownerName)) + "\n";
		String tradeTypeLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "交易類型: ") + 
				TextFormat.RESET + "販售\n";
		
		String itemNameLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "物品: ") + 
				TextFormat.RESET + item.getCustomName() + "\n";
		
		String itemIDLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "物品ID: ") + 
				TextFormat.RESET + item.getId() + ((item.getDamage() != 0)? ":" + item.getDamage() : "") + "\n";
		
		String countLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "一次交易數量: " + 
				TextFormat.RESET + item.getCount() + "\n");
		
		String costLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "成交價格: " + 
				TextFormat.RESET + this.cost + "\n");
		
		String perCostLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "物品單價: " + 
				TextFormat.RESET + (this.cost / item.getCount()) + "\n");
		
		String stockLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "庫存量: ") + 
				TextFormat.RESET + this.getStock() + "\n";
		
		String positionLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "位於: ") + 
				TextFormat.RESET + ((plugin.getServer().isOp(viewer))? this.getLevelName() : cosr.multiworld.Main.getWorldsConfig().get(this.getLevelName())) + 
				("("+this.getX()+", "+this.getY()+", "+this.getZ()+")") + "\n";
		
		shopInfo += shopNameLine + ownerNameLine + tradeTypeLine + itemNameLine + itemIDLine + countLine + costLine + perCostLine + stockLine + positionLine;
		
		return shopInfo;
	}
}