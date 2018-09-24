package cosr.multiworld;

import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementDropdown;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.level.Level;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

public class TPGUI {
	
	private Main plugin;
	
	public TPGUI() {
		plugin = Main.getInstance();
	}
	
	public void showMapsWindow(Player p) {
		Config worlds = Main.getWorldsConfig();
		FormWindowSimple window = new FormWindowSimple("請選擇您想傳送的世界", "");
		Map<Integer, Level> levels = plugin.getServer().getLevels();
		String title = "";
		for(Level level : levels.values()) {
			if(worlds != null) {
				title = worlds.getString(level.getFolderName(), level.getFolderName());
				window.addButton(new ElementButton(title + TextFormat.DARK_GRAY + " (" + level.getName() + ")"));
			}
		}
		window.addButton(new ElementButton("新增地圖"));
		
		p.showFormWindow(window);
	}
	
	public void showNewLevelWindow(Player p) {
		FormWindowCustom window = new FormWindowCustom("新增地圖");
		
		window.addElement(new ElementInput("請輸入地圖檔名"));
		window.addElement(new ElementInput("請輸入地圖標題"));
		
		ElementDropdown dropList = new ElementDropdown("請選擇地圖類型");
		dropList.addOption("survival", true);
		dropList.addOption("flat");
		dropList.addOption("nether");
		
		window.addElement(dropList);
		
		p.showFormWindow(window);
	}
	
	public void levelManageWindow(Player p, String levelName) {
		FormWindowSimple window = new FormWindowSimple("管理地圖" + levelName, "");
		window.addButton(new ElementButton("傳送至 " + levelName));
		window.addButton(new ElementButton("移除 " + levelName));
		window.addButton(new ElementButton("編輯 " + levelName));
		window.addButton(new ElementButton("返回至地圖列表"));
		
		p.showFormWindow(window);
	}
	
	public void LevelEditWindow(Player p, String levelName) {
		FormWindowCustom window = new FormWindowCustom("編輯地圖-" + levelName);
		window.addElement(new ElementInput("請輸入地圖標題(留空則不更改)", TextFormat.ITALIC + "地圖標題名", Main.getWorldsConfig().getString(levelName, levelName)));
		
		p.showFormWindow(window);
	}
	
	public void sureToDelWindow(Player p, String levelName) {
		FormWindowModal window = new FormWindowModal("確定刪除該地圖" + levelName + "嗎?", 
									(TextFormat.BOLD + (TextFormat.RED + "!")) + TextFormat.RESET + " 一旦刪除後將無法復原", "確定", "取消");
		p.showFormWindow(window);
	}
}
