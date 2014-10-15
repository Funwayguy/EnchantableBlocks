package enchantableblocks.enchantments.items;

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
        return stack.getItem() == Items.shears;
    }
}
