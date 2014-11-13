package enchantableblocks.enchantments.items;

import enchantableblocks.core.EnchantableBlocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ShearEnchantThreading extends AuxEnchantment
{
	public ShearEnchantThreading(int id, int weight)
	{
		super(id, weight);
		this.setName("threading");
	}

    public boolean canApply(ItemStack stack)
    {
        return stack.getItem() == Items.shears || stack.getItem() == EnchantableBlocks.itemShears;
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(int p_77321_1_)
    {
        return 15;
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(int p_77317_1_)
    {
        return super.getMinEnchantability(p_77317_1_) + 50;
    }

    /**
     * Determines if the enchantment passed can be applyied together with this enchantment.
     */
    public boolean canApplyTogether(Enchantment p_77326_1_)
    {
        return super.canApplyTogether(p_77326_1_) && p_77326_1_.effectId != tapestry.effectId;
    }
}
