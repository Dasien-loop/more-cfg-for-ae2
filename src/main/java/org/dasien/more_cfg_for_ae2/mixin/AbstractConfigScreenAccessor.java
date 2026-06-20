package org.dasien.more_cfg_for_ae2.mixin;

import dev.toma.configuration.client.screen.AbstractConfigScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AbstractConfigScreen.class, remap = false)
public interface AbstractConfigScreenAccessor {
    @Accessor("index")
    int moreCfgForAe2$getIndex();
}
