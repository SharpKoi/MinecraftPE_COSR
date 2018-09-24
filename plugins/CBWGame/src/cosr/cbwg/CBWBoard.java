package cosr.cbwg;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace.Axis;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.math.Vector3;

public class CBWBoard {
	
	public static final String INFO_TITLE = TextFormat.AQUA + "[PianoTile]";
	
	private Player player;
	private String levelName;
	private Vector3 gameButton;
	private Vector3 topPoint;
	private Vector3 buttomPoint;
	private Random blackBlockGenerator;
	private int times = 0;
	private long startTime = 0;
	private float timeVia = 0;
	
	private static final int MAX_TIMES = CBWMain.getBWData().getInt("times", 20);
	
	public CBWBoard() {
		setGameButton(new Vector3());
		setTopPoint(new Vector3());
		setButtomPoint(new Vector3());
		blackBlockGenerator = new Random();
	}
	
	public CBWBoard(String level, Vector3 gameButton, Vector3 topPoint, Vector3 buttomPoint) {
		this.setLevel(level);
		this.setGameButton(gameButton);
		this.setTopPoint(topPoint);
		this.setButtomPoint(buttomPoint);
		blackBlockGenerator = new Random();
	}

	public Vector3 getGameButton() {
		return gameButton;
	}

	public void setGameButton(Vector3 gameButton) {
		this.gameButton = gameButton;
	}

	public Vector3 getTopPoint() {
		return topPoint;
	}

	public void setTopPoint(Vector3 topPoint) {
		this.topPoint = topPoint;
	}

	public Vector3 getButtomPoint() {
		return buttomPoint;
	}

	public void setButtomPoint(Vector3 buttomPoint) {
		this.buttomPoint = buttomPoint;
	}
	
	public Level getLevel() {
		return CBWMain.getInstance().getServer().getLevelByName(levelName);
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevel(String levelName) {
		this.levelName = levelName;
	}
	
	public Axis getAxis() {
		return (topPoint.getX() == buttomPoint.getX()? Axis.Z : Axis.X);
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void setAllBlockTo(Block b) {
		if(topPoint.getX() == buttomPoint.getX()) {
			for(int j = buttomPoint.getFloorY(); j <= topPoint.getFloorY(); j++) {
				for(int i = buttomPoint.getFloorZ(); i <= topPoint.getFloorZ(); i++) {
					getLevel().setBlock(new Vector3(topPoint.getX(), j, i), b);
				}
			}
		}else {
			for(int j = buttomPoint.getFloorY(); j <= topPoint.getFloorY(); j++) {
				for(int i = buttomPoint.getFloorX(); i <= topPoint.getFloorX(); i++) {
					getLevel().setBlock(new Vector3(i, j, topPoint.getZ()), b);
				}
			}
		}
	}
	
	public void gameStart() {
		gameStartText();
		times = 0;
		startTime = System.currentTimeMillis();
		Level level = CBWMain.getInstance().getServer().getLevelByName(levelName);
		int ran = 0;
		
		setAllBlockTo(Block.get(35));
		
		if(topPoint.getX() == buttomPoint.getX()) {
			for(int j = buttomPoint.getFloorY(); j <= topPoint.getFloorY(); j++) {
				ran = blackBlockGenerator.nextInt(4);
				level.setBlock(new Vector3(topPoint.getFloorX(), j, buttomPoint.getFloorZ()+ran), Block.get(35, 15));
			}
		}else {
			for(int j = buttomPoint.getFloorY(); j <= topPoint.getFloorY(); j++) {
				ran = blackBlockGenerator.nextInt(4);
				level.setBlock(new Vector3(buttomPoint.getFloorZ()+ran, j, topPoint.getZ()), Block.get(35, 15));
			}
		}
	}
	
	public void run() {
		int ran = blackBlockGenerator.nextInt(4);
		Vector3 blackPoint = null;
		Vector3 checkPoint = null;
		
		if(times == 0) {
			startTime = System.currentTimeMillis();
		}
		if(times >= MAX_TIMES) {
			timeVia = (float) ((System.currentTimeMillis() - startTime)/1000.00);
			victory();
			return;
		}
		
		if(getTopPoint().getX() == getButtomPoint().getX()) {
			for(int j = getButtomPoint().getFloorY(); j < getTopPoint().getFloorY(); j++) {
				for(int i = getButtomPoint().getFloorZ(); i <= getTopPoint().getFloorZ(); i++) {
					checkPoint = new Vector3(getTopPoint().getX(), j, i);
					Block checkBlock = getLevel().getBlock(checkPoint.add(new Vector3(0, 1, 0)));
					if(checkBlock.getId() == 35 && checkBlock.getDamage() == 15) {
						getLevel().setBlock(checkBlock.getLocation(), Block.get(35));
						getLevel().setBlock(checkPoint, Block.get(35, 15));
					}else {
						if(getLevel().getBlock(checkPoint).getDamage() == 15)
							getLevel().setBlock(checkPoint, Block.get(35));
					}
				}
			}
			blackPoint = new Vector3(getTopPoint().getX(), getTopPoint().getY(), getTopPoint().getZ() - ran);
		}else {
			for(int j = getButtomPoint().getFloorY(); j < getTopPoint().getFloorY(); j++) {
				for(int i = getButtomPoint().getFloorX(); i <= getTopPoint().getFloorX(); i++) {
					checkPoint = new Vector3(i, j, getTopPoint().getZ());
					Block checkBlock = getLevel().getBlock(checkPoint.add(new Vector3(0, 1, 0)));
					if(checkBlock.getId() == 35 && checkBlock.getDamage() == 15) {
						getLevel().setBlock(checkBlock.getLocation(), Block.get(35));
						getLevel().setBlock(checkPoint, Block.get(35, 15));
					}else {
						if(getLevel().getBlock(checkPoint).getDamage() == 15)
							getLevel().setBlock(checkPoint, Block.get(35));
					}
				}
			}
			blackPoint = new Vector3(getTopPoint().getX() - ran, getTopPoint().getY(), getTopPoint().getZ());
		}
		getLevel().setBlock(blackPoint, Block.get(35, 15));
		times++;
	}
	
	public void victory() {
		setAllBlockTo(Block.get(35, 5));
		player.sendMessage(TextFormat.GREEN + "遊戲結束! 您總共花費了" + TextFormat.WHITE + timeVia + TextFormat.GREEN + "秒, 反應真的很好呢!");
		
		rank(player.getName(), timeVia, CBWMain.getPlayerRank());
		if(CBWMain.getPlayerRank().keySet().toArray()[0].equals(player.getName())) {
			Server.getInstance().broadcastMessage(TextFormat.ITALIC + (TextFormat.AQUA + 
					"玩家" + TextFormat.WHITE + player.getName() + TextFormat.AQUA + "在" + 
					TextFormat.YELLOW + "別踩白塊兒" +TextFormat.AQUA + "遊戲中贏得了" + TextFormat.BOLD + "第一名!!"));
		}
		for(CBWBoard board : CBWMain.getBoardList()) {
			board.writeRank();
		}
		CBWMain.getGamingPool().remove(player.getName());
		this.setPlayer(null);
		this.initText();
	}
	
	public void gameover() {
		setAllBlockTo(Block.get(35, 14));
		CBWMain.getGamingPool().remove(player.getName());
		this.setPlayer(null);
		this.initText();
	}
	
	@SuppressWarnings("serial")
	public ConfigSection dataSection() {
		return new ConfigSection() {
			private static final long serialVersionUID = 1L;
			{
				set("level", levelName);
				set("game-button", new ConfigSection() {{set("x", gameButton.x);set("y", gameButton.y);set("z", gameButton.z);}});
				set("buttom-point", new ConfigSection() {{set("x", buttomPoint.x);set("y", buttomPoint.y);set("z", buttomPoint.z);}});
				set("top-point", new ConfigSection() {{set("x", topPoint.x);set("y", topPoint.y);set("z", topPoint.z);}});
			}
		};
	}
	
	public void load(int index) {
		String indexStr = Integer.toString(index);
		Config conf = new Config(new File(CBWMain.getInstance().getDataFolder(), "bwBoards.yml"), Config.YAML);
		this.levelName = conf.getString(indexStr+".level");
		this.gameButton = new Vector3(conf.getDouble(indexStr+".game-button.x"), conf.getDouble(indexStr+".game-button.y"), conf.getDouble(indexStr+".game-button.z"));
		this.buttomPoint = new Vector3(conf.getDouble(indexStr+".buttom-point.x"), conf.getDouble(indexStr+".buttom-point.y"), conf.getDouble(indexStr+".buttom-point.z"));
		this.topPoint = new Vector3(conf.getDouble(indexStr+".top-point.x"), conf.getDouble(indexStr+".top-point.y"), conf.getDouble(indexStr+".top-point.z"));
	}
	
	private void initText() {
		BlockEntitySign sign = (BlockEntitySign) getLevel().getBlockEntity(gameButton);
		sign.setText("§f別§0踩§f白§0塊§f兒", TextFormat.GOLD + "點擊一下", TextFormat.GREEN + "開始遊戲");
	}
	
	private void gameStartText() {
		BlockEntitySign sign = (BlockEntitySign) getLevel().getBlockEntity(gameButton);
		sign.setText("§f別§0踩§f白§0塊§f兒", TextFormat.GOLD + player.getName());
	}
	
	private void writeRank() {
		List<Map.Entry<String, Float>> rankList = new ArrayList<Map.Entry<String, Float>>(CBWMain.getPlayerRank().entrySet());
		int rank = 1;
		for(int i = 5; i >= 1; i--) {
			Vector3 rankPos = gameButton.add(new Vector3(0, i, 0));
			BlockEntitySign sign = (BlockEntitySign)getLevel().getBlockEntity(rankPos);
			sign.setText(TextFormat.BOLD + (TextFormat.GOLD + ("Top"+rank)), 
					(rankList.size() >= rank)? TextFormat.AQUA + rankList.get(rank-1).getKey():"", 
					(rankList.size() >= rank)?TextFormat.GREEN + Float.toString(rankList.get(rank-1).getValue()):"");
			rank++;
		}
	}
	
	private void rank(String playerName, float timeVia, Map<String, Float> map) {
		if(map.containsKey(playerName)) {
			if(map.get(playerName) < timeVia) {
				return;
			}
		}
		map.put(playerName, timeVia);
		Comparator<Map.Entry<String, Float>> valueComparator = new Comparator<Map.Entry<String, Float>>() {
			@Override
			public int compare(Entry<String, Float> o1,
	                Entry<String, Float> o2) {
				if(o1.getValue() > o2.getValue()) {
					return 1;
				}else if(o1.getValue() == o2.getValue()) {
					return 0;
				}else 
					return -1;
			}
		};
		List<Map.Entry<String, Float>> list = new ArrayList<Map.Entry<String, Float>>(map.entrySet());
		Collections.sort(list, valueComparator);
		map.clear();
		for(Map.Entry<String, Float> entry : list) {
			map.put(entry.getKey(), entry.getValue());
		}
	}
}
