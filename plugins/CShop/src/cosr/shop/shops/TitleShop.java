package cosr.shop.shops;

import java.io.File;
import java.io.FileNotFoundException;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import cosr.economy.CEconomy;
import cosr.economy.CMoney;
import cosr.economy.CPoint;
import cosr.roleplay.CRolePlay;
import cosr.roleplay.gcollection.Title;
import cosr.shop.CShopMain;
import cosr.shop.MoneyCostable;
import cosr.shop.Sellable;

public class TitleShop extends CShop implements MoneyCostable, Sellable {

	public enum CostType {
		MONEY, 
		POINT;
	}
	
	public static final String CONFIGPATH = "TitleShop" + File.separator;
	
	private Title title;
	private float cost;
	private CostType costType;
	
	public TitleShop() {
		this(OwnerType.NONE, "Unknown", new Title(), CostType.MONEY, 0);
	}
	
	public TitleShop(OwnerType type, String name, Title title, CostType ctype, double cost) {
		this(type, name, title, ctype, cost, null, null, null, true);
	}
	
	public TitleShop(OwnerType type, String name, Title title, CostType ctype, double cost, Position signPos, Position itemPos) {
		this(type, name, title, ctype, cost, 
				signPos.getLevel().getFolderName(), 
				new Vector3(signPos.getX(), signPos.getY(), signPos.getZ()), new Vector3(itemPos.getX(), itemPos.getY(), itemPos.getZ()), 
				true);
	}
	
	public TitleShop(OwnerType type, String name, String titleHead, CostType ctype, double cost, 
			String levelName, Vector3 signPos, Vector3 itemPos) {
		this(type, name, Title.get(titleHead), ctype, cost, levelName, signPos, itemPos, true);
	}
	
	public TitleShop(OwnerType type, String name, Title title, CostType ctype, double cost, 
						String levelName, Vector3 signPos, Vector3 itemPos, boolean isItemShown) {
		this.type = type;
		this.name = name;
		this.ownerName = OwnerType.SERVER.getName();
		this.title = title;
		this.costType = ctype;
		this.cost = (float)cost;
		this.levelName = levelName;
		this.signPos = signPos;
		this.itemPos = itemPos;
		this.isItemShown = isItemShown;
	}
	
	public Title getTitle() {
		return title;
	}

	public void setTitle(Title title) {
		this.title = title;
	}
	
	public void setTitle(String head) {
		this.title = Title.get(head);
	}

	public CostType getCostType() {
		return costType;
	}

	public void setCostType(CostType costType) {
		this.costType = costType;
	}

	@Override
	public float getCost() {
		return cost;
	}

	@Override
	public void setCost(float cost) {
		this.cost = cost;
	}
	
	public ConfigSection dataSection() {
		
		ConfigSection dataSection = new ConfigSection();
		
		dataSection.set("Type", this.getClass().getSimpleName());
		dataSection.set("Name", name);
		dataSection.set("Title", title.getHead());
		dataSection.set("CostType", costType.name());
		dataSection.set("Cost", cost);
		dataSection.set("World", levelName);
		dataSection.set("X", signPos.getX());
		dataSection.set("Y", signPos.getY());
		dataSection.set("Z", signPos.getZ());
		
		dataSection.set("Showpiece", new ConfigSection() {
			private static final long serialVersionUID = 1L;
			{
				set("IsItemShown", isItemShown);
				set("X", itemPos.getX());
				set("Y", itemPos.getY());
				set("Z", itemPos.getZ());
			}
		});
		
		return dataSection;
	}
	
	public TitleShop loadFor(String ownerName, int number) {
		Config conf;
		if(this.getType().equals(OwnerType.SERVER)) {
			conf = new Config(new File(CShopMain.getInstance().getDataFolder(), CONFIGPATH+("ServerShop.yml")), Config.YAML);
		}else if(this.getType().equals(OwnerType.PLAYER)) {
			conf = new Config(new File(CShopMain.getInstance().getDataFolder(), CONFIGPATH+(ownerName+".yml")), Config.YAML);
		}else {
			conf = ownerName.replace("Shop", "").equalsIgnoreCase("Server")? 
				new Config(new File(CShopMain.getInstance().getDataFolder(), CONFIGPATH+("ServerShop.yml")), Config.YAML) : 
				new Config(new File(CShopMain.getInstance().getDataFolder(), CONFIGPATH+(ownerName+".yml")), Config.YAML);
		}
		String numStr = Integer.toString(number);
		this.name = conf.getString(numStr+".Name");
		this.title = Title.get(conf.getString(numStr+".Title").toUpperCase());
		this.costType = CostType.valueOf(conf.getString(numStr+".CostType"));
		this.cost = (float) conf.getDouble(numStr+".Cost");
		this.levelName = conf.getString(numStr+".World");
		this.signPos = new Vector3(conf.getDouble(numStr+".X"), conf.getDouble(numStr+".Y"), conf.getDouble(numStr+".Z"));
		this.itemPos = new Vector3(conf.getDouble(numStr+".Showpiece.X"), conf.getDouble(numStr+".Showpiece.Y"), conf.getDouble(numStr+".Showpiece.Z"));
		this.isItemShown = conf.getBoolean(numStr+".Showpiece.IsItemShown");
		
		return this;
	}
	
	@Override
	public void sellTo(Player buyer) {
		if(buyer == null) return;
		
		try {
			if(CRolePlay.getOnlinePDB().get(buyer.getName()).getPlayerTitleMap().containsKey(title.getHead())) {
				buyer.sendMessage(CShop.infoTitle() + TextFormat.RED + "您已擁有該稱號, 無法再次購買");
				return;
			}
			float currency;
			if(costType.equals(CostType.MONEY)) {
				currency = CMoney.getMoney(buyer.getName());
				if(currency >= cost) {
					CMoney.takeMoney(buyer.getName(), cost);
				}else {
					buyer.sendMessage(infoTitle() + TextFormat.RED + "抱歉! 您未有足夠的金錢, 無法購買此商品!");
					return;
				}
			}else if(costType.equals(CostType.POINT)){
				currency = CPoint.getPoint(buyer.getName());
				if(currency >= cost) {
					CPoint.takePoint(buyer.getName(), cost);
				}else {
					buyer.sendMessage(infoTitle() + TextFormat.RED + "抱歉! 您未有足夠的" + TextFormat.LIGHT_PURPLE + CPoint.name() + 
							TextFormat.RED + ", 無法購買此商品!");
					return;
				}
			}
			title.grantTo(buyer.getName());
			buyer.sendMessage(infoTitle() + TextFormat.GREEN + "購買成功!");
		}catch(FileNotFoundException e) {
			
		}
	}

	@Override
	public void keyOntoSign(BlockEntitySign sign) {
		if(sign == null) return;
		String line2 = TextFormat.BOLD + ((TextFormat.GREEN + "擁有者: ") + (TextFormat.WHITE + OwnerType.SERVER.getName()));
		String line3 = TextFormat.BOLD + (TextFormat.DARK_GREEN + "售" + TextFormat.WHITE + ">") + 
				TextFormat.RESET + (TextFormat.ITALIC + (title.getRarity().getColor() + title.getName()));
		String line4 = TextFormat.BOLD + (TextFormat.GOLD + "$" + cost);
		sign.setText(this.name, line2, line3, line4);
	}

	@Override
	public String Information(String viewer) {
		String shopInfo = "";
		
		CEconomy plugin = CEconomy.getInstance();
		String shopNameLine = TextFormat.RESET + (TextFormat.GREEN + "商店名稱: ") + (TextFormat.RESET + this.getName()) + "\n";
		String ownerNameLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "擁有者: " + (TextFormat.RESET + this.ownerName)) + "\n";
		String tradeTypeLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "交易類型: ") + 
				TextFormat.RESET + "稱號\n";
		
		String itemNameLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "稱號: ") + 
				TextFormat.RESET + title.getHead() + "\n";
		
		String itemIDLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "稱號名稱: ") + 
				TextFormat.RESET + title.getName() + "\n";
		
		String costLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "成交價格: " + 
				TextFormat.RESET + this.cost + "\n");
		
		String positionLine = TextFormat.RESET + (TextFormat.DARK_GREEN + "位於: ") + 
				TextFormat.RESET + ((plugin.getServer().isOp(viewer))? this.getLevelName() : cosr.multiworld.Main.getWorldsConfig().get(this.getLevelName())) + 
				("("+this.getX()+", "+this.getY()+", "+this.getZ()+")") + "\n";
		
		shopInfo += shopNameLine + ownerNameLine + tradeTypeLine + itemNameLine + itemIDLine + costLine + positionLine;
		
		return shopInfo;
	}

	@Override
	public boolean isCompleted() {
		return true;
	}
}
