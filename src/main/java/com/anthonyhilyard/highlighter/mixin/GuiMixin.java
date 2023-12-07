package com.anthonyhilyard.highlighter.mixin;

import com.anthonyhilyard.highlighter.Highlighter;
import com.mojang.blaze3d.vertex.PoseStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Mixin(Gui.class)
public class GuiMixin
{
	@Inject(method = "renderSlot",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", shift = Shift.AFTER))
	public void renderSlot(GuiGraphics graphics, int x, int y, float time, Player player, ItemStack item, int something, CallbackInfo info)
	{
		int index = player.getInventory().items.indexOf(item);
		Highlighter.renderHotBarItemMark(index, new PoseStack(), item, x, y);
	}
}
