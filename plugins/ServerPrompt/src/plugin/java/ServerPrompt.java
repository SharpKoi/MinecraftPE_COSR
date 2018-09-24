package plugin.java;

import java.io.File;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class ServerPrompt extends PluginBase implements Listener {
	
	private Config content;
	public String set_text;
	public String default_text = "§eWelcome to our server. Have a nice time : )";
	public String Usage = "------進服提示使用方法(1/1)------\n"
						+ "/prompt set [content] ----在content內輸入訊息以設定進服提示\n"
						+ "/prompt default       ----清除進服提示的內容\n"
						+ "/prompt show          ----顯示當前進服提示的內容\n"
						+ "/prompt on            ----開啟進服提示的顯示\n"
						+ "/prompt off           ----關閉進服提示的顯示";

	public void onEnable() {
		
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getDataFolder().mkdirs();
		this.content = new Config(new File(this.getDataFolder() + "/Config.yml"), Config.YAML);
		if(this.content.get("Prompt") == null) {
			this.content.set("Prompt", "§eWelcome to our server. Have a nice time : )");
		}
		if(this.content.get("Turn") == null) {
			this.content.set("Turn", "on");
		}
		this.content.save();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if(cmd.getName().equals("prompt")) {
				if(args[0].equals("set")) {
					if(!args[1].equals(null)) {
						this.set_text = args[1];
						this.content.set("Prompt", set_text);
						sender.sendMessage("正在等候您的設定完成...");
						this.content.save();
						sender.sendMessage("§a儲存完畢!");
						sender.sendMessage(TextFormat.AQUA+"當前的提示內容為: " + TextFormat.RESET+this.content.getString("Prompt"));
					}else {
						sender.sendMessage(Usage);
					}
				}
				if(args[0].equals("default")) {
					this.content.set("Prompt", default_text);
					this.content.save();
					sender.sendMessage("§a已成功設為預設!");
				}
				if(args[0].equals("show")) {
					sender.sendMessage("§b當前的提示內容為: " + this.content.getString("Prompt"));
				}
				if(args[0].equals("off")) {
					this.content.set("Turn", "off");
					sender.sendMessage("§a已成功關閉進服提示的顯示");
				}
				if(args[0].equals("on")) {
					this.content.set("Turn", "on");
					sender.sendMessage("§a已成功開啟進服提示的顯示");
				}
				else if(args[0].equals(null)) {
					sender.sendMessage(Usage);
				}
			}
			return true;
		}
		catch(ArrayIndexOutOfBoundsException e) {
			sender.sendMessage(Usage);
			e.printStackTrace();
		}
		return true;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if(this.content.get("Turn").equals("on")) {
			player.sendMessage(this.content.getString("Prompt"));
		}
	}
}
