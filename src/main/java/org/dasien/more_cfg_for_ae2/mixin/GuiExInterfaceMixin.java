package org.dasien.more_cfg_for_ae2.mixin;

import appeng.api.config.FuzzyMode;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.definitions.AEItems;
import appeng.menu.slot.AppEngSlot;
import com.glodblock.github.extendedae.client.button.ActionEPPButton;
import com.glodblock.github.extendedae.client.gui.GuiExInterface;
import com.glodblock.github.extendedae.container.ContainerExInterface;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.dasien.more_cfg_for_ae2.compat.ConfigurableEppInterfaceMenu;
import org.dasien.more_cfg_for_ae2.compat.InterfaceConfigHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = GuiExInterface.class, remap = false)
public abstract class GuiExInterfaceMixin extends UpgradeableScreen<ContainerExInterface> {
    @Unique
    private static final int MORE_CFG_FOR_AE2_SLOT_LEFT = 8;

    @Unique
    private static final int MORE_CFG_FOR_AE2_CONFIG_TOP = 53;

    @Unique
    private static final int MORE_CFG_FOR_AE2_STORAGE_TOP = 71;

    @Unique
    private static final int MORE_CFG_FOR_AE2_SLOT_SPACING = 18;

    @Shadow
    @Final
    private SettingToggleButton<FuzzyMode> fuzzyMode;

    @Shadow
    @Final
    private List<Button> amountButtons;

    @Shadow
    @Final
    private ActionEPPButton nextPage;

    @Shadow
    @Final
    private ActionEPPButton prePage;

    protected GuiExInterfaceMixin(ContainerExInterface menu, Inventory playerInventory, Component title,
            appeng.client.gui.style.ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "updateBeforeRender", at = @At("HEAD"), cancellable = true)
    private void moreCfgForAe2$updateConfiguredPages(CallbackInfo ci) {
        if (!(this.menu instanceof ConfigurableEppInterfaceMenu configuredMenu)) {
            return;
        }

        int maxPages = configuredMenu.moreCfgForAe2$getEppMaxPages();

        super.updateBeforeRender();
        this.fuzzyMode.set(this.menu.getFuzzyMode());
        this.fuzzyMode.setVisibility(this.menu.hasUpgrade(AEItems.FUZZY_CARD));

        int page = configuredMenu.moreCfgForAe2$getEppPage();
        this.menu.page = page;
        this.menu.showPage(page);
        this.moreCfgForAe2$showConfiguredPage(page, maxPages);
        this.moreCfgForAe2$updateAmountButtons(page);

        boolean hasMultiplePages = maxPages > 1;
        this.nextPage.setVisibility(hasMultiplePages);
        this.nextPage.active = hasMultiplePages;
        this.prePage.setVisibility(false);
        this.prePage.active = false;
        ci.cancel();
    }

    @Unique
    private void moreCfgForAe2$showConfiguredPage(int page, int maxPages) {
        for (int configuredPage = 0; configuredPage < maxPages; configuredPage++) {
            boolean hidden = configuredPage != page;
            for (int row = 0; row < InterfaceConfigHelper.EPP_ROWS_PER_PAGE; row++) {
                int top = row == 0 ? MORE_CFG_FOR_AE2_CONFIG_TOP : MORE_CFG_FOR_AE2_CONFIG_TOP + 60;
                this.moreCfgForAe2$setSlotsHidden(InterfaceConfigHelper.eppConfigSemantic(configuredPage, row),
                        hidden, top);
                top = row == 0 ? MORE_CFG_FOR_AE2_STORAGE_TOP : MORE_CFG_FOR_AE2_STORAGE_TOP + 60;
                this.moreCfgForAe2$setSlotsHidden(InterfaceConfigHelper.eppStorageSemantic(configuredPage, row),
                        hidden, top);
            }
        }
    }

    @Unique
    private void moreCfgForAe2$setSlotsHidden(appeng.menu.SlotSemantic semantic, boolean hidden, int top) {
        List<Slot> slots = this.menu.getSlots(semantic);
        for (int i = 0; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            SlotAccessor accessor = (SlotAccessor) slot;
            accessor.moreCfgForAe2$setX(hidden ? -9999 : MORE_CFG_FOR_AE2_SLOT_LEFT + i * MORE_CFG_FOR_AE2_SLOT_SPACING);
            accessor.moreCfgForAe2$setY(hidden ? -9999 : top);
            if (slot instanceof AppEngSlot appEngSlot) {
                appEngSlot.setActive(!hidden);
            }
        }
    }

    @Unique
    private void moreCfgForAe2$updateAmountButtons(int page) {
        for (Button button : this.amountButtons) {
            button.active = false;
            button.visible = false;
        }

        int visibleButtons = Math.min(this.amountButtons.size(), InterfaceConfigHelper.EPP_SLOTS_PER_PAGE);
        for (int i = 0; i < visibleButtons; i++) {
            Button button = this.amountButtons.get(i);
            Slot configSlot = this.moreCfgForAe2$getConfigSlot(page, i);
            button.visible = true;
            button.active = InterfaceConfigHelper.hasConfiguredStack(configSlot);
        }
    }

    @Unique
    private Slot moreCfgForAe2$getConfigSlot(int page, int visibleSlot) {
        int row = visibleSlot / InterfaceConfigHelper.EPP_SLOTS_PER_ROW;
        int index = visibleSlot % InterfaceConfigHelper.EPP_SLOTS_PER_ROW;
        List<Slot> slots = this.menu.getSlots(InterfaceConfigHelper.eppConfigSemantic(page, row));
        return index >= 0 && index < slots.size() ? slots.get(index) : null;
    }
}
