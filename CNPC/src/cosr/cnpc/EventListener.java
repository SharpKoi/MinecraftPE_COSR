package cosr.cnpc;

import java.util.Arrays;

import cmen.essalg.CJEF;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityMotionEvent;
import cn.nukkit.event.entity.EntitySpawnEvent;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.utils.TextFormat;
import cosr.cnpc.entities.CNPC_Entity;
import cosr.cnpc.entities.NPC_Human;
import cosr.cnpc.type.*;

public class EventListener implements Listener {
	
	@EventHandler
	public void onNPCSpawn(EntitySpawnEvent event) {
		Entity npc = event.getEntity();
		
		if(npc instanceof CNPC_Entity || npc instanceof NPC_Human) {
			if(!npc.namedTag.getString("NameTag").equals("")) {
				npc.setNameTag(npc.namedTag.getString("NameTag"));
				npc.setNameTagAlwaysVisible();
			}
		}
	}
	
	@EventHandler
	public void onEntityMoving(EntityMotionEvent event) {
		Entity e = event.getEntity();
		if ((e instanceof CNPC_Entity) || (e instanceof NPC_Human) && e.namedTag.getBoolean("Npc")) {
			if(event instanceof Cancellable)
				event.setCancelled();
			else 
				e.setMotion(new Vector3());
		}
	}
	
	@EventHandler
	public void onTouchNPC(EntityDamageByEntityEvent event) {
		Player p = null;
		Entity e = event.getEntity();
		Entity damager = event.getDamager();
		if(damager instanceof Player) {
			p = (Player) damager;
			p.sendMessage(e.getClass().getSimpleName() + ": " + e.namedTag.getBoolean("Npc"));
		}
		if((e instanceof CNPC_Entity || e instanceof NPC_Human) && e.namedTag.getBoolean("Npc")) {
			event.setCancelled();
			if(damager instanceof Player) {
				Player trigger = (Player)damager;
				ListTag<StringTag> typeList = e.namedTag.getList("NPCType", StringTag.class);
				if(CNPCMain.getKillerPool().contains(trigger.getName())) {
					trigger.sendMessage("§a成功將§bNPC" + e.getName() + "§a移除");
					e.close();
                    return;
				}
				if(typeList.get(0).data.equals("CommandNPC")) {
					String senderName = typeList.get(1).data.replaceAll("%p", trigger.getName());
					String command = typeList.get(2).data;
					if(command != "")
					new CommandNPC((senderName.equals(Server.getInstance().getConsoleSender().getName())? new ConsoleCommandSender() : Server.getInstance().getPlayer(senderName)), command).execute(trigger);
				}
				else if(typeList.get(0).data.equals("MessageNPC")) {
					String message = typeList.get(1).data.replaceAll("%p", trigger.getName()).replaceAll("%n", "\n");
					if(message != "")
					new MessageNPC(message, e.getNameTag()).execute(trigger);
				}
				else if(typeList.get(0).data.equals("TeleportNPC")) {
					String toName = typeList.get(1).data;
					String xStr = typeList.get(2).data;
					String yStr = typeList.get(3).data;
					String zStr = typeList.get(4).data;
					if(toName.equals("")) {
						if(!xStr.equals("") && !yStr.equals("") && !zStr.equals("")) {
							new TeleportNPC(trigger.getLevel(), new Vector3(Double.parseDouble(xStr), Double.parseDouble(yStr), Double.parseDouble(zStr)));
						}
					}else {
						Level to = Server.getInstance().getLevelByName(toName);
						if(!xStr.equals("") && !yStr.equals("") && !zStr.equals("")) {
							if(to != null)
								new TeleportNPC(to, new Vector3(Double.parseDouble(xStr), Double.parseDouble(yStr), Double.parseDouble(zStr))).execute(trigger);
							else {
								trigger.sendMessage(e.getName() + TextFormat.GRAY + "找不到名為" + TextFormat.WHITE + toName + TextFormat.GRAY + "的地圖, 無法進行傳送");
								return;
							}
						}else {
							if(to != null)
								new TeleportNPC(to, to.getSpawnLocation()).execute(trigger);
							else {
								trigger.sendMessage(e.getName() + TextFormat.GRAY + "找不到名為" + TextFormat.WHITE + toName + TextFormat.GRAY + "的地圖, 無法進行傳送");
								return;
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onChat(PlayerChatEvent event) {
		Player p = event.getPlayer();
		String msg = event.getMessage();
		if(msg.startsWith("@")) {
			String[] args = msg.split(" ");
			//Cancel
			if(args[0].equalsIgnoreCase("@cancel")) {
				if(CNPCMain.getNpcEditRequestions().containsKey(p.getName())) {
					event.setCancelled();
					CNPCMain.getNpcEditRequestions().remove(p.getName());
					p.sendMessage(TextFormat.ITALIC + (TextFormat.GRAY + "已退出NPC編輯模式"));
				}
				if(CNPCMain.getKillerPool().contains(p.getName())) {
					event.setCancelled();
					CNPCMain.getKillerPool().remove(p.getName());
					p.sendMessage(TextFormat.ITALIC + (TextFormat.GRAY + "已退出NPC移除模式"));
				}
			}
			//Edit NPC
			if(CNPCMain.getNpcEditRequestions().containsKey(p.getName())) {
				Entity npc = CNPCMain.getNpcEditRequestions().get(p.getName());
				if((npc instanceof CNPC_Entity || npc instanceof NPC_Human) && npc.namedTag.getBoolean("Npc")) {
					event.setCancelled();
					ListTag<StringTag> typeList = npc.namedTag.getList("NPCType", StringTag.class);
					if(args[0].equalsIgnoreCase("@n")) {
						String name = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).replaceAll("%n", "\n");
						((StringTag)npc.namedTag.get("NameTag")).data = name;
						npc.setNameTag(name);
						npc.setNameTagVisible();
						npc.setNameTagAlwaysVisible();
						p.sendMessage(TextFormat.GREEN + "成功設定" + TextFormat.AQUA + npc.getName() + TextFormat.GREEN + "的" + 
									TextFormat.AQUA + "名字: " + TextFormat.WHITE + name);
					}
					else if(args[0].equalsIgnoreCase("@sc")) {
						if(!CJEF.isDigit(args[1])) {
							p.sendMessage(TextFormat.RED + "請輸入正確的數字(@sc <lv>)");
							return;
						}
						float scale = (float) Double.parseDouble(args[1]);
						npc.setScale(scale);
						((FloatTag)npc.namedTag.get("Scale")).data = scale;
						p.sendMessage(TextFormat.GREEN + "成功設定" + TextFormat.AQUA + npc.getName() + TextFormat.GREEN + "的" + 
								TextFormat.AQUA + "尺寸: " + TextFormat.WHITE + scale);
					}
					else if(typeList.get(0).data.equals("CommandNPC")) {
						if(args[0].equalsIgnoreCase("@c")) {
							String command = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
							typeList.get(2).data = command;
							p.sendMessage(TextFormat.GREEN + "成功設定" + TextFormat.AQUA + npc.getName() + TextFormat.GREEN + "的" + 
									TextFormat.AQUA + "指令: " + TextFormat.WHITE + command);
						}
						else if(args[0].equalsIgnoreCase("@s")) {
							String senderName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
							typeList.get(1).data = senderName;
							p.sendMessage(TextFormat.GREEN + "成功設定" + TextFormat.AQUA + npc.getName() + TextFormat.GREEN + "的" + 
									TextFormat.AQUA + "指令傳送者: " + TextFormat.WHITE + senderName);
						}
						else {
							p.sendMessage(TextFormat.RED + "請輸入正確的標籤名(@n, @c, @s)");
							return;
						}
					}
					else if(typeList.get(0).data.equals("MessageNPC")) {
						if(args[0].equalsIgnoreCase("@m")) {
							String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
							message = message.replaceAll("%n", "\n");
							typeList.get(1).data = message;
							p.sendMessage(TextFormat.GREEN + "成功設定" + TextFormat.AQUA + npc.getName() + TextFormat.GREEN + "的" + 
									TextFormat.AQUA + "對話訊息: " + TextFormat.WHITE + message);
						}
						else {
							p.sendMessage(TextFormat.RED + "請輸入正確的標籤名(@n, @m)");
							return;
						}
					}
					else if(typeList.get(0).data.equals("TeleportNPC")) {
						if(args[0].equalsIgnoreCase("@w")) {
							String toName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
							typeList.get(1).data = toName;
							p.sendMessage(TextFormat.GREEN + "成功設定" + TextFormat.AQUA + npc.getName() + TextFormat.GREEN + "的" + 
									TextFormat.AQUA + "傳送地圖: " + TextFormat.WHITE + toName);
						}
						else if(args[0].equalsIgnoreCase("@p")) {
							if(!CJEF.isDigit(args[1]) || !CJEF.isDigit(args[2]) || !CJEF.isDigit(args[3])) {
								p.sendMessage(TextFormat.RED + "請輸入正確的座標數字(@p <x> <y> <z>)");
								return;
							}
							typeList.get(2).data = args[1];
							typeList.get(3).data = args[2];
							typeList.get(4).data = args[3];
							p.sendMessage(TextFormat.GREEN + "成功設定" + TextFormat.AQUA + npc.getName() + TextFormat.GREEN + "的" + 
									TextFormat.AQUA + "傳送座標: " + TextFormat.WHITE + "("+args[1]+", "+args[2]+", "+args[3]+")");
						}else {
							p.sendMessage(TextFormat.RED + "請輸入正確的標籤名(@n, @w, @p)");
							return;
						}
						p.sendMessage(TextFormat.ITALIC + (TextFormat.GRAY + "若要退出編輯請輸入@cancel"));
					}
					npc.saveNBT();
				}
			}
		}
	}
}
