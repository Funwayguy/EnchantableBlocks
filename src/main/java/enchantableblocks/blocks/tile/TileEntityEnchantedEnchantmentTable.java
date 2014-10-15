package enchantableblocks.blocks.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityEnchantmentTable;

public class TileEntityEnchantedEnchantmentTable extends TileEntityEnchantmentTable
{
	NBTTagList enchantments = new NBTTagList();

    public void readFromNBT(NBTTagCompound tags)
    {
    	super.readFromNBT(tags);
    	enchantments = tags.getTagList("ench", 10);
    }

    public void writeToNBT(NBTTagCompound tags)
    {
    	super.writeToNBT(tags);
    	tags.setTag("ench", enchantments);
    }
}
