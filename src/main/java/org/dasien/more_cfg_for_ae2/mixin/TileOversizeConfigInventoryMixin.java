package org.dasien.more_cfg_for_ae2.mixin;

import appeng.api.stacks.AEKey;
import org.dasien.more_cfg_for_ae2.compat.ConfiguredSlotLimitInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.glodblock.github.extendedae.common.tileentities.TileOversizeInterface$OversizeConfigInv", remap = false)
public abstract class TileOversizeConfigInventoryMixin {
    @Inject(method = "getMaxAmount", at = @At("HEAD"), cancellable = true)
    private void moreCfgForAe2$getConfiguredMaxAmount(AEKey key, CallbackInfoReturnable<Long> cir) {
        ConfiguredSlotLimitInventory inventory = (ConfiguredSlotLimitInventory) this;
        if (inventory.moreCfgForAe2$usesConfiguredSlotLimit()) {
            cir.setReturnValue(((appeng.util.ConfigInventory) (Object) this).getCapacity(key.getType()));
        }
    }
}
