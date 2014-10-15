package enchantableblocks.enchantments.blocks;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

public class BlockEnchantThorns extends BlockEnchantment
{
    public BlockEnchantThorns(int id, int weight)
	{
		super(id, weight);
        this.setName("thorns");
	}

	public int getMaxLevel()
    {
        return 3;
    }
	
	@Override
	public void DoEffect(Entity entity, EnchantEventType type, int amp, Object... auxObjs)
	{
		if(type == EnchantEventType.TOUCH || type == EnchantEventType.INTERACT)
		{
			entity.attackEntityFrom(DamageSource.cactus, 1F * (amp + 1));
		}
	}

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(int p_77321_1_)
    {
        return 1 + 5 * (p_77321_1_ - 1);
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(int p_77317_1_)
    {
        return super.getMinEnchantability(p_77317_1_) + 5;
    }
}
