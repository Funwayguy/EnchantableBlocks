package enchantableblocks.enchantments.blocks;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import enchantableblocks.core.EnchantableBlocks;

public class BlockEnchantHype extends BlockEnchantment
{
    public BlockEnchantHype(int id, int weight)
	{
		super(id, weight);
        this.setName("hype");
		
		this.allowedBlocks.add((ItemBlock)Item.getItemFromBlock(Blocks.crafting_table));
		this.allowedBlocks.add((ItemBlock)Item.getItemFromBlock(EnchantableBlocks.blockWorkbench));
		
		this.allowedBlocks.add((ItemBlock)Item.getItemFromBlock(Blocks.furnace));
		this.allowedBlocks.add((ItemBlock)Item.getItemFromBlock(Blocks.lit_furnace));
		this.allowedBlocks.add((ItemBlock)Item.getItemFromBlock(EnchantableBlocks.blockFurnace));
		this.allowedBlocks.add((ItemBlock)Item.getItemFromBlock(EnchantableBlocks.blockFurnaceOn));
	}

	public int getMaxLevel()
    {
        return 3;
    }
}
