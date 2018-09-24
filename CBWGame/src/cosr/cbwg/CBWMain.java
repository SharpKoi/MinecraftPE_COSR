package cosr.cbwg;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;

public class CBWMain extends PluginBase {
	
	private static CBWMain main;
	
	private static Config rankConf;			//玩家排名
	private static Config bwConfig;			//Bottom, Right-Side, times, miss-times, ticks
	
	private static Map<String, Float> playerRank = new LinkedHashMap<String, Float>();
	private static List<CBWBoard> boardList = new ArrayList<CBWBoard>();
	private static Map<String, CBWBoard> gamingPool = new HashMap<String, CBWBoard>();
	private static Map<String, CBWBoard> unsetted = new HashMap<String, CBWBoard>();
	
	public void onEnable() {
		main = this;
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		
		rankConf = new Config(new File(this.getDataFolder(), "playerRank.yml"), Config.YAML);
		bwConfig = new Config(new File(this.getDataFolder(), "bwConfig.yml"), Config.YAML);
		Config conf = new Config(new File(CBWMain.getInstance().getDataFolder(), "bwBoards.yml"), Config.YAML);
		for(String indexStr : conf.getKeys(false)) {
			CBWBoard board = new CBWBoard();
			board.load(Integer.parseInt(indexStr));
			boardList.add(board);
		}
		for(String pn : rankConf.getKeys(false)) {
			playerRank.put(pn, (float) rankConf.getDouble(pn));
		}
	}
	
	@Override
	public void onDisable() {
		ConfigSection allBoard = new ConfigSection();
		if(boardList.size() > 0) {
			for(int i = 0; i < boardList.size(); i++) {
				allBoard.set(Integer.toString(i), boardList.get(i).dataSection());
			}
		}
		Config conf = new Config(new File(CBWMain.getInstance().getDataFolder(), "bwBoards.yml"), Config.YAML);
		conf.setAll(allBoard);
		conf.save();
		
		ConfigSection rankData = new ConfigSection();
		for(String key : playerRank.keySet()) {
			rankData.set(key, playerRank.get(key));
		}
		rankConf.setAll(rankData);
		rankConf.save();
	}
	
	public static CBWMain getInstance() {
		return main;
	}
	
	public static Config getBWData() {
		return bwConfig;
	}
	
	public static Map<String, CBWBoard> getUnsettedPool() {
		return unsetted;
	}
	
	public static List<CBWBoard> getBoardList() {
		return boardList;
	}
	
	public static Config getRankConf() {
		return rankConf;
	}

	public static Map<String, Float> getPlayerRank() {
		return playerRank;
	}

	public static Map<String, CBWBoard> getGamingPool() {
		return gamingPool;
	}

	public static CBWBoard getBoardByGameButton(BlockEntitySign sign) {
		String[] lines = sign.getText();
		if(lines.length < 3 || lines[0] == null || lines[1] == null || lines[2] == null) return null;
		if(lines[0].equals("§f別§0踩§f白§0塊§f兒") && 
				lines[1].equals(TextFormat.GOLD + "點擊一下") && lines[2].equals(TextFormat.GREEN + "開始遊戲")) {
			for(CBWBoard cbwb : boardList) {
				if(cbwb.getLevelName().equals(sign.getLevel().getFolderName()) &&
						cbwb.getGameButton().getFloorX() == sign.getFloorX() && 
						cbwb.getGameButton().getFloorY() == sign.getFloorY() && 
						cbwb.getGameButton().getFloorZ() == sign.getFloorZ()) {
					return cbwb;
				}
			}
		}
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = null;
		if(sender.isPlayer()) player = (Player) sender;
		
		if(command.getName().equalsIgnoreCase("cbw")) {
			if(args[0].equalsIgnoreCase("build") || args[0].equalsIgnoreCase("b")) {
				if(!sender.isPlayer()) {
					sender.sendMessage(TextFormat.RED + "請在遊戲中執行此指令");
				}
				else {
					if(!sender.isOp()) {
						sender.sendMessage(TextFormat.RED + "不明指令/cbw build, 請檢查該指令是否存在\n或您未有權限執行此指令");
					}else {
						unsetted.put(sender.getName(), new CBWBoard());
						sender.sendMessage(TextFormat.GOLD + "請點擊您所想要設置的位置");
					}
				}
			}
			else if(args[0].equalsIgnoreCase("rank") || args[0].equalsIgnoreCase("r")) {
				String list = "";
				for(String pn : playerRank.keySet()) {
					list += "-" + TextFormat.AQUA + pn + TextFormat.WHITE + ": " + TextFormat.GREEN + playerRank.get(pn) + "\n";
				}
				sender.sendMessage("§b[§f別§0踩§f白§0塊§f兒§b]" + TextFormat.GOLD + "玩家排行榜: \n" + list);
			}
			else if(args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("c")) {
				
			}
			
			return true;
		}
		else if(command.getName().equalsIgnoreCase("loc")) {
			player.sendMessage("您當前的座標為: (" + player.getX()+", "+player.getY()+", "+player.getZ() + ")");
			player.sendMessage("您當前面向" + player.getYaw() + "度角");
			
			if(Math.tan(toRadians(player.getYaw())) <= 1 && Math.tan(toRadians(player.getYaw())) >= -1) {
				player.sendMessage("面向z軸");
			}
			else {
				player.sendMessage("面向x軸");
			}
			
			return true;
		}
		return false;
	}
	
	private double toRadians(double angle) {
		return (angle * Math.PI/180);
	}
}
