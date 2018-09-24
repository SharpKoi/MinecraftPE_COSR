package cosr.mcpemail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

public class MailBox {
	private String owner;
	private Map<Integer, Mail> mailmap;
	private Main plugin = Main.getInstance();
	
	public MailBox() {
		this.owner = "Server";
		mailmap = new TreeMap<Integer, Mail>();
	}
	
	public MailBox(String owner) {
		this(owner, new TreeMap<Integer, Mail>());
	}
	
	public MailBox(String owner, Map<Integer, Mail> mailmap) {
		this.owner = owner;
		this.mailmap = mailmap;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public Map<Integer, Mail> getMails() {
		return mailmap;
	}
	
	public Mail getMailbyID(int number) {
		if(mailmap.containsKey(number)) {
			return mailmap.get(number);
		}
		else {
			return null;
		}
	}
	
	public int getIDof(Mail mail) {
		int num = 1;
		for(Mail target : mailmap.values()) {
			if(target.equals(mail)) return num;
			num++;
		}
		return -1;
	}
	
	public void put(Mail mail) {
		this.mailmap.put(mailmap.size()+1, mail);
	}
	
	public void delete(int number) throws IOException {
		this.clearConfig();
		this.mailmap.remove(number);
		this.saveAll();
		this.readAll();
	}
	
	public void clear() throws IOException {
		this.mailmap.clear();
		this.clearConfig();
	}
	
	public String listMailsOut() {
		String maillist = "";
		Mail mail;
		for(int i = 1; i <= mailmap.size(); i++) {
			mail = mailmap.get(i);
			maillist += TextFormat.RESET + (TextFormat.ITALIC + ("#"+i));
			
			if(mail.isRead()) maillist += (TextFormat.GREEN + "(已讀)");
			else maillist += (TextFormat.RED + "(未讀)");
			
			maillist += (TextFormat.RESET + (TextFormat.AQUA + mail.getTopic()) + (TextFormat.RESET + "||") + (TextFormat.YELLOW + "寄件者: "+mail.getSender())+"\n");
		}
		return maillist;
	}
	
	public void readAll() {
		mailmap.clear();
		int num = 1;
		File mb_file = new File(plugin.getDataFolder(), owner+".yml");
		if(!mb_file.exists()) return;
		Config mb_conf = new Config(mb_file, Config.YAML);
		
		while(mb_conf.exists(Integer.toString(num))) {
			mailmap.put(num, Mail.readConfig(mb_file, num));
			num++;
		}
	}
	
	public void saveAll() {
		Config mbc = new Config(new File(plugin.getDataFolder(), owner+".yml"));
		int num = 1;
		for(Mail mail : mailmap.values()) {
			mbc.set(Integer.toString(num), mail.formatData("--"));
			num++;
		}
		mbc.save();
	}
	
	public void clearConfig() throws IOException {
		File file = new File(plugin.getDataFolder(), this.owner+".yml");
		FileWriter fw = null;
		if(file.exists()) {
			fw = new FileWriter(file);
			fw.write("");
			fw.flush();
			fw.close();
		}else return;
	}
}
