package enchantableblocks.inventory;

import java.util.Iterator;
import java.util.List;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

public class SlotEnchantedCrafting extends SlotCrafting
{
    /** The craft matrix inventory linked to this result slot. */
    private final IInventory craftMatrix;
    /** The player that is using the GUI where this slot resides. */
    private EntityPlayer thePlayer;
    
	public float doubleUp;
	public float efficiency;
	public float hype;
	
	public Container parentContainer;
	
	public SlotEnchantedCrafting(EntityPlayer player, IInventory craftInv, IInventory mainInv, int id, int x, int y, float doubleUp, float efficiency, float hype, Container container)
	{
		super(player, craftInv, mainInv, id, x, y);
		this.doubleUp = doubleUp;
		this.efficiency = efficiency;
		this.hype = hype;
		
		this.craftMatrix = craftInv;
		this.thePlayer = player;
		
		this.parentContainer = container;
	}

    public void onPickupFromSlot(EntityPlayer player, ItemStack p_82870_2_)
    {
    	boolean requiresResync = false;
        if(!player.worldObj.isRemote && player.worldObj.rand.nextFloat() < doubleUp && p_82870_2_.stackSize <= p_82870_2_.getMaxStackSize()/2)
        {
        	// Randomly doubles the picked up stack if it is less than or equal to half the max stack size.
        	p_82870_2_.stackSize *= 2;
        	requiresResync = true;
        }
        
        if(!player.worldObj.isRemote && player.worldObj.rand.nextFloat() < hype)
        {
        	@SuppressWarnings("unchecked")
			List<EntityPlayer> playerList = player.worldObj.getEntitiesWithinAABB(EntityPlayer.class, player.boundingBox.expand(5D, 5D, 5D));
        	Iterator<EntityPlayer> iterator = playerList.iterator();
        	
        	while(iterator.hasNext())
        	{
        		EntityPlayer iPlayer = iterator.next();
    			
        		iPlayer.addPotionEffect(new PotionEffect(Potion.regeneration.id, 200));
        		iPlayer.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 200));
        		iPlayer.addPotionEffect(new PotionEffect(Potion.jump.id, 200));
        	}
        }
        
        FMLCommonHandler.instance().firePlayerCraftingEvent(player, p_82870_2_, craftMatrix);
        this.onCrafting(p_82870_2_);
        
        for (int i = 0; i < this.craftMatrix.getSizeInventory(); ++i)
        {
            ItemStack itemstack1 = this.craftMatrix.getStackInSlot(i);
            
            if (itemstack1 != null)
            {
            	if(!player.worldObj.isRemote && player.worldObj.rand.nextFloat() < efficiency) // 'worldObj.rand' Causes sync issues between client & server, please fix me :(
            	{
            		// Randomly skip removing items from the crafting matrix based on the efficiency level of the table.
            		requiresResync = true;
                    this.craftMatrix.setInventorySlotContents(i, itemstack1.copy());
            		continue;
            	}
            	
                this.craftMatrix.decrStackSize(i, 1);

                if (itemstack1.getItem().hasContainerItem(itemstack1))
                {
                    ItemStack itemstack2 = itemstack1.getItem().getContainerItem(itemstack1);

                    if (itemstack2 != null && itemstack2.isItemStackDamageable() && itemstack2.getItemDamage() > itemstack2.getMaxDamage())
                    {
                        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(thePlayer, itemstack2));
                        continue;
                    }

                    if (!itemstack1.getItem().doesContainerItemLeaveCraftingGrid(itemstack1) || !this.thePlayer.inventory.addItemStackToInventory(itemstack2))
                    {
                        if (this.craftMatrix.getStackInSlot(i) == null)
                        {
                            this.craftMatrix.setInventorySlotContents(i, itemstack2);
                        }
                        else
                        {
                            this.thePlayer.dropPlayerItemWithRandomChoice(itemstack2, false);
                        }
                    }
                }
            }
        }
        
        if(!player.worldObj.isRemote && requiresResync && this.thePlayer instanceof EntityPlayerMP)
        {
        	((EntityPlayerMP)this.thePlayer).sendContainerAndContentsToPlayer(this.parentContainer, this.parentContainer.getInventory());
        	this.parentContainer.detectAndSendChanges();
        	requiresResync = false;
        }
    }
}
