package enchantableblocks.items;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import enchantableblocks.blocks.tile.ITileRetention;
import enchantableblocks.blocks.tile.TileEntityEnchantedGeneric;
import enchantableblocks.core.EnchantableBlocks;
import enchantableblocks.core.GenericProxyUtils;
import enchantableblocks.enchantments.blocks.BlockEnchantment;

public class ItemBlockEnchanter extends Item
{
	public ItemBlockEnchanter()
	{
		this.setUnlocalizedName("block_enchanter");
		this.setMaxStackSize(1);
		this.setMaxDamage(127);
		this.setTextureName("enchantableblocks:enchanter");
		this.setCreativeTab(EnchantableBlocks.creativeTab);
	}

    /**
     * allows items to add custom lines of information to the mouseover description
     */
	@SideOnly(Side.CLIENT)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced)
    {
    	list.add("Add enchantments with an Anvil");
    	list.add("then right click a block to enchant.");
    	list.add("Hold shift to pickup enchanted blocks,");
    	list.add("blocks with Rentention keep items.");
    	list.add("");
    }

    /**
     * Called before a block is broken.  Return true to prevent default block harvesting.
     *
     * Note: In SMP, this is called on both client and server sides!
     *
     * @param itemstack The current ItemStack
     * @param X The X Position
     * @param Y The X Position
     * @param Z The X Position
     * @param player The Player that is wielding the item
     * @return True to prevent harvesting, false to continue as normal
     */
    public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player)
    {
        return false;
    }
    
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
    	return false;
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
    	if(world.isRemote)
    	{
    		return true;
    	}
    	
		Block block = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		TileEntity tile = world.getTileEntity(x, y, z);
    	
		if(player.isSneaking())
		{
			if(GenericProxyUtils.blockGeneric.containsValue(block) && tile != null)
			{
				ItemStack pickup = new ItemStack(block, 1, meta);
				
				NBTTagCompound tileTags = new NBTTagCompound();
				tile.writeToNBT(tileTags);
				NBTTagList tagList = tileTags.getTagList("ench", 10);
				
				boolean flag = true;
        		
        		for(int i = 0; i < tagList.tagCount(); i++)
    			{
    				NBTTagCompound enchTag = tagList.getCompoundTagAt(i);
    				short id = enchTag.getShort("id");
    				short lvl = enchTag.getShort("lvl");
    				
    				pickup.addEnchantment(Enchantment.enchantmentsList[id], lvl);
    				
    				if(id == BlockEnchantment.retention.effectId && tile instanceof ITileRetention)
    				{
    			        pickup.getTagCompound().setTag("tile", tileTags);
    			        flag = false;
    				}
    			}
        		
        		EntityItem droppedItem = new EntityItem(world);
        		droppedItem.setEntityItemStack(pickup);
        		droppedItem.setPosition(x + 0.5D, y + 0.5D, z + 0.5D);
        		world.spawnEntityInWorld(droppedItem);
        		if(!flag && tile instanceof IInventory) // Wipe all known data
        		{
        			IInventory invo = (IInventory)tile;
        			for(int i = 0; i < invo.getSizeInventory(); i++)
        			{
        				invo.setInventorySlotContents(i, null);
        			}
        		}
        		world.setBlockToAir(x, y, z);
				stack.damageItem(1, player);
			} else
			{
				player.addChatMessage(new ChatComponentText("This block cannot be picked up with this"));
			}
		} else if((GenericProxyUtils.blockGeneric.containsKey(block) || tile instanceof TileEntityEnchantedGeneric) && stack.isItemEnchanted())
		{
			if(GenericProxyUtils.blockGeneric.containsKey(block))
			{
				world.setBlock(x, y, z, GenericProxyUtils.blockGeneric.get(block), meta, 3);
			}
			
			if(tile != null)
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
				
				NBTTagList nbttaglist = tags.getTagList("ench", 10);
				
				for(int i = 0; i < enchList.tagCount(); i++)
				{
					NBTTagCompound itemTag = enchList.getCompoundTagAt(i);
					short id = itemTag.getShort("id");
					short lvl = itemTag.getShort("lvl");
					NBTTagCompound nbttagcompound = new NBTTagCompound();
					nbttagcompound.setShort("id", id);
					nbttagcompound.setShort("lvl", lvl);
					
					if(id == BlockEnchantment.owned.effectId)
					{
						nbttagcompound.setString("owner", player.capabilities.isCreativeMode? "" : player.getCommandSenderName());
					} else if(id == BlockEnchantment.retention.effectId)
					{
						tags.setTag("Items", stack.getTagCompound().getTagList("Items", 10));
					}
					
					nbttaglist.appendTag(nbttagcompound);
				}
				
				tile.readFromNBT(tags);
				
				if(!player.capabilities.isCreativeMode)
				{
					stack.setTagCompound(null); // Remove all enchantment data now that it has been applied
				}
   				
				player.addChatMessage(new ChatComponentText("Successfully enchanted block"));
				stack.damageItem(1, player);
			}
		} else if(!stack.isItemEnchanted())
		{
			player.addChatMessage(new ChatComponentText("No enchantment set first"));
		} else
		{
			player.addChatMessage(new ChatComponentText("This block cannot be enchanted"));
		}
		
		return true;
    }
	
	@Override
    public int getItemEnchantability()
    {
        return 15;
    }
}