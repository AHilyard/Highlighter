package com.anthonyhilyard.highlighter.mixin;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.anthonyhilyard.highlighter.Highlighter;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin
{
	@Inject(method = { "doClick(IILnet/minecraft/world/inventory/ClickType;Lnet/minecraft/world/entity/player/Player;)V" },
			at = { @At("HEAD") })
	public void doClick(final int slotIndex, final int mask, final ClickType clickType, final Player player, final CallbackInfo info)
	{
		if ((Object)this instanceof InventoryMenu)
		{
			Highlighter.itemClicked(slotIndex);
		}
	}
}