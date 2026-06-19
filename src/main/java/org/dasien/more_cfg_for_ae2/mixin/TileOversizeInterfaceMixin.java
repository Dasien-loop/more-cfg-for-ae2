package org.dasien.more_cfg_for_ae2.mixin;

import com.glodblock.github.extendedae.common.tileentities.TileOversizeInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.dasien.more_cfg_for_ae2.compat.InterfaceConfigHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileOversizeInterface.class, remap = false)
public abstract class TileOversizeInterfaceMixin {
    @ModifyArg(
            method = "<init>",
            at = @At(value = "INVOKE",
                    target = "Lcom/glodblock/github/extendedae/common/tileentities/TileOversizeInterface$OversizeConfigInv;<init>(Lappeng/api/storage/AEKeyFilter;Lappeng/helpers/externalstorage/GenericStackInv$Mode;ILjava/lang/Runnable;Z)V"),
            index = 2)
    private int moreCfgForAe2$useConfiguredSlotCount(int originalSlotCount) {
        return InterfaceConfigHelper.slotCountForHost((TileOversizeInterface) (Object) this, originalSlotCount);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void moreCfgForAe2$applyConfiguredSlotLimit(BlockPos pos, BlockState blockState, CallbackInfo ci) {
        TileOversizeInterface host = (TileOversizeInterface) (Object) this;
        InterfaceConfigHelper.applySlotLimit(host.getInterfaceLogic(), host);
    }
}
