package cosr.cmdsign;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import cosr.cmdsign.CommandSign.CmdExecutor;

public class Main extends PluginBase implements Listener {
	
	private static Main main;
	
	private Map<String, CommandSign> unsetted;
	private Set<CommandSign> csSet;
	
	public static Main getInstance() {
		return main;
	}

	public void onEnable() {
		main = this;
		this.getServer().getPluginManager().registerEvents(this, this);
		unsetted = new HashMap<String, CommandSign>();
		csSet = new HashSet<CommandSign>();
		File dir = this.getDataFolder();
		if(dir.isDirectory()) {
			for(File f : dir.listFiles()) {
				CommandSign cmdsign = CommandSign.loadFrom(f.getName());
				csSet.add(cmdsign);
			}
		}
		this.getLogger().info(TextFormat.AQUA + "指令告示牌v" + this.getDescription().getVersion());
	}
	
	public void onDisable() {
		for(CommandSign cs : csSet) {
			cs.saveConfig();
		}
		csSet.clear();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender.isPlayer()) {
			if(sender.isOp()) {
				Player p = (Player)sender;
				if(cmd.getName().equals("csign") || label.equals("csn") || label.equals("cs")) {
					String title = args[1].replaceAll("%_", " ").replaceAll("%n", "\n");
					String content = args[2].replaceAll("%_", " ").replaceAll("%n", "\n");
					CommandSign cmdSign = new CommandSign(title, content, Arrays.copyOfRange(args, 3, args.length), 
							(args[0].equals("1") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("CONSOLE"))? 
									CmdExecutor.CONSOLE : CmdExecutor.PLAYER);
					unsetted.put(p.getName(), cmdSign);
					p.sendMessage(TextFormat.GOLD + "Please touch the sign you want to set.");
					return true;
				}
			}
		}
		return false;
	}
	
	@EventHandler
	public void onTouch(PlayerInteractEvent event) {
		Block block = event.getBlock();
		Player player = event.getPlayer();
		
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && (block.getId() == 323 || block.getId() == 63 || block.getId() == 68)) {
			BlockEntitySign signEntity = (BlockEntitySign) block.getLevel().getBlockEntity(block.getLocation());
			
			CommandSign commandSign = this.getCommandSignData(signEntity);
			
			if(unsetted.containsKey(player.getName())) {
				if(commandSign != null) {
					player.sendMessage(TextFormat.RED + "[錯誤]此告示牌上已有指令紀錄");
					return;
				}
				CommandSign unsetSign = unsetted.get(player.getName());
				unsetSign.setLevelname(signEntity.getLevel().getFolderName());
				unsetSign.setCoordinate(new Vector3(signEntity.getX(), signEntity.getY(), signEntity.getZ()));
				unsetSign.keyOntoSign(signEntity);
				csSet.add(unsetSign);
				unsetted.remove(player.getName());
				player.sendMessage(TextFormat.GREEN + "指令告示牌設置成功!");
			}
			else if(commandSign != null) {
				commandSign.execute((commandSign.getExecutorType().equals("CONSOLE")? new ConsoleCommandSender() : player));
			}
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		
		if(block.getId() == 323 || block.getId() == 63 || block.getId() == 68) {
			BlockEntitySign signEntity = (BlockEntitySign) block.getLevel().getBlockEntity(block.getLocation());
			CommandSign commandSign = this.getCommandSignData(signEntity);
			if(commandSign != null) {
				if(event.getPlayer().isOp()) {
					csSet.remove(commandSign);
					new File(this.getDataFolder(), commandSign.getTitle()+".yml").delete();
					event.getPlayer().sendMessage(TextFormat.GRAY + "指令告示牌已被銷毀");
				}else {
					event.getPlayer().sendMessage(TextFormat.RED + "您沒有足夠的權限摧毀該指令告示牌!");
					event.setCancelled();
				}
			}
		}
	}
	
	private CommandSign getCommandSignData(BlockEntitySign sign) {
		String[] lines = sign.getText();
		if(lines.length == 0) return null;
		for(CommandSign cs : csSet) {
			if(lines[0].equals(TextFormat.YELLOW + "[CONSOLE]") || lines[0].equals(TextFormat.YELLOW + "[PLAYER]")) {
				if(cs.getLevelname().equals(sign.getLevel().getFolderName()) && cs.getCoordinate().equals((Vector3)sign.getLocation())) return cs;
			}
		}
		return null;
	}
}
