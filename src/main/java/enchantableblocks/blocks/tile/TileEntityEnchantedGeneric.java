package enchantableblocks.blocks.tile;

import java.lang.reflect.Method;
import java.util.HashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileEntityEnchantedGeneric extends TileEntity
{
	public static HashMap<String, Method> methodList = new HashMap<String, Method>();
	
	NBTTagCompound cachedTags = null;
	NBTTagList enchantments = new NBTTagList();
	
	/**
	 * Fixes the tile entity after it has loaded by recreating it through proxied methods before
	 * anything has a chance to interact with it and trigger a ClassCastException caused by
	 * blocks going looking for their corresponding tile entities.
	 */
	public void updateEntity()
	{
	}
	
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
