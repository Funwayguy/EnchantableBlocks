package enchantableblocks.enchantments.blocks;

import java.util.ArrayList;
import enchantableblocks.core.EB_Settings;
import enchantableblocks.core.GenericProxyUtils;
import enchantableblocks.items.ItemEnchantableBlock;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class BlockEnchantment extends Enchantment
{
	public static BlockEnchantment packingPro;
	public static BlockEnchantment overClock;
	public static BlockEnchantment retention;
	public static BlockEnchantment solar;
	public static BlockEnchantment doubleUp;
	public static BlockEnchantment efficiency;
	public static BlockEnchantment thorns;
	public static BlockEnchantment owned;
	public static BlockEnchantment flames;
	public static BlockEnchantment hype;
	public static BlockEnchantment gabe;
	public static BlockEnchantment blastResist;
	public static BlockEnchantment fireProof;
	
	public ArrayList<ItemBlock> allowedBlocks = new ArrayList<ItemBlock>();
	
	public BlockEnchantment(int id, int weight)
	{
		super(id, weight, EnumEnchantmentType.all);
	}
	
	public static Block GetEnchantedBlock(Block block)
	{
		if(GenericProxyUtils.blockGeneric.containsKey(block))
		{
			return GenericProxyUtils.blockGeneric.get(block);
		} else
		{
			return block;
		}
	}
	@Override
    public boolean canApply(ItemStack stack)
    {
        return stack.getItem() instanceof ItemEnchantableBlock && (allowedBlocks.size() == 0 || allowedBlocks.contains(stack.getItem()));
    }
	
	public static void Load()
	{
		packingPro = new BlockEnchantPacking(EB_Settings.enchantPacking, 10); 		// More storage space
		overClock = new BlockEnchantOverclock(EB_Settings.enchantOverclock, 5); 	// Faster smelting rate
		retention = new BlockEnchantRetention(EB_Settings.enchantRetention, 10); 	// Chests keep their inventory
		solar = new BlockEnchantSolar(EB_Settings.enchantSolar, 3); 				// Furnace powered by the sun
		doubleUp = new BlockEnchantDoubleUp(EB_Settings.enchantDoubleUp, 3); 		// Chance of double output
		efficiency = new BlockEnchantEfficiency(EB_Settings.enchantEfficiency, 5); // Less fuel consumption / chance of returned item
		thorns = new BlockEnchantThorns(EB_Settings.enchantThorns, 1); 				// Block does damage on contact
		owned = new BlockEnchantOwned(EB_Settings.enchantOwnership, 5); 			// Deny block access to non-owners
		flames = new BlockEnchantFlames(EB_Settings.enchantFireFlower, 1); 			// Sets fire to entities on contact
		hype = new BlockEnchantHype(EB_Settings.enchantHype, 3); 					// Chance to hype up surrounding players on crafting/smelting
		gabe = new BlockEnchantGabe(EB_Settings.enchantGabe, 3); 					// Reduces enchanting cost by 10% per level (up to 70%)
		blastResist = new BlockEnchantBlastResist(EB_Settings.enchantBlastResist, 5); 	// Increases resistance to explosions
		fireProof = new BlockEnchantFireProof(EB_Settings.enchantFireProof, 5); 		// Prevents block from burning
	}
	
	public void DoEffect(Entity entity, EnchantEventType type, int amp, Object... auxObj) {}
	
	public static enum EnchantEventType
	{
		BREAK,
		TOUCH,
		INTERACT,
		GUI
	}
}
