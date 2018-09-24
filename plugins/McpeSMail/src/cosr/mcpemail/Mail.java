package cosr.mcpemail;

import java.io.File;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

public class Mail {
	
	private String sender;
	private String reciever;
	private String topic;
	private String content;
	private boolean isread;
	
	private Main plugin = Main.getInstance();
	
	public Mail() {
		this("Server", "", "Test Mail", "Test mail sended from COSR game manager.", false);
	}
	
	public Mail(String sender, String reciever, String topic, String content) {
		this(sender, reciever, topic, content, false);
	}
	
	public Mail(String sender, String reciever, String topic, String content, boolean isread) {
		this.sender = sender;
		this.reciever = reciever;
		this.topic = topic;
		this.content = content;
		this.isread = isread;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReciever() {
		return reciever;
	}

	public void setReciever(String reciever) {
		this.reciever = reciever;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public boolean isRead() {
		return isread;
	}
	
	public void toRead() {
		isread = true;
	}
	
	public void unRead() {
		isread = false;
	}
	
	public String[] getData() {
		return new String[] {
			this.getTopic(), 
			this.getSender(),
			this.getContent(),
			Boolean.toString(isread)
		};
	}
	
	public String formatData(String separator) {
		String data = "";
		for(int i = 0; i <= this.getData().length-1; i++) {
			if(i == (this.getData().length-1)) data += this.getData()[i];
			else data += (this.getData()[i] + separator);
		}
		return data;
	}
	
	public static String formMailTitle(int num, Mail mail) {
		String format = "";
		format += "#"+num+" " +(TextFormat.BOLD + (TextFormat.DARK_AQUA + mail.getTopic())) + (TextFormat.RESET + "-") + TextFormat.ITALIC + (TextFormat.LIGHT_PURPLE+mail.getSender());
		
		if(mail.isRead()) format += TextFormat.BOLD + (TextFormat.GRAY + "(已讀)");
		else format += TextFormat.BOLD + (TextFormat.RED + "(未讀)");
		
		return format;
	}
	
	public static String formMailDetail(Mail mail) {
		String detail = "";
		detail += (TextFormat.AQUA + "主旨: " + mail.getTopic() + "\n");
		detail += (TextFormat.YELLOW + "寄件人: " + mail.getSender() + "\n");
		detail += (TextFormat.RESET + mail.getContent());
		return detail;
	}
	
	public void readOut() {
		String contt = "";
		contt += ">>"+(TextFormat.AQUA+this.topic)+(TextFormat.RESET+"-")+(TextFormat.ITALIC+(TextFormat.YELLOW+this.sender))+"\n";
		contt += (TextFormat.RESET+this.content);
		
		plugin.getServer().getPlayer(reciever).sendMessage(contt);
		this.toRead();
	}
	
	public void sendOut() {
		if(plugin.getServer().getPlayer(this.reciever) != null) plugin.getMailboxes().get(reciever).put(this);
		Config mbfile = new Config(new File(plugin.getDataFolder(), reciever+".yml"), Config.YAML);
		mbfile.set(Integer.toString(mbfile.getAll().size()+1), this.formatData("--"));
		mbfile.save();
	}
	
	public static Mail readConfig(File mailfile, int number) {
		Config mailconf = new Config(mailfile, Config.YAML);
		String[] maildata = (mailconf.getString(Integer.toString(number))).split("--");
		boolean _isread = (maildata[3].equals("true"));
		return new Mail(maildata[1], mailfile.getName().replaceAll(".yml", ""), maildata[0], maildata[2], _isread);
	}
	
	public static void readConfigDebug(File mailfile, int number, Player player) {
		Config mailconf = new Config(mailfile, Config.YAML);
		String maildata = mailconf.getString(Integer.toString(number));
		String[] md_arr = maildata.split("--");
		player.sendMessage(maildata);
		player.sendMessage(md_arr[0]+"-"+md_arr[1]+"-"+md_arr[2]+"-"+md_arr[3]);
	}
}
