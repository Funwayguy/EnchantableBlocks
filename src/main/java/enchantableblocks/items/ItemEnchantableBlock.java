package enchantableblocks.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import enchantableblocks.blocks.BlockEnchantedGeneric;
import enchantableblocks.blocks.tile.ITileRetention;
import enchantableblocks.core.EnchantableBlocks;
import enchantableblocks.enchantments.blocks.BlockEnchantment;

public class ItemEnchantableBlock extends ItemBlock
{
	public ItemEnchantableBlock(Block block)
	{
		super(block);
	}
	
	@Override
    public int getItemEnchantability()
    {
        return 1;
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack stack)
    {
    	if(this.field_150939_a instanceof BlockEnchantedGeneric)
    	{
    		Item oldItem = Item.getItemFromBlock(((BlockEnchantedGeneric)this.field_150939_a).original);
    		
    		if(oldItem != null)
    		{
    			return oldItem.getUnlocalizedName(stack);
    		} else
    		{
        		return this.field_150939_a.getUnlocalizedName();
    		}
    	} else
    	{
    		return this.field_150939_a.getUnlocalizedName();
    	}
    }
    
    /**
     * Returns the metadata of the block which this Item (ItemBlock) can place
     */
    public int getMetadata(int meta)
    {
        return meta%16;
    }
	
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
       if(super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata))
       {
    	   TileEntity tile = world.getTileEntity(x, y, z);
    	   
    	   if(tile != null && stack.isItemEnchanted())
    	   {
    		   NBTTagCompound tags = new NBTTagCompound();
    		   NBTTagCompound retained = null;
    		   
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
    		    	   nbttagcompound.setString("owner", player.capabilities.isCreativeMode? "" : player.getCommandSenderName());
    		       } else if(id == BlockEnchantment.retention.effectId && tile instanceof ITileRetention)
    		       {
    		    	   if(stack.getTagCompound().hasKey("tile"))
    		    	   {
    		    		   retained = stack.getTagCompound().getCompoundTag("tile");
    		    	   }
    		       }
    		       
    		       nbttaglist.appendTag(nbttagcompound);
    		   }
    		   
    		   tile.readFromNBT(tags);
    		   
    		   if(retained != null)
    		   {
    			   tile.readFromNBT(retained);
    			   tile.xCoord = x;
    			   tile.yCoord = y;
    			   tile.zCoord = z;
    			   tile.blockType = world.getBlock(x, y, z);
    			   tile.blockMetadata = world.getBlockMetadata(x, y, z);
    			   tile.setWorldObj(world);
    		   }
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
