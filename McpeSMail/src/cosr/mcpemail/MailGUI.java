package cosr.mcpemail;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;

public class MailGUI {
	
	private Main plugin;
	
	public MailGUI(Main plugin) {
		this.plugin = plugin;
	}
	
	public void homePage(Player p) {
		FormWindowSimple window = new FormWindowSimple("信箱首頁", "");
		
		window.addButton(new ElementButton(TextFormat.BOLD + "寄信"));
		
		if(plugin.gmailConfig().exists("MailAddress") && plugin.gmailConfig().exists("Password")) {
			window.addButton(new ElementButton(TextFormat.BOLD + "Gmail寄信"));
		}else {
			if(p.isOp()) window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "設定Gmail信箱")));
		}
		
		window.addButton(new ElementButton(TextFormat.BOLD + "我的信箱"));
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.RED + "清空信箱")));
		window.addButton(new ElementButton(TextFormat.BOLD + ("關閉視窗")));
		
		p.showFormWindow(window);
	}
	
	public void gmailSetW(Player p) {
		FormWindowCustom window = new FormWindowCustom("Gmail信箱設定");
		
		window.addElement(new ElementInput("個人信箱地址", "example@gmail.com"));
		window.addElement(new ElementInput("Server信箱地址", "example@gmail.com"));
		window.addElement(new ElementInput("Server信箱密碼"));
		window.addElement(new ElementInput("信件發送來源", "Server(Minecraft PE)"));
		
		p.showFormWindow(window);
	}
	
	public void mailOutW(Player p) {
		FormWindowCustom window = new FormWindowCustom("信件郵寄");
		
		window.addElement(new ElementInput("收件人"));
		window.addElement(new ElementInput("主旨"));
		window.addElement(new ElementInput("內容"));
		
		p.showFormWindow(window);
	}
	
	public void mailOutW(Player p, String reciever) {
		FormWindowCustom window = new FormWindowCustom("寄信給 " + reciever);
		window.addElement(new ElementInput("請輸入主旨"));
		window.addElement(new ElementInput("請輸入內容"));
		
		p.showFormWindow(window);
	}
	
	public void gmailOutW(Player p) {
		FormWindowCustom window = new FormWindowCustom("Gmail信件郵寄");
		
		window.addElement(new ElementInput("收件人", "example@gmail.com"));
		window.addElement(new ElementInput("主旨"));
		window.addElement(new ElementInput("內容"));
		
		p.showFormWindow(window);
	}
	
	public void mailListW(Player p) {
		FormWindowSimple window = new FormWindowSimple("信件列表", "");
		int num = 1;
		for(Mail element : plugin.getMailboxes().get(p.getName()).getMails().values()) {
			window.addButton(new ElementButton(Mail.formMailTitle(num, element)));
			num++;
		}
		window.addButton(new ElementButton(TextFormat.BOLD + "返回首頁"));
		
		p.showFormWindow(window);
	}
	
	public void mailActionW(Mail mail, Player p) {
		//mailboxes是儲存 玩家名-信箱物件 的Map
		FormWindowSimple window = new FormWindowSimple("信件#"+plugin.getMailboxes().get(p.getName()).getIDof(mail), "請選擇您想要對此信件執行的動作");
		
		window.addButton(new ElementButton("閱讀信件"));
		window.addButton(new ElementButton("標示為已讀"));
		window.addButton(new ElementButton("標示為未讀"));
		window.addButton(new ElementButton("標示為重要"));
		window.addButton(new ElementButton("標示為不重要"));
		window.addButton(new ElementButton(TextFormat.RED + "刪除此信件"));
		window.addButton(new ElementButton(TextFormat.BOLD + "返回至列表"));
		
		p.showFormWindow(window);
	}
	
	public void mailReadingW(Mail mail, Player p) {
		FormWindowModal window = new FormWindowModal("信件內容", Mail.formMailDetail(mail), "回信", "返回");
		p.showFormWindow(window);
	}
	
	public void mailDeleteW(Mail mail, Player p) {
		FormWindowModal window = new FormWindowModal("確認刪除此信件?", "若刪除後將無法復原，是否確定刪除此信件?\n\n" + Mail.formMailDetail(mail), "確認", "取消");
		p.showFormWindow(window);
	}
	
	public void mailboxCleareW(Player p) {
		FormWindowModal window = new FormWindowModal("確認清空信箱?", "若清空後將無法復原，是否確定清空您的信箱?", "確認", "取消");
		p.showFormWindow(window);
	}
}
