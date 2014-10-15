package enchantableblocks.handlers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import javassist.util.proxy.MethodHandler;
import org.apache.logging.log4j.Level;
import enchantableblocks.blocks.BlockEnchantedGeneric;
import enchantableblocks.blocks.tile.TileEntityEnchantedGeneric;
import enchantableblocks.core.EnchantableBlocks;

public class BlockMethodHandler implements MethodHandler
{
	Object generic;
	/**
	 * A method stack that will only allow overriding of one of each type of method per invoke and instance.
	 * To override this and permit an extra uses of methods users must remove them off the method stack.
	 * 
	 * In the event that a user 
	 */
	public ArrayList<Method> curProcess = new ArrayList<Method>();
	
	public BlockMethodHandler(BlockEnchantedGeneric generic)
	{
		this.generic = generic;
	}
	
	public BlockMethodHandler(TileEntityEnchantedGeneric generic)
	{
		this.generic = generic;
	}

	@Override
	public Object invoke(Object object, Method method, Method proceed, Object[] args) throws Throwable
	{
		if(!curProcess.contains(method))
		{
			for(Method genMet : generic.getClass().getDeclaredMethods())
			{
				if(genMet.getName().equals(method.getName()) && SameArguments(genMet.getParameterTypes(), method.getParameterTypes()))
				{
					curProcess.add(method); // Set method has been overridden
					//EnchantableBlocks.logger.log(Level.INFO, "Proxied " + method.getName() + " in " + generic.getClass().getSimpleName());
					Object retObj =  genMet.invoke(generic, args);
					curProcess.remove(method); // Remove method from stack now that it has been completed
					return retObj;
				}
			}
			
			EnchantableBlocks.logger.log(Level.ERROR, "Unable to find connecting proxy method for " + method.getName() + " in " + object.getClass().getSimpleName() + " with " + method.getParameterTypes().length + " arguments");
		}
		
		//curProcess = ""; // Allows recursive functions can run when the overridden method calls itself
		
		if(proceed == null)
		{
			return null;
		} else
		{
			return proceed.invoke(object, args);	
		}
	}
	
	public static boolean SameArguments(Class<?>[] pramA, Class<?>[] pramB)
	{
		if(pramA.length != pramB.length)
		{
			return false;
		} else if(pramA.length == 0 && pramB.length == 0)
		{
			return true;
		}
		
		for(int i = 0; i < pramA.length && i < pramB.length; i++)
		{
			if(pramA[i] == pramB[i] || pramA[i].isAssignableFrom(pramB[i]) || pramB[i].isAssignableFrom(pramA[i]))
			{
				continue;
			} else
			{
				return false;
			}
		}
		
		return true;
	}
}
