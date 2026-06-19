package org.dasien.more_cfg_for_ae2.mixin;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.menu.AEBaseMenu;
import com.almostreliable.merequester.client.abstraction.RequestDisplay;
import com.almostreliable.merequester.client.abstraction.AbstractRequesterScreen;
import com.almostreliable.merequester.client.widgets.RequestWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = AbstractRequesterScreen.class, remap = false)
public abstract class MERequesterScreenWidgetInitMixin extends AEBaseScreen<AEBaseMenu> {
    @Shadow
    @Final
    private List<RequestWidget> requestWidgets;

    @Shadow
    protected int rowAmount;

    protected MERequesterScreenWidgetInitMixin(AEBaseMenu menu, Inventory playerInventory, Component title,
            ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "drawFG", at = @At("HEAD"))
    private void moreCfgForAe2$ensureWidgetsPresent(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y,
            CallbackInfo ci) {
        if (!this.requestWidgets.isEmpty() || this.rowAmount <= 0) {
            return;
        }

        RequestDisplay host = (RequestDisplay) (Object) this;
        for (int i = 0; i < this.rowAmount; i++) {
            RequestWidget widget = new RequestWidget(host, i, 8, (i + 1) * 19, this.style);
            widget.postInit();
            this.requestWidgets.add(widget);
        }
    }
}
