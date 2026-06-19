package org.dasien.more_cfg_for_ae2.mixin;

import appeng.client.Point;
import appeng.client.gui.widgets.VerticalButtonBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VerticalButtonBar.class, remap = false)
public abstract class VerticalButtonBarMixin {
    @Shadow
    private Point position;

    @Inject(method = "updateBeforeRender", at = @At("HEAD"))
    private void moreCfgForAe2$defaultMissingPosition(CallbackInfo ci) {
        if (this.position == null) {
            this.position = new Point(-2, 6);
        }
    }
}
