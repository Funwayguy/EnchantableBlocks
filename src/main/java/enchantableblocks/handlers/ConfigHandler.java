package enchantableblocks.handlers;

import java.io.File;
import enchantableblocks.core.EB_Settings;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler
{
	static String catBloEnch = "Block Enchantment IDs";
	static String catEntEnch = "Entity Enchantment IDs";
	static String catAuxEnch = "Auxilary Enchantment IDs";
	
	public static void Load(File file)
	{
		Configuration config = new Configuration(file);
		
		config.load();
		
		EB_Settings.enchantDoubleUp = config.get(catBloEnch, "Double-Up", EB_Settings.enchantDoubleUp).getInt();
		EB_Settings.enchantEfficiency = config.get(catBloEnch, "Efficiency", EB_Settings.enchantEfficiency).getInt();
		EB_Settings.enchantFireFlower = config.get(catBloEnch, "Fire Aspect", EB_Settings.enchantFireFlower).getInt();
		EB_Settings.enchantFireProof = config.get(catBloEnch, "Fire Proof", EB_Settings.enchantFireProof).getInt();
		EB_Settings.enchantHype = config.get(catBloEnch, "Hype", EB_Settings.enchantHype).getInt();
		EB_Settings.enchantOverclock = config.get(catBloEnch, "Overclock", EB_Settings.enchantOverclock).getInt();
		EB_Settings.enchantOverheat = config.get(catBloEnch, "Overheat", EB_Settings.enchantOverheat).getInt();
		EB_Settings.enchantOwnership = config.get(catBloEnch, "Owned", EB_Settings.enchantOwnership).getInt();
		EB_Settings.enchantPacking = config.get(catBloEnch, "Packing Pro", EB_Settings.enchantPacking).getInt();
		EB_Settings.enchantRetention = config.get(catBloEnch, "Retention", EB_Settings.enchantRetention).getInt();
		EB_Settings.enchantSolar = config.get(catBloEnch, "Solar", EB_Settings.enchantSolar).getInt();
		EB_Settings.enchantThorns = config.get(catBloEnch, "Thorns", EB_Settings.enchantThorns).getInt();
		EB_Settings.enchantGabe = config.get(catBloEnch, "Gabe", EB_Settings.enchantGabe).getInt();
		EB_Settings.enchantBlastResist = config.get(catBloEnch, "Blast Resist", EB_Settings.enchantBlastResist).getInt();
		
		EB_Settings.enchantThreading = config.get(catAuxEnch, "Threading", EB_Settings.enchantThreading).getInt();
		EB_Settings.enchantTapestry = config.get(catAuxEnch, "Tapestry", EB_Settings.enchantTapestry).getInt();
		EB_Settings.enchantFortune = config.get(catAuxEnch, "Fortune", EB_Settings.enchantFortune).getInt();
		
		config.save();
	}
}
