package org.dasien.more_cfg_for_ae2.mixin;

import appeng.client.gui.AEBaseScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = AEBaseScreen.class, remap = false)
public interface AEBaseScreenAccessor {
    @Invoker("switchToScreen")
    void moreCfgForAe2$switchToScreen(AEBaseScreen<?> screen);
}
