package enchantableblocks.enchantments.blocks;

public class BlockEnchantBlastResist extends BlockEnchantment
{
    public BlockEnchantBlastResist(int id, int weight)
	{
		super(id, weight);
        this.setName("protect.explosion");
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
        return 15 + (p_77321_1_ - 1) * 9;
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(int p_77317_1_)
    {
        return super.getMinEnchantability(p_77317_1_) + 50;
    }
}
