package enchantableblocks.enchantments.blocks;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import enchantableblocks.core.EnchantableBlocks;

public class BlockEnchantGabe extends BlockEnchantment
{
    public BlockEnchantGabe(int id, int weight)
	{
		super(id, weight);
        this.setName("gabe");
        
		this.allowedBlocks.add((ItemBlock)Item.getItemFromBlock(Blocks.enchanting_table));
		this.allowedBlocks.add((ItemBlock)Item.getItemFromBlock(EnchantableBlocks.blockEnch));
	}

	public int getMaxLevel()
    {
        return 7;
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(int p_77321_1_)
    {
        return 1 + (p_77321_1_ - 1) * 5;
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(int p_77317_1_)
    {
        return this.getMinEnchantability(p_77317_1_) + 20;
    }
}
