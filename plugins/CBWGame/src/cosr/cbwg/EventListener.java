package cosr.cbwg;

import cmen.essalg.CJEF;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSignPost;
import cn.nukkit.block.BlockWallSign;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Axis;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.math.Vector3;

public class EventListener implements Listener {
	
	@EventHandler
	public void onTouch(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		
		Block b = event.getBlock();
		Level l = b.getLevel();
		
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(CBWMain.getUnsettedPool().containsKey(p.getName())) {
				p.sendMessage(TextFormat.GRAY + "別踩白塊兒正在創建中...");
				
				CBWBoard unsettedBoard = CBWMain.getUnsettedPool().get(p.getName());
				unsettedBoard.setLevel(l.getFolderName());
				
				Vector3 startPoint = new Vector3(b.x, b.y+1, b.z);
				double yaw = p.getYaw();
				Axis playerAxis = CJEF.getFaceDirection(yaw).getAxis();
				BlockFace face = CJEF.getFaceDirection(yaw);
				int starti = 0;
				
				if(face.getAxis().equals(Axis.X)) {
					starti = startPoint.getFloorZ();
					
				}else if(face.getAxis().equals(Axis.Z)) {
					starti = startPoint.getFloorX();
				}
				
				Vector3 forSet = null;
				for(int i = starti; i <= starti + 4; i++) {
					for(int j = startPoint.getFloorY(); j <= startPoint.getFloorY() + 5; j++) {
						//設定遊玩介面的底點與頂點
						if(playerAxis.equals(Axis.X)) {
							forSet = new Vector3(startPoint.x, j, i);
							if(forSet.equals(startPoint.add(new Vector3(0, 1, 1)))) {
								unsettedBoard.setButtomPoint(forSet);
							}
							if(forSet.equals(startPoint.add(new Vector3(0, 5, 4)))) {
								unsettedBoard.setTopPoint(forSet);
							}
						}
						else if(playerAxis.equals(Axis.Z)) {
							forSet = new Vector3(i, j, startPoint.z);
							if(forSet.equals(startPoint.add(new Vector3(1, 1, 0)))) {
								unsettedBoard.setButtomPoint(forSet);
							}
							if(forSet.equals(startPoint.add(new Vector3(4, 5, 0)))) {
								unsettedBoard.setTopPoint(forSet);
							}
						}
						
						//設定遊戲介面的方塊
						if(i == starti) {
							String[] itemForm = CBWMain.getBWData().getString("right-side", "20").split(":");
							int blockId = Integer.parseInt(itemForm[0]);
							int blockMeta = itemForm.length == 2? Integer.parseInt(itemForm[1]) : 0;
							l.setBlock(forSet, Block.get(blockId, blockMeta));
						}
						else {
							if(j == startPoint.getFloorY()) {
								String[] itemForm = CBWMain.getBWData().getString("buttom", "24").split(":");
								int blockId = Integer.parseInt(itemForm[0]);
								int blockMeta = itemForm.length == 2? Integer.parseInt(itemForm[1]) : 0;
								l.setBlock(forSet, Block.get(blockId, blockMeta));
							}else {
								l.setBlock(forSet, Block.get(35));
							}
						}
					}
				}
				//設定遊戲介面的告示牌
				forSet = startPoint.add(face.getOpposite().getUnitVector());
				int startY = forSet.getFloorY();
				unsettedBoard.setGameButton(new Vector3(forSet.x, forSet.y, forSet.z));
				for(int j = startY; j <= startY + 5; j++) {
					forSet.y = j;
					placeSign(l, forSet, new BlockWallSign(face.getOpposite().getIndex()));
					if(j == startY) writeText((BlockEntitySign) l.getBlockEntity(forSet));
				}
				CBWMain.getBoardList().add(unsettedBoard);
				CBWMain.getUnsettedPool().remove(p.getName());
			}
			else {
				if(b.getId() == 68) {
					BlockEntitySign signEntity = (BlockEntitySign)l.getBlockEntity(b);
					CBWBoard board = CBWMain.getBoardByGameButton(signEntity);
					if(board != null) {
						board.setPlayer(p);
						CBWMain.getGamingPool().put(p.getName(), board);
						p.sendMessage(TextFormat.GREEN + "遊戲開始!!");
						board.gameStart();
					}
				}
				else {
					if(b.getId() == 35) {
						if(CBWMain.getGamingPool().containsKey(p.getName())) {
							CBWBoard gameBoard = CBWMain.getGamingPool().get(p.getName());
							if(gameBoard.getAxis().equals(Axis.X)) {
								if(b.getFloorZ() == gameBoard.getButtomPoint().getFloorZ() &&
										b.getFloorX() >= gameBoard.getButtomPoint().getFloorX() && b.getFloorX() <= gameBoard.getTopPoint().getFloorX() && 
										b.getFloorY() == gameBoard.getButtomPoint().getFloorY()) {
									if(b.getDamage() == 15) {
										gameBoard.run();
									}else if(b.getDamage() == 0) {
										gameBoard.gameover();
									}
								}
							}
							else if(gameBoard.getAxis().equals(Axis.Z)) {
								if(b.getFloorX() == gameBoard.getButtomPoint().getFloorX() &&
										b.getFloorZ() >= gameBoard.getButtomPoint().getFloorZ() && b.getFloorZ() <= gameBoard.getTopPoint().getFloorZ() && 
										b.getFloorY() == gameBoard.getButtomPoint().getFloorY()) {
									if(b.getDamage() == 15) {
										gameBoard.run();
									}else if(b.getDamage() == 0) {
										gameBoard.gameover();
									}
								}
							}
						}//該玩家沒有在玩小遊戲
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		Block b = event.getBlock();
		for(CBWBoard board : CBWMain.getBoardList()) {
			Vector3 fullButtomPoint = null;
			if(board.getAxis().equals(Axis.X)) {
				fullButtomPoint = board.getButtomPoint().add(new Vector3(-1, -1, 0));
			}else {
				fullButtomPoint = board.getButtomPoint().add(new Vector3(0, -1, -1));
			}
			if(b.getId() == 68) {
				if(b.getFloorX() == board.getGameButton().getFloorX() && b.getFloorZ() == board.getGameButton().getFloorZ() &&
						b.getFloorY() <= board.getGameButton().getFloorY()+5 && b.getFloorY() >= board.getGameButton().getFloorY()) {
					if(p.isOp()) {
						CBWMain.getBoardList().remove(board);
						p.sendMessage(TextFormat.GRAY + "別踩白塊兒遊戲介面已被刪除!");
						return;
					}
					event.setCancelled();
				}
			}
			if(b.getFloorX() >= fullButtomPoint.getFloorX() && b.getFloorX() <= board.getTopPoint().getFloorX() && 
					b.getFloorY() >= fullButtomPoint.getFloorY() && b.getFloorY() <= board.getTopPoint().getFloorY() && 
					b.getFloorZ() >= fullButtomPoint.getFloorZ() && b.getFloorZ() <= board.getTopPoint().getFloorZ()) {
				if(p.isOp()) {
					CBWMain.getBoardList().remove(board);
					p.sendMessage(TextFormat.GRAY + "別踩白塊兒遊戲介面已被刪除!");
					return;
				}
				event.setCancelled();
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if(CBWMain.getGamingPool().containsKey(p.getName())) {
			CBWMain.getGamingPool().get(p.getName()).gameover();
		}
	}
	
	private void placeSign(Level level, Vector3 pos, BlockSignPost sign) {
		//直接用setBlock設置告示牌會被判斷為非實體，因此必須利用自訂義方法告訴伺服器此木牌為實體
		CompoundTag nbt = new CompoundTag()
				.putString("id", BlockEntity.SIGN)
                .putInt("x", (int) pos.x)
                .putInt("y", (int) pos.y)
                .putInt("z", (int) pos.z)
                .putString("Text1", "")
                .putString("Text2", "")
                .putString("Text3", "")
                .putString("Text4", "");
		
		level.setBlock(pos, sign, true);
		//x座標*16 且 z座標*16
		new BlockEntitySign(level.getChunk((int) pos.x >> 4, (int) pos.z >> 4), nbt);
	}
	
	private void writeText(BlockEntitySign sign) {
		sign.setText("§f別§0踩§f白§0塊§f兒", TextFormat.GOLD + "點擊一下", TextFormat.GREEN + "開始遊戲");
	}
}
