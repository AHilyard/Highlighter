package com.anthonyhilyard.highlighter;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.IConfigEvent;

import com.electronwill.nightconfig.core.Config;

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

	public HighlighterConfig(ForgeConfigSpec.Builder build)
	{
		build.comment("Client Configuration").push("client").push("options");

		clearOnInventoryClose = build.comment(" If new item markers should be cleared when the inventory is closed.").define("clear_on_close", true);
		clearOnHover = build.comment(" If new item markers should be cleared when the item tooltip is displayed.").define("clear_on_hover", true);
		useItemNameColor = build.comment(" If icons should match the color of items names (as shown in tooltips).  Otherwise icons will all be gold.").define("item_name_color", false);
		showOnHotbar = build.comment(" If new item markers should show on the hotbar.").define("show_on_hotbar", true);

		build.pop().pop();
	}

	@SubscribeEvent
	public static void onLoad(IConfigEvent e)
	{
	}
}