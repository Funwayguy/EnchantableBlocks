package enchantableblocks.enchantments.blocks;

public class BlockEnchantOwned extends BlockEnchantment
{
    public BlockEnchantOwned(int id, int weight)
	{
		super(id, weight);
        this.setName("owned");
	}

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(int p_77321_1_)
    {
        return 10;
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(int p_77317_1_)
    {
        return super.getMinEnchantability(p_77317_1_) + 40;
    }
}
