package cosr.roleplay;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import cosr.roleplay.database.PlayerDataBase;
import cosr.roleplay.gcollection.Title;

public class PlayerTitle {
	/*
	 * Titles:
	 * - BIGMAN:
	 *  Tag: 
	 *  BeenSeen:
	 * 
	 */
	public static final String PARENTKEY = "Titles";
	
	private Title title;
	private boolean isTag;
	private boolean hasBeenSeen;
	
	public PlayerTitle() {
		this(new Title(), false, false);
	}
	
	public PlayerTitle(Title title) {
		this(title, false, false);
	}
	
	public PlayerTitle(Title title, boolean hasBeenSeen) {
		this(title, false, hasBeenSeen);
	}
	
	public PlayerTitle(Title title, boolean isTag, boolean hasBeenSeen) {
		this.title = title;
		this.isTag = isTag;
		this.hasBeenSeen = hasBeenSeen;
	}
	
	public Title getTitle() {
		return title;
	}

	public void setTitle(Title title) {
		this.title = title;
	}

	public boolean isTag() {
		return isTag;
	}
	
	public void toTag() {
		this.isTag = true;
	}
	
	public void unTag() {
		this.isTag = false;
	}

	public boolean hasBeenSeen() {
		return hasBeenSeen;
	}

	public void setBeenSeen(boolean hasBeenSeen) {
		this.hasBeenSeen = hasBeenSeen;
	}
	
	public String nameTagForm() {
		return TextFormat.RESET + (TextFormat.BOLD + 
				(TextFormat.WHITE + "[" + this.getTitle().getRarity().getColor() + this.getTitle().getName() + TextFormat.WHITE + "]")) + 
				TextFormat.RESET;
	}

	public void setTag(Player player) {
		this.toTag();
		player.setDisplayName(this.nameTagForm() + " " + player.getName());
	}

	public static boolean pinTag(Player p, String head) {
		if(p == null) return false;
		if(CRolePlay.getInstance().getServer().getOnlinePlayers().containsValue(p) && CRolePlay.getOnlinePDB().containsKey(p.getName())) {
			PlayerDataBase pdb = CRolePlay.getOnlinePDB().get(p.getName());
			if(pdb.getPlayerTitleMap().containsKey(head)) {
				pdb.titleTagReset();
				pdb.getPlayerTitleMap().get(head).setTag(p);
				return true;
			}else
				return false;
		}
		
		return false;
	}
	
	public static boolean unPinTag(Player p) {
		if(p == null) return false;
		if(CRolePlay.getInstance().getServer().getOnlinePlayers().containsValue(p) && CRolePlay.getOnlinePDB().containsKey(p.getName())) {
			PlayerDataBase pdb = CRolePlay.getOnlinePDB().get(p.getName());
			for(PlayerTitle pt : pdb.getPlayerTitleMap().values()) {
				if(pt.isTag() == true) {
					pt.unTag();
					p.setDisplayName(p.getName());
					return true;
				}
			}
			return true;
		}
		return false;
	}
	
	public ConfigSection dataSection() {
		return new ConfigSection() {
			private static final long serialVersionUID = 1L;
			{
				set("IsTag", isTag);
				set("BeenSeen", hasBeenSeen);
			}
		};
	}
	
	public void loadData(Config config, String head) {
		this.title = new Title(head);
		this.isTag = config.getBoolean(PARENTKEY + "." + head + ".IsTag");
		this.hasBeenSeen = config.getBoolean(PARENTKEY + "." + head + ".BeenSeen");
	}
}