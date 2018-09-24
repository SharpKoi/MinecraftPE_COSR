package cosr.shop.shops;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import cosr.economy.CEconomy;
import cosr.economy.CMoney;
import cosr.shop.CShopMain;
import cosr.shop.MoneyCostable;
import cosr.shop.Sellable;

public class LotteryShop extends CShop implements MoneyCostable, Sellable {
	
	public static final String CONFIGPATH = "LotteryShop" + File.separator;
	
	private Map<Item, Integer> probabilityMap = new HashMap<Item, Integer>();
	private Map<Item, Integer> stockMap = new HashMap<Item, Integer>();
	private List<Item> lotteryPool = new ArrayList<Item>();
	private Random selector = new Random();
	private float cost;
	
	public LotteryShop() {
		this(OwnerType.NONE, "Unknown", "Unknown", 0);
	}
	
	public LotteryShop(OwnerType type, String name, String ownerName, float cost) {
		this(type, name, ownerName, cost, null, null, null, true);
	}
	
	public LotteryShop(OwnerType type, String name, String ownerName, float cost, 
			String levelName, Vector3 signPos, Vector3 itemPos, boolean isItemShown) {
		this.type = type;
		this.name = name;
		this.ownerName = type.equals(OwnerType.PLAYER)? ownerName : OwnerType.SERVER.getName();
		this.cost = cost;
		this.levelName = levelName;
		this.signPos = signPos;
		this.itemPos = itemPos;
		this.isItemShown = isItemShown;
	}

	@Override
	public float getCost() {
		return cost;
	}

	@Override
	public void setCost(float cost) {
		this.cost = cost;
	}

	public Map<Item, Integer> getProbabilityMap() {
		return probabilityMap;
	}

	public Map<Item, Integer> getStockMap() {
		return stockMap;
	}

	public List<Item> getItemList() {
		return lotteryPool;
	}
	
	public void replenish(Item item) {
		if(type.equals(OwnerType.SERVER)) return;
		Player player = CEconomy.getInstance().getServer().getPlayer(ownerName);
		if(!stockMap.containsKey(item)) {
			player.sendMessage(CShop.infoTitle() + TextFormat.RED + "該抽獎商店中無該物品, 若欲添加請重新配置機率");
			return;
		}
		if(player != null) {
			if(this.deItem(player, item.clone(), item.count)) {
				stockMap.put(item, stockMap.get(item) + item.count);
				player.sendMessage(CShop.infoTitle() + TextFormat.GREEN + "物品添加成功");
			}else {
				player.sendMessage(CShop.infoTitle() + TextFormat.RED + "您身上沒有足夠的物品");
			}
		}
	}
	
	public void replenish(Item item, int amount) {
		if(type.equals(OwnerType.SERVER)) return;
		Player player = CEconomy.getInstance().getServer().getPlayer(ownerName);
		if(!stockMap.containsKey(item)) {
			player.sendMessage(CShop.infoTitle() + TextFormat.RED + "該抽獎商店中無該物品, 若欲添加請重新配置機率");
			return;
		}
		if(player != null) {
			if(this.deItem(player, item.clone(), amount)) {
				stockMap.put(item, stockMap.get(item) + amount);
				player.sendMessage(CShop.infoTitle() + TextFormat.GREEN + "物品添加成功");
			}else {
				player.sendMessage(CShop.infoTitle() + TextFormat.RED + "您身上沒有足夠的物品");
			}
		}
	}
	
	public void extract(Item item, int amount) {
		if(type.equals(OwnerType.SERVER)) return;
		Player owner = Server.getInstance().getPlayer(ownerName);
		if(owner == null) {
			Server.getInstance().getLevelByName(this.levelName).dropItem(this.itemPos, item.clone());
			return;
		}
		int stock = stockMap.getOrDefault(item, 0);
		if(stock == 0) {
			owner.sendMessage(infoTitle() + TextFormat.RED + "該抽獎商店中不存在該物品");
			return;
		}
		if(stock < amount) {
			if(owner.getInventory().canAddItem(item.clone())) {
				owner.sendMessage(infoTitle() + TextFormat.RED + "您的背包已滿，請清空後再執行此動作");
				return;
			}
			owner.sendMessage(infoTitle() + TextFormat.GRAY + "您的商店存貨不足，已將所有存貨退還給您");
			owner.getInventory().addItem(item.clone());
			stockMap.remove(item);
			probabilityMap.remove(item);
		}else {
			if(!owner.getInventory().canAddItem(item.clone())) {
				owner.sendMessage(infoTitle() + TextFormat.RED + "您的背包已滿，請清空後再執行此動作");
				return;
			}
			stock -= amount;
			stockMap.put(item, stock);
			owner.getInventory().addItem(item.clone());
			owner.sendMessage(infoTitle() + TextFormat.GREEN + "已成功將" + amount + "個" + item.getCustomName() + "退還給您");
		}
	}
	
	public void returnAllGoods() {
		if(type.equals(OwnerType.SERVER)) return;
		for(Item item : stockMap.keySet()) {
			this.extract(item, stockMap.get(item));
		}
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
		
		List<Map<String, Object>> allItemData = new ArrayList<Map<String, Object>>(); 
		for(Item item : probabilityMap.keySet()) {
			LinkedHashMap<String, Object> itemData = new LinkedHashMap<String, Object>();
			itemData.put("ItemId", item.getId());
			itemData.put("ItemMeta", item.getDamage());
			itemData.put("Count", item.getCount());
			itemData.put("Stock", (stockMap.containsKey(item)? stockMap.get(item) : 0));
			itemData.put("Name", item.getCustomName());
			itemData.put("Lore", Arrays.asList(item.getLore()));
			List<String> enchList = new ArrayList<String>();
			for(Enchantment ench : item.getEnchantments()) {
				enchList.add(ench.getId()+", "+ench.getLevel());
			}
			itemData.put("Enchantments", enchList);
			itemData.put("Chance", probabilityMap.get(item));
			allItemData.add(itemData);
		}
		dataSection.set("AllItems", allItemData);
		
		return dataSection;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LotteryShop loadFor(String ownerName, int number) {
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
		this.cost = (float) conf.getDouble(numStr+".Cost");
		this.levelName = conf.getString(numStr+".World");
		this.signPos = new Vector3(conf.getDouble(numStr+".X"), conf.getDouble(numStr+".Y"), conf.getDouble(numStr+".Z"));
		this.itemPos = new Vector3(conf.getDouble(numStr+".Showpiece.X"), conf.getDouble(numStr+".Showpiece.Y"), conf.getDouble(numStr+".Showpiece.Z"));
		this.isItemShown = conf.getBoolean(numStr+".Showpiece.IsItemShown");
		List<Map> allItemData = conf.getMapList(numStr+".AllItems"); 
		for(Map<String, Object> itemData : allItemData) {
			Item item = Item.get((int) itemData.get("ItemId"), (int) itemData.get("ItemMeta"), (int) itemData.get("Count"));
			String itemName = conf.getString(numStr+".ItemData.Name");
			if(!itemName.equals("")) item.setCustomName(itemName);
			String[] lore = conf.getStringList(numStr+".ItemData.Lore").toArray(new String[]{});
			if(lore.length > 0) item.setLore(lore);
			List<Enchantment> enchList = new ArrayList<Enchantment>();
			List<String> enchDataList = (List<String>) itemData.get("Enchantments");
			for(String enchData : enchDataList) {
				String[] dataArray = enchData.split(", ");
				Enchantment ench = Enchantment.get(Integer.parseInt(dataArray[0]));
				ench.setLevel(Integer.parseInt(dataArray[1]));
				enchList.add(ench);
			}
			if(enchList.size() > 0)
				item.addEnchantment(enchList.toArray(new Enchantment[] {}));
			probabilityMap.put(item, (Integer)itemData.get("Chance"));
			stockMap.put(item, (Integer)itemData.get("Stock"));
		}
		if(this.getRemain() <= 0) {
			initLotteryPool();
		}
		
		return this;
	}
	
	@Override
	public void buildUp(Vector3 touchPoint, Player builder) {
		super.buildUp(touchPoint, builder);
		Level level = Server.getInstance().getLevelByName(this.levelName);
		if(level != null)
			level.dropItem(itemPos, Item.get(358), new Vector3(0, 0, 0), false, Integer.MAX_VALUE);
	}

	@Override
	public void keyOntoSign(BlockEntitySign sign) {
		if(sign == null) return;
		String line2 = TextFormat.BOLD + ((TextFormat.GREEN + "擁有者: ") + (TextFormat.WHITE + ownerName));
		String line3 = TextFormat.BOLD + (TextFormat.AQUA + ">>抽獎商店<<");
		String line4 = TextFormat.GOLD + "$" + cost;
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
				TextFormat.RESET + "抽獎\n";
		
		String costLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "成交價格: " + 
				TextFormat.RESET + this.cost + "\n");
		
		String positionLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "位於: ") + 
				TextFormat.RESET + ((plugin.getServer().isOp(viewer))? this.getLevelName() : cosr.multiworld.Main.getWorldsConfig().get(this.getLevelName())) + 
				("("+this.getX()+", "+this.getY()+", "+this.getZ()+")") + "\n";
		
		shopInfo += shopNameLine + ownerNameLine + tradeTypeLine + costLine + positionLine;
		
		return shopInfo;
	}
	
	public int getRemain() {
		int sum = 0;
		for(int val : probabilityMap.values()) {
			sum += val;
		}
		return 100 - sum;
	}
	
	public boolean isCompleted() {
		if(this.getRemain() <= 0 && this.lotteryPool.size() == 100) {
			if(type.equals(OwnerType.PLAYER))
				for(Item item : probabilityMap.keySet()) {
					if(stockMap.containsKey(item)) {
						if(stockMap.get(item) < item.getCount()) return false;
					}
				}
			return true;
		}
		return false;
	}
	
	public String getChanceList() {
		String list = TextFormat.ITALIC + (TextFormat.GREEN + "--- 獎品內容機率清單 ---\n") + TextFormat.RESET;
		String separator = "=======================\n";
		list += separator;
		for(Item item : probabilityMap.keySet()) {
			list += "- " + item.getName() + ":  " + TextFormat.AQUA + probabilityMap.get(item) + "\n" + TextFormat.RESET;
		}
		return list;
	}
	
	public void initLotteryPool() {
		lotteryPool.clear();
		for(Item it : probabilityMap.keySet()) {
			for(int i = 0; i < probabilityMap.get(it); i++) {
				lotteryPool.add(it);
			}
		}
	}
	
	public boolean addLottery(Player builder, Item item, int rand) {
		int remain = this.getRemain();
		rand = rand > remain? remain : rand;
		if(rand <= 0) return false;
		
		if(this.type.equals(OwnerType.PLAYER)) {
			if(!this.deItem(builder, item)) {
				builder.sendMessage(TextFormat.RED + "添加物品失敗, 請檢查背包中是否有足夠的物品!");
				return true;
			}
		}
		builder.sendMessage(TextFormat.GREEN + "成功添加獎品, 該獎品的機率為" + TextFormat.WHITE + rand + "%");
		
		if(probabilityMap.containsKey(item)) {
			probabilityMap.put(item, probabilityMap.get(item) + rand);
		}else {
			probabilityMap.put(item, rand);
		}
		stockMap.put(item, 0);
		if(this.getRemain() <= 0) {
			initLotteryPool();
		}
		return true;
	}
	
	public Item result() {
		if(!this.isCompleted()) return null;
		
		int index = selector.nextInt(100);
		return lotteryPool.get(index);
	}
	
	@Override
	public void sellTo(Player winner) {
		Item item = result();
		try {
			float money = CMoney.getMoney(winner.getName());
			if(money < cost) {
				winner.sendMessage(infoTitle() + TextFormat.RED + "抱歉! 您未有足夠的金錢, 無法進行抽獎!");
				return;
			}
			if(stockMap.get(item) < item.getCount() && type.equals(OwnerType.PLAYER)) {
				winner.sendMessage(infoTitle() + TextFormat.RED + "抱歉! 此商店當前獎品庫存不足，無法進行抽獎!");
				return;
			}
			if(!winner.getInventory().canAddItem(item)) {
				winner.sendMessage(infoTitle() + TextFormat.RED + "抱歉! 您的背包滿了, 請清空後再購買商品!");
				return;
			}
			winner.getInventory().addItem(item.clone());
			if(type.equals(OwnerType.PLAYER)) stockMap.put(item, stockMap.get(item) - item.getCount());
			
			CMoney.getMoneyMap().put(winner.getName(), (float)(money-cost));
			if(type.equals(OwnerType.PLAYER)) {
				if(CMoney.getMoneyMap().containsKey(ownerName)) {
					CMoney.getMoneyMap().put(ownerName, (float)(money+cost));
				}else {
					Config conf = new Config(new File(CShopMain.getInstance().getDataFolder(), CEconomy.PDBPATH+(ownerName+".yml")));
					conf.set("Money", money+cost);
					conf.save();
				}
			}

			winner.sendTitle(infoTitle() + TextFormat.GREEN + "恭喜您抽到了", TextFormat.WHITE + item.getName() + "x" + item.getCount() + TextFormat.GREEN + "!", 
								1, 1, 1);
		}catch(FileNotFoundException e) {
			//just catch
		}
		
	}
}
