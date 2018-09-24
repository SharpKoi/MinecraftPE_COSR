package cosr.shop.listener;

import java.util.ArrayList;

import cmen.essalg.CJEF;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.event.entity.EntityDespawnEvent;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.TextFormat;
import cosr.roleplay.gcollection.Title;
import cosr.shop.CShopMain;
import cosr.shop.ItemSingle;
import cosr.shop.MoneyCostable;
import cosr.shop.shops.BarterShop;
import cosr.shop.shops.CShop;
import cosr.shop.shops.LotteryShop;
import cosr.shop.shops.PointShop;
import cosr.shop.shops.PurchaseShop;
import cosr.shop.shops.SoldShop;
import cosr.shop.shops.TitleShop;
import cosr.shop.shops.CShop.OwnerType;
import cosr.shop.utils.BuildTool;

public class EventListener implements Listener {
	
	@EventHandler
	public void onTouchGround(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Block b = event.getBlock();
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getFace().equals(BlockFace.UP)) {
			if(CShopMain.getBuildingPool().containsKey(p.getName())) {
				BuildTool bt = CShopMain.getBuildingPool().get(p.getName());
				if(bt.step == 0) {
					CShop shop = bt.getShop();
					shop.setLevel(b.getLevel().getFolderName());
					shop.buildUp(b, p);
					p.sendMessage(TextFormat.GREEN + "商店設置成功!");
					if(shop instanceof PurchaseShop) {
						if(!CShopMain.getPurchaseShopMap().containsKey(shop.getOwnerName())) {
							CShopMain.getPurchaseShopMap().put(shop.getOwnerName(), new ArrayList<PurchaseShop>());
						}
						CShopMain.getPurchaseShopMap().get(shop.getOwnerName()).add((PurchaseShop)shop);
					}
					if(shop instanceof SoldShop) {
						if(!CShopMain.getSoldShopMap().containsKey(shop.getOwnerName())) {
							CShopMain.getSoldShopMap().put(shop.getOwnerName(), new ArrayList<SoldShop>());
						}
						CShopMain.getSoldShopMap().get(shop.getOwnerName()).add((SoldShop)shop);
					}
					if(shop instanceof LotteryShop) {
						if(!CShopMain.getLotteryShopMap().containsKey(shop.getOwnerName())) {
							CShopMain.getLotteryShopMap().put(shop.getOwnerName(), new ArrayList<LotteryShop>());
						}
						CShopMain.getLotteryShopMap().get(shop.getOwnerName()).add((LotteryShop)shop);
					}
					if(shop instanceof PointShop) {
						if(!CShopMain.getPointShopMap().containsKey(OwnerType.SERVER.getName())) {
							CShopMain.getPointShopMap().put(OwnerType.SERVER.getName(), new ArrayList<PointShop>());
						}
						CShopMain.getPointShopMap().get(OwnerType.SERVER.getName()).add((PointShop)shop);
					}
					if(shop instanceof TitleShop) {
						if(!CShopMain.getTitleShopMap().containsKey(OwnerType.SERVER.getName())) {
							CShopMain.getTitleShopMap().put(OwnerType.SERVER.getName(), new ArrayList<TitleShop>());
						}
						CShopMain.getTitleShopMap().get(OwnerType.SERVER.getName()).add((TitleShop)shop);
					}
					if(shop instanceof BarterShop) {
						if(!CShopMain.getBarterShopMap().containsKey(shop.getOwnerName())) {
							CShopMain.getBarterShopMap().put(shop.getOwnerName(), new ArrayList<BarterShop>());
						}
						CShopMain.getBarterShopMap().get(shop.getOwnerName()).add((BarterShop)shop);
					}
					CShopMain.getBuildingPool().remove(p.getName());
				}
			}
		}
		
	}
	
	@EventHandler
	public void onTouchShop(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Block b = event.getBlock();
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(b.getId() == 68) {
				CShop shop = CShopMain.getShopData(b);
				if(shop != null) {
					if(CShopMain.getRemovingPool().contains(p.getName())) {
						if(shop.getType().equals(OwnerType.PLAYER)) {
							if(p.getName().equals(shop.getOwnerName())) {
								shop.destroy();
								//TODO: return all goods
								if(shop instanceof PurchaseShop) {
									((PurchaseShop)shop).returnAllGoods();
									CShopMain.getPurchaseShopMap().get(p.getName()).remove(shop);
								}
								if(shop instanceof SoldShop) {
									((SoldShop)shop).returnAllGoods();
									CShopMain.getSoldShopMap().get(p.getName()).remove(shop);
								}
								if(shop instanceof LotteryShop) {
									((LotteryShop)shop).returnAllGoods();
									CShopMain.getLotteryShopMap().get(p.getName()).remove(shop);
								}
								if(shop instanceof BarterShop) {
									((BarterShop)shop).returnAllGoods();
									CShopMain.getBarterShopMap().get(p.getName()).remove(shop);
								}
								return;
							}
						}else {
							if(p.isOp()) {
								shop.destroy();
								if(shop instanceof PurchaseShop) {
									CShopMain.getPurchaseShopMap().get(OwnerType.SERVER.getName()).remove(shop);
								}
								if(shop instanceof SoldShop) {
									CShopMain.getSoldShopMap().get(OwnerType.SERVER.getName()).remove(shop);
								}
								if(shop instanceof LotteryShop) {
									CShopMain.getLotteryShopMap().get(OwnerType.SERVER.getName()).remove(shop);
								}
								if(shop instanceof PointShop) {
									CShopMain.getPointShopMap().get(OwnerType.SERVER.getName()).remove(shop);
								}
								if(shop instanceof TitleShop) {
									CShopMain.getTitleShopMap().get(OwnerType.SERVER.getName()).remove(shop);
								}
								if(shop instanceof BarterShop) {
									CShopMain.getBarterShopMap().get(OwnerType.SERVER.getName()).remove(shop);
								}
								return;
							}
						}
					}
					
					if(shop.getType().equals(OwnerType.PLAYER)) {
						if(p.getName().equals(shop.getOwnerName())) {
							//TODO: show the ui
							p.sendMessage(shop.Information(p.getName()) + "\n");
							return;
						}
					}else {
						if(!CShopMain.getTradeRequestMap().containsKey(p.getName())) {
							CShopMain.getTradeRequestMap().put(p.getName(), shop);
							p.sendMessage(CShop.infoTitle() + "請再點擊一次以完成交易");
						}else {
							if(CShopMain.getTradeRequestMap().get(p.getName()).equals(shop)) {
								if(shop instanceof PurchaseShop) {
									PurchaseShop pShop = (PurchaseShop) shop;
									pShop.buyFrom(p);
								}
								if(shop instanceof SoldShop) {
									SoldShop sShop = (SoldShop) shop;
									sShop.sellTo(p);
								}
								if(shop instanceof LotteryShop) {
									LotteryShop lShop = (LotteryShop) shop;
									lShop.sellTo(p);
								}
								if(shop instanceof PointShop) {
									PointShop ptShop = (PointShop) shop;
									ptShop.sellTo(p);
								}
								if(shop instanceof TitleShop) {
									TitleShop tShop = (TitleShop) shop;
									tShop.sellTo(p);
								}
								if(shop instanceof BarterShop) {
									BarterShop btShop = (BarterShop) shop;
									btShop.sellTo(p);
								}
								CShopMain.getTradeRequestMap().remove(p.getName());
							}else {
								CShopMain.getTradeRequestMap().put(p.getName(), shop);
								p.sendMessage(CShop.infoTitle() + "請再點擊一次以完成交易");
							}
						}
					}//end of else
				}
			}
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		Item item = event.getItem();
		Block touchedBlock = event.getBlockAgainst();
		if(touchedBlock.getId() == 68) {
			CShop shop = CShopMain.getShopData(touchedBlock);
			if(shop == null) return;
			
			if(shop.getType().equals(OwnerType.PLAYER)) {
				if(shop.getOwnerName().equals(p.getName())) {
					if(shop instanceof SoldShop) {
						SoldShop ss = ((SoldShop)shop);
						if(item.equals(ss.getItem())) {
							ss.replenish(event.getItem().getCount());
							event.setCancelled();
							return;
						}
					}else if(shop instanceof BarterShop) {
						BarterShop bts = ((BarterShop)shop);
						if(item.equals(bts.getItem())) {
							bts.replenish(event.getItem().getCount());
							event.setCancelled();
							return;
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		//TODO: 破壞商店則取消事件
		Block b = event.getBlock();
		Player p = event.getPlayer();
		if(CShopMain.getShopData(b) != null) {
			event.setCancelled();
			p.sendMessage(CShop.infoTitle() + TextFormat.GRAY + "欲刪除該商店請輸入/cshop remove進入移除模式並點擊該商店");
			return;
		}
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		
		CShop shop = CShopMain.getShopData(block);
		if(shop != null) {
			if(shop.isRenaming()) return;
			player.sendMessage(CShop.infoTitle() + TextFormat.RED + "該告示牌記錄著商店資訊, 無法隨意更改");
			event.setCancelled();
		}
	}
	
	@EventHandler
	public void onPieceDespawn(EntityDespawnEvent event) {
		if(event.isItem()) {
			for(String owner : CShopMain.getPurchaseShopMap().keySet()) {
				for(PurchaseShop ps : CShopMain.getPurchaseShopMap().get(owner)) {
					if(ps.isItemShown())
						if(ps.getLevelName().equals(event.getPosition().getLevel().getFolderName()) && ps.getItemPos().equals(event.getPosition())) {
							Item it = ps.getItem().clone();
							it.setCount(1);
							ps.showPiece(it);
							return;
						}
				}
			}
			for(String owner : CShopMain.getSoldShopMap().keySet()) {
				for(SoldShop ss : CShopMain.getSoldShopMap().get(owner)) {
					if(ss.isItemShown())
						if(ss.getLevelName().equals(event.getPosition().getLevel().getFolderName()) && ss.getItemPos().equals(event.getPosition())) {
							Item it = ss.getItem().clone();
							it.setCount(1);
							ss.showPiece(it);
							return;
					}
				}
			}
			for(String owner : CShopMain.getLotteryShopMap().keySet()) {
				for(LotteryShop ls : CShopMain.getLotteryShopMap().get(owner)) {
					if(ls.isItemShown())
						if(ls.getLevelName().equals(event.getPosition().getLevel().getFolderName()) && ls.getItemPos().equals(event.getPosition())) {
							ls.showPiece(Item.get(358));
							return;
						}
				}
			}
		}
	}

	@EventHandler
	public void onChat(PlayerChatEvent event) {
		Player p = event.getPlayer();
		String msg = event.getMessage();
		
		if(CShopMain.getRemovingPool().contains(p.getName())) {
			if(msg.equalsIgnoreCase("@cancel")) {
				CShopMain.getRemovingPool().remove(p.getName());
				p.sendMessage(TextFormat.ITALIC + (TextFormat.GRAY + "您已結束了移除模式"));
				event.setCancelled();
			}
		}
		if(CShopMain.getBuildingPool().containsKey(p.getName())) {
			BuildTool bt = CShopMain.getBuildingPool().get(p.getName());
			//set ShopType -> set OwnerType -> set Name -> set Item -> set Position
			if(bt.step == 5) {
				if(!msg.startsWith("@")) return;
				switch(msg.toLowerCase()) {
					case "@buy":
						bt.setShop(new PurchaseShop());
						break;
					case "@sell":
						bt.setShop(new SoldShop());
						break;
					case "@barter":
						if(p.isOp()) {
							bt.setShop(new BarterShop());
							break;
						}else return;
					case "@lottery":
						bt.setShop(new LotteryShop());
						break;
					case "@point": 
						if(p.isOp()) {
							bt.setShop(new PointShop());
							break;
						}else return;
					case "@title":
						if(p.isOp()) {
							bt.setShop(new TitleShop());
							break;
						}else return;
					default: 
						return;
					//TODO: more shop
				}
				
				if(bt.getShop() instanceof PointShop || bt.getShop() instanceof TitleShop) {
					bt.getShop().setType(OwnerType.SERVER);
					bt.step -= 2;
				}else {
					if(p.isOp()) bt.step--;
					else bt.step -= 2;
					bt.getShop().setType(OwnerType.PLAYER);
				}
				
				bt.prompt();
				event.setCancelled();
			}
			else if(bt.step == 4) {
				if(!msg.startsWith("@")) return;
				switch(msg.toLowerCase()) {
					case "@p":
						event.setCancelled();
						bt.getShop().setType(OwnerType.PLAYER);
						bt.getShop().setOwnerName(p.getName());
						bt.step--;
						break;
					case "@s":
						event.setCancelled();
						bt.getShop().setType(OwnerType.SERVER);
						bt.step--;
						break;
					case "@back":
						event.setCancelled();
						bt.step++;
						bt.prompt();
						return;
					default: 
						return;
				}
				bt.prompt();
			}
			else if(bt.step == 3) {
				event.setCancelled();
				if(msg.equalsIgnoreCase("@back")) {
					event.setCancelled();
					bt.step++;
					bt.prompt();
					return;
				}
				String name = msg.replaceAll("%n", "\n");
				bt.getShop().setName(name);
				p.sendMessage(name);
				bt.step--;
				bt.prompt();
			}
			else if(bt.step == 2) {
				if(msg.equalsIgnoreCase("@back")) {
					event.setCancelled();
					bt.step++;
					bt.prompt();
					return;
				}
				String[] args = msg.split(" ");
				if(!msg.startsWith("@") || args.length < 2) return;
				
				if(bt.getShop() instanceof ItemSingle) {
					if(args[0].equalsIgnoreCase("@i")) {
						event.setCancelled();
						if(args.length < 3) return;
						if(!CJEF.isItemForm(args[1])) {
							p.sendMessage(TextFormat.RED + "請輸入正確的物品ID");
							return;
						}
						if(!CJEF.isInteger(args[2])) {
							p.sendMessage(TextFormat.RED + "請輸入正確的物品數量");
							return;
						}
						String[] itemID = args[1].split(":");
						int id = Integer.parseInt(itemID[0]);
						int meta = itemID.length == 2? Integer.parseInt(itemID[1]) : 0;
						int count = Integer.parseInt(args[2]);
						if(bt.getShop() instanceof ItemSingle) {
							((ItemSingle)bt.getShop()).setItem(Item.get(id, meta, count));
							p.sendMessage(TextFormat.GREEN + "商品設置完畢!");
							bt.step--;
							bt.prompt();
						}
					}else if(args[0].equalsIgnoreCase("@h")) {
						event.setCancelled();
						if(args.length < 2) return;
						if(!CJEF.isInteger(args[1])) {
							p.sendMessage(TextFormat.RED + "請輸入正確的物品數量");
							return;
						}
						int count = Integer.parseInt(args[1]);
						if(bt.getShop() instanceof ItemSingle) {
							Item item = p.getInventory().getItemInHand();
							item.setCount(count);
							((ItemSingle)bt.getShop()).setItem(item);
							p.sendMessage(TextFormat.GREEN + "商品設置完畢!");
							bt.step--;
							bt.prompt();
						}
					}else return;
				}
				else if(bt.getShop() instanceof LotteryShop) {
					if(args[0].equalsIgnoreCase("@i")) {
						event.setCancelled();
						if(args.length < 4) {
							p.sendMessage(TextFormat.ITALIC + (TextFormat.RED + "請輸入物品ID, 交易數量, 機率"));
							return;
						}
						if(!CJEF.isItemForm(args[1])) {
							p.sendMessage(TextFormat.RED + "請輸入正確的物品ID");
							return;
						}
						if(!CJEF.isInteger(args[2])) {
							p.sendMessage(TextFormat.RED + "請輸入正確的物品數量");
							return;
						}
						if(!CJEF.isInteger(args[3])) {
							p.sendMessage(TextFormat.RED + "請輸入正確的獎品機率");
							return;
						}
						String[] itemID = args[1].split(":");
						int id = Integer.parseInt(itemID[0]);
						int meta = itemID.length == 2? Integer.parseInt(itemID[1]) : 0;
						int count = Integer.parseInt(args[2]);
						int chance = Integer.parseInt(args[3]);
						if(bt.getShop() instanceof LotteryShop) {
							((LotteryShop)bt.getShop()).addLottery(bt.getBuilder(), Item.get(id, meta, count), chance);
							if(((LotteryShop)bt.getShop()).getRemain() <= 0)
								bt.step--;
							bt.prompt();
						}
					}else if(args[0].equalsIgnoreCase("@h")) {
						event.setCancelled();
						if(args.length < 3) {
							p.sendMessage(TextFormat.ITALIC + (TextFormat.RED + "請輸入物品交易數量, 機率"));
							return;
						}
						if(!CJEF.isInteger(args[1])) {
							p.sendMessage(TextFormat.RED + "請輸入正確的物品數量");
							return;
						}
						if(!CJEF.isInteger(args[2].replace("%", ""))) {
							p.sendMessage(TextFormat.RED + "請輸入正確的獎品機率");
							return;
						}
						int count = Integer.parseInt(args[1]);
						int chance = Integer.parseInt(args[2]);
						if(bt.getShop() instanceof LotteryShop) {
							Item item = p.getInventory().getItemInHand().clone();
							item.setCount(count);
							((LotteryShop)bt.getShop()).addLottery(bt.getBuilder(), item, chance);
							if(((LotteryShop)bt.getShop()).getRemain() <= 0)
								bt.step--;
							bt.prompt();
						}
					}
				}
				else if(bt.getShop() instanceof TitleShop) {
					if(args[0].equalsIgnoreCase("@t")) {
						if(args.length < 2) {
							p.sendMessage(TextFormat.RED + "請指定稱號");
							return;
						}
						Title title = Title.get(args[1].toUpperCase());
						((TitleShop)bt.getShop()).setTitle(title);
						bt.step--;
						bt.prompt();
						event.setCancelled();
					}
				}
				//TODO: more shop
			}
			else if(bt.step == 1) {
				if(msg.equalsIgnoreCase("@back")) {
					event.setCancelled();
					bt.step++;
					bt.prompt();
					return;
				}
				if(bt.getShop() instanceof BarterShop) {
					if(msg.startsWith("@")) {
						String[] args = msg.split(" ");
						if(args[0].equalsIgnoreCase("@i")) {
							event.setCancelled();
							if(args.length < 3) return;
							if(!CJEF.isItemForm(args[1])) {
								p.sendMessage(TextFormat.RED + "請輸入正確的物品ID");
								return;
							}
							if(!CJEF.isInteger(args[2])) {
								p.sendMessage(TextFormat.RED + "請輸入正確的物品數量");
								return;
							}
							String[] itemID = args[1].split(":");
							int id = Integer.parseInt(itemID[0]);
							int meta = itemID.length == 2? Integer.parseInt(itemID[1]) : 0;
							int count = Integer.parseInt(args[2]);
								((BarterShop)bt.getShop()).setCurrency(Item.get(id, meta, count));
								p.sendMessage(TextFormat.GREEN + "兌換品設置完畢!");
								bt.step--;
								bt.prompt();
						}else if(args[0].equalsIgnoreCase("@h")) {
							event.setCancelled();
							if(args.length < 2) return;
							if(!CJEF.isInteger(args[1])) {
								p.sendMessage(TextFormat.RED + "請輸入正確的物品數量");
								return;
							}
							int count = Integer.parseInt(args[1]);
							Item item = p.getInventory().getItemInHand().clone();
							item.setCount(count);
							((BarterShop)bt.getShop()).setCurrency(item);
							p.sendMessage(TextFormat.GREEN + "兌換品設置完畢!");
							bt.step--;
							bt.prompt();
						}else return;
					}
				}else {
					String main = msg.replaceAll("$", "").replaceAll("元", "").replaceAll("dollar", "").trim();
					if(!CJEF.isDigit(main)) {
						return;
					}
					event.setCancelled();
					float cost = Float.parseFloat(main);
					if(bt.getShop() instanceof MoneyCostable) {
						((MoneyCostable) bt.getShop()).setCost(cost);  //test
						p.sendMessage(Float.toString(CJEF.getD2(cost)));
						bt.step--;
						bt.prompt();
					}
				}
			}
		}
	}
}
