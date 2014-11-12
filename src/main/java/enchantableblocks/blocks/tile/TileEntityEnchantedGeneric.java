package enchantableblocks.blocks.tile;

import java.lang.reflect.Method;
import java.util.HashMap;
import org.apache.logging.log4j.Level;
import enchantableblocks.core.EnchantableBlocks;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileEntityEnchantedGeneric extends TileEntity
{
	public static HashMap<String, Method> methodList = new HashMap<String, Method>();
	
	NBTTagCompound cachedTags = null;
	NBTTagList enchantments = new NBTTagList();
	TileEntity actualTile = null;
	
	/**
	 * Default instantiation method for Anvil loading. Must be corrected in updateEntity before
	 * anything touches it in order not to crash everything!
	 */
	public TileEntityEnchantedGeneric()
	{
		actualTile = this;
	}
	
	public TileEntityEnchantedGeneric(TileEntity tile)
	{
		actualTile = tile;
	}
	
	/**
	 * Fixes the tile entity after it has loaded by recreating it through proxied methods before
	 * anything has a chance to interact with it and trigger a ClassCastException caused by
	 * blocks going looking for their corresponding tile entities.
	 */
	public void updateEntity()
	{
		if(actualTile == this)
		{
			actualTile = null;
			World world = this.getWorldObj();
			
			Block block = world.getBlock(xCoord, yCoord, zCoord);
			int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
			
			if(block.hasTileEntity(meta))
			{
				TileEntity tile = block.createTileEntity(world, meta);
				
				if(tile != null)
				{
					this.invalidate();
					
					if(cachedTags != null)
					{
						tile.readFromNBT(cachedTags);
					}
					
					world.addTileEntity(tile);
					world.getChunkFromBlockCoords(tile.xCoord, tile.zCoord).addTileEntity(tile);
				} else
				{
					EnchantableBlocks.logger.log(Level.WARN, "Block failed to create a new tile entity!");
				}
			} else
			{
				EnchantableBlocks.logger.log(Level.WARN, "Block has no tiles accociated with it!");
			}
		} else if(actualTile != null)
		{
			actualTile.updateEntity();
		}
	}
	
    public void readFromNBT(NBTTagCompound tags)
    {
    	if(actualTile != null && actualTile == this)
    	{
    		cachedTags = tags; // Saving tags for later repairing the entity
    		super.readFromNBT(tags);
    	} else if(actualTile == null)
    	{
    		super.readFromNBT(tags);
    	} else
    	{
    		actualTile.readFromNBT(tags);
    	}
    	
    	enchantments = tags.getTagList("ench", 10);
    }

    public void writeToNBT(NBTTagCompound tags)
    {
    	if(actualTile != null && actualTile == this && cachedTags != null)
    	{
    		tags = cachedTags; // Reusing cached tags while waiting for tile to be fixed
    		super.writeToNBT(tags);
    	} else if(actualTile == null)
    	{
    		super.writeToNBT(tags);
    	} else
    	{
    		actualTile.writeToNBT(tags);
    	}
    	
    	tags.setTag("ench", enchantments);
    }
	
	static
	{
		Method[] declaredMethods = TileEntityEnchantedGeneric.class.getDeclaredMethods();
		
		for(Method method : declaredMethods)
		{
			methodList.put(method.getName(), method);
		}
	}
}
