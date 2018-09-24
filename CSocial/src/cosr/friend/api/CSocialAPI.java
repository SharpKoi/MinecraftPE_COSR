package cosr.friend.api;

import java.io.File;
import java.util.ArrayList;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import cosr.friend.SocialMain;

public class CSocialAPI {

	public static Item socialItem() {
		Item item = Item.get(450);
		item.setCustomName(TextFormat.ITALIC + (TextFormat.LIGHT_PURPLE + "酒館的小人偶"));
		item.setLore(TextFormat.ITALIC + (TextFormat.YELLOW + "聽了酒館老爺的一席話後, "), 
				TextFormat.ITALIC + (TextFormat.YELLOW + "還是決定把這東西帶上了..."), 
				TextFormat.ITALIC + (TextFormat.YELLOW + "上面寫著: " + TextFormat.GRAY + "\"再強的英雄也需要朋友\""));
		return item.clone();
	}
	
	public static void makeFriend(String playerName, String otherName) {
		Player p = Server.getInstance().getPlayer(playerName);
		if (SocialMain.FPOOL.get(playerName).contains(otherName)) {
			if(p != null)
				p.sendMessage(TextFormat.GRAY + "該玩家已經是您的好友囉");
			return;
		}
		Player target = SocialMain.getInstance().getServer().getPlayer(otherName);
		if (target != null) {
			//實例化對方的好友請求池
			if (!SocialMain.friendRequestPool.containsKey(otherName)) {
				SocialMain.friendRequestPool.put(otherName, new ArrayList<String>());
			}
			SocialMain.friendRequestPool.get(otherName).add(p.getName());
			p.sendMessage(TextFormat.DARK_GREEN + "已傳送好友邀請給" + otherName);
			target.sendMessage(TextFormat.ITALIC
					+ (TextFormat.YELLOW + "玩家" + p.getName() + "向您提出了好友邀請\n")
					+ TextFormat.RESET + "輸入/cfriend accept " + playerName + "  接受\n"
					+ TextFormat.RESET + "輸入/cfriend deny " + playerName + "  拒絕");
		} else {
			File file = new File(SocialMain.getInstance().getDataFolder(), otherName + ".yml");
			if (!file.exists()) {
				p.sendMessage(TextFormat.RED + "找不到該玩家");
				return;
			}

			Config conf = new Config(file);
			if (!conf.exists("friend_requests")) {
				conf.set("friend_requests", new ArrayList<String>());
			}
			conf.getStringList("friend_requests").add(p.getName());
			conf.save();
			p.sendMessage(TextFormat.DARK_GREEN + "已傳送好友邀請給" + otherName);
		}
	}
}
