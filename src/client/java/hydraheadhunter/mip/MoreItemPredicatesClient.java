package hydraheadhunter.mip;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MoreItemPredicatesClient implements ClientModInitializer {
	public static final String   MOD_ID = "mip"	;
	public static final String   EMPTY 	  = "" 				;
	public static final String[] ENCHANTMENTS_NAMES = {	"protection",		"fire_protection",		"feather_falling",	"blast_protection",	"projectile_protection",	"respiration",		"aqua_affinity",
												"thorns", 		"depth_strider",		"frost_walker",	"binding_curse",	"soul_speed",			"swift_sneak",		"sharpness",
												"smite",			"bane_of_arthropods",	"knockback", 		"fire_aspect", 	"looting", 			"sweeping_edge",	"efficiency",
												"silk_touch", 		"unbreaking", 			"fortune",		"power", 			"punch", 				"flame", 			"infinity",
												"luck_of_the_sea", 	"lure", 				"loyalty", 		"impaling", 		"riptide", 			"channeling",		"multishot",
												"quick_charge", 	"piercing", 			"density", 		"breach", 		"wind_burst", 			"mending", 		"vanishing_curse",
	};
	
	@Override
	public void onInitializeClient() {
		ModelPredicateProviderRegistry.register( Identifier.of(MOD_ID,"count")			, (stack, world, entity, seed) -> CompareStackCountToInt(stack));
		ModelPredicateProviderRegistry.register( Identifier.of(MOD_ID,"max_stack_size")		, (stack, world, entity, seed) -> CoerceInt(stack.getMaxCount()));
		ModelPredicateProviderRegistry.register( Identifier.of(MOD_ID,"count_under")		, (stack, world, entity, seed) -> CoerceInt(stack.getMaxCount()-stack.getCount()));
		
		ModelPredicateProviderRegistry.register( Identifier.of(MOD_ID,"enchantments")		, (stack, world, entity, seed) -> convertEnchantmentsToFloat(stack, true) );
		ModelPredicateProviderRegistry.register( Identifier.of(MOD_ID,"stored_enchantments")	, (stack, world, entity, seed) -> convertEnchantmentsToFloat(stack, false) );
		RegisterIndividualEnchantmentPredicates();
		
	}
	
	private static void RegisterIndividualEnchantmentPredicates(){
		for (String enchantmentName: ENCHANTMENTS_NAMES) {
			ModelPredicateProviderRegistry.register(Identifier.of(MOD_ID, enchantmentName), (stack, world, entity, seed) -> convertEnchantmentToFloat(stack, enchantmentName));
			ModelPredicateProviderRegistry.register(Identifier.of(MOD_ID, join(enchantmentName, "active")), (stack, world, entity, seed) -> convertEnchantmentToFloat     (stack, enchantmentName, true ));
			ModelPredicateProviderRegistry.register(Identifier.of(MOD_ID, join(enchantmentName, "stored")), (stack, world, entity, seed) -> convertEnchantmentToFloat     (stack, enchantmentName, false));
			ModelPredicateProviderRegistry.register(Identifier.of(MOD_ID, join(enchantmentName, "level")) , (stack, world, entity, seed) -> convertEnchantmentLevelToFloat(stack, enchantmentName       ));
		}
	}
	
	private static float CompareStackCountToInt (ItemStack stack){
		Item item = stack.getItem();
		return 0.0F;
	}
	
	private static float CoerceInt(int count){
		
		return (float) count / 10000;
	}
	
	
	
	private static float convertEnchantmentsToFloat(ItemStack stack, Boolean enchantmentsActive){
		ComponentMap components = stack.getComponents();
		ItemEnchantmentsComponent enchantments = components.get( enchantmentsActive? DataComponentTypes.ENCHANTMENTS: DataComponentTypes.STORED_ENCHANTMENTS);
		try { return enchantments.getSize() < 1 ? 0.0F : 1.0F; }
		catch (NullPointerException ignored) { return 0.0F; }
		
	}
	
	private static float convertEnchantmentToFloat(ItemStack stack, String enchantmentName){
		float activeEnchantment= convertEnchantmentToFloat(stack,enchantmentName,true);
		float inactiveEnchantment= convertEnchantmentToFloat(stack,enchantmentName,false);
		return Math.max(activeEnchantment, inactiveEnchantment);
	}
	private static float convertEnchantmentToFloat(ItemStack stack, String enchantmentName, Boolean enchantmentsActive){
		ComponentMap components = stack.getComponents();
		ItemEnchantmentsComponent enchantments = components.get( enchantmentsActive? DataComponentTypes.ENCHANTMENTS: DataComponentTypes.STORED_ENCHANTMENTS);
		
		try { assert enchantments != null; if (enchantments.getSize() < 1)  		return 0.0F; }
		catch (AssertionError | NullPointerException ignored) 				{	return 0.0F; }
		
		Set<RegistryEntry<Enchantment>> enchantmentRegistries = enchantments.getEnchantments();
		for(RegistryEntry<Enchantment> registryEntry: enchantmentRegistries){
			if (registryEntry.matchesId( Identifier.ofVanilla(enchantmentName)) && enchantments.getLevel(registryEntry) >=1 )
				return 1.0F ;
		}
		return 0.0F;
		
	}
	
	private static float convertEnchantmentLevelToFloat(ItemStack stack, String enchantmentName){
		float activeEnchantment  = convertEnchantmentLevelToFloat(stack,enchantmentName,true);
		float inactiveEnchantment= convertEnchantmentLevelToFloat(stack,enchantmentName,false);
		return Math.max(activeEnchantment, inactiveEnchantment);
	}
	private static float convertEnchantmentLevelToFloat(ItemStack stack, String enchantmentName, Boolean enchantmentsActive){
		ComponentMap components = stack.getComponents();
		ItemEnchantmentsComponent enchantments = components.get( enchantmentsActive? DataComponentTypes.ENCHANTMENTS: DataComponentTypes.STORED_ENCHANTMENTS);
		
		try { assert enchantments != null; if (enchantments.getSize() < 1)  		return 0.0F; }
		catch (AssertionError | NullPointerException ignored) 				{	return 0.0F; }
		
		Set<RegistryEntry<Enchantment>> enchantmentRegistries = enchantments.getEnchantments();
		for(RegistryEntry<Enchantment> registryEntry: enchantmentRegistries){
			if (registryEntry.matchesId( Identifier.ofVanilla(enchantmentName)))
				return (float) enchantments.getLevel(registryEntry) / 1000;
		}
		return 0.0F;
		
	}
	
	
	public static String join       ( String @NotNull ... strings){
		String toReturn = "";
		for ( String str : strings)
			if (!str.equals(EMPTY)) toReturn = str.equals(strings[0]) ? strings[0]:String.join(".", toReturn, str) ;
		return toReturn;
	}
	
}