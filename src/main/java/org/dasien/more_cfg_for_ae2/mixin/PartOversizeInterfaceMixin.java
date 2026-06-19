package org.dasien.more_cfg_for_ae2.mixin;

import appeng.api.parts.IPartItem;
import com.glodblock.github.extendedae.common.parts.PartOversizeInterface;
import org.dasien.more_cfg_for_ae2.compat.InterfaceConfigHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PartOversizeInterface.class, remap = false)
public abstract class PartOversizeInterfaceMixin {
    @ModifyArg(
            method = "<init>",
            at = @At(value = "INVOKE",
                    target = "Lcom/glodblock/github/extendedae/common/parts/PartOversizeInterface$OversizeConfigInv;<init>(Lappeng/api/storage/AEKeyFilter;Lappeng/helpers/externalstorage/GenericStackInv$Mode;ILjava/lang/Runnable;Z)V"),
            index = 2)
    private int moreCfgForAe2$useConfiguredSlotCount(int originalSlotCount) {
        return InterfaceConfigHelper.slotCountForHost((PartOversizeInterface) (Object) this, originalSlotCount);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void moreCfgForAe2$applyConfiguredSlotLimit(IPartItem<?> partItem, CallbackInfo ci) {
        PartOversizeInterface host = (PartOversizeInterface) (Object) this;
        InterfaceConfigHelper.applySlotLimit(host.getInterfaceLogic(), host);
    }
}
