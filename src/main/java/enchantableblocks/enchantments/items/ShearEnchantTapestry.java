package enchantableblocks.enchantments.items;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ShearEnchantTapestry extends AuxEnchantment
{
	public ShearEnchantTapestry(int id, int weight)
	{
		super(id, weight);
		this.setName("tapestry");
	}

    public boolean canApply(ItemStack stack)
    {
        return stack.getItem() == Items.shears;
    }
}
