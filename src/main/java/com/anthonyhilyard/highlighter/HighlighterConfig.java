package com.anthonyhilyard.highlighter;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.fml.config.ModConfig;

import com.electronwill.nightconfig.core.Config;
import com.google.common.collect.Maps;

import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;

public class HighlighterConfig
{
	public static final ForgeConfigSpec SPEC;
	public static final HighlighterConfig INSTANCE;
	static
	{
		Config.setInsertionOrderPreserved(true);
		Pair<HighlighterConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(HighlighterConfig::new);
		SPEC = specPair.getRight();
		INSTANCE = specPair.getLeft();
	}

	public final BooleanValue clearOnInventoryClose;
	public final BooleanValue clearOnHover;
	public final BooleanValue useItemNameColor;
	public final BooleanValue showOnHotbar;

	private static Map<Pair<Item, CompoundTag>, TextColor> colorCache = Maps.newHashMap();

	public HighlighterConfig(ForgeConfigSpec.Builder build)
	{
		ModConfigEvents.reloading(Loader.MODID).register(HighlighterConfig::onReload);

		build.comment("Client Configuration").push("client").push("options");

		clearOnInventoryClose = build.comment(" If new item markers should be cleared when the inventory is closed.").define("clear_on_close", true);
		clearOnHover = build.comment(" If new item markers should be cleared when the item tooltip is displayed.").define("clear_on_hover", true);
		useItemNameColor = build.comment(" If icons should match the color of items names (as shown in tooltips).  Otherwise icons will all be gold.").define("item_name_color", false);
		showOnHotbar = build.comment(" If new item markers should show on the hotbar.").define("show_on_hotbar", true);

		build.pop().pop();
	}

	@SuppressWarnings({"deprecation", "removal"})
	public static TextColor getColorForItem(ItemStack itemStack, TextColor defaultColor)
	{
		Pair<Item, CompoundTag> key = Pair.of(itemStack.getItem(), itemStack.getTag());
		if (!colorCache.containsKey(key))
		{
			TextColor color = com.anthonyhilyard.iceberg.util.ItemColor.getColorForItem(itemStack, defaultColor);
			colorCache.put(key, color);
		}

		return colorCache.get(key);
	}

	public static void onReload(ModConfig config)
	{
		if (config.getModId().equals(Loader.MODID))
		{
			// Clear the color cache if the config changes.
			colorCache.clear();
		}
	}
}