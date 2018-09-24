package cosr.shop.listener;

import java.util.HashMap;

import cmen.essalg.CJEF;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseData;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import cosr.economy.CMoney;
import cosr.mcpemail.MailGUI;
import cosr.shop.CShopMain;
import cosr.shop.Sellable;
import cosr.shop.shops.BarterShop;
import cosr.shop.shops.CShop;
import cosr.shop.shops.SoldShop;
import cosr.shop.shops.TitleShop;
import cosr.shop.shops.CShop.OwnerType;
import cosr.shop.shops.LotteryShop;
import cosr.shop.shops.PointShop;
import cosr.shop.shops.PurchaseShop;
import cosr.shop.utils.CAdvertisement;
import cosr.shop.utils.CShopUI;

public class GuiEventListener implements Listener {
	
	public static HashMap<String, CShop> uiPShopMap = new HashMap<String, CShop>();				//玩家經營自己的個人商店
	public static HashMap<String, CShop> uiCShopMap = new HashMap<String, CShop>();				//玩家造訪他人商店
	public static HashMap<String, Item> uiLotResultMap = new HashMap<String, Item>();
	
	@EventHandler
	public void onForm(PlayerFormRespondedEvent event) {
		Player p = event.getPlayer();
		FormWindow window = event.getWindow();
		FormResponse response = event.getResponse();
		
		if(response == null) {
			flush(p);
			return;
		}
		
		if(window instanceof FormWindowSimple) {
			FormWindowSimple w = (FormWindowSimple)window;
			FormResponseSimple r = (FormResponseSimple)response;
			String btxt = r.getClickedButton().getText();
			if(w.getTitle().equals("商店首頁")) {
				if(btxt.equals(TextFormat.BOLD + "逛逛市集")) {
					p.showFormWindow(CShopUI.marketWindow());
				}
				else if(btxt.equals(TextFormat.BOLD + "我的商店")) {
					p.showFormWindow(CShopUI.myShopListWindow(p.getName()));
				}
				else if(w.getTitle().equals(TextFormat.BOLD + (TextFormat.GRAY + "關閉視窗"))) {
					flush(p);
				}
			}
			else if(w.getTitle().equals(CShop.getShopConfig().getString("svfair_name", "市集"))) {
				if(btxt.equals(TextFormat.BOLD + "購買物品")) {
					p.showFormWindow(CShopUI.soldShopListWindow(p.getName()));
				}
				else if(btxt.equals(TextFormat.BOLD + "出售物品")) {
					p.showFormWindow(CShopUI.purchaseShopListWindow(p.getName()));
				}
				else if(btxt.equals(TextFormat.BOLD + "交換物品")) {
					p.showFormWindow(CShopUI.barterShopListWindow(p.getName()));
				}
				else if(btxt.equals(TextFormat.BOLD + "幸運抽獎")) {
					p.showFormWindow(CShopUI.lotteryShopListWindow(p.getName()));
				}
				else if(btxt.equals(TextFormat.BOLD + "購買稱號")) {
					p.showFormWindow(CShopUI.titleShopListWindow(p.getName()));
				}
				else if(btxt.equals(TextFormat.BOLD + "稀有商店")) {
					p.showFormWindow(CShopUI.pointShopListWindow(p.getName()));
				}else if(btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "返回至首頁"))) {
					p.showFormWindow(CShopUI.homePage());
				}
			}
			else if(w.getTitle().equals("販賣商店市集")) {
				if(!btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "返回"))) {
					String ownerName = (btxt.equals(CShop.getShopConfig().getString("svfair_name", "伺服器商店市集"))? 
							OwnerType.SERVER.getName() : btxt);
					p.showFormWindow(CShopUI.pShopListWindow(ownerName, SoldShop.class));
				}else
					p.showFormWindow(CShopUI.marketWindow());
			}
			else if(w.getTitle().equals("收購商店市集")) {
				if(!btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "返回"))) {
					String ownerName = (btxt.equals(CShop.getShopConfig().getString("svfair_name", "伺服器商店市集"))? 
							OwnerType.SERVER.getName() : btxt);
					p.showFormWindow(CShopUI.pShopListWindow(ownerName, PurchaseShop.class));
				}else
					p.showFormWindow(CShopUI.marketWindow());
			}
			else if(w.getTitle().equals("兌換商店市集")) {
				if(!btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "返回"))) {
					String ownerName = (btxt.equals(CShop.getShopConfig().getString("svfair_name", "伺服器商店市集"))? 
							OwnerType.SERVER.getName() : btxt);
					p.showFormWindow(CShopUI.pShopListWindow(ownerName, BarterShop.class));
				}else
					p.showFormWindow(CShopUI.marketWindow());
			}
			else if(w.getTitle().equals("抽獎商店市集")) {
				if(!btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "返回"))) {
					String ownerName = (btxt.equals(CShop.getShopConfig().getString("svfair_name", "伺服器商店市集"))? 
							OwnerType.SERVER.getName() : btxt);
					p.showFormWindow(CShopUI.pShopListWindow(ownerName, LotteryShop.class));
				}else
					p.showFormWindow(CShopUI.marketWindow());
			}
			else if(w.getTitle().equals("稱號商店列表")) {
				if(!btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "返回"))) {
					int index = Integer.parseInt(btxt.split(" ")[0].replace(TextFormat.ITALIC + "#", ""));
					uiCShopMap.put(p.getName(), CShopMain.getTitleShopMap().get(OwnerType.SERVER.getName()).get(index));
					p.showFormWindow(CShopUI.shopSystemWindow(p.getName()));
				}else
					p.showFormWindow(CShopUI.marketWindow());
			}
			else if(w.getTitle().equals("稀有商店列表")) {
				if(!btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "返回"))) {
					int index = Integer.parseInt(btxt.split(" ")[0].replace(TextFormat.ITALIC + "#", ""));
					uiCShopMap.put(p.getName(), CShopMain.getPointShopMap().get(OwnerType.SERVER.getName()).get(index));
					p.showFormWindow(CShopUI.shopSystemWindow(p.getName()));
				}else
					p.showFormWindow(CShopUI.marketWindow());
			}
			else if(w.getTitle().endsWith("的販賣商店列表")) {
				if(!btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "返回"))) {
					String ownerName = (w.getTitle().startsWith(CShop.getShopConfig().getString("svfair_name", "伺服器"))? 
							OwnerType.SERVER.getName() : w.getTitle().replace("的販賣商店列表", ""));
					int index = Integer.parseInt(btxt.split(" ")[0].replace(TextFormat.WHITE + "#", ""));
					uiCShopMap.put(p.getName(), CShopMain.getSoldShopMap().get(ownerName).get(index));
					p.showFormWindow(CShopUI.shopSystemWindow(p.getName()));
				}else
					p.showFormWindow(CShopUI.soldShopListWindow(p.getName()));
			}
			else if(w.getTitle().endsWith("的收購商店列表")) {
				if(!btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "返回"))) {
					String ownerName = (w.getTitle().startsWith(CShop.getShopConfig().getString("svfair_name", "伺服器"))? 
							OwnerType.SERVER.getName() : w.getTitle().replace("的販賣商店列表", ""));
					int index = Integer.parseInt(btxt.split(" ")[0].replace(TextFormat.WHITE + "#", ""));
					uiCShopMap.put(p.getName(), CShopMain.getPurchaseShopMap().get(ownerName).get(index));
					p.showFormWindow(CShopUI.shopSystemWindow(p.getName()));
				}else
					p.showFormWindow(CShopUI.purchaseShopListWindow(p.getName()));
			}
			else if(w.getTitle().endsWith("的兌換商店列表")) {
				if(!btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "返回"))) {
					String ownerName = (w.getTitle().startsWith(CShop.getShopConfig().getString("svfair_name", "伺服器"))? 
							OwnerType.SERVER.getName() : w.getTitle().replace("的販賣商店列表", ""));
					int index = Integer.parseInt(btxt.split(" ")[0].replace(TextFormat.WHITE + "#", ""));
					uiCShopMap.put(p.getName(), CShopMain.getBarterShopMap().get(ownerName).get(index));
					p.showFormWindow(CShopUI.shopSystemWindow(p.getName()));
				}else
					p.showFormWindow(CShopUI.barterShopListWindow(p.getName()));
			}
			else if(w.getTitle().endsWith("的抽獎商店列表")) {
				if(!btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "返回"))) {
					String ownerName = (w.getTitle().startsWith(CShop.getShopConfig().getString("svfair_name", "伺服器"))? 
							OwnerType.SERVER.getName() : w.getTitle().replace("的販賣商店列表", ""));
					int index = Integer.parseInt(btxt.split(" ")[0].replace(TextFormat.WHITE + "#", ""));
					uiCShopMap.put(p.getName(), CShopMain.getLotteryShopMap().get(ownerName).get(index));
					p.showFormWindow(CShopUI.shopSystemWindow(p.getName()));
				}else
					p.showFormWindow(CShopUI.lotteryShopListWindow(p.getName()));
			}
			else if(w.getTitle().equals("我的商店列表")) {
				if(!btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "返回至首頁"))) {
					String[] parts = btxt.replace(btxt.split("[")[0]+"[", "").split("] ");
					// [XX商店01] 商店名稱  ->  XX商店01 & 商店名稱
					if(parts[0].startsWith("收購商店")) {
						int index = Integer.parseInt(CJEF.drop(parts[0].replaceAll("0", ""), "收購商店"));
						uiPShopMap.put(p.getName(), CShopMain.getPurchaseShopMap().get(p.getName()).get(index));
					}
					else if(parts[0].startsWith("販售商店")) {
						int index = Integer.parseInt(CJEF.drop(parts[0].replaceAll("0", ""), "販賣商店"));
						uiPShopMap.put(p.getName(), CShopMain.getSoldShopMap().get(p.getName()).get(index));
					}
					else if(parts[0].startsWith("兌換商店")) {
						int index = Integer.parseInt(CJEF.drop(parts[0].replaceAll("0", ""), "兌換商店"));
						uiPShopMap.put(p.getName(), CShopMain.getBarterShopMap().get(p.getName()).get(index));
					}
					else if(parts[0].startsWith("抽獎商店")) {
						int index = Integer.parseInt(CJEF.drop(parts[0].replaceAll("0", ""), "抽獎商店"));
						uiPShopMap.put(p.getName(), CShopMain.getLotteryShopMap().get(p.getName()).get(index));
					}
					p.showFormWindow(CShopUI.myShopSystemWindow(p.getName()));
				}else {
					p.showFormWindow(CShopUI.homePage());
				}
			}
			else {
				if(uiCShopMap.containsKey(p.getName())) {
					CShop shop = uiCShopMap.get(p.getName());
					if(w.getTitle().equals(uiCShopMap.get(p.getName()).getName())) {
						if(btxt.equals(TextFormat.BOLD + "查看商店資訊")) {
							p.showFormWindow(CShopUI.shopInfoWindow(p.getName()));
						}
						else if(btxt.equals(TextFormat.BOLD + "更改商店名稱")) {
							p.showFormWindow(CShopUI.shopRenameWindow(p.getName()));
						}
						else if(btxt.equals(TextFormat.BOLD + "拜訪該商店(需花費50" + CMoney.name() + ")")) {
							if(Server.getInstance().getLevelByName(shop.getLevelName()) != null) {
								//TODO: 消耗物品
								p.teleport(shop.getPosition());
							}else {
								p.sendMessage(CShop.infoTitle() + TextFormat.RED + "該商店所在的世界似乎已經消失了呢......");
							}
							uiCShopMap.remove(p.getName());
						}
						else if(btxt.equals(TextFormat.BOLD + "聯繫商店主人")) {
							MailGUI mailUI = new MailGUI(cosr.mcpemail.Main.getInstance());
							mailUI.mailOutW(p, shop.getOwnerName());
							uiCShopMap.remove(p.getName());
						}
						else if(btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "返回至商店列表"))) {
							if(shop instanceof PurchaseShop)
								p.showFormWindow(CShopUI.purchaseShopListWindow(p.getName()));
							else if(shop instanceof SoldShop)
								p.showFormWindow(CShopUI.soldShopListWindow(p.getName()));
							else if(shop instanceof BarterShop)
								p.showFormWindow(CShopUI.barterShopListWindow(p.getName()));
							else if(shop instanceof LotteryShop)
								p.showFormWindow(CShopUI.lotteryShopListWindow(p.getName()));
							else if(shop instanceof TitleShop)
								p.showFormWindow(CShopUI.titleShopListWindow(p.getName()));
							else if(shop instanceof PointShop)
								p.showFormWindow(CShopUI.pointShopListWindow(p.getName()));
						}
						else {
							p.showFormWindow(CShopUI.sureToTradeWindow(p.getName()));
						}
					}
				}
				else if(uiPShopMap.containsKey(p.getName())) {
					CShop shop = uiPShopMap.get(p.getName());
					if(w.getTitle().equals(uiPShopMap.get(p.getName()).getName())) {
						if(btxt.equals(TextFormat.BOLD + "查看商店當前資訊")) {
							p.showFormWindow(CShopUI.myShopInfoWindow(p.getName()));
						}
						else if(btxt.equals(TextFormat.BOLD + "更改商店名稱")) {
							p.showFormWindow(CShopUI.shopRenameWindow(p.getName()));
						}
						else if(btxt.equals(TextFormat.BOLD + "傳送至該商店(需花費50" + CMoney.name() + ")")) {
							if(Server.getInstance().getLevelByName(shop.getLevelName()) != null) {
								//TODO: 消耗物品
								p.teleport(shop.getPosition());
							}else {
								p.sendMessage(CShop.infoTitle() + TextFormat.RED + "該商店所在的世界似乎已經消失了呢......");
							}
							uiCShopMap.remove(p.getName());
						}
						else if(btxt.equals(TextFormat.BOLD + "補貨")) {
							p.showFormWindow(CShopUI.replenishWindow(p.getName()));
						}
						else if(btxt.equals(TextFormat.BOLD + "提取物品")) {
							p.showFormWindow(CShopUI.extractWindow(p.getName()));
						}
						else if(btxt.equals(TextFormat.BOLD + "替該商店打廣告")) {
							p.showFormWindow(CShopUI.advertiseWindow(p.getName()));
						}
						else if(btxt.equals(TextFormat.BOLD + (TextFormat.RED + "刪除該商店"))) {
							p.showFormWindow(CShopUI.sureToRemoveShopWindow(p.getName()));
						}
						else if(btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "返回至商店列表"))) {
							p.showFormWindow(CShopUI.myShopListWindow(p.getName()));
							uiPShopMap.remove(p.getName());
						}
					}
				}
			}
		}
		else if(window instanceof FormWindowCustom) {
			FormWindowCustom w = (FormWindowCustom)window;
			FormResponseCustom r = (FormResponseCustom)response;
			
			if((!uiPShopMap.containsKey(p.getName())) && (!uiCShopMap.containsKey(p.getName()))) return;
			
			if(w.getTitle().equals("重新命名商店")) {
				CShop shop = null;
				if(GuiEventListener.uiCShopMap.containsKey(p.getName())) {
					shop = GuiEventListener.uiCShopMap.get(p.getName());
				}else if(GuiEventListener.uiPShopMap.containsKey(p.getName())) {
					shop = GuiEventListener.uiPShopMap.get(p.getName());
				}else {
					return;
				}
				String shopName = r.getInputResponse(1);
				shop.setName(shopName);
			}
			else if(w.getTitle().equals("替" + uiPShopMap.get(p.getName()).getName() + TextFormat.RESET + "商店補貨")) {
				CShop shop = uiPShopMap.get(p.getName());
				if(shop instanceof SoldShop || shop instanceof BarterShop) {
					if(!CJEF.isInteger(r.getInputResponse(1))) {
						p.sendMessage(CShop.infoTitle() + TextFormat.RED + "請輸入正確的整數格式");
						p.showFormWindow(CShopUI.replenishWindow(p.getName()));
						return;
					}
					int stock = Integer.parseInt(r.getInputResponse(1));
					if(shop instanceof SoldShop) {
						((SoldShop)shop).replenish(stock);
					}else if(shop instanceof BarterShop) {
						((BarterShop)shop).replenish(stock);
					}
					uiPShopMap.remove(p.getName());
				}
				else if(shop instanceof LotteryShop) {
					if(!CJEF.isInteger(r.getInputResponse(2))) {
						p.sendMessage(CShop.infoTitle() + TextFormat.RED + "請輸入正確的整數格式");
						p.showFormWindow(CShopUI.replenishWindow(p.getName()));
						return;
					}
					LotteryShop lShop = (LotteryShop)shop;
					int i = 0;
					for(Item item : lShop.getStockMap().keySet()) {
						if(i == r.getDropdownResponse(1).getElementID() && 
								r.getDropdownResponse(1).getElementContent().equals(item.getName())) {
							lShop.replenish(item, (int)Long.parseLong(r.getInputResponse(2)));
						}
						i++;
						break;
					}
					p.showFormWindow(CShopUI.replenishWindow(p.getName()));
				}
			}
			else if(w.getTitle().equals("從" + uiPShopMap.get(p.getName()).getName() + TextFormat.RESET + "商店中提取物品")) {
				CShop shop = uiPShopMap.get(p.getName());
				if(shop instanceof SoldShop) {
					if(!CJEF.isInteger(r.getInputResponse(1))) {
						p.sendMessage(CShop.infoTitle() + TextFormat.RED + "請輸入正確的整數格式");
						p.showFormWindow(CShopUI.extractWindow(p.getName()));
						return;
					}
					int stock = Integer.parseInt(r.getInputResponse(1));
					if(shop instanceof SoldShop) {
						((SoldShop)shop).extract(stock);
					}
					uiPShopMap.remove(p.getName());
				}
				else if(shop instanceof LotteryShop || shop instanceof BarterShop) {
					if(!CJEF.isInteger(r.getInputResponse(2))) {
						p.sendMessage(CShop.infoTitle() + TextFormat.RED + "請輸入正確的整數格式");
						p.showFormWindow(CShopUI.extractWindow(p.getName()));
						return;
					}
					FormResponseData dlist = r.getDropdownResponse(1);
					int amount = (int)Long.parseLong(r.getInputResponse(2));
					if(shop instanceof LotteryShop) {
						LotteryShop lShop = (LotteryShop)shop;
						int i = 0;
						for(Item item : lShop.getStockMap().keySet()) {
							if(i == dlist.getElementID() && 
									dlist.getElementContent().equals(item.getName())) {
								lShop.extract(item, amount);
							}
							i++;
							break;
						}
						p.showFormWindow(CShopUI.replenishWindow(p.getName()));
					}
					else if(shop instanceof BarterShop) {
						BarterShop bShop = (BarterShop) shop;
						if(dlist.getElementID() == 0)
							bShop.extractCommodity(amount);
						else
							bShop.extractCurrency(amount);
						uiPShopMap.remove(p.getName());
					}
				}
			}
			else if(w.getTitle().equals("替" + uiPShopMap.get(p.getName()).getName() + TextFormat.RESET + "打廣告")) {
				if(!CJEF.isInteger(r.getInputResponse(2))) {
					p.sendMessage(CShop.infoTitle() + TextFormat.RED + "請輸入正確的整數格式");
					p.showFormWindow(CShopUI.advertiseWindow(p.getName()));
					return;
				}
				String content = r.getInputResponse(1);
				int times = (int) Long.parseLong(r.getInputResponse(2));
				CShopMain.getInstance().getAdTask().getAdList().add(new CAdvertisement(uiPShopMap.get(p.getName()), content, times));
				p.sendMessage(CShop.infoTitle() + TextFormat.GREEN + "成功新增廣告!");
				uiPShopMap.remove(p.getName());
			}
			
		}
		else if(window instanceof FormWindowModal) {
			FormWindowModal w = (FormWindowModal)window;
			FormResponseModal r = (FormResponseModal)response;
			String btxt = r.getClickedButtonText();
			if(w.getTitle().equals("商店訊息")) {
				if(btxt.equals("返回")) {
					p.showFormWindow(CShopUI.shopSystemWindow(p.getName()));
				}else if(btxt.equals("確認")) {
					CShop shop = uiCShopMap.get(p.getName());
					p.showFormWindow(CShopUI.pShopListWindow(shop.getOwnerName(), shop.getClass()));
					uiCShopMap.remove(p.getName());
				}
			}
			else if(w.getTitle().startsWith("是否確定")) {
				if(btxt.equals("確定")) {
					if(uiCShopMap.containsKey(p.getName())) {
						CShop shop = uiCShopMap.get(p.getName());
						if(shop instanceof PurchaseShop) {
							((PurchaseShop) shop).buyFrom(p);
						}
						else if(shop instanceof Sellable) {
							((Sellable) shop).sellTo(p);
						}
						p.showFormWindow(CShopUI.successfullyTradeWindow(p.getName()));
					}
				}else if(btxt.equals("取消")) {
					p.showFormWindow(CShopUI.shopSystemWindow(p.getName()));
				}
			}
			else if(w.getTitle().equals("購買成功") || w.getTitle().equals("出售成功") || 
					w.getTitle().equals("兌換成功") || w.getTitle().equals("抽獎結果")) {
				//不管按什麼都回商店系統介面，除非x
				p.showFormWindow(CShopUI.shopSystemWindow(p.getName()));
			}
			else if(w.getTitle().startsWith("我的商店") && w.getTitle().endsWith("資訊")) {
				if(btxt.equals("返回")) {
					p.showFormWindow(CShopUI.myShopSystemWindow(p.getName()));
				}else if(btxt.equals("確認")) {
					p.showFormWindow(CShopUI.myShopListWindow(p.getName()));
					uiCShopMap.remove(p.getName());
				}
			}
			else if(w.getTitle().equals("確定刪除該商店?")) {
				if(btxt.equals("確定")) {
					CShop shop = uiPShopMap.get(p.getName());
					shop.hidePiece();
					if(shop instanceof PurchaseShop) {
						CShopMain.getPurchaseShopMap().get(p.getName()).remove(shop);
					}
					else if(shop instanceof SoldShop) {
						CShopMain.getSoldShopMap().get(p.getName()).remove(shop);
					}
					else if(shop instanceof BarterShop) {
						CShopMain.getBarterShopMap().get(p.getName()).remove(shop);
					}
					else if(shop instanceof LotteryShop) {
						CShopMain.getLotteryShopMap().get(p.getName()).remove(shop);
					}
					uiPShopMap.remove(p.getName());
					p.sendMessage(CShop.infoTitle() + TextFormat.GRAY + "已成功刪除您的商店!");
				}else if(btxt.equals("取消")) {
					p.showFormWindow(CShopUI.myShopSystemWindow(p.getName()));
				}
			}
		}
	}	
	
	private static void flush(Player p) {
		if(uiPShopMap.containsKey(p.getName()))
			uiPShopMap.remove(p.getName());
		if(uiCShopMap.containsKey(p.getName()))
			uiCShopMap.remove(p.getName());
	}
}
