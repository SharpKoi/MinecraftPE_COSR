package cosr.shop.utils;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import cosr.shop.ItemSingle;
import cosr.shop.MoneyCostable;
import cosr.shop.shops.BarterShop;
import cosr.shop.shops.CShop;
import cosr.shop.shops.LotteryShop;
import cosr.shop.shops.TitleShop;

public class BuildTool {
	
	public int step = 5;
	private Player builder;
	private CShop shop;
	
	public BuildTool(Player builder) {
		this(builder, null);
	}
	
	public BuildTool(Player builder, CShop shop) {
		this.builder = builder;
		this.shop = shop;
	}
	
	public Player getBuilder() {
		return builder;
	}

	public void setBuilder(Player builder) {
		this.builder = builder;
	}

	public CShop getShop() {
		return shop;
	}

	public void setShop(CShop shop) {
		this.shop = shop;
	}
	
	public void prompt() {
		if(builder == null) return;
		switch(step) {
			case 5: 
				builder.sendMessage(TextFormat.ITALIC + (TextFormat.YELLOW + "請選擇該商店的種類\n" + TextFormat.WHITE
									+ "@buy      -購買型商店\n"
									+ "@sell     -販售型商店\n"
									+ "@lottery  -抽獎型商店\n"
									+ "@barter   -交換型商店\n"
									+ (builder.isOp()? "@point    -點券稀有商店\n" : "")
									+ (builder.isOp()? "@title    -稱號商店\n" : "")));
				//if(builder.isOp())step--;
				//else step -= 2;
				break;
			case 4: 
				builder.sendMessage(TextFormat.ITALIC + (TextFormat.YELLOW + "請設定該商店的擁有者\n" + TextFormat.WHITE
									+ "@p     -個人\n"
									+ "@s     -伺服器\n"
									+ "@back  -返回至上一個設定\n"));
				break;
			case 3:
				builder.sendMessage(TextFormat.ITALIC + (TextFormat.YELLOW + "請設定該商店的名字: "));
				break;
			case 2: 
				String title = TextFormat.ITALIC + (TextFormat.YELLOW + "請設定該商店的交易物(輸入指令or物品點地)\n");
				String content = TextFormat.WHITE + "";
				if(shop instanceof ItemSingle) {
					content += "@i <物品ID> <交易數量>  -指定物品ID及交易數量\n"
							+  "@h <交易數量>           -判斷手持物品為商品 並指定交易數量\n"
							+  "@back                   -返回至上一個設定";
				}else if(shop instanceof LotteryShop) {
					title += TextFormat.RED + "(您尚有" + ((LotteryShop)shop).getRemain() + "%的機率尚未分配)\n";
					content += "@i <物品ID> <交易數量> <機率>  -指定物品ID, 交易數量, 機率\n"
							+  "@h <交易數量> <機率>           -判斷手持物品為商品 並指定交易數量及機率\n"
							+  "@back                         -返回至上一個設定";
				}else if(shop instanceof TitleShop) {
					content += "@t <Head>  -以稱號標頭指定稱號\n"
							+  "@back      -返回至上一個設定";
				}
				builder.sendMessage(title + content);
				break;
			case 1: 
				//TODO: 判斷以物易物、點券、
				if(shop instanceof MoneyCostable) {
					if(shop instanceof TitleShop) {
						builder.sendMessage(TextFormat.ITALIC + (TextFormat.YELLOW + "請輸入以下指令設定交易價格\n"
								+ TextFormat.WHITE + "@c <m:p> <價格>  -指定交易貨幣(m為金錢/p為點券)及價格\n"
								+ TextFormat.WHITE + "@back返回至上一個設定"));
					}else 
						builder.sendMessage(TextFormat.ITALIC + (TextFormat.YELLOW + "請設定交易價格: "));
				}else {
					if(shop instanceof BarterShop) {
						String title1 = TextFormat.ITALIC + (TextFormat.YELLOW + "請設定該商店的兌換物(輸入指令or物品點地)\n");
						String content1 = TextFormat.WHITE + "";
						content1 += "@i <物品ID> <交易數量>  -指定物品ID及兌換所需數量\n"
								 +  "@h <交易數量>           -判斷手持物品為兌換品 並指定兌換所需數量\n"
								 +  "@back                   -返回至上一個設定";
						builder.sendMessage(title1 + content1);
					}
				}
				break;
			case 0: 
				builder.sendMessage(TextFormat.ITALIC + (TextFormat.YELLOW + "請點擊地板設定地點"));
				break;
		}
	}
}
