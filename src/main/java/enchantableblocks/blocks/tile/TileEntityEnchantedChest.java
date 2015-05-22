package enchantableblocks.blocks.tile;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityChest;
import enchantableblocks.blocks.BlockEnchantedChest;
import enchantableblocks.enchantments.blocks.BlockEnchantment;

public class TileEntityEnchantedChest extends TileEntityChest implements ITileRetention
{
	 /**New ItemStack array used in place of 'TileEntityChest.chestContents'.
	  * Original stack array should not be used in anyway through this class
	  * so as to not cause an ArrayOutOfBoundsException due to the enchantments
	  * modifying the size beyond normal numbers.
	  */
	public ItemStack[] enchChestContents = new ItemStack[63];
	public NBTTagList enchantments = new NBTTagList();
	public int packingLvl = 0;
	
	public TileEntityEnchantedChest()
	{
		super(2);
	}
	
    public void LoadEnchantments()
    {
    	for(int i = 0; i < enchantments.tagCount(); i++)
		{
			NBTTagCompound enchTag = enchantments.getCompoundTagAt(i);
			short id = enchTag.getShort("id");
			short lvl = enchTag.getShort("lvl");
			
			if(id == BlockEnchantment.packingPro.effectId)
			{
				packingLvl = lvl;
			}
		}
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory()
    {
        return 27 + (9 * packingLvl);
    }
    
    @Override
    public int func_145980_j()
    {
        return 2;
    }
    
    public void func_145978_a(TileEntityChest chestTile, int p_145978_2_)
    {
        if (chestTile.isInvalid())
        {
            this.adjacentChestChecked = false;
        }
        else if (this.adjacentChestChecked)
        {
            switch (p_145978_2_)
            {
                case 0:
                	if(!(chestTile instanceof TileEntityEnchantedChest))
                	{
                		chestTile.adjacentChestZNeg = null;
                	}
                	if (this.adjacentChestZPos != chestTile)
                    {
                        this.adjacentChestChecked = false;
                    }

                    break;
                case 1:
                	if(!(chestTile instanceof TileEntityEnchantedChest))
                	{
                		chestTile.adjacentChestXPos = null;
                	}
                	if (this.adjacentChestXNeg != chestTile)
                    {
                        this.adjacentChestChecked = false;
                    }

                    break;
                case 2:
                	if(!(chestTile instanceof TileEntityEnchantedChest))
                	{
                		chestTile.adjacentChestZPos = null;
                	}
                	if (this.adjacentChestZNeg != chestTile)
                    {
                        this.adjacentChestChecked = false;
                    }

                    break;
                case 3:
                	if(!(chestTile instanceof TileEntityEnchantedChest))
                	{
                		chestTile.adjacentChestXNeg = null;
                	}
                	if (this.adjacentChestXPos != chestTile)
                    {
                        this.adjacentChestChecked = false;
                    }
            }
        }
    }
    
    public void checkForAdjacentChests()
    {
        if (!this.adjacentChestChecked)
        {
            this.adjacentChestChecked = true;
            this.adjacentChestZNeg = null;
            this.adjacentChestXPos = null;
            this.adjacentChestXNeg = null;
            this.adjacentChestZPos = null;

            if (this.func_145977_a(this.xCoord - 1, this.yCoord, this.zCoord))
            {
                this.adjacentChestXNeg = (TileEntityChest)this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord);
            }

            if (this.func_145977_a(this.xCoord + 1, this.yCoord, this.zCoord))
            {
                this.adjacentChestXPos = (TileEntityChest)this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord);
            }

            if (this.func_145977_a(this.xCoord, this.yCoord, this.zCoord - 1))
            {
                this.adjacentChestZNeg = (TileEntityChest)this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1);
            }

            if (this.func_145977_a(this.xCoord, this.yCoord, this.zCoord + 1))
            {
                this.adjacentChestZPos = (TileEntityChest)this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1);
            }

            if (this.adjacentChestZNeg != null && this.adjacentChestZNeg instanceof TileEntityEnchantedChest)
            {
                ((TileEntityEnchantedChest)this.adjacentChestZNeg).func_145978_a(this, 0);
            } else
            {
            	adjacentChestZNeg = null;
            }

            if (this.adjacentChestZPos != null && this.adjacentChestZPos instanceof TileEntityEnchantedChest)
            {
            	((TileEntityEnchantedChest)this.adjacentChestZPos).func_145978_a(this, 2);
            } else
            {
            	adjacentChestZPos = null;
            }

            if (this.adjacentChestXPos != null && this.adjacentChestXPos instanceof TileEntityEnchantedChest)
            {
            	((TileEntityEnchantedChest)this.adjacentChestXPos).func_145978_a(this, 1);
            } else
            {
            	adjacentChestXPos = null;
            }

            if (this.adjacentChestXNeg != null && this.adjacentChestXNeg instanceof TileEntityEnchantedChest)
            {
            	((TileEntityEnchantedChest)this.adjacentChestXNeg).func_145978_a(this, 3);
            } else
            {
            	adjacentChestXNeg = null;
            }
        }
    }

    private boolean func_145977_a(int p_145977_1_, int p_145977_2_, int p_145977_3_)
    {
        if (this.worldObj == null)
        {
            return false;
        }
        else
        {
            Block block = this.worldObj.getBlock(p_145977_1_, p_145977_2_, p_145977_3_);
            return block instanceof BlockEnchantedChest && ((BlockEnchantedChest)block).field_149956_a == this.func_145980_j();
        }
    }

    public void readFromNBT(NBTTagCompound tags)
    {
    	super.readFromNBT(tags);
    	enchantments = tags.getTagList("ench", 10);
    	LoadEnchantments();
        NBTTagList nbttaglist = tags.getTagList("Items", 10);
        this.enchChestContents = new ItemStack[this.getSizeInventory()];
        
        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound1.getByte("Slot") & 255;

            if (j >= 0 && j < this.enchChestContents.length)
            {
                this.enchChestContents[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
    }

    public void writeToNBT(NBTTagCompound tags)
    {
    	super.writeToNBT(tags);
    	tags.setTag("ench", enchantments);
    	
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.enchChestContents.length; ++i)
        {
            if (this.enchChestContents[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.enchChestContents[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        tags.setTag("Items", nbttaglist);
    }
    
    // SYNC FUNCTIONS
    
    @Override
    public Packet getDescriptionPacket()
    {
    	NBTTagCompound tags = new NBTTagCompound();
    	this.writeToNBT(tags);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, tags);
    }
    
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
    	this.readFromNBT(pkt.func_148857_g());
    }
    
    // OVERRIDE STUFFS

    /**
     * Returns the stack in slot i
     */
    @Override
    public ItemStack getStackInSlot(int p_70301_1_)
    {
        return this.enchChestContents[p_70301_1_];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    @Override
    public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_)
    {
        if (this.enchChestContents[p_70298_1_] != null)
        {
            ItemStack itemstack;

            if (this.enchChestContents[p_70298_1_].stackSize <= p_70298_2_)
            {
                itemstack = this.enchChestContents[p_70298_1_];
                this.enchChestContents[p_70298_1_] = null;
                this.markDirty();
                return itemstack;
            }
            else
            {
                itemstack = this.enchChestContents[p_70298_1_].splitStack(p_70298_2_);

                if (this.enchChestContents[p_70298_1_].stackSize == 0)
                {
                    this.enchChestContents[p_70298_1_] = null;
                }

                this.markDirty();
                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    @Override
    public ItemStack getStackInSlotOnClosing(int p_70304_1_)
    {
        if (this.enchChestContents[p_70304_1_] != null)
        {
            ItemStack itemstack = this.enchChestContents[p_70304_1_];
            this.enchChestContents[p_70304_1_] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_)
    {
        this.enchChestContents[p_70299_1_] = p_70299_2_;

        if (p_70299_2_ != null && p_70299_2_.stackSize > this.getInventoryStackLimit())
        {
            p_70299_2_.stackSize = this.getInventoryStackLimit();
        }

        this.markDirty();
    }
}