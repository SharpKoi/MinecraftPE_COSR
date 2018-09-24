package cosr.shop.shops;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import cosr.economy.CEconomy;
import cosr.economy.CPoint;
import cosr.shop.CShopMain;
import cosr.shop.ItemSingle;
import cosr.shop.MoneyCostable;
import cosr.shop.Sellable;

public class PointShop extends CShop implements MoneyCostable, ItemSingle, Sellable {
	
	public static final String CONFIGPATH = "PointShop" + File.separator;
	
	private Item item;
	private float cost;
	
	public PointShop() {
		this(OwnerType.NONE, "Unknown", "Unknown", Item.get(Item.AIR), 0, 0);
	}
	
	public PointShop(OwnerType type, String name, String ownerName, Item item, int count, double cost) {
		this(type, name, ownerName, item, count, cost, null, null, null, true);
	}
	
	public PointShop(OwnerType type, String name, String ownerName, int itemId, int itemMeta, int count, double cost) {
		this(type, name, ownerName, Item.get(itemId, itemMeta), count, cost, null, null, null, true);
	}
	
	public PointShop(OwnerType type, String name, String ownerName, Item item, int count, double cost, Position signPos, Position itemPos) {
		this(type, name, ownerName, item, count, cost, 
				signPos.getLevel().getFolderName(), 
				new Vector3(signPos.getX(), signPos.getY(), signPos.getZ()), new Vector3(itemPos.getX(), itemPos.getY(), itemPos.getZ()), 
				true);
	}
	
	public PointShop(OwnerType type, String name, String ownerName, Item item, int count, double cost, 
						String levelName, Vector3 signPos, Vector3 itemPos, boolean isItemShown) {
		this.type = type;
		this.name = name;
		this.ownerName = type.equals(OwnerType.PLAYER)? ownerName : OwnerType.SERVER.getName();
		this.item = item;
		this.cost = (float)cost;
		this.levelName = levelName;
		this.signPos = signPos;
		this.itemPos = itemPos;
		this.isItemShown = isItemShown;
	}

	public PointShop(OwnerType type, String name, String ownerName, int itemId, int itemMeta, int count, double cost, 
			String levelName, Vector3 signPos, Vector3 itemPos) {
		this(type, name, ownerName, Item.get(itemId, itemMeta), count, cost, levelName, signPos, itemPos, true);
	}

	@Override
	public void sellTo(Player buyer) {
		if(buyer == null) return;
		
		double point = CPoint.getPointMap().getOrDefault(buyer.getName(), 0.0f);
		if(point >= cost) {
			if(!buyer.getInventory().canAddItem(item.clone())) {
				buyer.sendMessage(infoTitle() + TextFormat.RED + "抱歉! 您的背包滿了, 請清空後再購買商品!");
				return;
			}
			buyer.getInventory().addItem(item.clone());
			
			try {
				CPoint.takePoint(buyer.getName(), this.cost);
			} catch (FileNotFoundException e) {
				buyer.sendMessage(CShop.infoTitle() + TextFormat.RED + "您身上的" + TextFormat.LIGHT_PURPLE + CPoint.name() + TextFormat.RED + "不足, 無法購買此商品");
				return;
			}
			
			buyer.sendMessage(infoTitle() + TextFormat.GREEN + "購買成功!");
		}else {
			buyer.sendMessage(infoTitle() + TextFormat.RED + "抱歉! 您未有足夠的" + TextFormat.LIGHT_PURPLE + CPoint.name() + TextFormat.RED + ", 無法購買此商品!");
			return;
		}
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
		return 9999;
	}

	@Override
	public void setStock(int stock) {
		return;
	}

	@Override
	public float getCost() {
		return cost;
	}

	@Override
	public void setCost(float cost) {
		this.cost = cost;
	}
	
	public ConfigSection dataSection() {
		ConfigSection dataSection = new ConfigSection();
		
		dataSection.set("Type", this.getClass().getSimpleName());
		dataSection.set("Name", name);
		dataSection.set("Cost", cost);
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
	
	public PointShop loadFor(String ownerName, int number) {
		Config conf;
		if(this.getType().equals(OwnerType.SERVER)) {
			conf = new Config(new File(CShopMain.getInstance().getDataFolder(), CONFIGPATH+("ServerShop.yml")), Config.YAML);
		}else if(this.getType().equals(OwnerType.PLAYER)) {
			conf = new Config(new File(CShopMain.getInstance().getDataFolder(), CONFIGPATH+(ownerName+".yml")), Config.YAML);
		}else {
			conf = ownerName.replace("Shop", "").equalsIgnoreCase("Server")? 
				new Config(new File(CShopMain.getInstance().getDataFolder(), CONFIGPATH+("ServerShop.yml")), Config.YAML) : 
				new Config(new File(CShopMain.getInstance().getDataFolder(), CONFIGPATH+(ownerName+".yml")), Config.YAML);
		}
		String numStr = Integer.toString(number);
		this.name = conf.getString(numStr+".Name");
		this.ownerName = ownerName;
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
	public void keyOntoSign(BlockEntitySign sign) {
		if(sign == null) return;
		String line2 = TextFormat.BOLD + ((TextFormat.GREEN + "擁有者: ") + (TextFormat.WHITE + "Server"));
		String line3 = TextFormat.BOLD + (TextFormat.LIGHT_PURPLE + "賣" + TextFormat.WHITE + ">") + 
				TextFormat.RESET + (TextFormat.ITALIC + (TextFormat.YELLOW + item.getName()) + 
				(TextFormat.GRAY + " x" + TextFormat.GOLD + item.getCount()));
		String line4 = TextFormat.LIGHT_PURPLE + "$" + cost + CPoint.name();
		sign.setText(this.name, line2, line3, line4);
	}

	@Override
	public String Information(String viewer) {
		String shopInfo = "";
		
		CEconomy plugin = CEconomy.getInstance();
		//cosr.multiworld.Main是套件完整名稱，由於類名為Main的套件多，因此不用import的方式，以便區分
		
		String shopNameLine = TextFormat.RESET + (TextFormat.GREEN + "商店名稱: ") + (TextFormat.RESET + this.getName()) + "\n";
		String ownerNameLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "擁有者: " + (TextFormat.RESET + this.ownerName)) + "\n";
		String tradeTypeLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "交易類型: ") + 
				TextFormat.LIGHT_PURPLE + "點券交易\n";
		
		String itemNameLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "物品: ") + 
				TextFormat.RESET + item.getCustomName() + "\n";
		
		String itemIDLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "物品ID: ") + 
				TextFormat.RESET + item.getId() + ((item.getDamage() != 0)? ":" + item.getDamage() : "") + "\n";
		
		String countLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "一次交易數量: " + 
				TextFormat.RESET + item.getCount() + "件\n");
		
		String costLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "成交價格: " + 
				TextFormat.RESET + "$" + this.cost + "\n");
		
		String perCostLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "物品單價: " + 
				TextFormat.RESET + "$" + (this.cost / item.getCount()) + "\n");
		
		String stockLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "庫存量: ") + 
				TextFormat.RESET + this.getStock() + "件\n";
		
		String positionLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "位於: ") + 
				TextFormat.RESET + ((plugin.getServer().isOp(viewer))? this.getLevelName() : cosr.multiworld.Main.getWorldsConfig().get(this.getLevelName())) + 
				("("+this.getX()+", "+this.getY()+", "+this.getZ()+")") + "\n";
		
		shopInfo += shopNameLine + ownerNameLine + tradeTypeLine + itemNameLine + itemIDLine + countLine + costLine + perCostLine + stockLine + positionLine;
		
		return shopInfo;
	}

	@Override
	public boolean isCompleted() {
		return true;
	}

}
