package cosr.friend;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;
import cosr.roleplay.CRolePlay;
import cosr.roleplay.database.PlayerDataBase;

public class SocialGUI {
	
	/*社交系統首頁*/
	public static FormWindowSimple homePage() {
		FormWindowSimple window = new FormWindowSimple("社交首頁", "");
		window.addButton(new ElementButton(TextFormat.BOLD + "好友列表"));
		window.addButton(new ElementButton(TextFormat.BOLD + "伴侶系統"));
		window.addButton(new ElementButton(TextFormat.BOLD + "寫下心情"));
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "關閉視窗")));
		return window;
	}
	
	/*好友系統所有相關介面*/
	public static FormWindowSimple friendListWindow(Player p) {
		FormWindowSimple window = new FormWindowSimple("好友列表", "點選好友已獲得更多資訊");
		window.addButton(new ElementButton(TextFormat.BOLD + "新增好友"));
		window.addButton(new ElementButton(TextFormat.BOLD + "處理好友請求"));
		for(String friend : SocialMain.FPOOL.get(p.getName())) {
			try {
				window.addButton(new ElementButton(friend + " Lv." + CRolePlay.getPlv(friend).getLv()));
			}catch(FileNotFoundException e) {
				window.addButton(new ElementButton(friend));
				continue;
			}
		}
		window.addButton(new ElementButton("返回至首頁"));
		
		return window;
	}
	
	public static FormWindowCustom newFriendWindow(String playerName) {
		FormWindowCustom window = new FormWindowCustom("新增好友");
		ElementDropdown onlinePlayerList = new ElementDropdown("線上玩家");
		
		onlinePlayerList.addOption("None");
		for(Player p : SocialMain.getInstance().getServer().getOnlinePlayers().values()) {
			if(p.getName() != playerName)
				onlinePlayerList.addOption(p.getName());
		}
		window.addElement(new ElementInput("請輸入玩家名稱"));
		window.addElement(onlinePlayerList);
		return window;
	}
	
	public static FormWindowSimple friendRequestWindow(String playerName) {
		ArrayList<String> rqList = SocialMain.getFriendRequests(playerName);
		FormWindowSimple window = new FormWindowSimple("好友申請列表", "");
		if(rqList != null) {
			for(String rq : rqList) {
				window.addButton(new ElementButton(rq));
			}
		}
		window.addButton(new ElementButton(TextFormat.BOLD + (TextFormat.GRAY + "返回")));
		return window;
	}
	
	public static FormWindowSimple friendWindow(String friendName) {
		FormWindowSimple window = new FormWindowSimple(friendName, "");
		window.addButton(new ElementButton("查看個人資訊"));
		window.addButton(new ElementButton("給予金錢"));
		window.addButton(new ElementButton("寄信"));
		window.addButton(new ElementButton(TextFormat.RED + "解除好友關係"));
		window.addButton(new ElementButton("返回至好友列表"));
		
		return window;
	}
	
	public static FormWindowModal playerDataWindow(String playerName, String _who) throws FileNotFoundException {
		return new FormWindowModal("玩家"+playerName+"的個人檔案", playerInfo(_who), 
				"加為好友", (SocialMain.getFriendRequests(playerName).contains(_who)? "拒絕好友申請" : "取消"));
	}
	
	public static FormWindowModal friendInfoWindow(String friendName) throws FileNotFoundException {
		return new FormWindowModal("好友: " + friendName, playerInfo(friendName), "確定", "返回");
	}
	
	public static FormWindowModal sureToDelFriendWindow(String friendName) {
		return new FormWindowModal("好友: " + friendName, "確認刪除該好友嗎?\n( ! 刪除後將無法復原)", "確認", "取消");
	}
	
	/*結婚系統所有相關介面*/
	public static FormWindowCustom proposingWindow(String playerName) {
		FormWindowCustom window = new FormWindowCustom("您還沒有伴侶, 趕快找一個TA吧");
		
		ElementDropdown dropList = new ElementDropdown("線上玩家");
		dropList.addOption("None");
		for(Player p : SocialMain.getInstance().getServer().getOnlinePlayers().values()) {
			if(p.getName() != playerName)
				dropList.addOption(p.getName());
		}
		window.addElement(dropList);
		
		return window;
	}
	
	public static FormWindowSimple marrySystemWindow() {
		FormWindowSimple window = new FormWindowSimple("伺服器伴侶", "");
		window.addButton(new ElementButton("查看伴侶資訊"));
		window.addButton(new ElementButton("到TA身邊"));
		window.addButton(new ElementButton("給予金錢"));
		window.addButton(new ElementButton("寄信"));
		window.addButton(new ElementButton(TextFormat.RED + "解除伴侶關係"));
		window.addButton(new ElementButton("返回至首頁"));
		
		return window;
	}
	
	public static FormWindowModal mateInfoWindow(String mateName) throws FileNotFoundException {
		Player mate = SocialMain.getInstance().getServer().getPlayer(mateName);
		String moodMsg = SocialMain.getMoodMsg(mateName);
		String content = TextFormat.DARK_GREEN + "伴侶名稱: " + TextFormat.RESET + ((mate != null)? mate.getDisplayName() : mateName) + "\n" + 
						 TextFormat.DARK_GREEN + "伴侶稱號: " + TextFormat.RESET + CRolePlay.getPinnedTitle(mateName).body() + 
						 TextFormat.DARK_GREEN + "伴侶等級: " + TextFormat.RESET + CRolePlay.getPlv(mateName).getLv() + "\n" + 
						 TextFormat.DARK_GREEN + "在線狀態: " + TextFormat.RESET + ((mate != null)? TextFormat.GREEN+"在線" : TextFormat.GRAY+"離線") + "\n" + 
						 TextFormat.DARK_GREEN + "上次登入: " + TextFormat.RESET + 
						 	new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new PlayerDataBase(mateName).loginMoment.getTime()) + "\n" + 
						 TextFormat.DARK_GREEN + "心情小語: " + TextFormat.RESET + 
						 	(moodMsg != null? moodMsg : TextFormat.GRAY+"這位魔法師很懶, 什麼都沒留下") + "\n";
		return new FormWindowModal("伴侶: " + mateName, content, "確定", "返回");
	}
	
	public static FormWindowModal sureToDelMateWindow(String mateName) {
		return new FormWindowModal("伴侶: " + mateName, "您確定要離開TA了嗎?QQ\n", "確認", "取消");
	}
	
	/*心情小語*/
	public static FormWindowCustom moodMsgWindow(String user) {
		FormWindowCustom window = new FormWindowCustom("寫下此刻的心情");
		
		window.addElement(new ElementInput("請輸入訊息", "", (SocialMain.msgMap.containsKey(user)? SocialMain.msgMap.get(user) : "")));
		return window;
	}
	
	private static String playerInfo(String playerName) throws FileNotFoundException {
		Player friend = SocialMain.getInstance().getServer().getPlayer(playerName);
		String mateName = SocialMain.getMate(playerName);
		String moodMsg = SocialMain.getMoodMsg(playerName);
		String content = TextFormat.DARK_GREEN + "玩家名稱: " + TextFormat.RESET + ((friend != null)? friend.getDisplayName() : playerName) + "\n" + 
						 TextFormat.DARK_GREEN + "玩家等級: " + TextFormat.RESET + CRolePlay.getPlv(playerName).getLv() + "\n" + 
						 TextFormat.DARK_GREEN + "玩家稱號: " + TextFormat.RESET + CRolePlay.getPinnedTitle(playerName).body() + "\n" + 
						 TextFormat.DARK_GREEN + "在線狀態: " + TextFormat.RESET + ((friend != null)? TextFormat.GREEN+"在線" : TextFormat.GRAY+"離線") + "\n" + 
						 TextFormat.DARK_GREEN + "伴侶: "     + TextFormat.RESET + ((mateName != null)? mateName : "無") + "\n" + 
						 TextFormat.DARK_GREEN + "上次登入: " + TextFormat.RESET + 
						 	new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new PlayerDataBase(playerName).loginMoment.getTime()) + "\n" + 
						 TextFormat.DARK_GREEN + "心情小語: " + TextFormat.RESET + 
						 	(moodMsg != null? moodMsg : TextFormat.GRAY+"這位魔法師很懶, 什麼都沒留下") + "\n";
		//玩家公會
		return content;
	}
}
