package enchantableblocks.items;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IShearable;
import enchantableblocks.enchantments.items.AuxEnchantment;

public class ItemEnchantedShears extends ItemShears
{
	@Override
    public int getItemEnchantability()
    {
        return 1;
    }
	
    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    @Override
    public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity)
    {
        if (entity.worldObj.isRemote)
        {
            return false;
        }
        if (entity instanceof IShearable)
        {
            IShearable target = (IShearable)entity;
            if (target.isShearable(itemstack, entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ))
            {
            	int fortuneLvl = EnchantmentHelper.getEnchantmentLevel(AuxEnchantment.shearFortune.effectId, itemstack);
            	int threadingLvl = EnchantmentHelper.getEnchantmentLevel(AuxEnchantment.threading.effectId, itemstack);
            	int tapestryLvl = EnchantmentHelper.getEnchantmentLevel(AuxEnchantment.tapestry.effectId, itemstack);
                ArrayList<ItemStack> drops = target.onSheared(itemstack, entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ, fortuneLvl);

                Random rand = new Random();
                
                if(tapestryLvl > 0 || threadingLvl > 0)
                {
	                for(int i = 0; i < drops.size(); i++)
	                {
	                	ItemStack stack = drops.get(i);
	                	if(stack.getItem() == Item.getItemFromBlock(Blocks.wool))
	                	{
	                		if(threadingLvl > 0)
	                		{
	                			drops.set(i, new ItemStack(Items.string, 4));
	                		} else
	                		{
	                			drops.set(i, new ItemStack(Item.getItemFromBlock(Blocks.carpet), 2, stack.getItemDamage()));
	                		}
	                	}
	                }
                }
                
                for(int i = 0; i <= fortuneLvl; i++)
                {
	                for(ItemStack stack : drops)
	                {
	                    EntityItem ent = entity.entityDropItem(stack.copy(), 1.0F);
	                    ent.motionY += rand.nextFloat() * 0.05F;
	                    ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
	                    ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
	                }
                }
                itemstack.damageItem(1, entity);
            }
            return true;
        }
        return false;
    }
}
