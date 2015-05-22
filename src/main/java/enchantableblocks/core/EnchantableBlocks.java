package enchantableblocks.core;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Logger;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import enchantableblocks.blocks.BlockEnchantedChest;
import enchantableblocks.blocks.BlockEnchantedEnchantmentTable;
import enchantableblocks.blocks.BlockEnchantedFurnace;
import enchantableblocks.blocks.BlockEnchantedWorkbench;
import enchantableblocks.blocks.tile.TileEntityEnchantedChest;
import enchantableblocks.blocks.tile.TileEntityEnchantedEnchantmentTable;
import enchantableblocks.blocks.tile.TileEntityEnchantedFurnace;
import enchantableblocks.blocks.tile.TileEntityEnchantedGeneric;
import enchantableblocks.blocks.tile.TileEntityEnchantedWorkbench;
import enchantableblocks.core.proxies.CommonProxy;
import enchantableblocks.enchantments.blocks.BlockEnchantment;
import enchantableblocks.enchantments.items.AuxEnchantment;
import enchantableblocks.handlers.ConfigHandler;
import enchantableblocks.items.ItemBlockEnchanter;
import enchantableblocks.items.ItemEnchantableBlock;
import enchantableblocks.items.ItemEnchantedShears;

@Mod(modid = EB_Settings.modID, name = EB_Settings.name, version = EB_Settings.version)
public class EnchantableBlocks
{
	public static Logger logger;
	
	public static Block blockEnch;
	public static Block blockChest;
	public static Block blockWorkbench;
	public static Block blockFurnace;
	public static Block blockFurnaceOn;
	
	public static Item itemShears;
	public static Item itemEnchanter;
	
	public static CreativeTabs creativeTab = new CreativeTabEnchantables("enchantableblocks");
	
	@Instance(EB_Settings.modID)
	public static EnchantableBlocks instance;
	
	@SidedProxy(clientSide = EB_Settings.proxy + ".ClientProxy", serverSide = EB_Settings.proxy + ".CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		logger = event.getModLog();
		ConfigHandler.Load(event.getSuggestedConfigurationFile());
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.registerEventHandlers();
		RegisterBlocks();
		RegisterItems();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		GenericProxyUtils.ScanForEnchantables();
		
		BlockEnchantment.Load();
		AuxEnchantment.Load();
	}
	
	public void RegisterItems()
	{
		itemShears = new ItemEnchantedShears().setUnlocalizedName("shears").setTextureName("shears");
		
		GameRegistry.registerItem(itemShears, "enchatedShears");
		
		itemEnchanter = new ItemBlockEnchanter();
		
		GameRegistry.registerItem(itemEnchanter, "block_enchanter");
		
		GameRegistry.addShapedRecipe(new ItemStack(itemEnchanter), " DE", " OD", "O  ", 'O', new ItemStack(Blocks.obsidian), 'D', new ItemStack(Items.diamond), 'E', new ItemStack(Items.ender_eye));
	}
	
	public void RegisterBlocks()
	{
		blockEnch = new BlockEnchantedEnchantmentTable().setHardness(5.0F).setResistance(2000.0F).setBlockName("enchantmentTable").setBlockTextureName("enchanting_table").setCreativeTab(creativeTab);
		blockChest = new BlockEnchantedChest(0).setHardness(2.5F).setStepSound(Block.soundTypeWood).setBlockName("chest").setCreativeTab(creativeTab);
		blockWorkbench = new BlockEnchantedWorkbench().setHardness(2.5F).setStepSound(Block.soundTypeWood).setBlockName("workbench").setBlockTextureName("crafting_table").setCreativeTab(creativeTab);
		blockFurnace = new BlockEnchantedFurnace(false).setHardness(3.5F).setStepSound(Block.soundTypePiston).setBlockName("furnace").setCreativeTab(creativeTab);
		blockFurnaceOn = new BlockEnchantedFurnace(true).setHardness(3.5F).setStepSound(Block.soundTypePiston).setLightLevel(0.875F).setBlockName("furnace");
		
		GenericProxyUtils.blockGeneric.put(Blocks.chest, blockChest);
		GenericProxyUtils.blockGeneric.put(Blocks.crafting_table, blockWorkbench);
		GenericProxyUtils.blockGeneric.put(Blocks.furnace, blockFurnace);
		GenericProxyUtils.blockGeneric.put(Blocks.lit_furnace, blockFurnaceOn);
		GenericProxyUtils.blockGeneric.put(Blocks.enchanting_table, blockEnch);
		
		GameRegistry.registerTileEntity(TileEntityEnchantedChest.class, "enchantedChest");
		GameRegistry.registerTileEntity(TileEntityEnchantedFurnace.class, "enchantedFurnace");
		GameRegistry.registerTileEntity(TileEntityEnchantedGeneric.class, "enchantedGeneric");
		GameRegistry.registerTileEntity(TileEntityEnchantedWorkbench.class, "enchantedWorkbench");
		GameRegistry.registerTileEntity(TileEntityEnchantedEnchantmentTable.class, "enchantedEnchanter");
		
		GameRegistry.registerBlock(blockEnch, ItemEnchantableBlock.class, "enchantment_table");
		GameRegistry.registerBlock(blockChest, ItemEnchantableBlock.class, "chest");
		GameRegistry.registerBlock(blockWorkbench, ItemEnchantableBlock.class, "workbench");
		GameRegistry.registerBlock(blockFurnace, ItemEnchantableBlock.class, "furnace");
		GameRegistry.registerBlock(blockFurnaceOn, ItemEnchantableBlock.class, "furnace_lit");
	}
}
