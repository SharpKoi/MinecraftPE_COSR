package cosr.newbie.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import cosr.newbie.CNewbie;

public class NBChestCommand extends Command {

	public NBChestCommand() {
		super("nbchest", "command for newbie chest", "/nbchest", new String[] {"nbc", "nbbox", "nbb"});
		this.commandParameters.clear();
		this.commandParameters.put("set", new CommandParameter[0]);
		this.commandParameters.put("content", new CommandParameter[0]);
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(args.length < 1) return false;
		
		switch(args[0].toLowerCase()) {
		case "set":
			if(sender.isPlayer()) {
				Player p = (Player) sender;
				CNewbie.getNbChestSettingPool().add(p.getName());
				p.sendMessage(CNewbie.TITLE + TextFormat.YELLOW + "請點擊箱子以設置新手獎勵箱");
				p.sendMessage(CNewbie.TITLE + "若欲取消設定請於聊天室輸入@cancel");
			}
			break;
		case "content":
			if(sender.isPlayer()) {
				Player p = (Player) sender;
				CNewbie.getNbChestContentingPool().add(p.getName());
				p.sendMessage(CNewbie.TITLE + TextFormat.YELLOW + "請打開新手獎勵箱以設置獎勵內容物");
				p.sendMessage(CNewbie.TITLE + "若欲取消設定請於聊天室輸入@cancel");
			}
			break;
		default:
			return false;
		}
		return true;
	}

}
