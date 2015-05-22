package enchantableblocks.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFurnace;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import enchantableblocks.blocks.tile.TileEntityEnchantedFurnace;
import enchantableblocks.core.EnchantableBlocks;
import enchantableblocks.enchantments.blocks.BlockEnchantment;

public class BlockEnchantedFurnace extends BlockFurnace
{
	static boolean switching = false;
	
	public BlockEnchantedFurnace(boolean active)
	{
		super(active);
	}

    /**
     * Location sensitive version of getExplosionRestance
     *
     * @param par1Entity The entity that caused the explosion
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param explosionX Explosion source X Position
     * @param explosionY Explosion source X Position
     * @param explosionZ Explosion source X Position
     * @return The amount of the explosion absorbed.
     */
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ)
    {
        float resist = super.getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
        
        TileEntity tile = world.getTileEntity(x, y, z);
        
        if(tile != null)
        {
    		NBTTagCompound tags = new NBTTagCompound();
    		tile.writeToNBT(tags);
    		
    		if(tags.hasKey("ench") && tags.getTagList("ench", 10).tagCount() > 0)
    		{
    			NBTTagList tagList = tags.getTagList("ench", 10);
    			
    			for(int i = 0; i < tagList.tagCount(); i++)
    			{
    				NBTTagCompound enchTag = tagList.getCompoundTagAt(i);
    				short id = enchTag.getShort("id");
    				short lvl = enchTag.getShort("lvl");
    				
    				if(id == BlockEnchantment.blastResist.effectId)
    				{
    					resist *= 1F - ((float)lvl/(float)BlockEnchantment.blastResist.getMaxLevel());
    					break;
    				}
    			}
    		}
        }
        
        return resist;
    }

    /**
     * Chance that fire will spread and consume this block.
     * 300 being a 100% chance, 0, being a 0% chance.
     *
     * @param world The current world
     * @param x The blocks X position
     * @param y The blocks Y position
     * @param z The blocks Z position
     * @param face The face that the fire is coming from
     * @return A number ranging from 0 to 300 relating used to determine if the block will be consumed by fire
     */
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face)
    {
        int chance =  Blocks.fire.getFlammability(this);
        
        TileEntity tile = world.getTileEntity(x, y, z);
        
        if(chance > 0 && tile != null)
        {
    		NBTTagCompound tags = new NBTTagCompound();
    		tile.writeToNBT(tags);
    		
    		if(tags.hasKey("ench") && tags.getTagList("ench", 10).tagCount() > 0)
    		{
    			NBTTagList tagList = tags.getTagList("ench", 10);
    			
    			for(int i = 0; i < tagList.tagCount(); i++)
    			{
    				NBTTagCompound enchTag = tagList.getCompoundTagAt(i);
    				short id = enchTag.getShort("id");
    				
    				if(id == BlockEnchantment.fireProof.effectId)
    				{
    					return 0;
    				}
    			}
    		}
        }
        
        return chance;
    }
	
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
		if(!switching)
		{
			// --- DEFAULT FURNACE ---

            TileEntityFurnace tileentityfurnace = (TileEntityFurnace)world.getTileEntity(x, y, z);

            if (tileentityfurnace != null)
            {
                for (int i1 = 0; i1 < tileentityfurnace.getSizeInventory(); ++i1)
                {
                    ItemStack itemstack = tileentityfurnace.getStackInSlot(i1);

                    if (itemstack != null)
                    {
                        float f = world.rand.nextFloat() * 0.8F + 0.1F;
                        float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
                        float f2 = world.rand.nextFloat() * 0.8F + 0.1F;

                        while (itemstack.stackSize > 0)
                        {
                            int j1 = world.rand.nextInt(21) + 10;

                            if (j1 > itemstack.stackSize)
                            {
                                j1 = itemstack.stackSize;
                            }

                            itemstack.stackSize -= j1;
                            EntityItem entityitem = new EntityItem(world, (double)((float)x + f), (double)((float)y + f1), (double)((float)z + f2), new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));

                            if (itemstack.hasTagCompound())
                            {
                                entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
                            }

                            float f3 = 0.05F;
                            entityitem.motionX = (double)((float)world.rand.nextGaussian() * f3);
                            entityitem.motionY = (double)((float)world.rand.nextGaussian() * f3 + 0.2F);
                            entityitem.motionZ = (double)((float)world.rand.nextGaussian() * f3);
                            world.spawnEntityInWorld(entityitem);
                        }
                    }
                }

                world.func_147453_f(x, y, z, block);
            }
		}
		
		// BLOCK CONTAINER

        world.removeTileEntity(x, y, z);
        
        // BLOCK
        
        if (hasTileEntity(meta) && !(this instanceof BlockContainer))
        {
            world.removeTileEntity(x, y, z);
        }
    }
	
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		TileEntity tile = world.getTileEntity(x, y, z);
		
		if(tile != null)
		{
			NBTTagCompound tags = new NBTTagCompound();
    		tile.writeToNBT(tags);
    		
    		if(tags.hasKey("ench") && tags.getTagList("ench", 10).tagCount() > 0)
    		{
    			NBTTagList tagList = tags.getTagList("ench", 10);
    			
    			for(int i = 0; i < tagList.tagCount(); i++)
    			{
    				NBTTagCompound enchTag = tagList.getCompoundTagAt(i);
    				short id = enchTag.getShort("id");
    				short lvl = enchTag.getShort("lvl");
    				
    				Enchantment enchant = Enchantment.enchantmentsList[id];
    				
    				if(enchant instanceof BlockEnchantment)
    				{
    					((BlockEnchantment)enchant).DoEffect(entity, BlockEnchantment.EnchantEventType.TOUCH, lvl, new Object[0]);
    				}
    			}
    		}
		}
	}

    /**
     * Update which block the furnace is using depending on whether or not it is burning
     */
    public static void updateFurnaceBlockState(boolean p_149931_0_, World p_149931_1_, int p_149931_2_, int p_149931_3_, int p_149931_4_)
    {
        int l = p_149931_1_.getBlockMetadata(p_149931_2_, p_149931_3_, p_149931_4_);
        TileEntity tileentity = p_149931_1_.getTileEntity(p_149931_2_, p_149931_3_, p_149931_4_);
        switching = true;

        if (p_149931_0_)
        {
            p_149931_1_.setBlock(p_149931_2_, p_149931_3_, p_149931_4_, EnchantableBlocks.blockFurnaceOn);
        }
        else
        {
            p_149931_1_.setBlock(p_149931_2_, p_149931_3_, p_149931_4_, EnchantableBlocks.blockFurnace);
        }

        switching = false;
        p_149931_1_.setBlockMetadataWithNotify(p_149931_2_, p_149931_3_, p_149931_4_, l, 2);

        if (tileentity != null)
        {
            tileentity.validate();
            p_149931_1_.setTileEntity(p_149931_2_, p_149931_3_, p_149931_4_, tileentity);
        }
    }
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityEnchantedFurnace();
	}
}
