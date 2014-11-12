package enchantableblocks.blocks;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.logging.log4j.Level;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.ProxyFactory;
import enchantableblocks.blocks.tile.TileEntityEnchantedGeneric;
import enchantableblocks.core.EnchantableBlocks;
import enchantableblocks.core.GenericProxyUtils;
import enchantableblocks.enchantments.blocks.BlockEnchantment;
import enchantableblocks.handlers.BlockMethodHandler;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

// Any methods created within this class will automatically override ones in the proxied version of 'actualBlock'
public class BlockEnchantedGeneric extends Block implements ITileEntityProvider
{
	public static HashMap<String, Method> methodList = new HashMap<String, Method>();
	public HashMap<String, NBTTagList> enchCache = new HashMap<String, NBTTagList>();
	
	/**
	 * The original block the proxied version copied
	 */
	public Block actualBlock;
	public Block proxyBlock;
	
	public BlockEnchantedGeneric(Block block)
	{
		super(block.getMaterial());
		actualBlock = block;
	}
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_, int p_149668_4_)
    {
        float f = 0.125F;
        AxisAlignedBB bounds = proxyBlock.getCollisionBoundingBoxFromPool(p_149668_1_, p_149668_2_, p_149668_3_, p_149668_4_);
        
        if(bounds == null)
        {
        	return null;
        } else
        {
        	return bounds.contract(f, f, f);
        }
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
        float resist = proxyBlock.getExplosionResistance(par1Entity, world, x, y, z, explosionX, explosionY, explosionZ);
        
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
    					if(lvl >= BlockEnchantment.blastResist.getMaxLevel())
    					{
    						resist = 2000F;
    					} else
    					{
    						resist += 12F * ((float)lvl/(float)BlockEnchantment.blastResist.getMaxLevel());
    					}
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
        int chance =  Blocks.fire.getFlammability(proxyBlock);
        
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
	
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        ArrayList<ItemStack> ret = actualBlock.getDrops(world, x, y, z, metadata, fortune);
        
        Iterator<ItemStack> iterator = ret.iterator();
        
        while(iterator.hasNext())
        {
        	ItemStack stack = iterator.next();
        	
        	if(stack.getItem() == Item.getItemFromBlock(this))
        	{
        		NBTTagList tagList = enchCache.get(x + "," + y + "," + z);
        		
        		for(int i = 0; i < tagList.tagCount(); i++)
    			{
    				NBTTagCompound enchTag = tagList.getCompoundTagAt(i);
    				short id = enchTag.getShort("id");
    				short lvl = enchTag.getShort("lvl");
    				
    				stack.addEnchantment(Enchantment.enchantmentsList[id], lvl);
    			}
        		
        		enchCache.remove(x + "," + y + "," + z);
        		
        		break;
        	}
        }
        
        return ret;
    }
	
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
		TileEntity tile = world.getTileEntity(x, y, z);
		
		if(tile != null)
    	{
    		NBTTagCompound tags = new NBTTagCompound();
    		tile.writeToNBT(tags);
    		
    		if(tags.hasKey("ench") && tags.getTagList("ench", 10).tagCount() > 0)
    		{
    			NBTTagList tagList = tags.getTagList("ench", 10);
    			this.enchCache.put(x + "," + y + "," + z, tagList);
    		} else if(this.enchCache.containsKey(x + "," + y + "," + z))
    		{
    			this.enchCache.remove(this.enchCache.get(x + "," + y + "," + z));
    		}
    	}
		
		proxyBlock.breakBlock(world, x, y, z, block, meta);
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
		TileEntityEnchantedGeneric tileGeneric = new TileEntityEnchantedGeneric(null);
		
		boolean flag = false;
		
		if(actualBlock != null && actualBlock instanceof ITileEntityProvider)
		{
			flag = true;
		} else if(actualBlock == null)
		{
			EnchantableBlocks.logger.log(Level.ERROR, "Block " + this.getLocalizedName() + " has no actual block to create Tile Entity!");
			return null;
		}
		
		if(flag)
		{
			TileEntity tile = ((ITileEntityProvider)proxyBlock).createNewTileEntity(world, meta);
			
			ProxyFactory factory = new ProxyFactory();
			factory.setSuperclass(tile.getClass());
			
			MethodFilter filter = new MethodFilter()
			{
				@Override
				public boolean isHandled(Method method)
				{
					return (method.getName().equals("readFromNBT") || method.getName().equals("writeToNBT"));
				}
			};
			
			factory.setFilter(filter);
			
			try
			{
				TileEntity proxyTile = ForceConstructor(tile, factory);
				
				if(proxyTile == null)
				{
					throw new Exception();
				}
				
				return proxyTile;
			} catch(Exception e)
			{
				EnchantableBlocks.logger.log(Level.ERROR, "Failed to created proxied tile entity for " + proxyBlock.getLocalizedName(), e);
				return null;
			}
		} else
		{
			return tileGeneric;
		}
	}
	
	public static TileEntity ForceConstructor(TileEntity tile, ProxyFactory factory)
	{
		TileEntity copy = null;
		Constructor[] constructors = tile.getClass().getDeclaredConstructors();
		
		for(int i = 0; i < constructors.length; i++)
		{
			constructors[i].setAccessible(true);
			Class[] prams = constructors[i].getParameterTypes();
			Object[] objs = new Object[prams.length];
			
			try
			{
				for(int c = 0; c < prams.length; c++)
				{
					// Even if this returns the wrong parameters it will be replaced later
					// It's only required to instantiate the object with non-null parameters
					objs[c] = FindConstructorObj(tile, prams[c]);
				}
				copy = (TileEntity)factory.create(prams, objs, new BlockMethodHandler(new TileEntityEnchantedGeneric(tile)));
				
				break;
			} catch(Exception e)
			{
				continue;
			}
		}
		
		return copy;
	}
	
	public static Object FindConstructorObj(TileEntity tile, Class clazz)
	{
		if(GenericProxyUtils.PRIM_TO_WRAP.containsKey(clazz))
		{
			clazz = GenericProxyUtils.PRIM_TO_WRAP.get(clazz);
		}
		
		Field modifiers;
		try
		{
			modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);
		} catch(Exception e1)
		{
			EnchantableBlocks.logger.log(Level.ERROR, "Unable to set modifiers to accessible");
			return null;
		}
		
		String failed = "Checked vars: ";
		
		if(tile.getClass() != TileEntity.class)
		{
			Class sClazz = tile.getClass();
			
			while(sClazz != null && sClazz != Block.class)
			{
				for(Field f : sClazz.getDeclaredFields())
				{
					try
					{
						if(Modifier.isStatic(f.getModifiers()))
						{
							continue;
						}
						
						f.setAccessible(true);
						modifiers.setInt(f, f.getModifiers() & ~Modifier.FINAL);
						Object var = f.get(tile);
						
						if(var != null && clazz.isAssignableFrom(var.getClass()))
						{
							return var;
						} else
						{
							failed += ", " + var.getClass().getSimpleName();
						}
					} catch(Exception e)
					{
						continue;
					}
				}
				
				sClazz = sClazz.getSuperclass();
			}
		}
		
		Field[] fields = Block.class.getDeclaredFields();
		
		for(Field f : fields)
		{
			try
			{
				if(Modifier.isStatic(f.getModifiers()))
				{
					continue;
				}
				
				f.setAccessible(true);
				modifiers.setInt(f, f.getModifiers() & ~Modifier.FINAL);
				Object var = f.get(tile);
				
				if(var != null && clazz.isAssignableFrom(var.getClass()))
				{
					return var;
				} else
				{
					failed += ", " + var.getClass().getSimpleName();
				}
			} catch(Exception e)
			{
				continue;
			}
		}
		
		EnchantableBlocks.logger.log(Level.ERROR, "Unable to locate variable type " + clazz.getSimpleName());
		EnchantableBlocks.logger.log(Level.ERROR, failed);
		return null;
	}
	
	static
	{
		Method[] declaredMethods = BlockEnchantedGeneric.class.getDeclaredMethods();
		
		for(Method method : declaredMethods)
		{
			methodList.put(method.getName(), method);
		}
	}
}
