package com.anthonyhilyard.highlighter.mixin;

import com.anthonyhilyard.highlighter.Highlighter;
import com.mojang.blaze3d.vertex.PoseStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Mixin(Gui.class)
public class GuiMixin extends GuiComponent
{
	@Inject(method = "renderSlot(IIFLnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;I)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderAndDecorateItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;III)V", shift = Shift.AFTER),
			locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	public void renderSlot(int x, int y, float time, Player player, ItemStack item, int something, CallbackInfo info, PoseStack poseStack)
	{
		//ItemBorders.renderBorder(poseStack, item, x, y);
		int index = player.getInventory().items.indexOf(item);
		Highlighter.renderHotBarItemMark(index, new PoseStack(), item, x, y);
	}
}
