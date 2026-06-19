package org.dasien.more_cfg_for_ae2.mixin;

import appeng.helpers.InterfaceLogic;
import appeng.helpers.InterfaceLogicHost;
import appeng.menu.SlotSemantic;
import appeng.menu.implementations.InterfaceMenu;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.slot.AppEngSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.dasien.more_cfg_for_ae2.compat.ConfigurableInterfaceHost;
import org.dasien.more_cfg_for_ae2.compat.ConfigurableInterfaceLogic;
import org.dasien.more_cfg_for_ae2.compat.ConfigurableInterfaceMenu;
import org.dasien.more_cfg_for_ae2.compat.InterfaceConfigHelper;
import org.dasien.more_cfg_for_ae2.compat.SlotSemanticsForPages;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = InterfaceMenu.class, remap = false)
public abstract class InterfaceMenuMixin extends UpgradeableMenu<InterfaceLogicHost> implements ConfigurableInterfaceMenu {
    @Unique
    private static final String MORE_CFG_FOR_AE2_ACTION_SET_PAGE = "moreCfgForAe2SetPage";

    @Shadow
    @Final
    public static MenuType<InterfaceMenu> TYPE;

    @Unique
    private int moreCfgForAe2$page;

    protected InterfaceMenuMixin(MenuType<?> menuType, int id, Inventory playerInventory, InterfaceLogicHost host) {
        super(menuType, id, playerInventory, host);
    }

    @org.spongepowered.asm.mixin.injection.Redirect(
            method = "<init>",
            at = @At(value = "INVOKE",
                    target = "Lappeng/helpers/InterfaceLogicHost;getInterfaceLogic()Lappeng/helpers/InterfaceLogic;"))
    private InterfaceLogic moreCfgForAe2$getConfiguredLogic(InterfaceLogicHost host) {
        InterfaceLogic logic = host.getInterfaceLogic();
        ((ConfigurableInterfaceLogic) logic).moreCfgForAe2$ensureConfiguredSlotCount();
        return logic;
    }

    @Redirect(
            method = "<init>",
            at = @At(value = "INVOKE",
                    target = "Lappeng/menu/implementations/InterfaceMenu;addSlot(Lnet/minecraft/world/inventory/Slot;Lappeng/menu/SlotSemantic;)Lnet/minecraft/world/inventory/Slot;"))
    private Slot moreCfgForAe2$addSlotWithConfiguredSemantic(InterfaceMenu menu, Slot slot, SlotSemantic semantic,
            MenuType<? extends InterfaceMenu> menuType, int id, Inventory playerInventory, InterfaceLogicHost host) {
        if (InterfaceConfigHelper.maxPagesForHost(host) <= 1) {
            return this.addSlot(slot, semantic);
        }

        int slotIndex = slot.getContainerSlot();
        SlotSemantic configuredSemantic = semantic == appeng.menu.SlotSemantics.CONFIG
                ? SlotSemanticsForPages.config(slotIndex)
                : semantic == appeng.menu.SlotSemantics.STORAGE
                        ? SlotSemanticsForPages.storage(slotIndex)
                        : semantic;
        return this.addSlot(slot, configuredSemantic);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void moreCfgForAe2$initializeConfiguredPage(MenuType<? extends InterfaceMenu> menuType, int id,
            Inventory playerInventory, InterfaceLogicHost host, CallbackInfo ci) {
        this.registerClientAction(MORE_CFG_FOR_AE2_ACTION_SET_PAGE, Integer.class, this::moreCfgForAe2$setPage);

        InterfaceLogic logic = host.getInterfaceLogic();
        InterfaceConfigHelper.applySlotLimit(logic, host);

        this.moreCfgForAe2$syncPageFromHost();
        if (InterfaceConfigHelper.maxPagesForHost(host) > 1) {
            this.moreCfgForAe2$showPage(this.moreCfgForAe2$page);
        }
    }

    @org.spongepowered.asm.mixin.injection.ModifyVariable(
            method = "openSetAmountMenu",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true)
    private int moreCfgForAe2$mapVisibleButtonToCurrentPage(int slot) {
        if (this.moreCfgForAe2$getMaxPages() > 1 && slot >= 0 && slot < InterfaceConfigHelper.NORMAL_SLOTS_PER_PAGE) {
            return this.moreCfgForAe2$getPage() * InterfaceConfigHelper.NORMAL_SLOTS_PER_PAGE + slot;
        }
        return slot;
    }

    @Override
    public void moreCfgForAe2$setPage(int page) {
        int clampedPage = InterfaceConfigHelper.clampPage(this.getHost(), page);
        this.moreCfgForAe2$page = clampedPage;
        if (this.getHost() instanceof ConfigurableInterfaceHost host) {
            host.moreCfgForAe2$setPage(clampedPage);
        }
        if (InterfaceConfigHelper.maxPagesForHost(this.getHost()) > 1) {
            this.moreCfgForAe2$showPage(clampedPage);
        }
    }

    @Unique
    public void moreCfgForAe2$sendPageToServer(int page) {
        this.sendClientAction(MORE_CFG_FOR_AE2_ACTION_SET_PAGE, page);
    }

    @Override
    public int moreCfgForAe2$getPage() {
        return InterfaceConfigHelper.clampPage(this.getHost(), this.moreCfgForAe2$page);
    }

    @Override
    public int moreCfgForAe2$getMaxPages() {
        return InterfaceConfigHelper.maxPagesForHost(this.getHost());
    }

    @Unique
    private void moreCfgForAe2$syncPageFromHost() {
        if (this.getHost() instanceof ConfigurableInterfaceHost host) {
            this.moreCfgForAe2$page = host.moreCfgForAe2$getPage();
        } else {
            this.moreCfgForAe2$page = InterfaceConfigHelper.clampPage(this.getHost(), this.moreCfgForAe2$page);
        }
    }

    @Unique
    private void moreCfgForAe2$showPage(int page) {
        int maxPages = InterfaceConfigHelper.maxPagesForHost(this.getHost());
        for (int configuredPage = 0; configuredPage < maxPages; configuredPage++) {
            boolean active = configuredPage == page;
            int firstSlot = configuredPage * InterfaceConfigHelper.NORMAL_SLOTS_PER_PAGE;
            this.moreCfgForAe2$setSlotsActive(SlotSemanticsForPages.config(firstSlot), active);
            this.moreCfgForAe2$setSlotsActive(SlotSemanticsForPages.storage(firstSlot), active);
        }
    }

    @Unique
    private void moreCfgForAe2$setSlotsActive(SlotSemantic semantic, boolean active) {
        List<Slot> slots = this.getSlots(semantic);
        for (Slot slot : slots) {
            if (slot instanceof AppEngSlot appEngSlot) {
                appEngSlot.setActive(active);
            }
        }
    }

}
