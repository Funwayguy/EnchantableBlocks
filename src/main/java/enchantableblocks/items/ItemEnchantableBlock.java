package enchantableblocks.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import enchantableblocks.core.EnchantableBlocks;
import enchantableblocks.enchantments.blocks.BlockEnchantment;

public class ItemEnchantableBlock extends ItemBlock
{
	Block enchantedVersion;
	
	public ItemEnchantableBlock(Block block)
	{
		super(block);
		this.enchantedVersion = block;
	}
	
	public ItemEnchantableBlock(Block block, Block blockEnchant)
	{
		super(block);
		this.enchantedVersion = blockEnchant;
	}
	
	@Override
    public int getItemEnchantability()
    {
        return 1;
    }
	
	public Block getEnchantedBlock()
	{
		if(enchantedVersion == null)
		{
			return this.field_150939_a;
		} else
		{
			return enchantedVersion;
		}
	}
	
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
       if (super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata))
       {
    	   TileEntity tile = world.getTileEntity(x, y, z);
    	   
    	   /*if(tile == null && enchantedVersion instanceof ITileEntityProvider)
    	   {
    		   tile = ((ITileEntityProvider)enchantedVersion).createNewTileEntity(world, metadata);
    		   
    		   if(tile != null)
    		   {
    			   tile.xCoord = x;
    			   tile.yCoord = y;
    			   tile.zCoord = z;
        		   
        		   world.addTileEntity(tile);
    		   }
    	   }*/
    	   
    	   if(tile != null && stack.isItemEnchanted())
    	   {
    		   NBTTagCompound tags = new NBTTagCompound();
    		   
    		   tile.writeToNBT(tags);
    		   
    		   if (!tags.hasKey("ench", 10))
		       {
    			   tags.setTag("ench", new NBTTagList());
		       }
    		   
    		   NBTTagList enchList = stack.getEnchantmentTagList();
    		   
    		   if(enchList == null)
    		   {
    			   return true;
    		   }
    		   
    		   for(int i = 0; i < enchList.tagCount(); i++)
    		   {
        		   NBTTagCompound itemTag = enchList.getCompoundTagAt(i);
        		   short id = itemTag.getShort("id");
        		   short lvl = itemTag.getShort("lvl");
    			   
    		       NBTTagList nbttaglist = tags.getTagList("ench", 10);
    		       NBTTagCompound nbttagcompound = new NBTTagCompound();
    		       nbttagcompound.setShort("id", id);
    		       nbttagcompound.setShort("lvl", lvl);
    		       
    		       if(id == BlockEnchantment.owned.effectId)
    		       {
    		    	   nbttagcompound.setString("owner", player.getCommandSenderName());
    		       } else if(id == BlockEnchantment.retention.effectId)
    		       {
    		    	   tags.setTag("Items", stack.getTagCompound().getTagList("Items", 10));
    		       }
    		       
    		       nbttaglist.appendTag(nbttagcompound);
    		   }
    		   
    		   tile.readFromNBT(tags);
    	   } else if(stack.isItemEnchanted())
    	   {
    		   EnchantableBlocks.logger.log(Level.WARN, "No tile at position! Failed to set enchantments!");
    	   }
    	   
           return true;
       } else
       {
    	   return false;
       }
    }
}
