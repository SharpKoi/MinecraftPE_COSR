package cosr.shop.utils;

import java.util.List;

import cmen.essalg.CJEF;
import cn.nukkit.Server;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import cosr.economy.CMoney;
import cosr.economy.CPoint;
import cosr.shop.CShopMain;
import cosr.shop.ItemSingle;
import cosr.shop.listener.GuiEventListener;
import cosr.shop.shops.BarterShop;
import cosr.shop.shops.CShop;
import cosr.shop.shops.SoldShop;
import cosr.shop.shops.TitleShop;
import cosr.shop.shops.TitleShop.CostType;
import cosr.shop.shops.CShop.OwnerType;
import cosr.shop.shops.LotteryShop;
import cosr.shop.shops.PointShop;
import cosr.shop.shops.PurchaseShop;

public class CShopUI {
	
	private static Config conf = CShop.getShopConfig();
	
	public static FormWindowSimple homePage() {
		FormWindowSimple window = new FormWindowSimple("商店首頁", "");
		window.addButton(new ElementButton(TextFormat.BOLD + "逛逛市集"));
		window.addButton(new ElementButton(TextFormat.BOLD + "我的商店"));
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "關閉視窗")));
		return window;
	}

	/* 逛逛市集 TODO:Simple */
	public static FormWindowSimple marketWindow() {
		FormWindowSimple window = new FormWindowSimple(conf.getString("svfair_name", "市集"), "請選擇交易種類");
		window.addButton(new ElementButton(TextFormat.BOLD + "購買物品"));
		window.addButton(new ElementButton(TextFormat.BOLD + "出售物品"));
		window.addButton(new ElementButton(TextFormat.BOLD + "交換物品"));
		window.addButton(new ElementButton(TextFormat.BOLD + "幸運抽獎"));
		window.addButton(new ElementButton(TextFormat.BOLD + "購買稱號"));
		window.addButton(new ElementButton(TextFormat.BOLD + "稀有商店"));
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "返回至首頁")));
		return window;
	}

	// 出售型商店市集 TODO:Simple
	public static FormWindowSimple soldShopListWindow(String viewer) {
		FormWindowSimple window = new FormWindowSimple("販賣商店市集", "請選擇其中一個市集去逛逛吧");
		for(String ownerName : CShopMain.getSoldShopMap().keySet()) {
			//List<SoldShop> sShopList = CShopMain.getSoldShopMap().get(ownerName);
			if (!ownerName.equals(viewer) && CShopMain.getSoldShopMap().get(ownerName).size() > 0) {
					window.addButton(new ElementButton((ownerName.equals("Server")? conf.getString("svfair_name", "伺服器商店市集") : ownerName)));
			}
		}
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "返回")));
		return window;
	}

	// 購買型商店市集 TODO:Simple
	public static FormWindowSimple purchaseShopListWindow(String viewer) {
		FormWindowSimple window = new FormWindowSimple("收購商店市集", "請選擇其中一個市集去逛逛吧");
		for (String ownerName : CShopMain.getPurchaseShopMap().keySet()) {
			if (!ownerName.equals(viewer) && CShopMain.getPurchaseShopMap().get(ownerName).size() > 0)
				window.addButton(new ElementButton((ownerName.equals("Server")? conf.getString("svfair_name", "伺服器商店市集") : ownerName)));
		}
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "返回")));
		return window;
	}
	
	// 抽獎商店市集 TODO:Simple
	public static FormWindowSimple lotteryShopListWindow(String viewer) {
		FormWindowSimple window = new FormWindowSimple("抽獎商店市集", "請選擇其中一個市集去逛逛吧");
		for (String ownerName : CShopMain.getLotteryShopMap().keySet()) {
			if (!ownerName.equals(viewer) && CShopMain.getLotteryShopMap().get(ownerName).size() > 0)
				window.addButton(new ElementButton((ownerName.equals("Server")? conf.getString("svfair_name", "伺服器商店市集") : ownerName)));
		}
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "返回")));
		return window;
	}
	
	// 兌換商店市集 TODO:Simple
	public static FormWindowSimple barterShopListWindow(String viewer) {
		FormWindowSimple window = new FormWindowSimple("兌換商店市集", "請選擇其中一個市集去逛逛吧");
		for (String ownerName : CShopMain.getBarterShopMap().keySet()) {
			if (!ownerName.equals(viewer) && CShopMain.getBarterShopMap().get(ownerName).size() > 0)
				window.addButton(new ElementButton((ownerName.equals("Server")? conf.getString("svfair_name", "伺服器商店市集") : ownerName)));
		}
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "返回")));
		return window;
	}

	// 稱號商店市集 TODO:Simple
	public static FormWindowSimple titleShopListWindow(String viewer) {
		FormWindowSimple window = new FormWindowSimple("稱號商店列表", "請選擇其中一個商店看看吧");
		if(CShopMain.getTitleShopMap().containsKey(OwnerType.SERVER.getName())) {
			if (CShopMain.getTitleShopMap().get(OwnerType.SERVER.getName()).size() > 0) {
				int i = 0;
				for(TitleShop ts : CShopMain.getTitleShopMap().get(OwnerType.SERVER.getName())) {
					window.addButton(new ElementButton(TextFormat.ITALIC + "#" + i + " " + TextFormat.RESET + ts.getName() + 
							(TextFormat.RESET+" (") + TextFormat.BOLD+ts.getTitle().getRarity().getColor()+ts.getTitle().getHead()+(TextFormat.RESET+")")));
					i++;
				}
			}
		}
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "返回")));
		return window;
	}
	
	// 稀有商店市集 TODO:Simple
	public static FormWindowSimple pointShopListWindow(String viewer) {
		FormWindowSimple window = new FormWindowSimple("稀有商店列表", "請選擇其中一個商店看看吧");
		if(CShopMain.getPointShopMap().containsKey(OwnerType.SERVER.getName())) {
			if (CShopMain.getPointShopMap().get(OwnerType.SERVER.getName()).size() > 0) {
				int i = 0;
				for(PointShop pts : CShopMain.getPointShopMap().get(OwnerType.SERVER.getName()))
					window.addButton(new ElementButton(TextFormat.ITALIC + "#" + i + " " + TextFormat.RESET + pts.getName() + 
						(TextFormat.RESET+" (") + TextFormat.LIGHT_PURPLE+pts.getItem().getName()+(TextFormat.RESET+")")));
				i++;
			}
		}
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "返回")));
		return window;
	}
	
	//owner的商店 TODO: Simple
	public static FormWindowSimple pShopListWindow(String owner, Class<?> shopClass) {
		FormWindowSimple window = null;
		if(shopClass.getSimpleName().equalsIgnoreCase("SoldShop")) {
			window = new FormWindowSimple((owner.equals(OwnerType.SERVER.getName())? conf.getString("svfair_name", "伺服器") : owner) + 
					"的販賣商店列表", "請選擇其中一個商店看看吧");
			List<SoldShop> sShopList = CShopMain.getSoldShopMap().get(owner);
			if (sShopList.size() > 0) {
				for (int i = 0; i < sShopList.size(); i++) {
					SoldShop sShop = sShopList.get(i);
					window.addButton(new ElementButton(
							TextFormat.WHITE + "#" + i + " " + TextFormat.DARK_GRAY + sShop.getName() + 
							(sShop.isCompleted()? "" : TextFormat.BOLD + (TextFormat.GRAY + "(未完成)"))));
				}
			}
		}else if(shopClass.getSimpleName().equalsIgnoreCase("PurchaseShop")) {
			window = new FormWindowSimple((owner.equals(OwnerType.SERVER.getName())? conf.getString("svfair_name", "伺服器") : owner) + 
					"的收購商店列表", "請選擇其中一個商店看看吧");
			List<PurchaseShop> pShopList = CShopMain.getPurchaseShopMap().get(owner);
			if (pShopList.size() > 0) {
				for (int i = 0; i < pShopList.size(); i++) {
					PurchaseShop pShop = pShopList.get(i);
					window.addButton(new ElementButton(
							TextFormat.WHITE + "#" + i + " " + TextFormat.DARK_GRAY + pShop.getName() + 
							(pShop.isCompleted()? "" : TextFormat.BOLD + (TextFormat.GRAY + "(未完成)"))));
				}
			}
		}else if(shopClass.getSimpleName().equalsIgnoreCase("BarterShop")) {
			window = new FormWindowSimple((owner.equals(OwnerType.SERVER.getName())? conf.getString("svfair_name", "伺服器") : owner) + 
					"的兌換商店列表", "請選擇其中一個商店看看吧");
			List<BarterShop> bShopList = CShopMain.getBarterShopMap().get(owner);
			if (bShopList.size() > 0) {
				for (int i = 0; i < bShopList.size(); i++) {
					BarterShop bShop = bShopList.get(i);
					window.addButton(new ElementButton(
							TextFormat.WHITE + "#" + i + " " + TextFormat.DARK_GRAY + bShop.getName() + 
							(bShop.isCompleted()? "" : TextFormat.BOLD + (TextFormat.GRAY + "(未完成)"))));
				}
			}
		}else if(shopClass.getSimpleName().equalsIgnoreCase("LotteryShop")) {
			window = new FormWindowSimple((owner.equals(OwnerType.SERVER.getName())? conf.getString("svfair_name", "伺服器") : owner) + 
					"的抽獎商店列表", "請選擇其中一個商店看看吧");
			List<SoldShop> sShopList = CShopMain.getSoldShopMap().get(owner);
			if (sShopList.size() > 0) {
				for (int i = 0; i < sShopList.size(); i++) {
					window.addButton(new ElementButton(
							TextFormat.WHITE + "#" + i + " " + TextFormat.DARK_GRAY + sShopList.get(i).getName()));
				}
			}
		}
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "返回")));
		return window;
	}

	// 光顧商店 XXX:Simple
	public static FormWindowSimple shopSystemWindow(String playerName) {
		CShop shop = GuiEventListener.uiCShopMap.get(playerName);
		if (shop == null) {
			return new FormWindowSimple("錯誤", TextFormat.RED + "找不到您所操作的商店");
		}
		
		FormWindowSimple window = new FormWindowSimple(shop.getName(), "請選擇您想對該商店執行的動作");
		window.addButton(new ElementButton(TextFormat.BOLD + "查看商店資訊"));
		if(Server.getInstance().isOp(playerName)) {
			window.addButton(new ElementButton(TextFormat.BOLD + "更改商店名稱"));
		}
		if (shop instanceof SoldShop) {
			window.addButton(new ElementButton(TextFormat.BOLD + "購買商品"));
		} else if (shop instanceof PurchaseShop) {
			window.addButton(new ElementButton(TextFormat.BOLD + "出售物品"));
		} else if (shop instanceof BarterShop) {
			window.addButton(new ElementButton(TextFormat.BOLD + "兌換商品"));
		} else if (shop instanceof LotteryShop) {
			window.addButton(new ElementButton(TextFormat.BOLD + "試試手氣"));
		} else if (shop instanceof TitleShop) {
			window.addButton(new ElementButton(TextFormat.BOLD + "購買稱號"));
		} else if (shop instanceof PointShop) {
			window.addButton(new ElementButton(TextFormat.BOLD + "購買稀有物品"));
		}
		//TODO: 花費消耗品
		window.addButton(new ElementButton(TextFormat.BOLD + "拜訪該商店(需花費50" + CMoney.name() + ")"));
		if (shop.getType().equals(OwnerType.PLAYER)) {
			window.addButton(new ElementButton(TextFormat.BOLD + "聯繫商店主人")); // 寄信介面
		}
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "返回至商店列表")));

		return window;
	}

	// 商店訊息 TODO:Modal
	public static FormWindowModal shopInfoWindow(String playerName) {
		CShop shop = GuiEventListener.uiCShopMap.get(playerName);
		if (shop == null) {
			return new FormWindowModal("錯誤", TextFormat.RED + "找不到您所操作的商店", "確認", "首頁");
		}
		FormWindowModal window = new FormWindowModal("商店訊息", shop.Information(playerName), "確認", "返回");
		return window;
	}

	// 確定交易 TODO:Modal
	public static FormWindowModal sureToTradeWindow(String playerName) {
		CShop shop = GuiEventListener.uiCShopMap.get(playerName);
		if (shop == null) {
			return new FormWindowModal("錯誤", TextFormat.RED + "找不到您所操作的商店", "確認", "首頁");
		}
		if (shop instanceof SoldShop) {
			return new FormWindowModal("是否確定購買?", shop.Information(playerName), "確定", "取消");
		} else if (shop instanceof PurchaseShop) {
			return new FormWindowModal("是否確定賣出?", shop.Information(playerName), "確定", "取消");
		} else if (shop instanceof BarterShop) {
			return new FormWindowModal("是否確定兌換?", shop.Information(playerName), "確定", "取消");
		} else if (shop instanceof LotteryShop) {
			return new FormWindowModal("是否確定抽獎?", shop.Information(playerName), "確定", "取消");
		} else if (shop instanceof TitleShop) {
			return new FormWindowModal("是否確定購買該稱號?", ((TitleShop)shop).getTitle().information(), "確定", "取消");
		} else if (shop instanceof PointShop) {
			return new FormWindowModal("是否確定購買?", shop.Information(playerName), "確定", "取消");
		} else
			return new FormWindowModal("商店類型未知", "", "確定", "取消");
	}

	// 交易成功 TODO:Modal
	public static FormWindowModal successfullyTradeWindow(String playerName) {
		CShop shop = GuiEventListener.uiCShopMap.get(playerName);
		if (shop == null) {
			return new FormWindowModal("錯誤", TextFormat.RED + "找不到您所操作的商店", "確認", "首頁");
		}
		if (shop instanceof SoldShop) {
			SoldShop sShop = ((SoldShop)shop);
			return new FormWindowModal("購買成功",
					"您花了" + sShop.getCost() + "元的金錢購買" + sShop.getItem().getCount() + "個" + sShop.getItem().getName(), "確認", "返回");
		} else if (shop instanceof PurchaseShop) {
			PurchaseShop pShop = ((PurchaseShop)shop);
			return new FormWindowModal("出售成功",
					"您售出了" + pShop.getItem().getCount() + "個" + pShop.getItem().getName() + "給賣家, 並獲得了" + pShop.getCost() + "元的金錢", "確認", "返回");
		} else if (shop instanceof BarterShop) {
			BarterShop bShop = ((BarterShop)shop);
			return new FormWindowModal("兌換成功", 
					"您交出了" + bShop.getItem().getCount() + "個" + bShop.getItem().getName() + "給賣家, 並獲得了" + 
							bShop.getCurrency().getCount() + "個" + bShop.getCurrency().getName(), "確認", "返回");
		} else if (shop instanceof LotteryShop) {
			Item result = GuiEventListener.uiLotResultMap.get(playerName);
			return new FormWindowModal("抽獎結果", "恭喜你抽中了" + result.getName() + "x" + result.getCount(), "確認", "返回");
		} else if (shop instanceof TitleShop) {
			TitleShop tShop = ((TitleShop)shop);
			return new FormWindowModal("購買成功", "您花了" + tShop.getCost() + 
					(tShop.getCostType().equals(CostType.MONEY)? CMoney.config.getString("name", "金錢") : CPoint.config.getString("name", "點券")) + 
					"購買了以下該稱號: \n\n" + 
					tShop.getTitle().information(), "確認", "返回");
		} else if (shop instanceof PointShop) {
			PointShop ptShop = ((PointShop)shop);
			return new FormWindowModal("購買成功", "您花了" + ptShop.getCost() + 
					CPoint.config.getString("name", "點券") + "購買了" + ptShop.getItem().getCount() + "個" + ptShop.getItem().getName(), "確定", "取消");
		} else
			return new FormWindowModal("商店類型未知", "", "確定", "取消");
	}

	/* 我的商店 (DONE) TODO:Simple */
	public static FormWindowSimple myShopListWindow(String owner) {
		FormWindowSimple window = new FormWindowSimple("我的商店列表", "");
		if(CShopMain.getPurchaseShopMap().containsKey(owner)) {
			List<PurchaseShop> shopList = CShopMain.getPurchaseShopMap().get(owner);
			if(shopList.size() > 0)
				for(int i = 0; i < shopList.size(); i++) {
					PurchaseShop pShop = shopList.get(i);
					window.addButton(new ElementButton(
							TextFormat.BOLD + (TextFormat.BLUE + "[收購商店" + CJEF.appendZero(2, i) +"]") + " " + TextFormat.RESET + TextFormat.DARK_GRAY + pShop.getName() + 
							(pShop.isCompleted()? "" : TextFormat.BOLD + (TextFormat.GRAY + "(未完成)"))));
				}
		}
			
		if(CShopMain.getSoldShopMap().containsKey(owner)) {
			List<SoldShop> shopList = CShopMain.getSoldShopMap().get(owner);
			if(shopList.size() > 0)
			for(int i = 0; i < shopList.size(); i++) {
				SoldShop sShop = shopList.get(i);
				window.addButton(new ElementButton(
						TextFormat.BOLD + (TextFormat.RED + "[販售商店" + CJEF.appendZero(2, i) +"]") + " " + TextFormat.RESET + TextFormat.DARK_GRAY + sShop.getName() + 
						(sShop.isCompleted()? "" : TextFormat.BOLD + (TextFormat.GRAY + "(未完成)"))));
			}
		}
			
		if(CShopMain.getBarterShopMap().containsKey(owner)) {
			List<BarterShop> shopList = CShopMain.getBarterShopMap().get(owner);
			if(shopList.size() > 0)
			for(int i = 0; i < shopList.size(); i++) {
				BarterShop sShop = shopList.get(i);
				window.addButton(new ElementButton(
						TextFormat.BOLD + (TextFormat.GOLD + "[兌換商店" + CJEF.appendZero(2, i) +"]") + " " + TextFormat.RESET + TextFormat.DARK_GRAY + sShop.getName() + 
						(sShop.isCompleted()? "" : TextFormat.BOLD + (TextFormat.GRAY + "(未完成)"))));
			}
		}
		
		if(CShopMain.getLotteryShopMap().containsKey(owner)) {
			List<LotteryShop> shopList = CShopMain.getLotteryShopMap().get(owner);
			if(shopList.size() > 0)
			for(int i = 0; i < shopList.size(); i++) {
				LotteryShop sShop = shopList.get(i);
				window.addButton(new ElementButton(
						TextFormat.BOLD + (TextFormat.AQUA + "[抽獎商店" + CJEF.appendZero(2, i) + "]") + " " + TextFormat.RESET + TextFormat.DARK_GRAY + sShop.getName() + 
						(sShop.isCompleted()? "" : TextFormat.BOLD + (TextFormat.GRAY + "(未完成)"))));
			}
		}
		
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "返回至首頁")));
		return window;
	}

	/*
	// 創建個人商店 TODO:Custom
	public static FormWindowCustom newPShopWindow(String builder) {
		FormWindowCustom window = new FormWindowCustom("創建個人商店");
		window.addElement(new ElementInput("請輸入商店名稱")); // 0
		ElementDropdown tradeTypeList = new ElementDropdown("請選擇交易類型"); // 1
		tradeTypeList.addOption("販售(sell)");
		tradeTypeList.addOption("收購(buy)");
		tradeTypeList.addOption("兌換(barter)");
		tradeTypeList.addOption("抽獎(lottery)");
		window.addElement(tradeTypeList);
		window.addElement(new ElementInput("請指定交易物品", "Example: 274 5:2 @h")); // 2
		window.addElement(new ElementSlider("請輸入交易數量", 0, 64, 1)); // 3
		window.addElement(new ElementInput("請輸入成交價格")); // 4
		return window;
	}
	*/

	// 我的商店系統 TODO:Simple
	public static FormWindowSimple myShopSystemWindow(String playerName) {
		CShop myShop = GuiEventListener.uiPShopMap.get(playerName);
		if (myShop == null) {
			return new FormWindowSimple("錯誤", TextFormat.RED + "找不到您所操作的商店");
		}
		FormWindowSimple window = new FormWindowSimple(myShop.getName(), "");
		window.addButton(new ElementButton(TextFormat.BOLD + "查看商店當前資訊"));
		window.addButton(new ElementButton(TextFormat.BOLD + "更改商店名稱"));
		//TODO: 花費消耗品
		window.addButton(new ElementButton(TextFormat.BOLD + "傳送至該商店(需花費50" + CMoney.name() + ")"));
		if(!(myShop instanceof PurchaseShop))
			window.addButton(new ElementButton(TextFormat.BOLD + "補貨"));
		window.addButton(new ElementButton(TextFormat.BOLD + "提取物品"));
		
		window.addButton(new ElementButton(TextFormat.BOLD + "替該商店打廣告"));
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.RED + "刪除該商店")));
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "返回至商店列表")));
		return window;
	}

	// 我的商店訊息 TODO:Modal
	public static FormWindowModal myShopInfoWindow(String owner) {
		CShop myShop = GuiEventListener.uiPShopMap.get(owner);
		if (myShop == null) {
			return new FormWindowModal("錯誤", "", "確認", "首頁");
		}
		FormWindowModal window = new FormWindowModal("我的商店" + myShop.getName() + TextFormat.RESET + "資訊",
				myShop.Information(owner), "確認", "返回");
		return window;
	}
	
	// 重新命名商店 CUSTOM
	public static FormWindowCustom shopRenameWindow(String renamer) {
		CShop shop = null;
		FormWindowCustom window = new FormWindowCustom("重新命名商店");
		if(GuiEventListener.uiCShopMap.containsKey(renamer)) {
			shop = GuiEventListener.uiCShopMap.get(renamer);
			if(shop.getType().equals(OwnerType.PLAYER) || !Server.getInstance().isOp(renamer)) {
				window.addElement(new ElementLabel("抱歉, 您的權限不足以變更此商店"));
				return window;
			}
		}else if(GuiEventListener.uiPShopMap.containsKey(renamer)) {
			shop = GuiEventListener.uiPShopMap.get(renamer);
		}else {
			return new FormWindowCustom("Bad ui");
		}
		
		window.addElement(new ElementLabel(shop.Information(renamer)));
		window.addElement(new ElementInput("請輸入商店名", "商店名稱", shop.getName()));
		return window;
	}

	// 替...商店補貨 TODO:Custom
	public static FormWindowCustom replenishWindow(String owner) {
		CShop myShop = GuiEventListener.uiPShopMap.get(owner);
		if (myShop == null) {
			return new FormWindowCustom("錯誤");
		}
		FormWindowCustom window = new FormWindowCustom("替" + myShop.getName() + TextFormat.RESET + "商店補貨");
		String info = "";
		if(myShop instanceof ItemSingle) {
			ItemSingle _myShop = (ItemSingle)myShop;
			info = TextFormat.BOLD + (TextFormat.GREEN + _myShop.getItem().getName()) + TextFormat.RESET
					+ TextFormat.DARK_GREEN + "當前貨存數: " + TextFormat.RESET + _myShop.getStock() + "\n"
					+ TextFormat.DARK_GREEN + "一次交易數量: " + TextFormat.RESET + _myShop.getItem().getCount() + "\n";
			if(myShop instanceof BarterShop) {
				BarterShop bShop = (BarterShop)_myShop;
				info += TextFormat.BOLD + (TextFormat.GREEN + bShop.getCurrency().getName()) + TextFormat.RESET
						+ TextFormat.DARK_GREEN + "當前貨存數: " + TextFormat.RESET + bShop.getCurrencyStock() + "\n"
						+ TextFormat.DARK_GREEN + "一次交易數量: " + TextFormat.RESET + bShop.getCurrency().getCount() + "\n";
			}
				
			window.addElement(new ElementLabel(info));
		}else {
			if(myShop instanceof LotteryShop) {
				LotteryShop lShop = ((LotteryShop)myShop);
				ElementDropdown itemList = new ElementDropdown("請選擇補貨物品"); 
				for(Item item : lShop.getStockMap().keySet()) {
					info += TextFormat.BOLD + (TextFormat.GREEN + item.getName()) + TextFormat.RESET
							+ TextFormat.DARK_GREEN + "當前貨存數: " + TextFormat.RESET + lShop.getStockMap().get(item) + "\n"
							+ TextFormat.DARK_GREEN + "一次交易數量: " + TextFormat.RESET + item.getCount() + "\n"
							+ TextFormat.DARK_GREEN + "獎品機率: " + TextFormat.RESET + lShop.getProbabilityMap().get(item) + "\n";
					itemList.addOption(item.getName() + "("+item.getCount()+")");
				}
				window.addElement(new ElementLabel(info));
				window.addElement(itemList);
			}
		}
		window.addElement(new ElementInput("請輸入補充貨物數量"));
		return window;
	}

	// 從...商店中提取物品 TODO:Custom
	public static FormWindowCustom extractWindow(String owner) {
		CShop myShop = GuiEventListener.uiPShopMap.get(owner);
		if (myShop == null) {
			return new FormWindowCustom("錯誤");
		}
		FormWindowCustom window = new FormWindowCustom("從" + myShop.getName() + TextFormat.RESET + "商店中提取物品");
		String info = "";
		if(myShop instanceof ItemSingle) {
			ItemSingle _myShop = (ItemSingle)myShop;
			info = TextFormat.BOLD + (TextFormat.GREEN + _myShop.getItem().getName()) + TextFormat.RESET
					+ TextFormat.DARK_GREEN + "當前貨存數: " + TextFormat.RESET + _myShop.getStock() + "\n"
					+ TextFormat.DARK_GREEN + "一次交易數量: " + TextFormat.RESET + _myShop.getItem().getCount() + "\n";
			if(myShop instanceof BarterShop) {
				BarterShop bShop = (BarterShop)_myShop;
				info += TextFormat.BOLD + (TextFormat.GREEN + bShop.getCurrency().getName()) + TextFormat.RESET
						+ TextFormat.DARK_GREEN + "當前貨存數: " + TextFormat.RESET + bShop.getCurrencyStock() + "\n"
						+ TextFormat.DARK_GREEN + "一次交易數量: " + TextFormat.RESET + bShop.getCurrency().getCount() + "\n";
				window.addElement(new ElementLabel(info));
				ElementDropdown itemList = new ElementDropdown("請選擇提取物品"); 
				itemList.addOption(bShop.getItem().getName());
				itemList.addOption(bShop.getCurrency().getName());
				window.addElement(new ElementLabel(info));
				window.addElement(itemList);
			}else {
				window.addElement(new ElementLabel(info));
			}
		}else {
			if(myShop instanceof LotteryShop) {
				LotteryShop lShop = ((LotteryShop)myShop);
				ElementDropdown itemList = new ElementDropdown("請選擇提取物品"); 
				for(Item item : lShop.getStockMap().keySet()) {
					info += TextFormat.BOLD + (TextFormat.GREEN + item.getName()) + TextFormat.RESET
							+ TextFormat.DARK_GREEN + "當前貨存數: " + TextFormat.RESET + lShop.getStockMap().get(item) + "\n"
							+ TextFormat.DARK_GREEN + "一次交易數量: " + TextFormat.RESET + item.getCount() + "\n"
							+ TextFormat.DARK_GREEN + "獎品機率: " + TextFormat.RESET + lShop.getProbabilityMap().get(item) + "\n";
					itemList.addOption(item.getName() + "("+item.getCount()+")");
				}
				window.addElement(new ElementLabel(info));
				window.addElement(itemList);
			}
		}
		window.addElement(new ElementInput("請輸入提取物品數量"));

		return window;
	}

	// 替...打廣告 TODO:Custom
	public static FormWindowCustom advertiseWindow(String owner) {
		CShop pShop = GuiEventListener.uiPShopMap.get(owner);
		if (pShop == null) {
			return new FormWindowCustom("錯誤");
		}
		FormWindowCustom window = new FormWindowCustom("替" + pShop.getName() + TextFormat.RESET + "打廣告");
		String info = pShop.Information(owner);
		window.addElement(new ElementLabel(info));
		window.addElement(new ElementInput("請輸入廣告信息", "歡迎光臨!!"));
		window.addElement(new ElementInput("請輸入廣告次數", "5"));

		return window;
	}

	// 確定刪除該商店? TODO:Modal
	public static FormWindowModal sureToRemoveShopWindow(String owner) {
		CShop pShop = GuiEventListener.uiPShopMap.get(owner);
		if (pShop == null) {
			return new FormWindowModal("錯誤", "", "確認", "首頁");
		}
		String warning = TextFormat.ITALIC + (TextFormat.RED + "! 刪除後將無法恢復");
		FormWindowModal window = new FormWindowModal("確定刪除該商店?", "", "確定", "取消");
		window.setContent(warning + "\n" + TextFormat.RESET + pShop.Information(owner));
		return window;
	}
}
