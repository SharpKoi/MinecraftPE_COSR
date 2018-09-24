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
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import cosr.shop.CShopMain;
import cosr.shop.ItemSingle;
import cosr.shop.Sellable;

public class BarterShop extends CShop implements ItemSingle, Sellable {

	public static final String CONFIGPATH = "BarterShop" + File.separator;
	
	private Item commodity;
	private Item currency;
	private int commodityStock;
	private int currencyStock;
	
	public BarterShop() {
		this(OwnerType.NONE, "Unknown", "Unknown", Item.get(Item.AIR), 0, 0, Item.get(Item.AIR));
	}
	
	public BarterShop(OwnerType type, String name, String ownerName, Item commodity, int stock0, int stock1, Item currency) {
		this(type, name, ownerName, commodity, stock0, stock1, currency, null, null, null, true);
	}
	
	public BarterShop(OwnerType type, String name, String ownerName, Item commodity, int stock0, int stock1, Item currency, Position signPos, Position itemPos) {
		this(type, name, ownerName, commodity, stock0, stock1, currency, 
				signPos.getLevel().getFolderName(), 
				new Vector3(signPos.getX(), signPos.getY(), signPos.getZ()), new Vector3(itemPos.getX(), itemPos.getY(), itemPos.getZ()), 
				true);
	}
	
	public BarterShop(OwnerType type, String name, String ownerName, Item commodity, int stock0, int stock1, Item currency, 
						String levelName, Vector3 signPos, Vector3 itemPos, boolean isItemShown) {
		this.type = type;
		this.name = name;
		this.ownerName = type.equals(OwnerType.PLAYER)? ownerName : OwnerType.SERVER.getName();
		this.commodity = commodity;
		this.commodityStock = stock0;
		this.currencyStock = stock1;
		this.currency = currency;
		this.levelName = levelName;
		this.signPos = signPos;
		this.itemPos = itemPos;
		this.isItemShown = isItemShown;
	}

	public BarterShop(OwnerType type, String name, String ownerName, int itemId, int itemMeta, int count, int stock0, int stock1, Item currency, 
			String levelName, Vector3 signPos, Vector3 itemPos) {
		this(type, name, ownerName, Item.get(itemId, itemMeta, count), stock0, stock1, currency, levelName, signPos, itemPos, true);
	}
	
	@Override
	public Item getItem() {
		return commodity;
	}

	@Override
	public void setItem(Item commodity) {
		this.commodity = commodity;
	}
	
	public Item getCurrency() {
		return currency;
	}
	
	public void setCurrency(Item currency) {
		this.currency = currency;
	}

	@Override
	public int getStock() {
		return commodityStock;
	}

	@Override
	public void setStock(int stock) {
		this.commodityStock = stock;
	}
	
	public int getCurrencyStock() {
		return this.currencyStock;
	}
	
	public void setCurrencyStock(int currencyStock) {
		this.currencyStock = currencyStock;
	}
	
	public void replenish(int count) {
		if(type.equals(OwnerType.SERVER)) return;
		Player player = Server.getInstance().getPlayer(ownerName);
		if(player != null) {
			if(this.deItem(player, this.commodity.clone(), count)) {
				this.commodityStock += count;
				player.sendMessage(CShop.infoTitle() + TextFormat.GREEN + "物品添加成功");
			}else {
				player.sendMessage(CShop.infoTitle() + TextFormat.RED + "您身上沒有足夠的物品");
			}
		}
	}
	
	public void replenishAll() {
		if(type.equals(OwnerType.SERVER)) return;
		Player player = Server.getInstance().getPlayer(ownerName);
		if(player != null) {
			for(Item item : player.getInventory().slots.values()) {
				if(item.equals(this.commodity)) {
					this.commodityStock += item.getCount();
					item.count = 0;
				}
			}
			player.getInventory().remove(this.commodity.clone());
		}
	}
	
	public void replenishAll(Player player) {
		if(type.equals(OwnerType.SERVER)) return;
		if(player != null) {
			for(Item item : player.getInventory().slots.values()) {
				if(item.equals(this.commodity)) {
					this.commodityStock += item.getCount();
					item.count = 0;
				}
			}
			player.getInventory().remove(this.commodity.clone());
		}
	}
	
	public void extractCommodity(int amount) {
		if(type.equals(OwnerType.SERVER)) return;
		Player owner = Server.getInstance().getPlayer(ownerName);
		if(owner == null) {
			Server.getInstance().getLevelByName(this.levelName).dropItem(this.itemPos, commodity.clone());
			return;
		}
		Item target = commodity.clone();
		if(commodityStock < amount) {
			target.setCount(commodityStock);
			if(owner.getInventory().canAddItem(target)) {
				owner.sendMessage(infoTitle() + TextFormat.RED + "您的背包已滿，請清空後再執行此動作");
				return;
			}
			owner.sendMessage(infoTitle() + TextFormat.GRAY + "您的商店存貨不足，已將所有存貨退還給您");
			owner.getInventory().addItem(target);
			this.commodityStock = 0;
		}else {
			target.setCount(amount);
			if(!owner.getInventory().canAddItem(target)) {
				owner.sendMessage(infoTitle() + TextFormat.RED + "您的背包已滿，請清空後再執行此動作");
				return;
			}
			this.commodityStock -= amount;
			owner.sendMessage(infoTitle() + TextFormat.GREEN + "已成功將" + amount + "個" + this.currency.getName() + "退還給您");
		}
		owner.getInventory().addItem(target);
	}
	
	public void extractCurrency(int amount) {
		if(type.equals(OwnerType.SERVER)) return;
		Player owner =Server.getInstance().getPlayer(ownerName);
		if(owner == null) {
			Server.getInstance().getLevelByName(this.levelName).dropItem(this.itemPos, currency.clone());
			return;
		}
		Item target = currency.clone();
		if(currencyStock < amount) {
			target.setCount(currencyStock);
			if(owner.getInventory().canAddItem(target)) {
				owner.sendMessage(infoTitle() + TextFormat.RED + "您的背包已滿，請清空後再執行此動作");
				return;
			}
			owner.sendMessage(infoTitle() + TextFormat.GRAY + "您的商店存貨不足，已將所有存貨退還給您");
			owner.getInventory().addItem(target);
			this.currencyStock = 0;
		}else {
			target.setCount(amount);
			if(!owner.getInventory().canAddItem(target)) {
				owner.sendMessage(infoTitle() + TextFormat.RED + "您的背包已滿，請清空後再執行此動作");
				return;
			}
			this.currencyStock -= amount;
			owner.sendMessage(infoTitle() + TextFormat.GREEN + "已成功將" + amount + "個" + this.currency.getName() + "退還給您");
		}
		owner.getInventory().addItem(target);
	}
	
	public void returnAllGoods() {
		if(type.equals(OwnerType.SERVER)) return;
		this.extractCommodity(this.commodityStock);
		this.extractCurrency(this.currencyStock);
	}
	
	public ConfigSection dataSection() {
		
		ConfigSection dataSection = new ConfigSection();
		
		dataSection.set("Type", this.getClass().getSimpleName());
		dataSection.set("Name", name);
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
		
		dataSection.set("CommodityData", new ConfigSection() {
			private static final long serialVersionUID = 1L;
			{
				set("ItemId", commodity.getId());
				set("ItemMeta", commodity.getDamage());
				set("Count", commodity.getCount());
				set("Stock", commodityStock);
				set("Name", commodity.getCustomName());
				set("Lore", Arrays.asList(commodity.getLore()));
				List<String> enchList = new ArrayList<String>();
				for(Enchantment ench : commodity.getEnchantments()) {
					enchList.add(ench.getId()+", "+ench.getLevel());
				}
				set("Enchantments", enchList);
			}
		});
		
		dataSection.set("CurrencyData", new ConfigSection() {
			private static final long serialVersionUID = 1L;
			{
				set("ItemId", currency.getId());
				set("ItemMeta", currency.getDamage());
				set("Count", currency.getCount());
				set("Stock", currencyStock);
				set("Name", currency.getCustomName());
				set("Lore", Arrays.asList(currency.getLore()));
				List<String> enchList = new ArrayList<String>();
				for(Enchantment ench : currency.getEnchantments()) {
					enchList.add(ench.getId()+", "+ench.getLevel());
				}
				set("Enchantments", enchList);
			}
		});
		
		return dataSection;
	}
	
	public BarterShop loadFor(String ownerName, int number) {
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
		this.levelName = conf.getString(numStr+".World");
		this.signPos = new Vector3(conf.getDouble(numStr+".X"), conf.getDouble(numStr+".Y"), conf.getDouble(numStr+".Z"));
		this.itemPos = new Vector3(conf.getDouble(numStr+".Showpiece.X"), conf.getDouble(numStr+".Showpiece.Y"), conf.getDouble(numStr+".Showpiece.Z"));
		this.isItemShown = conf.getBoolean(numStr+".Showpiece.IsItemShown");
		
		Item _item1 = Item.get(conf.getInt(numStr+".CommodityData.ItemId"), conf.getInt(numStr+".CommodityData.ItemMeta"), conf.getInt(numStr+".CommodityData.Count"));
		this.commodityStock = conf.getInt(numStr+".CommodityData.Stock");
		String itemName = conf.getString(numStr+".CommodityData.Name");
		if(!itemName.equals("")) _item1.setCustomName(itemName);
		String[] lore = conf.getStringList(numStr+".CommodityData.Lore").toArray(new String[]{});
		if(lore.length > 0) _item1.setLore(lore);
		List<Enchantment> enchList = new ArrayList<Enchantment>();
		List<String> enchDataList = conf.getStringList(numStr+".CommodityData.Enchantments");
		for(String enchData : enchDataList) {
			String[] dataArray = enchData.split(", ");
			Enchantment ench = Enchantment.get(Integer.parseInt(dataArray[0]));
			ench.setLevel(Integer.parseInt(dataArray[1]));
			enchList.add(ench);
		}
		if(enchList.size() > 0)
			_item1.addEnchantment(enchList.toArray(new Enchantment[] {}));
		this.commodity = _item1.clone();
		
		Item _item2 = Item.get(conf.getInt(numStr+".CurrencyData.ItemId"), conf.getInt(numStr+".CurrencyData.ItemMeta"), conf.getInt(numStr+".CurrencyData.Count"));
		this.currencyStock = conf.getInt(numStr+".CurrencyData.Stock");
		String itemName2 = conf.getString(numStr+".CurrencyData.Name");
		if(!itemName2.equals("")) _item2.setCustomName(itemName2);
		String[] lore2 = conf.getStringList(numStr+".CurrencyData.Lore").toArray(new String[]{});
		if(lore2.length > 0) _item2.setLore(lore2);
		List<Enchantment> enchList2 = new ArrayList<Enchantment>();
		List<String> enchDataList2 = conf.getStringList(numStr+".CurrencyData.Enchantments");
		for(String enchData : enchDataList2) {
			String[] dataArray = enchData.split(", ");
			Enchantment ench = Enchantment.get(Integer.parseInt(dataArray[0]));
			ench.setLevel(Integer.parseInt(dataArray[1]));
			enchList2.add(ench);
		}
		if(enchList2.size() > 0)
			_item2.addEnchantment(enchList2.toArray(new Enchantment[] {}));
		this.currency = _item2.clone();
		
		return this;
	}
	
	@Override
	public void sellTo(Player buyer) {
		if(buyer == null) return;
		if(type.equals(OwnerType.PLAYER) && commodityStock < commodity.count) {
			buyer.sendMessage(infoTitle() + TextFormat.RED + "抱歉! 此商店當前庫存不足，無法購買此商品!");
			return;
		}
		
		if(!buyer.getInventory().canAddItem(commodity.clone())) {
			buyer.sendMessage(infoTitle() + TextFormat.RED + "抱歉! 您的背包滿了, 請清空後再購買商品!");
			return;
		}
		
		if(deItem(buyer, currency.clone())) {
			buyer.getInventory().addItem(commodity.clone());
			if(type.equals(OwnerType.PLAYER)) {
				this.commodityStock -= commodity.count;
				this.currencyStock += currency.count;
			}
			buyer.sendMessage(infoTitle() + TextFormat.GREEN + "購買成功!");
		}else {
			buyer.sendMessage(infoTitle() + TextFormat.RED + "抱歉! 您未有足夠的物品交換, 交易失敗!");
		}
	}

	@Override
	public void keyOntoSign(BlockEntitySign sign) {
		if(sign == null) return;
		String line2 = TextFormat.BOLD + ((TextFormat.GREEN + "擁有者: ") + (TextFormat.WHITE + ownerName));
		String line3 = TextFormat.BOLD + (TextFormat.AQUA + "出" + TextFormat.WHITE + ">") + 
				TextFormat.RESET + (TextFormat.ITALIC + (TextFormat.YELLOW + commodity.getName()) + 
				(TextFormat.GRAY + " x" + TextFormat.GOLD + commodity.getCount()));
		String line4 = TextFormat.BOLD + (TextFormat.AQUA + "換" + TextFormat.WHITE + ">") + 
				TextFormat.RESET + (TextFormat.ITALIC + (TextFormat.YELLOW + currency.getName()) + 
				(TextFormat.GRAY + " x" + TextFormat.GOLD + currency.getCount()));
		sign.setText(this.name, line2, line3, line4);
	}

	@Override
	public String Information(String viewer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCompleted() {
		return type.equals(OwnerType.SERVER) || (commodityStock > commodity.getCount());
	}

}
