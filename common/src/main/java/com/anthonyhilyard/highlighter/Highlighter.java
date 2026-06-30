package com.anthonyhilyard.highlighter;

import com.anthonyhilyard.highlighter.config.HighlighterConfig;
import com.anthonyhilyard.iceberg.events.client.ItemTooltipEvent;
import com.anthonyhilyard.iceberg.events.client.NewItemPickupEvent;
import com.anthonyhilyard.iceberg.util.Easing;
import com.anthonyhilyard.iceberg.util.GuiHelper;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.ChatFormatting;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Highlighter
{
	public static final String MODID = "highlighter";
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static final Identifier NEW_ITEM_MARKS = Identifier.fromNamespaceAndPath(MODID, "textures/gui/newitemmarks.png");
	private static Set<Integer> markedSlots = new HashSet<Integer>(36);

	public static void init()
	{
		HighlighterConfig.register(HighlighterConfig.class, MODID);

		NewItemPickupEvent.EVENT.register(Highlighter::newItemPickup);
		ItemTooltipEvent.EVENT.register(Highlighter::onItemTooltip);
	}

	public static void newItemPickup(UUID uuid, ItemStack itemStack)
	{
		Minecraft mc = Minecraft.getInstance();
		Player player = mc.level.getPlayerByUUID(uuid);

		if (player != null && player.equals(mc.player))
		{
			handlePreItemPickup(player, itemStack);
		}
	}

	private static void handlePreItemPickup(Player player, ItemStack item)
	{
		// First see if there is a stack with available space in the player's inventory.
		int slot = player.getInventory().getSlotWithRemainingSpace(item);

		// If not, check for a free slot.
		if (slot == -1)
		{
			slot = player.getInventory().getFreeSlot();
		}

		// If we found a valid slot, that's the slot the item should go into.
		// We will mark that as a "new item" slot.
		if (slot != -1)
		{
			markedSlots.add(slot);
		}
	}

	public static void clearMark(final int slotIndex)
	{
		markedSlots.remove(slotIndex);
	}

	public static void itemClicked(final int slotIndex)
	{
		clearMark(slotIndex);
	}

	public static void inventoryClosed()
	{
		if (HighlighterConfig.getInstance().clearOnInventoryClose.get())
		{
			markedSlots.clear();
		}
	}

	public static void onItemTooltip(ItemStack stack, TooltipContext context, TooltipFlag flag, List<Component> lines)
	{
		if (HighlighterConfig.getInstance().clearOnHover.get())
		{
			// This event can be raised from any sort of tooltip, but we only care about item tooltips 
			// when the inventory is open, so ensure that is the case.
			Minecraft mc = Minecraft.getInstance();
			if (mc.gui.screen() != null && mc.gui.screen() instanceof AbstractContainerScreen)
			{
				AbstractContainerScreen<?> invScreen = (AbstractContainerScreen<?>)mc.gui.screen();
				Slot slot = invScreen.hoveredSlot;
				if (slot != null && slot.getItem() == stack)
				{
					clearMark(slot.getContainerSlot());
				}
			}
		}
	}

	public static void renderNewItemMark(GuiGraphicsExtractor graphics, Slot slot)
	{
		Minecraft mc = Minecraft.getInstance();
		if (!mc.player.isCreative())
		{
			if (markedSlots.contains(slot.getContainerSlot()) && slot.hasItem())
			{
				render(graphics, slot.getItem(), slot.x, slot.y);
			}
			else
			{
				// If this slot doesn't contain a item, don't display a mark.
				clearMark(slot.getContainerSlot());
			}
		}
	}

	public static void renderHotBarItemMark(int slotIndex, GuiGraphicsExtractor graphics, ItemStack item, int x, int y)
	{
		if (!HighlighterConfig.getInstance().showOnHotbar.get())
		{
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		if (!mc.player.isCreative())
		{
			if (markedSlots.contains(slotIndex))
			{
				graphics.pose().pushMatrix();
				graphics.pose().translate(0, 0);
				render(graphics, item, x, y);
				graphics.pose().popMatrix();
			}
		}
	}

	private static void render(GuiGraphicsExtractor graphics, ItemStack item, int x, int y)
	{
		if (item.isEmpty())
		{
			return;
		}

		float timeOffset = Math.abs(((Util.getMillis() % 2000) / 1000.0f) - 1.0f);

		// Default to white so the gold-colored icon isn't messed up.
		TextColor color = TextColor.fromLegacyFormat(ChatFormatting.WHITE);

		if (HighlighterConfig.getInstance().useItemNameColor.get())
		{
			// Grab the item's color.  This should match the color of the item's name in the tooltip.
			color = HighlighterConfig.getColorForItem(item, color);
		}

		graphics.pose().pushMatrix();
		graphics.pose().translate(0, -Easing.Ease(0, 1, timeOffset));

		int argbColor = 0xFF000000 | color.getValue();

		switch (HighlighterConfig.getInstance().iconPosition.get())
		{
			default:
			case UpperLeft:
				break;
			case UpperRight:
				x += 8;
				break;
			case LowerLeft:
				y += 8;
				break;
			case LowerRight:
				x += 8;
				y += 8;
				break;
		}

		float texX = HighlighterConfig.getInstance().useItemNameColor.get() ? 8 : 0;
		GuiHelper.blit(graphics, NEW_ITEM_MARKS, x, y, 8, 8, texX, 0, 8, 8, 16, 16, argbColor);

		graphics.pose().popMatrix();
	}
}
