package enchantableblocks.blocks.tile;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityFurnace;
import enchantableblocks.blocks.BlockEnchantedFurnace;
import enchantableblocks.enchantments.blocks.BlockEnchantment;

public class TileEntityEnchantedFurnace extends TileEntityFurnace
{
	NBTTagList enchantments = new NBTTagList();
	int hype = 0;
	int efficiency = 0;
	int overclock = 0;
	int doubleUp = 0;
	int solar = 0;
	int solarSleep = 300;

    public void readFromNBT(NBTTagCompound tags)
    {
    	super.readFromNBT(tags);
    	enchantments = tags.getTagList("ench", 10);
    	
    	for(int i = 0; i < enchantments.tagCount(); i++)
		{
			NBTTagCompound enchTag = enchantments.getCompoundTagAt(i);
			short id = enchTag.getShort("id");
			short lvl = enchTag.getShort("lvl");
			
			if(id == BlockEnchantment.efficiency.effectId)
			{
				efficiency = lvl;
			} else if(id == BlockEnchantment.overClock.effectId)
			{
				overclock = lvl;
			} else if(id == BlockEnchantment.doubleUp.effectId)
			{
				doubleUp = lvl;
			} else if(id == BlockEnchantment.solar.effectId)
			{
				solar = lvl;
			} else if(id == BlockEnchantment.hype.effectId)
			{
				hype = lvl;
			}
		}
    	
    	this.currentItemBurnTime = this.currentItemBurnTime * (1 + efficiency);
    }

    public void writeToNBT(NBTTagCompound tags)
    {
    	super.writeToNBT(tags);
    	tags.setTag("ench", enchantments);
    }
    
    @Override
    public void updateEntity()
    {
        boolean flag = this.furnaceBurnTime > 0;
        boolean flag1 = false;

        if (this.furnaceBurnTime > 0)
        {
            this.furnaceBurnTime -= 1 + overclock;
        }
        this.furnaceBurnTime = this.furnaceBurnTime < 0? 0 : this.furnaceBurnTime;

        if (!this.worldObj.isRemote)
        {
            if ((solar > 0 || this.furnaceBurnTime != 0 || this.getStackInSlot(1) != null) && this.getStackInSlot(0) != null)
            {
                if (this.furnaceBurnTime == 0 && this.canSmelt())
                {
                    this.currentItemBurnTime = this.furnaceBurnTime = getItemBurnTime(this.getStackInSlot(1)) * (1 + efficiency);

                    if (this.furnaceBurnTime > 0)
                    {
                    	solarSleep = 300;
                        flag1 = true;

                        if (this.getStackInSlot(1) != null)
                        {
                            --this.getStackInSlot(1).stackSize;

                            if (this.getStackInSlot(1).stackSize == 0)
                            {
                                this.setInventorySlotContents(1, getStackInSlot(1).getItem().getContainerItem(getStackInSlot(1)));
                            }
                        }
                    } else if(solar > 0 && this.getWorldObj().canBlockSeeTheSky(this.xCoord, this.yCoord + 1, this.zCoord))
                    {
                    	if(solarSleep <= 0)
                    	{
                            flag1 = true;
                    		solarSleep = 300;
                    		this.currentItemBurnTime = this.furnaceBurnTime = (solar * 200) * (1 + efficiency);
                    	} else
                    	{
                    		solarSleep -= 1 + overclock;
                    	}
                    } else
                    {
                    	solarSleep = 300;
                    }
                }

                if (this.isBurning() && this.canSmelt())
                {
                    this.furnaceCookTime += overclock + 1;

                    if (this.furnaceCookTime >= 200)
                    {
                        this.furnaceCookTime = 0;
                        this.smeltItem();
                        flag1 = true;
                    }
                }
                else
                {
                    this.furnaceCookTime = 0;
                }
            }

            if (flag != this.furnaceBurnTime > 0)
            {
                flag1 = true;
                BlockEnchantedFurnace.updateFurnaceBlockState(this.furnaceBurnTime > 0, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            }
        }

        if (flag1)
        {
            this.markDirty();
        }
    }

    /**
     * Returns true if the furnace can smelt an item, i.e. has a source item, destination stack isn't full, etc.
     */
    private boolean canSmelt()
    {
        if (this.getStackInSlot(0) == null)
        {
            return false;
        }
        else
        {
            ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(this.getStackInSlot(0));
            if (itemstack == null) return false;
            if (this.getStackInSlot(2) == null) return true;
            if (!this.getStackInSlot(2).isItemEqual(itemstack)) return false;
            int result = getStackInSlot(2).stackSize + itemstack.stackSize;
            return result <= getInventoryStackLimit() && result <= this.getStackInSlot(2).getMaxStackSize(); //Forge BugFix: Make it respect stack sizes properly.
        }
    }

    /**
     * Turn one item from the furnace source stack into the appropriate smelted item in the furnace result stack
     */
    public void smeltItem()
    {
        if (this.canSmelt())
        {
        	if(hype > 0 && !this.getWorldObj().isRemote && this.getWorldObj().rand.nextFloat() < hype*0.1F)
        	{
        		List<EntityPlayer> playerList = this.getWorldObj().getEntitiesWithinAABB(EntityPlayer.class, this.getBlockType().getCollisionBoundingBoxFromPool(this.getWorldObj(), this.xCoord, this.yCoord, this.zCoord).expand(5D, 5D, 5D));
        		
        		Iterator<EntityPlayer> iterator = playerList.iterator();
        		
        		while(iterator.hasNext())
        		{
        			EntityPlayer player = iterator.next();
        			
        			player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 200));
        			player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 200));
        			player.addPotionEffect(new PotionEffect(Potion.jump.id, 200));
        		}
        	}
        	
            ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(this.getStackInSlot(0));

            if (this.getStackInSlot(2) == null)
            {
            	ItemStack newStack = itemstack.copy();
            	if(itemstack.stackSize*2 <= itemstack.getMaxStackSize() && this.getWorldObj().rand.nextFloat() < doubleUp*0.1F)
            	{
            		newStack.stackSize *= 2;
            	}
                this.setInventorySlotContents(2, newStack);
            }
            else if (this.getStackInSlot(2).getItem() == itemstack.getItem())
            {
            	ItemStack newStack = itemstack.copy();
            	if(itemstack.stackSize*2 + this.getStackInSlot(2).stackSize <= itemstack.getMaxStackSize() && this.getWorldObj().rand.nextFloat() < doubleUp*0.1F)
            	{
            		newStack.stackSize *= 2;
            	}
                this.getStackInSlot(2).stackSize += newStack.stackSize; // Forge BugFix: Results may have multiple items
            }

            --this.getStackInSlot(0).stackSize;

            if (this.getStackInSlot(0).stackSize <= 0)
            {
                this.setInventorySlotContents(0, null);
            }
        }
    }
    
    public ItemStack[] getItemArray()
    {
    	ItemStack[] stacks = new ItemStack[this.getSizeInventory()];
    	
    	for(int i = 0; i < this.getSizeInventory(); i++)
    	{
    		stacks[i] = this.getStackInSlot(i);
    	}
    	
    	return stacks;
    }
    
    public void clearItemArray()
    {
    	for(int i = 0; i < this.getSizeInventory(); i++)
    	{
    		this.setInventorySlotContents(i, null);
    	}
    }
}
