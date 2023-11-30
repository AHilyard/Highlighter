package com.anthonyhilyard.highlighter;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import com.electronwill.nightconfig.core.Config;
import com.google.common.collect.Maps;

@EventBusSubscriber(modid = Loader.MODID, bus = Bus.MOD)
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

	public enum IconPosition
	{
		UpperLeft,
		UpperRight,
		LowerLeft,
		LowerRight
	}

	public final BooleanValue clearOnInventoryClose;
	public final BooleanValue clearOnHover;
	public final BooleanValue clearOnSelect;
	public final BooleanValue useItemNameColor;
	public final BooleanValue showOnHotbar;
	public final ConfigValue<IconPosition> iconPosition;

	private static Map<Pair<Item, CompoundTag>, TextColor> colorCache = Maps.newHashMap();

	public HighlighterConfig(ForgeConfigSpec.Builder build)
	{
		build.comment("Client Configuration").push("client").push("options");

		clearOnInventoryClose = build.comment(" If new item markers should be cleared when the inventory is closed.").define("clear_on_close", true);
		clearOnHover = build.comment(" If new item markers should be cleared when the item tooltip is displayed.").define("clear_on_hover", true);
		clearOnSelect = build.comment(" If new item markers should be cleared when the item is selected on the hotbar.").define("clear_on_select", true);
		useItemNameColor = build.comment(" If icons should match the color of items names (as shown in tooltips).  Otherwise icons will all be gold.").define("item_name_color", false);
		showOnHotbar = build.comment(" If new item markers should show on the hotbar.").define("show_on_hotbar", true);
		iconPosition = build.comment(" The position of new item markers.").defineEnum("icon_position", IconPosition.UpperLeft);

		build.pop().pop();
	}

	@SuppressWarnings({"removal"})
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

	@SubscribeEvent
	public static void onReload(ModConfigEvent.Reloading e)
	{
		if (e.getConfig().getModId().equals(Loader.MODID))
		{
			// Clear the color cache if the config changes.
			colorCache.clear();
		}
	}
}