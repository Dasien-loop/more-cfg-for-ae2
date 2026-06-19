package org.dasien.more_cfg_for_ae2.mixin;

import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Slot.class)
public interface SlotAccessor {
    @Accessor("x")
    void moreCfgForAe2$setX(int x);

    @Accessor("y")
    void moreCfgForAe2$setY(int y);
}
