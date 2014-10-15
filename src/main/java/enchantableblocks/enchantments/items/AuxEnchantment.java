package enchantableblocks.enchantments.items;

import java.util.ArrayList;
import java.util.HashMap;
import enchantableblocks.core.EB_Settings;
import enchantableblocks.core.EnchantableBlocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AuxEnchantment extends Enchantment
{
	public static ArrayList<Item> compatibleItems = new ArrayList<Item>();
	public static HashMap<Item, Item> conversions = new HashMap<Item, Item>();
	
	public static AuxEnchantment shearFortune;
	public static AuxEnchantment threading;
	public static AuxEnchantment tapestry;
	
	public AuxEnchantment(int id, int weight)
	{
		super(id, weight, EnumEnchantmentType.all);
	}
	
	public static Item GetEnchantedItem(Item item)
	{
		if(conversions.containsKey(item))
		{
			return conversions.get(item);
		} else
		{
			return item;
		}
	}
	
	public static void Load()
	{
		shearFortune = new ShearEnchantFortune(EB_Settings.enchantFortune, 5);
		threading = new ShearEnchantThreading(EB_Settings.enchantThreading, 5);
		tapestry = new ShearEnchantTapestry(EB_Settings.enchantTapestry, 5);
		
		compatibleItems.add(Items.shears);
		compatibleItems.add(EnchantableBlocks.itemShears);
		conversions.put(Items.shears, EnchantableBlocks.itemShears);
	}

    public boolean canApply(ItemStack stack)
    {
        return compatibleItems.contains(stack.getItem());
    }
}
