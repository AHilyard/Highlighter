package com.anthonyhilyard.highlighter.mixin;

import com.anthonyhilyard.highlighter.Highlighter;
import com.anthonyhilyard.highlighter.config.HighlighterConfig;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.DeltaTracker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Mixin(Hud.class)
public class HudMixin
{
	@Inject(method = "extractSlot",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;itemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", shift = Shift.AFTER))
	public void renderSlot(GuiGraphicsExtractor graphics, int x, int y, DeltaTracker tracker, Player player, ItemStack item, int something, CallbackInfo info)
	{
		int index = player.getInventory().getNonEquipmentItems().indexOf(item);

		// If configured to do so, clear new item marks if we've selected the slot on the hot bar.
		if (HighlighterConfig.getInstance().clearOnSelect.get() && player.getInventory().getSelectedSlot() == index)
		{
			Highlighter.clearMark(index);
		}

		Highlighter.renderHotBarItemMark(index, graphics, item, x, y);
	}
}
