package enchantableblocks.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import enchantableblocks.blocks.tile.TileEntityEnchantedChest;
import enchantableblocks.core.EnchantableBlocks;
import enchantableblocks.enchantments.blocks.BlockEnchantment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockEnchantedChest extends BlockChest
{
	public HashMap<String, NBTTagList> enchCache = new HashMap<String, NBTTagList>();
	public HashMap<String, NBTTagList> itemCache = new HashMap<String, NBTTagList>();
	public HashMap<String, ItemStack[]> invCache = new HashMap<String, ItemStack[]>();
	
	public BlockEnchantedChest(int powered)
	{
		super(powered + 2); //Must be offset to avoid attaching to normal types.
	}
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        float f = 0.125F;
        AxisAlignedBB bounds = super.getCollisionBoundingBoxFromPool(p_149668_1_, p_149668_2_, p_149668_3_, p_149668_4_);
        return bounds.contract(f, f, f);
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

    /**
     * Called upon block activation (right click on the block.)
     */
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        if (world.isRemote)
        {
            return true;
        }
        else
        {
            IInventory iinventory = this.func_149951_m(world, x, y, z);

            if (iinventory != null)
            {
                player.openGui(EnchantableBlocks.instance, 2, world, x, y, z);
            }

            return true;
        }
    }
	
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        ArrayList<ItemStack> ret = super.getDrops(world, x, y, z, metadata, fortune);
        boolean flag = true;
        Iterator<ItemStack> iterator = ret.iterator();
        
        while(iterator.hasNext())
        {
        	ItemStack stack = iterator.next();
        	
        	if(stack.getItem() == Item.getItemFromBlock(this) && enchCache.containsKey(x + "," + y + "," + z))
        	{
        		NBTTagList tagList = enchCache.get(x + "," + y + "," + z);
        		
        		for(int i = 0; i < tagList.tagCount(); i++)
    			{
    				NBTTagCompound enchTag = tagList.getCompoundTagAt(i);
    				short id = enchTag.getShort("id");
    				short lvl = enchTag.getShort("lvl");
    				
    				stack.addEnchantment(Enchantment.enchantmentsList[id], lvl);
    				
    				if(id == BlockEnchantment.retention.effectId && itemCache.containsKey(x + "," + y + "," + z))
    				{
    					flag = false;
    					stack.getTagCompound().setTag("Items", itemCache.get(x + "," + y + "," + z));
    					itemCache.remove(x + "," + y + "," + z);
    					invCache.remove(x + "," + y + "," + z);
    				}
    			}
        		
        		enchCache.remove(x + "," + y + "," + z);
        		
        		break;
        	}
        }
        
        if(flag && invCache.containsKey(x + "," + y + "," + z))
        {
        	for(ItemStack stack : invCache.get(x + "," + y + "," + z))
        	{
        		ret.add(stack);
        	}
        	
        	invCache.remove(x + "," + y + "," + z);
        }
        
        return ret;
    }
	
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
		TileEntityEnchantedChest tile = (TileEntityEnchantedChest)world.getTileEntity(x, y, z);
		
		if(tile != null)
    	{
    		NBTTagCompound tags = new NBTTagCompound();
    		tile.writeToNBT(tags);
    		
    		if(tags.hasKey("ench") && tags.getTagList("ench", 10).tagCount() > 0)
    		{
    			NBTTagList tagList = tags.getTagList("ench", 10);
    			enchCache.put(x + "," + y + "," + z, tagList);
    			
    			for(int i = 0; i < tagList.tagCount(); i++)
    			{
    				NBTTagCompound enchTag = tagList.getCompoundTagAt(i);
    				short id = enchTag.getShort("id");
    				
    				if(id == BlockEnchantment.retention.effectId)
    				{
    					//stack.getTagCompound().setTag("Items", tags.getTagList("Items", 10)); // Transfers inventory to item's NBT
    					itemCache.put(x + "," + y + "," + z, tags.getTagList("Items", 10)); // Transfers inventory to itemCache
    					invCache.put(x + "," + y + "," + z, tile.enchChestContents);
    					tags.removeTag("Items"); // Delete items from memory
    					tile.enchChestContents = new ItemStack[63]; // Delete items from inventory
    				}
    			}
    		} else if(this.enchCache.containsKey(x + "," + y + "," + z))
    		{
    			this.enchCache.remove(this.enchCache.get(x + "," + y + "," + z));
    		}
    	}
		
		super.breakBlock(world, x, y, z, block, meta);
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
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
        TileEntityEnchantedChest tileentitychest = new TileEntityEnchantedChest();
        return tileentitychest;
	}
}
