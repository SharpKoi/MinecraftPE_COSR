package Gui.java;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.*;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.plugin.PluginBase;

public final class Main extends PluginBase implements Listener{
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this,this);
		this.getLogger().info("GUI Start");
	}
	@EventHandler
	public void onJoin(PlayerDropItemEvent e) {
		/*
			FormWindowSimple window = new FormWindowSimple("title", "message");
			window.addButton(new ElementButton("µn³°"));
			
			e.getPlayer().showFormWindow(window);
			*/
			FormWindowCustom win = new FormWindowCustom("COSRµn¿ý¨t²Î");
			
		    win.addElement(new ElementInput("UserName", e.getPlayer().getName()));
		    win.addElement(new ElementInput("PassWord", "PassWord"));
			win.addElement(new ElementLabel(("Login")));
			e.getPlayer().showFormWindow(win);
			
			e.getPlayer().sendMessage("test");
		
		
	}

}
