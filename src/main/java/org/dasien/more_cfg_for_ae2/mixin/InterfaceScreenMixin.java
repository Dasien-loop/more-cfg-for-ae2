package org.dasien.more_cfg_for_ae2.mixin;

import appeng.client.gui.implementations.InterfaceScreen;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.api.config.FuzzyMode;
import appeng.core.definitions.AEItems;
import appeng.core.localization.ButtonToolTips;
import appeng.menu.SlotSemantic;
import appeng.menu.implementations.InterfaceMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.dasien.more_cfg_for_ae2.compat.ConfigurableInterfaceMenu;
import org.dasien.more_cfg_for_ae2.compat.InterfaceConfigHelper;
import org.dasien.more_cfg_for_ae2.compat.PageButton;
import org.dasien.more_cfg_for_ae2.compat.ScreenButtonHelper;
import org.dasien.more_cfg_for_ae2.compat.SlotSemanticsForPages;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = InterfaceScreen.class, remap = false)
public abstract class InterfaceScreenMixin<C extends InterfaceMenu> extends UpgradeableScreen<C> {
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
    private List<net.minecraft.client.gui.components.Button> amountButtons;

    @Shadow
    @Final
    private SettingToggleButton<FuzzyMode> fuzzyMode;

    @Unique
    private PageButton moreCfgForAe2$nextPageButton;

    protected InterfaceScreenMixin(C menu, Inventory playerInventory, Component title,
            appeng.client.gui.style.ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void moreCfgForAe2$addExtraAmountButtons(C menu, Inventory playerInventory, Component title,
            appeng.client.gui.style.ScreenStyle style, CallbackInfo ci) {
        if (!(menu instanceof ConfigurableInterfaceMenu configurableMenu) || configurableMenu.moreCfgForAe2$getMaxPages() <= 1) {
            return;
        }

        this.moreCfgForAe2$nextPageButton = new PageButton(true,
                button -> {
                    ConfigurableInterfaceMenu currentMenu = (ConfigurableInterfaceMenu) this.menu;
                    this.moreCfgForAe2$setPage((currentMenu.moreCfgForAe2$getPage() + 1)
                            % currentMenu.moreCfgForAe2$getMaxPages());
                });
        this.addToLeftToolbar(this.moreCfgForAe2$nextPageButton);

        for (int i = this.amountButtons.size(); i < InterfaceConfigHelper.NORMAL_SLOTS_PER_PAGE; i++) {
            int visibleIndex = i;
            net.minecraft.client.gui.components.Button button =
                    ScreenButtonHelper.createButton(InterfaceScreen.class, b -> menu.openSetAmountMenu(visibleIndex));
            button.setTooltip(net.minecraft.client.gui.components.Tooltip.create(ButtonToolTips.InterfaceSetStockAmount.text()));
            this.widgets.add("amtButton" + (i + 1), (net.minecraft.client.gui.components.AbstractWidget) button);
            this.amountButtons.add(button);
        }
    }

    @Inject(method = "updateBeforeRender", at = @At("HEAD"), cancellable = true)
    private void moreCfgForAe2$updateConfiguredPage(CallbackInfo ci) {
        super.updateBeforeRender();
        this.fuzzyMode.set(this.menu.getFuzzyMode());
        this.fuzzyMode.setVisibility(this.menu.hasUpgrade(AEItems.FUZZY_CARD));

        if (!(this.menu instanceof ConfigurableInterfaceMenu configurableMenu)) {
            return;
        }

        int page = configurableMenu.moreCfgForAe2$getPage();
        int maxPages = configurableMenu.moreCfgForAe2$getMaxPages();
        if (maxPages <= 1) {
            return;
        }

        this.moreCfgForAe2$showPage(page, maxPages);

        for (int i = 0; i < this.amountButtons.size(); i++) {
            int slot = page * InterfaceConfigHelper.NORMAL_SLOTS_PER_PAGE + i;
            Slot configSlot = moreCfgForAe2$getConfigSlot(slot, i);
            net.minecraft.client.gui.components.Button button = this.amountButtons.get(i);
            button.visible = maxPages > 0;
            button.active = InterfaceConfigHelper.hasConfiguredStack(configSlot);
        }

        this.moreCfgForAe2$nextPageButton.setVisibility(true);
        this.moreCfgForAe2$nextPageButton.active = true;
        ci.cancel();
    }

    @Unique
    private void moreCfgForAe2$setPage(int page) {
        if (this.menu instanceof ConfigurableInterfaceMenu configurableMenu) {
            configurableMenu.moreCfgForAe2$setPage(page);
            configurableMenu.moreCfgForAe2$sendPageToServer(page);
        }
    }

    @Unique
    private Slot moreCfgForAe2$getConfigSlot(int slot, int visibleSlot) {
        List<Slot> slots = this.menu.getSlots(SlotSemanticsForPages.config(slot));
        return visibleSlot >= 0 && visibleSlot < slots.size() ? slots.get(visibleSlot) : null;
    }

    @Unique
    private void moreCfgForAe2$showPage(int page, int maxPages) {
        for (int configuredPage = 0; configuredPage < maxPages; configuredPage++) {
            boolean hidden = configuredPage != page;
            int firstSlot = configuredPage * InterfaceConfigHelper.NORMAL_SLOTS_PER_PAGE;
            this.moreCfgForAe2$setPageSlotsHidden(SlotSemanticsForPages.config(firstSlot), hidden,
                    MORE_CFG_FOR_AE2_CONFIG_TOP);
            this.moreCfgForAe2$setPageSlotsHidden(SlotSemanticsForPages.storage(firstSlot), hidden,
                    MORE_CFG_FOR_AE2_STORAGE_TOP);
        }
    }

    @Unique
    private void moreCfgForAe2$setPageSlotsHidden(SlotSemantic semantic, boolean hidden, int top) {
        List<Slot> slots = this.menu.getSlots(semantic);
        if (slots.isEmpty()) {
            return;
        }

        for (int i = 0; i < slots.size(); i++) {
            SlotAccessor accessor = (SlotAccessor) slots.get(i);
            accessor.moreCfgForAe2$setX(hidden ? -9999 : MORE_CFG_FOR_AE2_SLOT_LEFT + i * MORE_CFG_FOR_AE2_SLOT_SPACING);
            accessor.moreCfgForAe2$setY(hidden ? -9999 : top);
        }
    }

}
