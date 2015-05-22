package enchantableblocks.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.init.Blocks;
import org.apache.logging.log4j.Level;
import cpw.mods.fml.common.registry.GameRegistry;
import enchantableblocks.blocks.BlockEnchantedGeneric;
import enchantableblocks.items.ItemEnchantableBlock;

public class GenericProxyUtils
{
	public static HashMap<Block, Block> blockGeneric = new HashMap<Block, Block>();
	
	public static void RegisterEnchantableVersion(Block block)
	{
		if(block == null || blockGeneric.containsKey(block) || block.getRenderType() != 0 || Block.blockRegistry.getNameForObject(block).startsWith(EB_Settings.modID) || block instanceof ITileEntityProvider)
		{
			return;
		}
		
		try
		{
			Block proxy = new BlockEnchantedGeneric(block);
			blockGeneric.put(block, GameRegistry.registerBlock(proxy, ItemEnchantableBlock.class, Block.blockRegistry.getNameForObject(block).split(":")[1] + "_enchanted"));
			Blocks.fire.setFireInfo(proxy, Blocks.fire.getEncouragement(block), Blocks.fire.getFlammability(block));
		} catch(Exception e)
		{
			EnchantableBlocks.logger.log(Level.ERROR, "Unable to register enchantable block " + block.getUnlocalizedName(), e);
		}
	}
	
	public static void ScanForEnchantables()
	{
		@SuppressWarnings("unchecked")
		Iterator<Block> iterator = Block.blockRegistry.iterator();
		
		ArrayList<Block> tmpList = new ArrayList<Block>(); // Blocks need to be stored in a temporary array to prevent a ConcurrentModificationException
		
		while(iterator.hasNext())
		{
			Block block = iterator.next();
			
			if(block != null)
			{
				tmpList.add(block);
			}
		}
		
		for(Block block : tmpList)
		{
			if(block != null)
			{
				RegisterEnchantableVersion(block);
			}
		}
	}
}
