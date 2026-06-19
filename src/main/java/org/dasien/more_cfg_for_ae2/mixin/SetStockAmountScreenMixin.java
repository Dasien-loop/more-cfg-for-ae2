package org.dasien.more_cfg_for_ae2.mixin;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.me.crafting.SetStockAmountScreen;
import appeng.client.gui.widgets.NumberEntryWidget;
import appeng.menu.implementations.SetStockAmountMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.dasien.more_cfg_for_ae2.compat.LongStockAmountMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SetStockAmountScreen.class, remap = false)
public abstract class SetStockAmountScreenMixin extends AEBaseScreen<SetStockAmountMenu> {
    @Shadow
    @Final
    private NumberEntryWidget amount;

    protected SetStockAmountScreenMixin(SetStockAmountMenu menu, Inventory playerInventory, Component title,
            appeng.client.gui.style.ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @ModifyArg(
            method = "updateBeforeRender",
            at = @At(value = "INVOKE",
                    target = "Lappeng/client/gui/widgets/NumberEntryWidget;setLongValue(J)V"))
    private long moreCfgForAe2$useLongInitialAmount(long originalAmount) {
        return ((LongStockAmountMenu) this.menu).moreCfgForAe2$getInitialAmount();
    }

    @ModifyArg(
            method = "updateBeforeRender",
            at = @At(value = "INVOKE",
                    target = "Lappeng/client/gui/widgets/NumberEntryWidget;setMaxValue(J)V"))
    private long moreCfgForAe2$useLongMaxAmount(long originalAmount) {
        return ((LongStockAmountMenu) this.menu).moreCfgForAe2$getMaxAmount();
    }

    @Inject(method = "confirm", at = @At("HEAD"), cancellable = true)
    private void moreCfgForAe2$confirmLongAmount(CallbackInfo ci) {
        this.amount.getLongValue()
                .ifPresent(amount -> ((LongStockAmountMenu) this.menu).moreCfgForAe2$confirm(amount));
        ci.cancel();
    }
}
