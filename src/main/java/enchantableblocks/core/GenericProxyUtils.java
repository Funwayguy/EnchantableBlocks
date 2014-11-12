package enchantableblocks.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.ProxyFactory;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.common.registry.GameRegistry;
import enchantableblocks.blocks.BlockEnchantedGeneric;
import enchantableblocks.handlers.BlockMethodHandler;
import enchantableblocks.items.ItemEnchantableBlock;

public class GenericProxyUtils
{
	public static HashMap<Class, Class> PRIM_TO_WRAP;
	public static HashMap<Block, Block> blockGeneric = new HashMap<Block, Block>();
	
	public static void GenerateGenerics()
	{
		Set<String> keySet = Block.blockRegistry.getKeys();
		String[] blockKeys = keySet.toArray(new String[keySet.size()]);
		
		for(int keyIndex = 0; keyIndex < blockKeys.length; keyIndex++)
		{
			Block block = (Block)Block.blockRegistry.getObject(blockKeys[keyIndex]);
			
			if(block == null || Item.getItemFromBlock(block) == null || !Block.blockRegistry.getNameForObject(block).startsWith("minecraft:"))
			{
				continue;
			}
			
			if(Item.getItemFromBlock(block).getClass() == ItemBlock.class && !block.getMaterial().isLiquid() && !blockGeneric.containsKey(block) && !Block.blockRegistry.getNameForObject(block).startsWith(EB_Settings.modID))
			{
				ArrayList<Field> failedVars = new ArrayList<Field>();
				
				try
				{
					Block copyBlock = null;
					Block proxyBlock = null;
					
					ProxyFactory factory = new ProxyFactory();
					factory.setSuperclass(block.getClass());
					
					/*Class[] ifs = block.getClass().getInterfaces();
					ArrayList<Class> ifsArray = new ArrayList<Class>();
					
					for(Class clazz : ifs)
					{
						ifsArray.add(clazz);
					}
					ifsArray.add(ITileEntityProvider.class);
					
					factory.setInterfaces(ifsArray.toArray(new Class[ifsArray.size()]));*/
					
					factory.setInterfaces(new Class[]{ITileEntityProvider.class});
					
					MethodFilter filter = new MethodFilter()
					{
						@Override
						public boolean isHandled(Method method)
						{
							return BlockEnchantedGeneric.methodList.containsKey(method.getName()) && BlockMethodHandler.SameArguments(BlockEnchantedGeneric.methodList.get(method.getName()).getParameterTypes(), method.getParameterTypes());
						}
					};
					
					factory.setFilter(filter);
					//Class proxyClass = factory.createClass();
					
					try
					{
						copyBlock = block.getClass().newInstance();
						BlockEnchantedGeneric BEG = new BlockEnchantedGeneric(block);
						proxyBlock = (Block)factory.create(new Class[0], new Object[0], new BlockMethodHandler(BEG));
						BEG.proxyBlock = proxyBlock;
					} catch(Exception e)
					{
						Block[] bs = ForceConstructor(block, factory);
						copyBlock = bs[0];
						proxyBlock = bs[1];
						
					}
					
					if(copyBlock == null || proxyBlock == null)
					{
						EnchantableBlocks.logger.log(Level.WARN, "Failed to recreate block " + block.getLocalizedName());
						continue;
					}
					
					if(!(proxyBlock instanceof ITileEntityProvider))
					{
						EnchantableBlocks.logger.log(Level.WARN, "Unable to interface block " + proxyBlock.getLocalizedName() + " with ITileEntityProvider");
						EnchantableBlocks.logger.log(Level.WARN, "This is very bad and will basically render this copy useless, congrats with your new paperweight (o_o)");
					}
					
					Field modifiers = Field.class.getDeclaredField("modifiers");
					modifiers.setAccessible(true);
					
					if(block.getClass() != Block.class)
					{
						Class sClazz = block.getClass();
						
						while(sClazz != null && sClazz != Block.class)
						{
							for(Field field : sClazz.getDeclaredFields())
							{
								try
								{
									if(Modifier.isStatic(field.getModifiers()))
									{
										continue;
									}
									field.setAccessible(true);
									modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
									field.set(copyBlock, field.get(block));
									field.set(proxyBlock, field.get(block));
								} catch(Exception e)
								{
									failedVars.add(field);
									continue;
								}
							}
							
							sClazz = sClazz.getSuperclass();
						}
					}
					
					for(Field field : Block.class.getDeclaredFields())
					{
						try
						{
							if(Modifier.isStatic(field.getModifiers()))
							{
								continue;
							}
							field.setAccessible(true);
							modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
							
							if(field.getName().equals("isTileProvider"))
							{
								field.setBoolean(copyBlock, true);
								field.setBoolean(proxyBlock, true);
							} else
							{
								field.set(copyBlock, field.get(block));
								field.set(proxyBlock, field.get(block));
							}
						} catch(Exception e)
						{
							failedVars.add(field);
							continue;
						}
					}
					
					if(failedVars.size() > 0)
					{
						EnchantableBlocks.logger.log(Level.WARN, "Failed to recreate " + failedVars.size() + " variables for block " + block.getLocalizedName());
						
						String failed = "Failed vars: ";
						
						for(Field field : failedVars)
						{
							failed += ", " + field.getName();
						}
						EnchantableBlocks.logger.log(Level.WARN, failed);
					}
					
					proxyBlock.setCreativeTab(EnchantableBlocks.creativeTab);
					blockGeneric.put(block, proxyBlock);
					
				} catch(Exception e)
				{
					EnchantableBlocks.logger.log(Level.ERROR, "Unable to create generic for block " + block.getLocalizedName(), e);
				}
			}
		}
		
		EnchantableBlocks.logger.log(Level.INFO, "Detected " + blockGeneric.size() + " blocks capable of enchantments");
		
		Set<Block> genericSet = blockGeneric.keySet();
		Block[] genericKeys = genericSet.toArray(new Block[genericSet.size()]);
		
		for(int keyIndex = 0; keyIndex < genericKeys.length; keyIndex++)
		{
			Block block = genericKeys[keyIndex];
			Block eBlock = blockGeneric.get(block);
			
			if(Block.getIdFromBlock(eBlock) == -1)
			{
				if(Item.getItemFromBlock(eBlock) != null)
				{
					EnchantableBlocks.logger.log(Level.WARN, "Skipping block " + eBlock.getLocalizedName() + ", already has an ItemBlock!");
					continue;
				}
				
				try
				{
					String name = Block.blockRegistry.getNameForObject(block).split(":")[1] + "_enchanted";
					
					/*while(Block.blockRegistry.containsKey(EB_Settings.modID + ":" + name + "_enchanted"))
					{
						name = name + "_enchanted";
					}*/
					
					GameRegistry.registerBlock(eBlock, ItemEnchantableBlock.class, name);
					
					if(Blocks.fire.getFlammability(block) > 0)
					{
						Blocks.fire.setFireInfo(eBlock, Blocks.fire.getEncouragement(block), Blocks.fire.getFlammability(block));
					}
				} catch(Exception e)
				{
					EnchantableBlocks.logger.log(Level.ERROR, "Failed to register enchantable variation of block " + block.getLocalizedName(), e);
					continue;
				}
			}
		}
	}
	
	public static Block[] ForceConstructor(Block block, ProxyFactory factory)
	{
		Block[] copy = new Block[2];
		Constructor[] constructors = block.getClass().getDeclaredConstructors();
		
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
					objs[c] = FindConstructorObj(block, prams[c]);
				}
				copy[0] = (Block)constructors[i].newInstance(objs);
				BlockEnchantedGeneric BEG = new BlockEnchantedGeneric(block);
				copy[1] = (Block)factory.create(prams, objs, new BlockMethodHandler(BEG));
				BEG.proxyBlock = copy[1];
				
				break;
			} catch(Exception e)
			{
				continue;
			}
		}
		
		return copy;
	}
	
	public static Object FindConstructorObj(Block block, Class clazz)
	{
		if(PRIM_TO_WRAP.containsKey(clazz))
		{
			clazz = PRIM_TO_WRAP.get(clazz);
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
		
		if(block.getClass() != Block.class)
		{
			Class sClazz = block.getClass();
			
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
						Object var = f.get(block);
						
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
				Object var = f.get(block);
				
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
	
	public static Object GetWrappedValue(Object obj)
	{
		if(obj instanceof Boolean)
		{
			return ((Boolean)obj).booleanValue();
		} else if(obj instanceof Double)
		{
			return ((Double)obj).doubleValue();
		} else if(obj instanceof Float)
		{
			return ((Float)obj).floatValue();
		} else if(obj instanceof Integer)
		{
			return ((Integer)obj).intValue();
		} else if(obj instanceof Long)
		{
			return ((Long)obj).longValue();
		} else if(obj instanceof Short)
		{
			return ((Short)obj).shortValue();
		} else if(obj instanceof Byte)
		{
			return ((Byte)obj).byteValue();
		} else
		{
			return obj;
		}
	}
	
	static
	{
		PRIM_TO_WRAP = new HashMap<Class, Class>();
		PRIM_TO_WRAP.put(boolean.class, Boolean.class);
		PRIM_TO_WRAP.put(double.class, Double.class);
		PRIM_TO_WRAP.put(float.class, Float.class);
		PRIM_TO_WRAP.put(int.class, Integer.class);
		PRIM_TO_WRAP.put(long.class, Long.class);
		PRIM_TO_WRAP.put(short.class, Short.class);
		PRIM_TO_WRAP.put(byte.class, Byte.class);
	}
}
