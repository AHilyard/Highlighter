package com.anthonyhilyard.highlighter.mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.anthonyhilyard.highlighter.Highlighter;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Container.class)
public class ContainerMixin
{
	@Inject(method = { "doClick(IILnet/minecraft/inventory/container/ClickType;Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/item/ItemStack;" },
			at = { @At("HEAD") })
	public void doClick(final int slotIndex, final int mask, final ClickType clickType, final PlayerEntity player, final CallbackInfoReturnable<ItemStack> info)
	{
		if ((Object)this instanceof PlayerContainer)
		{
			Highlighter.itemClicked(slotIndex);
		}
	}
}