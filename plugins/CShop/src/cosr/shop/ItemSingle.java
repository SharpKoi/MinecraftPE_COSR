package cosr.shop;

import cn.nukkit.item.Item;

public interface ItemSingle {

	public Item getItem();
	
	public void setItem(Item item);

	public int getStock();

	public void setStock(int stock);
}
