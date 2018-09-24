package cosr.mcpemail;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;

import javax.mail.AuthenticationFailedException;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
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
import cn.nukkit.utils.TextFormat;

public class EventListener implements Listener {
	
	private Main plugin = Main.getInstance();
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) throws EOFException {
		Player player = event.getPlayer();
		File file = new File(plugin.getDataFolder(), player.getName()+".yml");
		MailBox mailbox = new MailBox(player.getName());
		plugin.getMailboxes().put(player.getName(), mailbox);
		int amount = 0;
		try {
		if(file.exists()) mailbox.readAll();
		else {
			Mail guidemail = new Mail("嚮導精靈", player.getName(), "歡迎您的到來", "哈囉! 你是第一次加入伺服器對吧?請仔細閱讀完新手導覽後再開始遊戲!");
			guidemail.sendOut();
		}
		
		for(Mail mail : plugin.getMailboxes().get(player.getName()).getMails().values()) {
			if(!mail.isRead()) amount++;
		}
		
		if(amount != 0) player.sendMessage(TextFormat.GREEN + "您尚有"+Integer.toString(amount)+"新封信件未讀");
		
		}catch(Exception e) {}
	}
		
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if(plugin.getMailboxes().containsKey(player.getName())) {
			MailBox mb = plugin.getMailboxes().get(player.getName());
			try {
				mb.clearConfig();
				mb.saveAll();
				plugin.getMailboxes().remove(player.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@EventHandler
	public void onResponse(PlayerFormRespondedEvent event) {
		FormWindow window = event.getWindow();
		FormResponse response = event.getResponse();
		Player player = event.getPlayer();
		String title, button, pre_title = "";
		try {
			if(window instanceof FormWindowSimple) {
				title = ((FormWindowSimple) window).getTitle();
				button = ((FormResponseSimple) response).getClickedButton().getText();
				if(title.equals("信箱首頁")) {
					if(button.equals(TextFormat.BOLD + "寄信")) plugin.mailGUI().mailOutW(player);
					if(button.equals(TextFormat.BOLD + (TextFormat.GRAY + "設定Gmail信箱"))) plugin.mailGUI().gmailSetW(player);
					if(button.equals(TextFormat.BOLD + "Gmail寄信")) plugin.mailGUI().gmailOutW(player);
					if(button.equals(TextFormat.BOLD + "我的信箱")) plugin.mailGUI().mailListW(player);
					if(button.equals(TextFormat.BOLD + (TextFormat.RED + "清空信箱"))) plugin.mailGUI().mailboxCleareW(player);
				}
				//如果玩家在信件列表中點擊了A信件，就把 玩家-A信件 放入oppool
				if(title.equals("信件列表")) {
					int num = 1;
					for(Mail mail : plugin.getMailboxes().get(player.getName()).getMails().values()) {
						if(button.equals(Mail.formMailTitle(num, mail))) {
							plugin.getOppool().put(player.getName(), mail);
							plugin.mailGUI().mailActionW(mail, player);
							break;
						}
						num++;
					}
					if(button.equals(TextFormat.BOLD + "返回首頁")) plugin.mailGUI().homePage(player);
				}
				//mailActionWindow的標題由前一個視窗(mailListWindow)所點擊的信件決定
				if(plugin.getOppool().containsKey(player.getName())) pre_title = "信件#"+plugin.getMailboxes().get(player.getName()).getIDof(plugin.getOppool().get(player.getName()));
				if(title.equals(pre_title)) {
					if(button.equals("閱讀信件")) plugin.mailGUI().mailReadingW(plugin.getOppool().get(player.getName()), player);
					if(button.equals("標示為已讀")) {
						player.sendMessage(TextFormat.GREEN + (pre_title + "已被設為已讀"));
						plugin.getOppool().get(player.getName()).toRead();
						plugin.mailGUI().mailListW(player);
					}
					if(button.equals("標示為未讀")) {
						player.sendMessage(TextFormat.GREEN + (pre_title + "已被設為未讀"));
						plugin.getOppool().get(player.getName()).unRead();
						plugin.mailGUI().mailListW(player);
					}
					//if(button.equals("標示為重要"))		待更新
					//if(button.equals("標示為不重要"))		待更新
					if(button.equals(TextFormat.RED + "刪除此信件")) plugin.mailGUI().mailDeleteW(plugin.getOppool().get(player.getName()), player);
					if(button.equals(TextFormat.BOLD + "返回至列表")) plugin.mailGUI().mailListW(player);
				}
			}
			else if(window instanceof FormWindowCustom) {
				title = ((FormWindowCustom) window).getTitle();
				
				if(title.equals("Gmail信箱設定")) {
					String prvt = ((FormResponseCustom) response).getInputResponse(0);
					String address = ((FormResponseCustom) response).getInputResponse(1);
					String password = ((FormResponseCustom) response).getInputResponse(2);
					String source = ((FormResponseCustom) response).getInputResponse(3);
					
					if(prvt.equals("") || !prvt.contains("@")) {
						player.sendMessage(TextFormat.RED + "個人信箱帳號格式錯誤! 請重新輸入");
						return;
					}else {
						plugin.gmailConfig().set("TestAddress", prvt);
					}
					
					if(!address.equals("") && !password.equals("")) {
						if(address.endsWith("@gmail.com")) {
							plugin.gmailConfig().set("MailAddress", address);
						}else {
							player.sendMessage(TextFormat.RED + "信箱格式錯誤! 僅限使用一般Gmail信箱帳號");
							plugin.mailGUI().gmailSetW(player);
						}
						plugin.gmailConfig().set("Password", password);
						if(!source.equals("")) {
							plugin.gmailConfig().set("Source", source);
							McpeGmail.setMailSource(source);
						}
						
						plugin.gmailConfig().save();
						if(plugin.gmailConfig().exists("MailAddress") && plugin.gmailConfig().exists("Password")) {
							McpeGmail.setPublicSender(plugin.gmailConfig().getString("MailAddress"), plugin.gmailConfig().getString("Password"));
							McpeGmail.init();
							McpeGmail testmail = new McpeGmail(player.getName(), prvt, "The Gmail Sending Test of McpeSMail", "Just Test :)");
							try {
								player.sendMessage(TextFormat.GRAY + "正在執行Gmail測試 請勿離開遊戲......");
								testmail.sendOut();
								player.sendMessage(TextFormat.GREEN + "Gmail測試通過!");
								player.sendMessage(TextFormat.GREEN + "Gmail信箱設定成功! 可以開始使用Gmail寄件功能了");
							} catch (AuthenticationFailedException e) {
								if(player.isOp()) {
									player.sendMessage(TextFormat.RED + "信箱帳號或密碼有誤! 請重新設定");
									plugin.mailGUI().gmailSetW(player);
								}
							}
						}
					}else {
						player.sendMessage(TextFormat.RED + "信箱帳號以及密碼為必填欄位!");
						plugin.mailGUI().gmailSetW(player);
					}
				}
			
				if(title.equals("信件郵寄")) {
					String reciever, topic, content = "";
					reciever = ((FormResponseCustom) response).getInputResponse(0);
					topic = ((FormResponseCustom) response).getInputResponse(1);
					content = ((FormResponseCustom) response).getInputResponse(2);
					Mail newmail = new Mail(player.getName(), reciever, topic, content);
					newmail.sendOut();
					player.sendMessage(TextFormat.GREEN + "信件已成功寄出");
					if(plugin.getServer().getPlayer(newmail.getReciever()) != null)
						plugin.getServer().getPlayer(newmail.getReciever()).sendMessage(TextFormat.GOLD + "有一封來自" + player.getName() + "的信寄給您了，請查收!");
				}
				
				if(title.equals("Gmail信件郵寄")) {
					try {
						String recipient, subtitle, content = "";
						recipient = ((FormResponseCustom) response).getInputResponse(0);
						subtitle = ((FormResponseCustom) response).getInputResponse(1);
						content = ((FormResponseCustom) response).getInputResponse(2);
						McpeGmail gmail = new McpeGmail(player.getName(), recipient, subtitle, content);
						player.sendMessage(TextFormat.GRAY + "正在嘗試寄送Gmail信件...");
						gmail.sendOut();
						player.sendMessage(TextFormat.GREEN + "Gmail信件寄送成功!");
					}catch(AuthenticationFailedException err) {
						player.sendMessage(TextFormat.RED + "Gmail信件發送失敗! 原因未知");
						plugin.getServer().getLogger().alert(err.getMessage());
					}
				}
			}
			else if(window instanceof FormWindowModal) {
				title = ((FormWindowModal) window).getTitle();
				if(title.equals("信件內容")) {
					if(plugin.getOppool().containsKey(player.getName())) plugin.getOppool().get(player.getName()).toRead();
				
					if(((FormResponseModal) response).getClickedButtonText().equals("回信")) {
						plugin.mailGUI().mailOutW(player);
					}
					else plugin.mailGUI().mailListW(player);
				}
				if(title.equals("確認刪除此信件?")) {
					if(((FormResponseModal) response).getClickedButtonText().equals("確認")) {
						if(plugin.getOppool().containsKey(player.getName())) {
							try {
								//把該信件從玩家的信箱中移除
								plugin.getMailboxes().get(player.getName()).delete(plugin.getMailboxes().get(player.getName()).getIDof(plugin.getOppool().get(player.getName())));
							} catch (IOException e) {
								e.printStackTrace();
							}
							plugin.getOppool().remove(player.getName());
							player.sendMessage(TextFormat.GREEN + (pre_title + "已成功被刪除"));
						}else player.sendMessage(TextFormat.RED + "刪除失敗!(原因:信箱中找不到該信件)");
					}
					plugin.mailGUI().mailListW(player);
				}
				//直接在首頁中點擊的功能，無須操作oppool
				if(title.equals("確認清空信箱?")) {
					if(((FormResponseModal) response).getClickedButtonText().equals("確認")) {
						try {
							plugin.getMailboxes().get(player.getName()).clear();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}else 
						plugin.mailGUI().homePage(player);
				}
			}
		}catch(NullPointerException err) {
			//Just catch
		}
	}
}
