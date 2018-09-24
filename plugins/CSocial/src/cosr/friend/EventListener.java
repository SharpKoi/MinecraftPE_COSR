package cosr.friend;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import cosr.economy.EconomyGUI;
import cosr.friend.api.CSocialAPI;
import cosr.mcpemail.Mail;
import cosr.mcpemail.MailGUI;
import cosr.roleplay.CRolePlay;

public class EventListener implements Listener {

	Map<String, String> player_OtherInForm = new HashMap<String, String>();
	MailGUI mailUi = new MailGUI(new cosr.mcpemail.Main());

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		File file = new File(SocialMain.getInstance().getDataFolder(), player.getName() + ".yml");
		if (!file.exists()) {
			if (!SocialMain.FPOOL.containsKey(player.getName())) {
				SocialMain.FPOOL.put(player.getName(), new ArrayList<String>());
			}
			if (!SocialMain.friendRequestPool.containsKey(player.getName())) {
				SocialMain.friendRequestPool.put(player.getName(), new ArrayList<String>());
			}
			return;
		}

		Config conf = new Config(file, Config.YAML);
		if (conf.exists("mate")) {
			if (!SocialMain.MPOOL.containsKey(player.getName()) && !SocialMain.MPOOL.containsValue(player.getName())) {
				SocialMain.MPOOL.put(player.getName(), conf.getString("mate"));
			}
		}
		if (conf.exists("mood-msg")) {
			if (!SocialMain.msgMap.containsKey(player.getName())) {
				SocialMain.msgMap.put(player.getName(), conf.getString("mood-msg"));
			}
		}

		if (!conf.exists("friends"))
			conf.set("friends", new ArrayList<String>());
		if (!conf.exists("friend_requests"))
			conf.set("friend_requests", new ArrayList<String>());
		conf.save();

		if (!SocialMain.FPOOL.containsKey(player.getName())) {
			SocialMain.FPOOL.put(player.getName(), new ArrayList<String>(conf.getStringList("friends")));
		}
		if (!SocialMain.friendRequestPool.containsKey(player.getName())) {
			SocialMain.friendRequestPool.put(player.getName(),
					new ArrayList<String>(conf.getStringList("friend_requests")));
		}

		int size = SocialMain.friendRequestPool.get(player.getName()).size();
		if (size > 0) {
			player.sendMessage(SocialMain.infoTitle + "您當前尚有" + size + "個好友邀請未處理, 請輸入/cfriend requests查看所有邀請");
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent evnet) {
		Player player = evnet.getPlayer();
		Config conf = new Config(new File(SocialMain.getInstance().getDataFolder(), player.getName() + ".yml"),
				Config.YAML);

		String mate = SocialMain.getMate(player.getName());
		if (mate != null)
			conf.set("mate", mate);
		if (SocialMain.FPOOL.containsKey(player.getName()))
			conf.set("friends", SocialMain.FPOOL.get(player.getName()));
		if (SocialMain.friendRequestPool.containsKey(player.getName()))
			conf.set("friend_requests", SocialMain.friendRequestPool.get(player.getName()));
		if(SocialMain.msgMap.containsKey(player.getName()))
			conf.set("mood-msg", SocialMain.msgMap.get(player.getName()));
		conf.save();
	}
	
	@EventHandler
	public void onTouch(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Item item = event.getItem();
		Item socialItem = CSocialAPI.socialItem();
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && item.getId() == 450 &&
				item.getName().equals(socialItem.getName()) && 
				Arrays.equals(item.getLore(), socialItem.getLore())) {
			p.showFormWindow(SocialGUI.homePage());
		}
	}

	@EventHandler
	public void onChat(PlayerChatEvent event) {
		Player sender = event.getPlayer();
		String msg = event.getMessage();
		if (msg.equalsIgnoreCase("@Y") || msg.equalsIgnoreCase("@N")) {
			try {
				String mateName = SocialMain.getMate(sender.getName());
				Player mate = SocialMain.getInstance().getServer().getPlayer(mateName);
				if (SocialMain.breakingSet.contains(mateName)) {
					event.setCancelled();
					if (msg.equalsIgnoreCase("@Y")) {
						sender.sendMessage(TextFormat.RED + "您最終與您的伴侶達成協議, 就這樣離開了彼此......");
						CRolePlay.getAchvMap().get("LEARNTOLETGO").grantTo(sender.getName());
						if (mate != null) {
							mate.sendMessage(TextFormat.RED + "您的伴侶最終與您達成協議, 就這樣離開了彼此......");
						}
						CRolePlay.getAchvMap().get("LEARNTOLETGO").grantTo(mateName);
					} else if (msg.equalsIgnoreCase("@N")) {
						sender.sendMessage(TextFormat.GRAY + "您拒絕了對方提出的分手要求!");
						if (mate != null) {
							mate.sendMessage(TextFormat.GRAY + "您的伴侶依然不想離開您, TA似乎還想繼續留在您身邊呢......");
							mate.sendMessage(TextFormat.GRAY + "若您心意已決, 請再次提出分手要求");
						}
					}
					SocialMain.breakingSet.remove(mateName);
				}
			} catch (FileNotFoundException err) {
				// catch
			}
		}
	}

	@EventHandler
	public void onFormResponse(PlayerFormRespondedEvent event) {
		Player p = event.getPlayer();
		FormWindow window = event.getWindow();
		FormResponse response = event.getResponse();

		if (window == null || response == null) {
			// occure when user press the 'x'
			if(player_OtherInForm.containsKey(p.getName()))
				player_OtherInForm.remove(p.getName());
			return;
		}

		if (window instanceof FormWindowSimple) {
			// 首頁、朋友列表、朋友系統、結婚系統
			String btxt = ((FormResponseSimple) response).getClickedButton().getText();
			if (((FormWindowSimple) window).getTitle().equals("社交首頁")) {
				if (btxt.equals(TextFormat.BOLD + "好友列表")) {
					p.showFormWindow(SocialGUI.friendListWindow(p));
				} else if (btxt.equals(TextFormat.BOLD + "伴侶系統")) {
					if (SocialMain.getMate(p.getName()) == null) {
						p.showFormWindow(SocialGUI.proposingWindow(p.getName()));
					} else
						p.showFormWindow(SocialGUI.marrySystemWindow());
				} else if (btxt.equals(TextFormat.BOLD + "寫下心情")) {
					p.showFormWindow(SocialGUI.moodMsgWindow(p.getName()));
				} else if (btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "關閉視窗"))) {
					if(player_OtherInForm.containsKey(p.getName()))
						player_OtherInForm.remove(p.getName());
				}
			} else if (((FormWindowSimple) window).getTitle().equals("好友列表")) {
				if (btxt.equals(TextFormat.BOLD + "新增好友")) {
					p.showFormWindow(SocialGUI.newFriendWindow(p.getName()));
				} else if (btxt.equals(TextFormat.BOLD + "處理好友請求")) {
					p.showFormWindow(SocialGUI.friendRequestWindow(p.getName()));
				} else if (btxt.equals("返回至首頁")) {
					p.showFormWindow(SocialGUI.homePage());
				} else {
					String friendName = btxt.split(" ")[0];
					player_OtherInForm.put(p.getName(), friendName);
					p.showFormWindow(SocialGUI.friendWindow(friendName));
				}
			} else if(((FormWindowSimple) window).getTitle().equals("好友申請列表")) {
				try {
					if(btxt.equals(TextFormat.BOLD + (TextFormat.GRAY + "返回"))) {
						p.showFormWindow(SocialGUI.friendListWindow(p));
						return;
					}
					p.showFormWindow(SocialGUI.playerDataWindow(p.getName(), btxt));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else if (((FormWindowSimple) window).getTitle().equals("伺服器伴侶")) {
				String mateName = SocialMain.getMate(p.getName());
				if (btxt.equals("查看伴侶資訊")) {
					try {
						p.showFormWindow(SocialGUI.mateInfoWindow(mateName));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				} else if (btxt.equals("到TA身邊")) {
					Player mate = SocialMain.getInstance().getServer().getPlayer(mateName);
					if (mate != null) {
						p.teleport(mate);
					} else
						p.sendMessage(TextFormat.GRAY + "您的伴侶目前不在線上, 趕快找他來玩吧!");
				} else if (btxt.equals("給予金錢")) {
					p.showFormWindow(EconomyGUI.giveMoneyWindow(mateName));
				} else if (btxt.equals("寄信")) {
					mailUi.mailOutW(p, mateName);
				} else if (btxt.equals(TextFormat.RED + "解除伴侶關係")) {
					Player mate = SocialMain.getInstance().getServer().getPlayer(mateName);
					if (mate != null) {
						p.sendMessage(TextFormat.GRAY + "已送出離婚請求, 等待對方回覆......");
						mate.sendMessage(TextFormat.RED + "您的伴侶" + p.getName() + "向您提出了離婚請求......\n" + "再聊天室輸入@Y以表示同意\n"
								+ "再聊天室輸入@N表示拒絕");
					} else {
						if (SocialMain.isLongTimeNoLogin(mateName)) {
							p.sendMessage(TextFormat.GRAY + "對方未上線時隔已久, 無條件離婚成功");

							new Mail(TextFormat.RED + "GM", mateName, TextFormat.RED + "系統信件",
									TextFormat.RED + "玩家" + p.getName() + "提出了離婚要求\n" + TextFormat.GRAY
											+ "由於您太久沒有上線, 為了維護玩家權利, 已無條件自動離婚\n\n" + "COSR團隊 敬上").sendOut();

							SocialMain.breakingSet.remove(p.getName());
							SocialMain.MPOOL.remove(p.getName());
						} else {
							p.sendMessage(TextFormat.GRAY + "您的伴侶不在線上, 離婚須兩人同意!");
						}
					}
				} else if (btxt.equals("返回至首頁")) {
					p.showFormWindow(SocialGUI.homePage());
				}
			} else {
				// 好友系統介面
				if (((FormWindowSimple) window).getTitle().equals(player_OtherInForm.get(p.getName()))) {
					String friendName = ((FormWindowSimple) window).getTitle();
					if (btxt.equals("查看個人資訊")) {
						try {
							p.showFormWindow(SocialGUI.friendInfoWindow(friendName));
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					} else if (btxt.equals("給予金錢")) {
						p.showFormWindow(EconomyGUI.giveMoneyWindow(friendName));
					} else if (btxt.equals("寄信")) {
						mailUi.mailOutW(p, friendName);
					} else if (btxt.equals(TextFormat.RED + "解除好友關係")) {
						p.showFormWindow(SocialGUI.sureToDelFriendWindow(friendName));
					} else if (btxt.equals("返回至好友列表")) {
						p.showFormWindow(SocialGUI.friendListWindow(p));
					}
				}
			}
		} else if (window instanceof FormWindowCustom) {
			String title = ((FormWindowCustom) window).getTitle();
			FormResponseCustom responseCustom = ((FormResponseCustom) response);
			// 新增朋友、送出告白、給錢、寄信、設定心情小語
			if (title.equals("新增好友")) {
				String playerName = (responseCustom.getInputResponse(0).equals(""))
						? responseCustom.getDropdownResponse(1).getElementContent()
						: responseCustom.getInputResponse(0);
				if (!playerName.equals("None")) {
					if (!playerName.equals(p.getName())) {
						try {
							p.showFormWindow(SocialGUI.playerDataWindow(p.getName(), playerName));
							player_OtherInForm.put(p.getName(), playerName);
						} catch (FileNotFoundException e) {
							p.sendMessage(TextFormat.RED + "找不到該玩家");
						}
					}
				}
				responseCustom = null;
			} else if (title.equals("您還沒有伴侶, 趕快找一個TA吧")) {
				String targetName = responseCustom.getDropdownResponse(0).getElementContent();

				if (!targetName.equals("None")) {
					Player target = SocialMain.getInstance().getServer().getPlayer(targetName);

					if (SocialMain.proposingPool.containsValue(p.getName())) {
						p.sendMessage(TextFormat.GRAY + "抱歉! 一人無法同時向多個人求婚呦");
						p.sendMessage(TextFormat.YELLOW + "別再三心二意了, 趕緊向TA表示些什麼吧!");
						return;
					}

					if (SocialMain.MPOOL.containsValue(targetName) || SocialMain.MPOOL.containsKey(targetName)) {
						p.sendMessage(TextFormat.GRAY + "該對象已經有配偶囉, 無法向TA求婚惹......");
						return;
					}
					if (SocialMain.proposingPool.containsKey(targetName)) {
						p.sendMessage(TextFormat.GRAY + "該對象已經有其他人正在求婚囉, 還請稍後呢!");
						p.sendMessage(TextFormat.YELLOW + "(小提示: 寄封信給他, 讓他知道你的心意吧)");
						return;
					}

					if (target != null) {
						SocialMain.proposingPool.put(targetName, p.getName());
						target.sendMessage(TextFormat.ITALIC
								+ (TextFormat.YELLOW + "玩家" + p.getName() + "向您求婚了!! >///<\n") + TextFormat.RESET
								+ "輸入/cmarry accept 接受TA的心意><\n" + TextFormat.RESET + "輸入/cmarry deny  婉拒TA的心意QQ");
					} else {
						p.sendMessage(TextFormat.GRAY + "該對象似乎不在線上, 等TA上線了再向他表達心意吧!");
					}
					responseCustom = null;
				}
			} else if (title.startsWith("給予玩家") && title.endsWith("金錢")) {
				if (player_OtherInForm.containsKey(p.getName())) {
					player_OtherInForm.remove(p.getName());
				}
				responseCustom = null;
			} else if (title.startsWith("寄信給 ")) {
				String playerName = title.replace("寄信給 ", "").trim();
				String topic = responseCustom.getInputResponse(0);
				String content = responseCustom.getInputResponse(1);

				new Mail(p.getName(), playerName, topic, content).sendOut();
				if (player_OtherInForm.containsKey(p.getName())) {
					player_OtherInForm.remove(p.getName());
				}
				p.sendMessage(TextFormat.GREEN + "成功發送信件給" + TextFormat.RESET + playerName + TextFormat.GREEN + "玩家");
				responseCustom = null;
			} else if (title.equals("寫下此刻的心情")) {
				SocialMain.msgMap.put(p.getName(), responseCustom.getInputResponse(0));
				p.sendMessage(TextFormat.GREEN + "心情小語設定成功!");
				responseCustom = null;
			}
			System.gc();
		} else if (window instanceof FormWindowModal) {
			// 玩家資訊、伴侶資訊、確認刪除好友、確認解除伴侶
			String title = ((FormWindowModal) window).getTitle();
			FormResponseModal responseModal = (FormResponseModal) response;
			String btxt = responseModal.getClickedButtonText();
			String otherName = player_OtherInForm.get(p.getName());
			if(title.startsWith("玩家") && title.endsWith("的個人檔案")) {
				if(btxt.equals("加為好友")) {
					CSocialAPI.makeFriend(p.getName(), otherName);
				} else if(btxt.equals("拒絕好友申請")) {
					if(SocialMain.getFriendRequests(p.getName()) != null) {
						if(SocialMain.getFriendRequests(p.getName()).contains(otherName)) {
							p.sendMessage(TextFormat.GRAY + "您拒絕了" + otherName + "的好友邀請");
							SocialMain.getFriendRequests(p.getName()).remove(otherName);
							player_OtherInForm.remove(p.getName());
							return;
						}
					}
					p.sendMessage(TextFormat.GRAY + "無該玩家的好友邀請");
				} else if(btxt.equals("取消")) {
					if(player_OtherInForm.containsKey(p.getName())) player_OtherInForm.remove(p.getName());
				}
			} else if (title.equals("好友: " + player_OtherInForm.get(p.getName()))) {
				if (btxt.equals("返回") || btxt.equals("取消")) {
					// 若false-button文字為返回，則代表此視窗為friendInfo
					// 若false-button文字為取消，則代表此視窗為sureToDelFriend
					// 以上兩者皆須返回朋友系統視窗
					p.showFormWindow(SocialGUI.friendWindow(otherName));
				} else if (btxt.equals("確認")) {
					// 確認刪除好友
					ArrayList<String> friendList = SocialMain.FPOOL.get(p.getName());
					if (friendList.contains(otherName)) {
						friendList.remove(otherName);

						if (SocialMain.FPOOL.containsKey(otherName)) {
							SocialMain.FPOOL.get(otherName).remove(p.getName());
						} else {
							File file = new File(SocialMain.getInstance().getDataFolder(), otherName + ".yml");
							if (!file.exists()) {
								p.sendMessage(TextFormat.RED + "找不到該玩家");
								return;
							}

							Config conf = new Config(file);
							if (!conf.exists("friends")) {
								conf.set("friends", new ArrayList<String>());
							}
							if (!conf.getStringList("friends").contains(p.getName())) {
								p.sendMessage(TextFormat.RED + "您未在該玩家的好友名單內");
								return;
							}
							conf.getStringList("friends").remove(p.getName());
							conf.save();
						}
						p.sendMessage(TextFormat.GRAY + "已將玩家" + otherName + "從您的好友名單中移除");

					} else {
						p.sendMessage(TextFormat.RED + "該玩家未在您的好友名單內");
					}
				}
			} else if (title.equals("伴侶: " + SocialMain.getMate(p.getName()))) {
				String mateName = SocialMain.getMate(p.getName());
				if (btxt.equals("返回")) {
					p.showFormWindow(SocialGUI.marrySystemWindow());
				} else if (btxt.equals("確認")) {
					if (mateName != null) {
						SocialMain.breakingSet.add(p.getName());
						p.sendMessage(TextFormat.RED + "您真的要離開TA了嗎...?");
						Player mate = SocialMain.getInstance().getServer().getPlayer(mateName);

						if (mate != null) {
							p.sendMessage(TextFormat.GRAY + "已送出離婚請求, 等待對方回覆......");
							mate.sendMessage(TextFormat.RED + "您的伴侶" + p.getName() + "向您提出了離婚請求......\n"
									+ "再聊天室輸入@Y以表示同意\n" + "再聊天室輸入@N表示拒絕");
						} else {
							if (SocialMain.isLongTimeNoLogin(mateName)) {
								p.sendMessage(TextFormat.GRAY + "對方未上線時隔已久, 無條件離婚成功");

								new Mail(TextFormat.RED + "GM", mateName, TextFormat.RED + "系統信件",
										TextFormat.RED + "玩家" + p.getName() + "提出了離婚要求\n" + TextFormat.GRAY
												+ "由於您太久沒有上線, 為了維護玩家權利, 已無條件自動離婚\n" + "我們對此感到抱歉, 若您有任何問題或異議, 歡迎聯繫我們\n\n"
												+ TextFormat.RESET + "COSR團隊 敬上").sendOut();

								SocialMain.breakingSet.remove(p.getName());
								SocialMain.MPOOL.remove(p.getName());
							} else {
								p.sendMessage(TextFormat.GRAY + "您的伴侶不在線上, 離婚須兩人同意!");
							}
						}
					} else
						p.sendMessage(TextFormat.GRAY + "您當前還沒有伴侶, 趕快去找一個吧(#");
				}
			}
		}
	}
}
