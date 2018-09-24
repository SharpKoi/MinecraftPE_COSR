package cosr.multiworld;

import java.util.Iterator;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

public class EventListener implements Listener {
	
	private Main plugin = Main.getInstance();
	private TPGUI ui = new TPGUI();
	
	@EventHandler
	public void onForm(PlayerFormRespondedEvent event) {
		Player p = event.getPlayer();
		FormWindow window = event.getWindow();
		FormResponse response = event.getResponse();
		
		if(response == null) return;
		
		try {
			if(window instanceof FormWindowSimple) {
				FormResponseSimple r = (FormResponseSimple) response;
				String title = ((FormWindowSimple) window).getTitle();
				String btext = r.getClickedButton().getText();
				if(title.equals("請選擇您想傳送的世界")) {
					if(btext.equals("新增地圖")) {
						ui.showNewLevelWindow(event.getPlayer());
						return;
					}
					String target = btext.split(" ")[1].replace(")", "").replace("(", "");
					if(target != null) {
						if(p.isOp()) {
							ui.levelManageWindow(p, target);
						}else {
							if(plugin.getServer().isLevelLoaded(target)) {
								event.getPlayer().sendTitle(TextFormat.GRAY + "正在傳送至", Main.getWorldsConfig().getString(target, target));
								event.getPlayer().teleport(plugin.getServer().getLevelByName(target).getSafeSpawn());
								event.getPlayer().sendTitle(TextFormat.GREEN + "您已成功傳送至", Main.getWorldsConfig().getString(target, target), 1, 1, 1);
							}
						}
					}else {
						event.getPlayer().sendMessage(TextFormat.GRAY + "此地圖尚未準備好，無法進行傳送");
					}
				}
				else if(title.startsWith("管理地圖")) {
					if(btext.equals("返回至地圖列表")) {
						ui.showMapsWindow(p);
						return;
					}
					String levelName = btext.split(" ")[1];
					Level level = Server.getInstance().getLevelByName(levelName);
					if(level != null) {
						if(btext.startsWith("傳送至 ")) {
							if(Server.getInstance().isLevelLoaded(levelName)) {
								event.getPlayer().sendTitle(TextFormat.GRAY + "正在傳送至", Main.getWorldsConfig().getString(levelName, levelName));
								event.getPlayer().teleport(plugin.getServer().getLevelByName(levelName).getSafeSpawn());
								event.getPlayer().sendTitle(TextFormat.GREEN + "您已成功傳送至", Main.getWorldsConfig().getString(levelName, levelName), 1, 1, 1);
							}
						}
						else if(btext.startsWith("移除 ")) {
							ui.sureToDelWindow(p, levelName);
						}
						else if(btext.startsWith("編輯 ")) {
							ui.LevelEditWindow(p, levelName);
						}
					}
				}
			}
			else if(window instanceof FormWindowCustom) {
				FormResponseCustom res = (FormResponseCustom) response;
				String title = ((FormWindowCustom)window).getTitle();
				if(title.equals("新增地圖")) {
					String folderName = res.getInputResponse(0);
					String titleName = res.getInputResponse(1);
					switch(res.getDropdownResponse(2).getElementContent()) {
					case "flat":
						Main.getInstance().registLevel(folderName, titleName, Generator.TYPE_FLAT);
						break;
					case "survival":
						Main.getInstance().registLevel(folderName, titleName, Generator.TYPE_INFINITE);
						break;
					case "nether":
						Main.getInstance().registLevel(folderName, titleName, Generator.TYPE_NETHER);
						break;
					default:
						Main.getInstance().registLevel(folderName, titleName, Generator.TYPE_FLAT);
						break;
					}
					p.sendMessage(TextFormat.GREEN + "地圖註冊成功!");
				}
				else if(title.startsWith("編輯地圖-")) {
					Level target = Server.getInstance().getLevelByName(title.replace("編輯地圖-", ""));
					String titleName = res.getInputResponse(0);
					if(!titleName.equals("")) {
						Main.getWorldsConfig().set(target.getFolderName(), titleName);
						Main.getWorldsConfig().save();
						p.sendMessage(TextFormat.GREEN + "地圖" + TextFormat.RESET + target.getFolderName() +
								TextFormat.GREEN + "的標題已變更為" + TextFormat.RESET + titleName);
					}
				}
			}
			else if(window instanceof FormWindowModal) {
				String title = ((FormWindowModal) window).getTitle();
				FormResponseModal r = (FormResponseModal) response;
				if(title.startsWith("確定刪除該地圖")) {
					String levelName = title.replace("確定刪除該地圖", "").replace("嗎?", "");
					Level level = Server.getInstance().getLevelByName(levelName);
					if(r.getClickedButtonText().equals("確定")) {
						if(p.isOp()) {
							if(Main.getWorldsConfig().exists(levelName)) {
								Main.getWorldsConfig().remove(levelName);
								Main.getWorldsConfig().save();
							}
							if(level != null) {
								if(p.getLevel().equals(level)) p.teleport(Server.getInstance().getDefaultLevel().getSpawnLocation());
								Server.getInstance().getLevelByName(levelName).unload();
								if(Main.removeLevel(levelName)) {
									p.sendMessage(TextFormat.GRAY + "成功移除地圖" + levelName);
								}else p.sendMessage(TextFormat.RED + "地圖" + levelName + "不存在!");
							}
						}
					}else {
						ui.levelManageWindow(p, levelName);
					}
				}
			}
		}catch(NullPointerException err) {
			//just catch to avoid player pressing 'x'
		}
	}
	
	@EventHandler
	public void onTouch(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Item item = event.getItem();
		if(item.getLore().length < 2) return;
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && item.getId() == 345 && 
				item.getName().equals(TextFormat.ITALIC + (TextFormat.LIGHT_PURPLE + "世界傳送儀")) &&
				item.getLore()[0].equals(TextFormat.YELLOW + "這是前朝斯卡圖部落所流傳下來的遺物, ") &&
				item.getLore()[1].equals(TextFormat.YELLOW + "似乎能夠利用她前往各個世界呢")) {
			ui.showMapsWindow(player);
		}
	}
	
	public static String getNamebyTitle(Config worlds, String title) {
		Iterator<String> itr = worlds.getKeys().iterator();
		String key = "";
		while(itr.hasNext()) {
			key = itr.next();
			if(worlds.getString(key).equals(title)) return key;
		}
		return null;
	}
}
