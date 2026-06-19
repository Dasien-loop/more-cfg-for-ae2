package org.dasien.more_cfg_for_ae2.mixin;

import com.glodblock.github.extendedae.network.packet.CUpdatePage;
import net.minecraft.world.entity.player.Player;
import org.dasien.more_cfg_for_ae2.compat.ConfigurableEppInterfaceMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CUpdatePage.class, remap = false)
public abstract class CUpdatePageMixin {
    @Shadow
    private int page;

    @Inject(method = "onMessage", at = @At("HEAD"), cancellable = true)
    private void moreCfgForAe2$cycleConfiguredPages(Player player, CallbackInfo ci) {
        if (!(player.containerMenu instanceof ConfigurableEppInterfaceMenu menu)) {
            return;
        }

        int maxPages = menu.moreCfgForAe2$getEppMaxPages();
        int currentPage = menu.moreCfgForAe2$getEppPage();
        int targetPage = maxPages <= 1 ? 0 : (currentPage + 1) % maxPages;
        menu.moreCfgForAe2$setEppPage(targetPage);
        ci.cancel();
    }
}
