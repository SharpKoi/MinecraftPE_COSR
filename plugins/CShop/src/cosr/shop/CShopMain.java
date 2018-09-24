package cosr.shop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import cosr.shop.listener.EventListener;
import cosr.shop.listener.GuiEventListener;
import cosr.shop.shops.BarterShop;
import cosr.shop.shops.CShop;
import cosr.shop.shops.LotteryShop;
import cosr.shop.shops.PointShop;
import cosr.shop.shops.PurchaseShop;
import cosr.shop.shops.SoldShop;
import cosr.shop.shops.TitleShop;
import cosr.shop.task.AdvertisingTask;
import cosr.shop.shops.CShop.OwnerType;
import cosr.shop.utils.BuildTool;
import cosr.shop.utils.CShopUI;

public class CShopMain extends PluginBase {
	
	private static CShopMain main;
	
	private static Map<String, BuildTool> buildingPool = new HashMap<String, BuildTool>();
	private static List<String> removingPool = new ArrayList<String>();
	private static Map<String, List<PurchaseShop>> purchaseShops = new HashMap<String, List<PurchaseShop>>();
	private static Map<String, List<SoldShop>> soldShops = new HashMap<String, List<SoldShop>>();
	private static Map<String, List<LotteryShop>> lotShops = new HashMap<String, List<LotteryShop>>();
	private static Map<String, List<PointShop>> ptShops = new HashMap<String, List<PointShop>>();
	private static Map<String, List<TitleShop>> titleShops = new HashMap<String, List<TitleShop>>();
	private static Map<String, List<BarterShop>> btShops = new HashMap<String, List<BarterShop>>();
	
	private static HashMap<String, CShop> tradeRequestMap = new HashMap<String, CShop>();
	private AdvertisingTask adTask = new AdvertisingTask(this);
	
	public static CShopMain getInstance() {
		return main;
	}
	
	public static Map<String, BuildTool> getBuildingPool() {
		return buildingPool;
	}
	
	public static List<String> getRemovingPool() {
		return removingPool;
	}
	
	public static HashMap<String, CShop> getTradeRequestMap() {
		return tradeRequestMap;
	}
	
	public static Map<String, List<PurchaseShop>> getPurchaseShopMap() {
		return purchaseShops;
	}

	public static Map<String, List<SoldShop>> getSoldShopMap() {
		return soldShops;
	}

	public static Map<String, List<LotteryShop>> getLotteryShopMap() {
		return lotShops;
	}
	
	public static Map<String, List<PointShop>> getPointShopMap() {
		return ptShops;
	}

	public static Map<String, List<TitleShop>> getTitleShopMap() {
		return titleShops;
	}
	
	public static Map<String, List<BarterShop>> getBarterShopMap() {
		return btShops;
	}
	
	public AdvertisingTask getAdTask() {
		return adTask;
	}

	public void onEnable() {
		main = this;
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		this.getServer().getPluginManager().registerEvents(new GuiEventListener(), this);
		//加載購買型商店
		File shopDir = new File(this.getDataFolder(), PurchaseShop.CONFIGPATH);
		if(!shopDir.exists()) {
			shopDir.mkdir();
		}
		for(File f : shopDir.listFiles()) {
			String ownerName = f.getName().replace(".yml", "").replace("Shop", "");
			Config conf = new Config(f, Config.YAML);
			ArrayList<PurchaseShop> psList = new ArrayList<PurchaseShop>();
			for(String indexStr : conf.getKeys(false)) {
				PurchaseShop ps = new PurchaseShop();
				ps.loadFor(ownerName, Integer.parseInt(indexStr));
				if(ps.getLevel() != null)
					psList.add(ps);
			}
			purchaseShops.put(ownerName, psList);
		}
		//加載販賣型商店
		shopDir = new File(this.getDataFolder(), SoldShop.CONFIGPATH);
		if(!shopDir.exists()) {
			shopDir.mkdir();
		}
		for(File f : shopDir.listFiles()) {
			String ownerName = f.getName().replace(".yml", "").replace("Shop", "");
			Config conf = new Config(f, Config.YAML);
			ArrayList<SoldShop> ssList = new ArrayList<SoldShop>();
			for(String indexStr : conf.getKeys(false)) {
				SoldShop ss = new SoldShop();
				ss.loadFor(ownerName, Integer.parseInt(indexStr));
				if(ss.getLevel() != null)
					ssList.add(ss);
			}
			soldShops.put(ownerName, ssList);
		}
		
		shopDir = new File(this.getDataFolder(), LotteryShop.CONFIGPATH);
		if(!shopDir.exists()) {
			shopDir.mkdir();
		}
		for(File f : shopDir.listFiles()) {
			String ownerName = f.getName().replace(".yml", "").replace("Shop", "");
			Config conf = new Config(f, Config.YAML);
			ArrayList<LotteryShop> lsList = new ArrayList<LotteryShop>();
			for(String indexStr : conf.getKeys(false)) {
				LotteryShop ls = new LotteryShop();
				ls.loadFor(ownerName, Integer.parseInt(indexStr));
				if(ls.getLevel() != null)
					lsList.add(ls);
			}
			lotShops.put(ownerName, lsList);
		}
		
		shopDir = new File(this.getDataFolder(), PointShop.CONFIGPATH);
		if(!shopDir.exists()) {
			shopDir.mkdir();
		}
		for(File f : shopDir.listFiles()) {
			String ownerName = f.getName().replace(".yml", "").replace("Shop", "");
			Config conf = new Config(f, Config.YAML);
			ArrayList<PointShop> pssList = new ArrayList<PointShop>();
			for(String indexStr : conf.getKeys(false)) {
				PointShop pss = new PointShop();
				pss.loadFor(ownerName, Integer.parseInt(indexStr));
				if(pss.getLevel() != null)
					pssList.add(pss);
			}
			ptShops.put(ownerName, pssList);
		}
		
		shopDir = new File(this.getDataFolder(), TitleShop.CONFIGPATH);
		if(!shopDir.exists()) {
			shopDir.mkdir();
		}
		for(File f : shopDir.listFiles()) {
			String ownerName = f.getName().replace(".yml", "").replace("Shop", "");
			Config conf = new Config(f, Config.YAML);
			ArrayList<TitleShop> tssList = new ArrayList<TitleShop>();
			for(String indexStr : conf.getKeys(false)) {
				TitleShop tss = new TitleShop();
				tss.loadFor(ownerName, Integer.parseInt(indexStr));
				if(tss.getLevel() != null)
					tssList.add(tss);
			}
			titleShops.put(ownerName, tssList);
		}
		
		shopDir = new File(this.getDataFolder(), BarterShop.CONFIGPATH);
		if(!shopDir.exists()) {
			shopDir.mkdir();
		}
		for(File f : shopDir.listFiles()) {
			String ownerName = f.getName().replace(".yml", "").replace("Shop", "");
			Config conf = new Config(f, Config.YAML);
			ArrayList<BarterShop> btsList = new ArrayList<BarterShop>();
			for(String indexStr : conf.getKeys(false)) {
				BarterShop bts = new BarterShop();
				bts.loadFor(ownerName, Integer.parseInt(indexStr));
				if(bts.getLevel() != null)
					btsList.add(bts);
			}
			btShops.put(ownerName, btsList);
		}
		
		//定時廣告
		this.getServer().getScheduler().scheduleRepeatingTask(adTask, (20*60)*20);
	}
	
	public void onDisable() {
		Config shopConfig = null;
		for(String ownerName : purchaseShops.keySet()) {
			shopConfig = new Config(new File(this.getDataFolder(), PurchaseShop.CONFIGPATH + 
					(ownerName.equals("Server")? ownerName+"Shop" : ownerName)+".yml"), Config.YAML);
			List<PurchaseShop> psList = purchaseShops.get(ownerName);
			if(psList.size() > 0) {
				for(int i = 0; i < psList.size(); i++) {
					shopConfig.set(Integer.toString(i), psList.get(i).dataSection());
				}
			}
			shopConfig.save();
		}
		
		for(String ownerName : soldShops.keySet()) {
			shopConfig = new Config(new File(this.getDataFolder(), SoldShop.CONFIGPATH + 
					(ownerName.equals("Server")? ownerName+"Shop" : ownerName)+".yml"), Config.YAML);
			List<SoldShop> ssList = soldShops.get(ownerName);
			if(ssList.size() > 0) {
				for(int i = 0; i < ssList.size(); i++) {
					shopConfig.set(Integer.toString(i), ssList.get(i).dataSection());
				}
			}
			shopConfig.save();
		}
		
		for(String ownerName : lotShops.keySet()) {
			shopConfig = new Config(new File(this.getDataFolder(), LotteryShop.CONFIGPATH + 
					(ownerName.equals("Server")? ownerName+"Shop" : ownerName)+".yml"), Config.YAML);
			List<LotteryShop> lsList = lotShops.get(ownerName);
			if(lsList.size() > 0) {
				for(int i = 0; i < lsList.size(); i++) {
					shopConfig.set(Integer.toString(i), lsList.get(i).dataSection());
				}
			}
			shopConfig.save();
		}
		
		shopConfig = new Config(new File(this.getDataFolder(), PointShop.CONFIGPATH + 
				"ServerShop.yml"), Config.YAML);
		List<PointShop> pssList = ptShops.get(OwnerType.SERVER.getName());
		if(pssList != null) {
			if(pssList.size() > 0) {
				for(int i = 0; i < pssList.size(); i++) {
					shopConfig.set(Integer.toString(i), pssList.get(i).dataSection());
				}
			}
			shopConfig.save();
		}
		
		shopConfig = new Config(new File(this.getDataFolder(), TitleShop.CONFIGPATH + 
				"ServerShop.yml"), Config.YAML);
		List<TitleShop> tssList = titleShops.get(OwnerType.SERVER.getName());
		if(tssList != null) {
			if(tssList.size() > 0) {
				for(int i = 0; i < tssList.size(); i++) {
					shopConfig.set(Integer.toString(i), tssList.get(i).dataSection());
				}
			}
			shopConfig.save();
		}
		
		for(String ownerName : btShops.keySet()) {
			shopConfig = new Config(new File(this.getDataFolder(), BarterShop.CONFIGPATH + 
					(ownerName.equals("Server")? ownerName+"Shop" : ownerName)+".yml"), Config.YAML);
			List<BarterShop> btsList = btShops.get(ownerName);
			if(btsList.size() > 0) {
				for(int i = 0; i < btsList.size(); i++) {
					shopConfig.set(Integer.toString(i), btsList.get(i).dataSection());
				}
			}
			shopConfig.save();
		}
		
		//TODO: save more shops
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equals("cshop")) {
			if(args[0].equalsIgnoreCase("help")) {
				
			}else if(args[0].equalsIgnoreCase("ui")) {
				if(sender.isPlayer()) {
					Player p = (Player) sender;
					p.showFormWindow(CShopUI.homePage());
				}
			}
			else if(args[0].equalsIgnoreCase("my")) {
				String list = "";
				Player p = (Player) sender;
				if(purchaseShops.containsKey(p.getName()))
					for(PurchaseShop ps : purchaseShops.get(p.getName())) {
						list += ps.Information(p.getName()) + "\n";
					}
				if(soldShops.containsKey(p.getName()))
					for(SoldShop ss : soldShops.get(p.getName())) {
						list += ss.Information(p.getName());
					}
				if(lotShops.containsKey(p.getName()))
					for(LotteryShop ls : lotShops.get(p.getName())) {
						list += ls.Information(p.getName());
					}
				if(btShops.containsKey(p.getName()))
					for(BarterShop bts : btShops.get(p.getName())) {
						list += bts.Information(p.getName());
					}
				p.sendMessage(list);
			}
			else if(args[0].equalsIgnoreCase("build") || args[0].equalsIgnoreCase("b")) {
				Player p = (Player) sender;
				BuildTool bt = new BuildTool(p);
				bt.prompt();
				buildingPool.put(p.getName(), new BuildTool(p));
			}
			else if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("r")) {
				Player p = (Player) sender;
				if(!removingPool.contains(p.getName())) {
					removingPool.add(p.getName());
					p.sendMessage(TextFormat.ITALIC + (TextFormat.GOLD + "您已進入了移除模式\n" + TextFormat.GRAY + "若欲停止請再次輸入指令或輸入@cancel"));
				}else {
					removingPool.remove(p.getName());
					p.sendMessage(TextFormat.ITALIC + (TextFormat.GRAY + "您已結束了移除模式"));
				}
			}
		}
		return true;
	}
	
	public static CShop getShopData(Block block) {
		if(block == null) {
			CShopMain.getInstance().getLogger().info(TextFormat.RED + "block is null!!");
			return null;
		}
		if(block.getId() == 68) {
			BlockEntitySign sign = (BlockEntitySign) block.getLevel().getBlockEntity(block);
			String[] lines = sign.getText();
			
			if(lines.length < 4) return null;
			if(lines[0] == null || lines[1] == null || lines[2] == null || lines[3] == null) return null;
			//判斷第二行的文字是否為木牌商店的格式，若符合才判斷它是否為木牌商店(節省資源)
			if(lines[1].startsWith(TextFormat.BOLD + (TextFormat.GREEN + "擁有者: "))) {
				String owner = lines[1].replace(TextFormat.BOLD + (TextFormat.GREEN + "擁有者: ") + TextFormat.WHITE, "");
				if(lines[2].startsWith(TextFormat.BOLD + (TextFormat.AQUA + "收" + TextFormat.WHITE + ">"))) {
					if(purchaseShops.containsKey(owner))
						for(PurchaseShop ps : purchaseShops.get(owner)) {
							if(ps.getLevelName().equals(block.getLevel().getFolderName()) && 
									ps.getSignPos().equals(new Vector3(sign.getX(), sign.getY(), sign.getZ()))) 
								return ps;
						}
				}
					
				if(lines[2].startsWith(TextFormat.BOLD + (TextFormat.AQUA + "賣" + TextFormat.WHITE + ">"))) {
					if(soldShops.containsKey(owner))
						for(SoldShop ss : soldShops.get(owner)) {
							if(ss.getLevelName().equals(block.getLevel().getFolderName()) && 
									ss.getSignPos().equals(new Vector3(sign.getX(), sign.getY(), sign.getZ()))) 
								return ss;
						}
				}
				
				if(lines[2].equals(TextFormat.BOLD + (TextFormat.AQUA + ">>抽獎商店<<"))) {
					if(lotShops.containsKey(owner))
						for(LotteryShop ls : lotShops.get(owner)) {
							if(ls.getPosition().equals(block))
								return ls;
						}
				}
				
				if(lines[2].startsWith(TextFormat.BOLD + (TextFormat.LIGHT_PURPLE + "賣" + TextFormat.WHITE + ">"))) {
					if(ptShops.containsKey(OwnerType.SERVER.getName()))
						for(PointShop pss : ptShops.get(OwnerType.SERVER.getName())) {
							if(pss.getLevelName().equals(block.getLevel().getFolderName()) && 
									pss.getSignPos().floor().equals(new Vector3(block.getX(), block.getY(), block.getZ()))) 
								return pss;
						}
				}
				
				if(lines[2].startsWith(TextFormat.BOLD + (TextFormat.DARK_GREEN + "售" + TextFormat.WHITE + ">"))) {
					if(titleShops.containsKey(OwnerType.SERVER.getName()))
						for(TitleShop tss : titleShops.get(OwnerType.SERVER.getName())) {
							if(tss.getLevelName().equals(block.getLevel().getFolderName()) && 
									tss.getSignPos().floor().equals(new Vector3(block.getX(), block.getY(), block.getZ()))) 
								return tss;
						}
				}
				
				if(lines[2].startsWith(TextFormat.BOLD + (TextFormat.AQUA + "出" + TextFormat.WHITE + ">"))) {
					if(btShops.containsKey(owner))
						for(BarterShop bts : btShops.get(owner)) {
							if(bts.getLevelName().equals(block.getLevel().getFolderName()) && 
									bts.getSignPos().floor().equals(new Vector3(block.getX(), block.getY(), block.getZ()))) 
								return bts;
						}
				}
				//TODO: more shop
			}
		}
		else if(block.getId() == 155) {
			for(String owner : purchaseShops.keySet()) {
				for(PurchaseShop ps : purchaseShops.get(owner)) {
					if(ps.getLevelName().equals(block.getLevel().getFolderName()) && 
							ps.getItemPos().floor().equals(new Vector3(block.getX(), block.getY()+1, block.getZ()))) 
						return ps;
				}
			}
			for(String owner : soldShops.keySet()) {
				for(SoldShop ss : soldShops.get(owner)) {
					if(ss.getLevelName().equals(block.getLevel().getFolderName()) && 
							ss.getItemPos().floor().equals(new Vector3(block.getX(), block.getY()+1, block.getZ()))) 
						return ss;
				}
			}
			for(String owner : lotShops.keySet()) {
				for(LotteryShop ls : lotShops.get(owner)) {
					if(ls.getLevelName().equals(block.getLevel().getFolderName()) && 
							ls.getItemPos().floor().equals(new Vector3(block.getX(), block.getY()+1, block.getZ()))) 
						return ls;
				}
			}
			for(String owner : ptShops.keySet()) {
				for(PointShop pss : ptShops.get(owner)) {
					if(pss.getLevelName().equals(block.getLevel().getFolderName()) && 
							pss.getItemPos().floor().equals(new Vector3(block.getX(), block.getY()+1, block.getZ()))) 
						return pss;
				}
			}
			for(String owner : titleShops.keySet()) {
				for(TitleShop tss : titleShops.get(owner)) {
					if(tss.getLevelName().equals(block.getLevel().getFolderName()) && 
							tss.getItemPos().floor().equals(new Vector3(block.getX(), block.getY()+1, block.getZ()))) 
						return tss;
				}
			}
			for(String owner : btShops.keySet()) {
				for(BarterShop bts : btShops.get(owner)) {
					if(bts.getLevelName().equals(block.getLevel().getFolderName()) && 
							bts.getItemPos().floor().equals(new Vector3(block.getX(), block.getY()+1, block.getZ()))) 
						return bts;
				}
			}
			//TODO: more shop
		}
		else if(block.getId() == 20) {
			for(String owner : purchaseShops.keySet()) {
				for(PurchaseShop ps : purchaseShops.get(owner)) {
					if(ps.getLevelName().equals(block.getLevel().getFolderName()) && 
							ps.getItemPos().floor().equals(new Vector3(block.getX(), block.getY(), block.getZ()))) 
						return ps;
				}
			}
			for(String owner : soldShops.keySet()) {
				for(SoldShop ss : soldShops.get(owner)) {
					if(ss.getLevelName().equals(block.getLevel().getFolderName()) && 
							ss.getItemPos().floor().equals(new Vector3(block.getX(), block.getY(), block.getZ()))) 
						return ss;
				}
			}
			for(String owner : lotShops.keySet()) {
				for(LotteryShop ls : lotShops.get(owner)) {
					if(ls.getLevelName().equals(block.getLevel().getFolderName()) && 
							ls.getItemPos().floor().equals(new Vector3(block.getX(), block.getY(), block.getZ()))) 
						return ls;
				}
			}
			for(String owner : ptShops.keySet()) {
				for(PointShop pss : ptShops.get(owner)) {
					if(pss.getLevelName().equals(block.getLevel().getFolderName()) && 
							pss.getItemPos().floor().equals(new Vector3(block.getX(), block.getY(), block.getZ()))) 
						return pss;
				}
			}
			for(String owner : titleShops.keySet()) {
				for(TitleShop tss : titleShops.get(owner)) {
					if(tss.getLevelName().equals(block.getLevel().getFolderName()) && 
							tss.getItemPos().floor().equals(new Vector3(block.getX(), block.getY(), block.getZ()))) 
						return tss;
				}
			}
			for(String owner : btShops.keySet()) {
				for(BarterShop bts : btShops.get(owner)) {
					if(bts.getLevelName().equals(block.getLevel().getFolderName()) && 
							bts.getItemPos().floor().equals(new Vector3(block.getX(), block.getY(), block.getZ()))) 
						return bts;
				}
			}
			//TODO: more shop
		}else 
			return null;
		
		return null;
	}
}
