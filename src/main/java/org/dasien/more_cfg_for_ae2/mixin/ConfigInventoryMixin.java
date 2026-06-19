package org.dasien.more_cfg_for_ae2.mixin;

import appeng.api.stacks.AEKey;
import appeng.util.ConfigInventory;
import org.dasien.more_cfg_for_ae2.compat.ConfiguredSlotLimitInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ConfigInventory.class, remap = false)
public abstract class ConfigInventoryMixin implements ConfiguredSlotLimitInventory {
    @Unique
    private boolean moreCfgForAe2$usesConfiguredSlotLimit;

    @Override
    public void moreCfgForAe2$setUsesConfiguredSlotLimit(boolean usesConfiguredSlotLimit) {
        this.moreCfgForAe2$usesConfiguredSlotLimit = usesConfiguredSlotLimit;
    }

    @Override
    public boolean moreCfgForAe2$usesConfiguredSlotLimit() {
        return this.moreCfgForAe2$usesConfiguredSlotLimit;
    }

    @Inject(method = "getMaxAmount", at = @At("HEAD"), cancellable = true)
    private void moreCfgForAe2$getConfiguredMaxAmount(AEKey key, CallbackInfoReturnable<Long> cir) {
        if (this.moreCfgForAe2$usesConfiguredSlotLimit) {
            cir.setReturnValue(((ConfigInventory) (Object) this).getCapacity(key.getType()));
        }
    }
}
