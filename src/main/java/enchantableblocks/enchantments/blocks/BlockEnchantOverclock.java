package enchantableblocks.enchantments.blocks;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import enchantableblocks.core.EnchantableBlocks;

public class BlockEnchantOverclock extends BlockEnchantment
{
	public BlockEnchantOverclock(int id, int weight)
	{
		super(id, weight);
		this.setName("overclock");
		
		this.allowedBlocks.add((ItemBlock)Item.getItemFromBlock(Blocks.furnace));
		this.allowedBlocks.add((ItemBlock)Item.getItemFromBlock(Blocks.lit_furnace));
		this.allowedBlocks.add((ItemBlock)Item.getItemFromBlock(EnchantableBlocks.blockFurnace));
		this.allowedBlocks.add((ItemBlock)Item.getItemFromBlock(EnchantableBlocks.blockFurnaceOn));
	}

	public int getMaxLevel()
    {
        return 3;
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(int p_77321_1_)
    {
        return 10 + 20 * (p_77321_1_ - 1);
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(int p_77317_1_)
    {
        return super.getMinEnchantability(p_77317_1_) + 50;
    }
}
