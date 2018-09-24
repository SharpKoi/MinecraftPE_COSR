package cosr.mcpemail;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.mail.AuthenticationFailedException;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

public class Main extends PluginBase {
	
	private HashMap<String, MailBox> mailboxes;		//玩家名-玩家信箱
	private HashMap<String, Mail> oppool;			//玩家名-玩家在視窗介面上點擊的信件
	private Config gmailConf;
	private MailGUI ui;
	private String usage = "/mail send [player_name] [subject] [content]   寄送一封遊戲信件給玩家\n"
						 + "/mail read [mailID]                            閱讀該編號的信件\n"
						 + "/mail del [mailID]                             刪除該編號信件\n"
						 + "/mail clear                                    清空信箱\n"
						 + "/mail ui                                       使用信箱介面\n"
						 + "/mail gset tm [Example@gmail.com]              設置測試用信箱\n"
						 + "/mail gset addr [Example@gmail.com]            設定gmail信箱位址\n"
						 + "/mail gset pw [password]                       設定gmail信箱密碼\n"
						 + "/mail gset src [mail_source]                   設定gmail寄信來源";
	
	private static Main plugin = null;
	
	public static Main getInstance() {
		return Main.plugin;
	}
	
	public void onEnable() {
		plugin = this;
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		this.getDataFolder().mkdirs();
		mailboxes = new HashMap<String, MailBox>();
		oppool = new HashMap<String, Mail>();
		ui = new MailGUI(this);
		
	    gmailConf = new Config(new File(this.getDataFolder(), "GmailData/GmailServiceData.yml"), Config.YAML);
	    if(gmailConf.exists("MailAddress") && gmailConf.exists("Password")) {
			McpeGmail.setPublicSender(gmailConf.getString("MailAddress"), gmailConf.getString("Password"));
			McpeGmail.init();
		}else {
			this.getLogger().info(TextFormat.RED + "Gmail信箱尚未設置完成，若設置不完整將無法使用Gmail寄件功能。");
		}
	    if(gmailConf.exists("Source")) McpeGmail.setMailSource(gmailConf.getString("Source"));
	    
	    this.getLogger().info(TextFormat.GREEN + "Loaded Done!");
	}
	
	public void onDisable() {
		this.getLogger().info(TextFormat.GRAY + "正在儲存玩家信箱...");
		for(MailBox box : mailboxes.values()) {
			try {
				box.clearConfig();
				box.saveAll();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.getLogger().info(TextFormat.GRAY + "玩家信箱儲存完畢!");
		
		this.getLogger().info(TextFormat.GRAY + "正在儲存Gmail配置檔...");
		gmailConf.save();
		this.getLogger().info(TextFormat.GRAY + "Gmail配置檔儲存完畢!");
		
		this.getLogger().info(TextFormat.BOLD + (TextFormat.DARK_GREEN + "GoodBye!"));
		return;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if(cmd.getName().equals("mail")) {
				//可在遊戲及控制台中執行的指令
				if(args[0].equals("gset")) {
					if(sender.isOp()) {
						if(args[1].equals("tm")) {
							gmailConf.set("TestAddress", args[2]);
							sender.sendMessage(TextFormat.GREEN + "個人信箱位址設定成功!");
						}
						else if(args[1].equals("addr")) {
							if(!gmailConf.exists("TestAddress")) {
								sender.sendMessage(TextFormat.RED + "請先設置好測試用信箱 方法: /mail gset tm example@gmail.com");
							}
							if(args[2].endsWith("@gmail.com")) {
								gmailConf.set("MailAddress", args[2]);
								sender.sendMessage(TextFormat.GREEN + "信箱位址設定成功!");
							}else {
								sender.sendMessage(TextFormat.RED + "信箱格式錯誤! 僅限使用一般Gmail信箱帳號");
							}
						}
						else if(args[1].equals("pw")) {
							gmailConf.set("Password", args[2]);
							sender.sendMessage(TextFormat.GREEN + "信箱密碼設定成功!");
						}
						else if(args[1].equals("src")) {
							gmailConf.set("Source", args[2]);
							McpeGmail.setMailSource(args[2]);
							sender.sendMessage(TextFormat.GREEN + "寄件來源地址設定成功!");
						}
						else return false;
						gmailConf.save();
					
						if(gmailConf.exists("MailAddress") && gmailConf.exists("Password")) {
							McpeGmail.setPublicSender(gmailConf.getString("MailAddress"), gmailConf.getString("Password"));
							McpeGmail.init();
							if(gmailConf.exists("TestAddress")) {
								McpeGmail testmail = new McpeGmail(sender.getName(), gmailConf.getString("TestAddress"), "The Gmail Sending Test of McpeSMail", "Just Test :)");
								try {
									sender.sendMessage(TextFormat.GRAY + "正在執行Gmail測試 請勿離開遊戲......");
									testmail.sendOut();
									sender.sendMessage(TextFormat.GREEN + "Gmail測試通過!");
								} catch (AuthenticationFailedException e) {
									if(sender.isOp()) sender.sendMessage(TextFormat.RED + "伺服器信箱帳號或密碼有誤! 請重新設定");
								}
							}
						}
						return true;
					}else sender.sendMessage(TextFormat.RED + "不明指令/" + cmd.getName() + "\n或者您未有該權限執行此指令");	//end of 'if(sender.isOp())' 如果玩家非管理員則無法使用gset指令
				}
				
				if(args[0].equals("help")) {
					sender.sendMessage(usage);
					return true;
				}
				
				//執行到此代表使用者輸入的指令非gset，以下指令則需要在遊戲中才能執行
				if(!sender.isPlayer()) {
					sender.sendMessage(TextFormat.RED + "請在遊戲中執行此指令");
					return true;
				}
				
				if(args[0].equals("list") || args[0].equals("l")) {
					sender.sendMessage((TextFormat.BOLD + (TextFormat.GREEN + "我的信箱: \n"))
										+ TextFormat.RESET + mailboxes.get( ((Player)sender).getName() ).listMailsOut());
				}
				else if(args[0].equals("send") || args[0].equals("s")) {
					if(args[1] != null && args[2] != null && args[3] != null) {
						Mail mail = new Mail(sender.getName(), args[1], args[2], args[3]);
						mail.sendOut();
						sender.sendMessage(TextFormat.GREEN + "信件寄送成功!");
						if(this.getServer().getPlayer(mail.getReciever()) != null)
							this.getServer().getPlayer(mail.getReciever()).sendMessage(TextFormat.GOLD + "有一封來自" + sender.getName() + "的信寄給您了，請查收!");
					}
				}
				else if(args[0].equals("read") || args[0].equals("r")) {
					if(args[1] != null) {
						if(mailboxes.get(sender.getName()).getMails().containsKey(Integer.parseInt(args[1])))
							mailboxes.get(sender.getName()).getMailbyID(Integer.parseInt(args[1])).readOut();
					}
				}
				else if(args[0].equals("del") || args[0].equals("d")) {
					if(args[1] != null) {
						if(mailboxes.get(sender.getName()).getMails().containsKey(Integer.parseInt(args[1]))) {
							mailboxes.get(sender.getName()).delete(Integer.parseInt(args[1]));
							sender.sendMessage(TextFormat.GRAY + "信件刪除成功!");
						}
					}
				}
				else if(args[0].equals("clear") || args[0].equals("c")) {
					mailboxes.get(sender.getName()).clear();
					sender.sendMessage(TextFormat.GRAY + "您的信箱已清空!");
				}
				else if(args[0].equals("ui") || args[0].equals("u")) {
					ui.homePage((Player) sender);
				}
				else return false;
				
				return true;
			}
		}catch(IOException err) {
			sender.sendMessage(TextFormat.RED + "抱歉，找不到您的信箱配置檔!請立即向伺服器管理員反應");
			this.getLogger().alert(TextFormat.RED + "找不到玩家"+sender.getName()+"的信箱配置檔");
		}catch(ArrayIndexOutOfBoundsException err) {
			sender.sendMessage(TextFormat.RED + "請輸入正確的指令格式!");
		}
		return false;
	}

	public Config gmailConfig() {
		return gmailConf;
	}

	public HashMap<String, MailBox> getMailboxes() {
		return mailboxes;
	}

	public HashMap<String, Mail> getOppool() {
		return oppool;
	}
	
	public MailGUI mailGUI() {
		return ui;
	}
}
