package enchantableblocks.enchantments.blocks;

import enchantableblocks.core.EnchantableBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class BlockEnchantPacking extends BlockEnchantment
{
    public BlockEnchantPacking(int id, int weight)
	{
		super(id, weight);
        this.setName("packingPro");
        
		this.allowedBlocks.add((ItemBlock)Item.getItemFromBlock(Blocks.chest));
		this.allowedBlocks.add((ItemBlock)Item.getItemFromBlock(EnchantableBlocks.blockChest));
		
		//this.allowedBlocks.add((ItemBlock)Item.getItemFromBlock(Blocks.furnace));
		//this.allowedBlocks.add((ItemBlock)Item.getItemFromBlock(Blocks.lit_furnace));
		//this.allowedBlocks.add((ItemBlock)Item.getItemFromBlock(EnchantableBlocks.blockFurnace));
		//this.allowedBlocks.add((ItemBlock)Item.getItemFromBlock(EnchantableBlocks.blockFurnaceOn));
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
        return /*10 + */15 * (p_77321_1_ - 1);
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(int p_77317_1_)
    {
        return super.getMinEnchantability(p_77317_1_) + 15;
    }
}
