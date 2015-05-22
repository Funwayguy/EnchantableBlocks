package enchantableblocks.core.proxies;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import enchantableblocks.blocks.BlockEnchantedChest;
import enchantableblocks.client.gui.GuiChest;
import enchantableblocks.client.gui.GuiCrafting;
import enchantableblocks.client.gui.GuiEnchantment;
import enchantableblocks.core.EnchantableBlocks;
import enchantableblocks.handlers.EventHandler;
import enchantableblocks.inventory.ContainerChest;
import enchantableblocks.inventory.ContainerEnchantment;
import enchantableblocks.inventory.ContainerWorkbench;

public class CommonProxy implements IGuiHandler
{
	public boolean isClient()
	{
		return false;
	}

	public void registerEventHandlers()
	{
		EventHandler handler = new EventHandler();
		MinecraftForge.EVENT_BUS.register(handler);
		FMLCommonHandler.instance().bus().register(handler);
		NetworkRegistry.INSTANCE.registerGuiHandler(EnchantableBlocks.instance, this);
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == 0)
		{
			return new ContainerEnchantment(player.inventory, world, x, y, z);
		} else if(ID == 1)
		{
			return new ContainerWorkbench(player.inventory, world, x, y, z);
		} else if(ID == 2)
		{
			IInventory invo = ((BlockEnchantedChest)EnchantableBlocks.blockChest).func_149951_m(world, x, y, z);
			return new ContainerChest(player.inventory, invo);
		}
		
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == 0)
		{
			return new GuiEnchantment(player.inventory, world, x, y, z, null);
		} else if(ID == 1)
		{
			return new GuiCrafting(player.inventory, world, x, y, z);
		} else if(ID == 2)
		{
			IInventory invo = ((BlockEnchantedChest)EnchantableBlocks.blockChest).func_149951_m(world, x, y, z);
			return new GuiChest(player.inventory, invo);
		}
		
		return null;
	}
}
