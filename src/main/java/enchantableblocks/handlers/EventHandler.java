package enchantableblocks.handlers;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import enchantableblocks.core.EB_Settings;
import enchantableblocks.core.EnchantableBlocks;
import enchantableblocks.enchantments.blocks.BlockEnchantment;

public class EventHandler
{
	@SubscribeEvent
	public void onInteract(PlayerInteractEvent event)
	{
		if((event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK || event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) && !event.world.isRemote && !event.entityPlayer.capabilities.isCreativeMode)
		{
			TileEntity tile = event.world.getTileEntity(event.x, event.y, event.z);
			
			if(tile != null)
			{
				NBTTagCompound tags = new NBTTagCompound();
				tile.writeToNBT(tags);
				
				if(tags.hasKey("ench"))
				{
	    			NBTTagList tagList = tags.getTagList("ench", 10);
	    			String owner = "";
	    			
	    			for(int i = 0; i < tagList.tagCount(); i++)
	    			{
	    				NBTTagCompound enchTag = tagList.getCompoundTagAt(i);
	    				short id = enchTag.getShort("id");
	    				short lvl = enchTag.getShort("lvl");
	    				
	    				if(Enchantment.enchantmentsList[id] instanceof BlockEnchantment)
	    				{
	    					((BlockEnchantment)Enchantment.enchantmentsList[id]).DoEffect(event.entityPlayer, BlockEnchantment.EnchantEventType.INTERACT, lvl, new Object[0]);
	    				}
	    				
	    				if(id == BlockEnchantment.owned.effectId)
	    				{
	    					owner = enchTag.getString("owner");
	    				}
	    			}
	    			
	    			if(!owner.equals("") && !owner.equals(event.entityPlayer.getCommandSenderName()))
	    			{
    					event.entityPlayer.addChatComponentMessage(new ChatComponentText("This block belongs to " + owner));
    					event.setCanceled(true);
    					return;
	    			}
				}
			}
		}
		
		if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && (event.world.getBlock(event.x, event.y, event.z) == Blocks.enchanting_table || event.world.getBlock(event.x, event.y, event.z) == EnchantableBlocks.blockEnch) && !event.world.isRemote)
		{
			event.entityPlayer.openGui(EB_Settings.modID, 0, event.world, event.x, event.y, event.z);
			event.setCanceled(true);
			return;
		}
	}
}
