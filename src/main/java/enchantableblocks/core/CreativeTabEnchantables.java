package enchantableblocks.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

public class CreativeTabEnchantables extends CreativeTabs
{
	public CreativeTabEnchantables(String label)
	{
		super(label);
	}
	
	public CreativeTabEnchantables(int index, String label)
	{
		super(index, label);
	}
	
	@Override
	public Item getTabIconItem()
	{
		return Item.getItemFromBlock(Blocks.enchanting_table);
	}
}
