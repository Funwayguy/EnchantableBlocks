package enchantableblocks.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerChest extends Container
{
    private IInventory lowerChestInventory;
    private int numRows;
    public int localPg = 0;
    private static final String __OBFID = "CL_00001742";

    public ContainerChest(IInventory p_i1806_1_, IInventory p_i1806_2_)
    {
        this.lowerChestInventory = p_i1806_2_;
        this.numRows = p_i1806_2_.getSizeInventory() / 9;
        p_i1806_2_.openInventory();
        int i = ((this.numRows > 6? 6 : this.numRows) - 4) * 18;
        int j;
        int k;

        for (j = 0; j < this.numRows; ++j)
        {
        	if(j < 6)
        	{
	            for (k = 0; k < 9; ++k)
	            {
	                this.addSlotToContainer(new Slot(p_i1806_2_, k + j * 9, 8 + k * 18, 18 + j * 18));
	            }
        	} else
        	{
        		for (k = 0; k < 9; ++k)
	            {
	                this.addSlotToContainer(new Slot(p_i1806_2_, k + j * 9, -999, -999));
	            }
        	}
        }

        for (j = 0; j < 3; ++j)
        {
            for (k = 0; k < 9; ++k)
            {
                this.addSlotToContainer(new Slot(p_i1806_1_, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
            }
        }

        for (j = 0; j < 9; ++j)
        {
            this.addSlotToContainer(new Slot(p_i1806_1_, j, 8 + j * 18, 161 + i));
        }
    }

    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return this.lowerChestInventory.isUseableByPlayer(p_75145_1_);
    }
    
    public void updatePage(int page)
    {
    	localPg = page;
    	
        int i = ((this.numRows > 6 + (localPg * 6)? 6 : (this.numRows - 1)%6 + 1) - 4) * 18;
        int j;
        int k;
    	
    	for (j = 0; j < this.numRows; ++j)
        {
            for (k = 0; k < 9; ++k)
            {
                Slot slotC = (Slot)this.inventorySlots.get(k + j * 9); // Chest slots
                
                if(j >= localPg * 6 && j < (localPg + 1) * 6)
                {
                	int jj = j - localPg * 6;
                	slotC.xDisplayPosition = 8 + k * 18;
                	slotC.yDisplayPosition = 18 + jj * 18;
                } else
                {
                	slotC.xDisplayPosition = -999;
                	slotC.yDisplayPosition = -999;
                }
            }
        }

        for (j = 0; j < 3; ++j)
        {
            for (k = 0; k < 9; ++k)
            {
                Slot slotI = (Slot)this.inventorySlots.get(k + j * 9 + (this.numRows * 9)); // Inventory slots
                slotI.xDisplayPosition = 8 + k * 18;
                slotI.yDisplayPosition = 103 + j * 18 + i;
            }
        }

        for (j = 0; j < 9; ++j)
        {
            Slot slotH = (Slot)this.inventorySlots.get(j + 27 + (this.numRows * 9)); // Hotbar slots
            slotH.xDisplayPosition = 8 + j * 18;
            slotH.yDisplayPosition = 161 + i;
        }
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
     */
    public ItemStack transferStackInSlot(EntityPlayer p_82846_1_, int p_82846_2_)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(p_82846_2_);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (p_82846_2_ < this.numRows * 9)
            {
                if (!this.mergeItemStack(itemstack1, this.numRows * 9, this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, this.numRows * 9, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer p_75134_1_)
    {
        super.onContainerClosed(p_75134_1_);
        this.lowerChestInventory.closeInventory();
    }

    /**
     * Return this chest container's lower chest inventory.
     */
    public IInventory getLowerChestInventory()
    {
        return this.lowerChestInventory;
    }
}