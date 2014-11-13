package enchantableblocks.enchantments.items;

import enchantableblocks.core.EnchantableBlocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ShearEnchantFortune extends AuxEnchantment
{
	public ShearEnchantFortune(int id, int weight)
	{
		super(id, weight);
		this.setName("lootBonusDigger");
	}

    public boolean canApply(ItemStack stack)
    {
        return stack.getItem() == Items.shears || stack.getItem() == EnchantableBlocks.itemShears;
    }
    
    @Override
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
