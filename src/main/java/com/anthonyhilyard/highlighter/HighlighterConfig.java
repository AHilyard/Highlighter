package com.anthonyhilyard.highlighter;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "highlighter")
public class HighlighterConfig implements ConfigData
{
	@ConfigEntry.Gui.Excluded
	public static HighlighterConfig INSTANCE;

	public static void init()
	{
		AutoConfig.register(HighlighterConfig.class, JanksonConfigSerializer::new);
		INSTANCE = AutoConfig.getConfigHolder(HighlighterConfig.class).getConfig();
	}

	@Comment("If new item markers should be cleared when the inventory is closed.")
	public boolean clearOnInventoryClose = true;
	@Comment("If new item markers should be cleared when the item tooltip is displayed.")
	public boolean clearOnHover = true;
	@Comment("If icons should match the color of items names (as shown in tooltips).  Otherwise icons will all be gold.")
	public boolean useItemNameColor = false;
}