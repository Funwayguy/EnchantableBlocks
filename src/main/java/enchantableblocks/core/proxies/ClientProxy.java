package enchantableblocks.core.proxies;

import net.minecraft.client.renderer.entity.RenderEnchantmentTable;
import cpw.mods.fml.client.registry.ClientRegistry;
import enchantableblocks.blocks.tile.TileEntityEnchantedChest;
import enchantableblocks.blocks.tile.TileEntityEnchantedEnchantmentTable;
import enchantableblocks.client.renderer.TileEntityEnchantedChestRenderer;

public class ClientProxy extends CommonProxy
{
	@Override
	public boolean isClient()
	{
		return true;
	}
	
	@Override
	public void registerEventHandlers()
	{
		super.registerEventHandlers();
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnchantedEnchantmentTable.class, new RenderEnchantmentTable());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnchantedChest.class, new TileEntityEnchantedChestRenderer());
	}
}
