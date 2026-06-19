package org.dasien.more_cfg_for_ae2.mixin;

import appeng.helpers.InterfaceLogic;
import appeng.helpers.InterfaceLogicHost;
import appeng.menu.SlotSemantic;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.slot.AppEngSlot;
import com.glodblock.github.extendedae.container.ContainerExInterface;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.dasien.more_cfg_for_ae2.compat.ConfigurableEppInterfaceMenu;
import org.dasien.more_cfg_for_ae2.compat.InterfaceConfigHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ContainerExInterface.class, remap = false)
public abstract class ContainerExInterfaceMixin extends UpgradeableMenu<InterfaceLogicHost>
        implements ConfigurableEppInterfaceMenu {
    @Shadow
    @Final
    @Mutable
    private static SlotSemantic[] CONFIG_PATTERN;

    @Shadow
    @Final
    @Mutable
    private static SlotSemantic[] STORAGE_PATTERN;

    protected ContainerExInterfaceMixin(MenuType<?> menuType, int id, Inventory playerInventory,
            InterfaceLogicHost host) {
        super(menuType, id, playerInventory, host);
    }

    @Redirect(
            method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lappeng/helpers/InterfaceLogicHost;)V",
            at = @At(value = "INVOKE",
                    target = "Lappeng/helpers/InterfaceLogicHost;getInterfaceLogic()Lappeng/helpers/InterfaceLogic;"))
    private InterfaceLogic moreCfgForAe2$getConfiguredLogic(InterfaceLogicHost host) {
        InterfaceLogic logic = host.getInterfaceLogic();
        ((org.dasien.more_cfg_for_ae2.compat.ConfigurableInterfaceLogic) logic).moreCfgForAe2$ensureConfiguredSlotCount();
        InterfaceConfigHelper.applySlotLimit(logic, host);
        return logic;
    }

    @ModifyVariable(method = "openSetAmountMenu", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int moreCfgForAe2$mapVisibleAmountSlot(int slot) {
        int page = this.moreCfgForAe2$getEppPage();
        if (slot >= 0 && slot < InterfaceConfigHelper.EPP_SLOTS_PER_PAGE && page > 0) {
            return page * InterfaceConfigHelper.EPP_SLOTS_PER_PAGE + slot;
        }
        return slot;
    }

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void moreCfgForAe2$expandPatterns(CallbackInfo ci) {
        CONFIG_PATTERN = InterfaceConfigHelper.eppConfigPattern();
        STORAGE_PATTERN = InterfaceConfigHelper.eppStoragePattern();
    }

    @Inject(method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lappeng/helpers/InterfaceLogicHost;)V",
            at = @At("RETURN"))
    private void moreCfgForAe2$clampInitialPage(MenuType<?> menuType, int id, Inventory playerInventory,
            InterfaceLogicHost host, CallbackInfo ci) {
        this.moreCfgForAe2$setEppPage(this.moreCfgForAe2$getEppPage());
        this.moreCfgForAe2$showConfiguredPage(this.moreCfgForAe2$getEppPage());
    }

    @Inject(method = "showPage", at = @At("HEAD"), cancellable = true)
    private void moreCfgForAe2$showConfiguredPage(int page, CallbackInfo ci) {
        this.moreCfgForAe2$showConfiguredPage(page);
        ci.cancel();
    }

    @Inject(method = "setPage", at = @At("HEAD"), cancellable = true)
    private void moreCfgForAe2$setConfiguredPage(int page, CallbackInfo ci) {
        this.moreCfgForAe2$setEppPage(page);
        ci.cancel();
    }

    @Override
    public void moreCfgForAe2$setEppPage(int page) {
        int clampedPage = InterfaceConfigHelper.clampPage(this.getHost(), page);
        ((ContainerExInterface) (Object) this).page = clampedPage;
        if (this.getHost() instanceof com.glodblock.github.extendedae.api.IPage hostPage) {
            hostPage.setPage(clampedPage);
        }
        this.moreCfgForAe2$showConfiguredPage(clampedPage);
    }

    @Override
    public int moreCfgForAe2$getEppPage() {
        return InterfaceConfigHelper.clampPage(this.getHost(), ((ContainerExInterface) (Object) this).page);
    }

    @Override
    public int moreCfgForAe2$getEppMaxPages() {
        return InterfaceConfigHelper.maxPagesForHost(this.getHost());
    }

    private void moreCfgForAe2$showConfiguredPage(int page) {
        int maxPages = this.moreCfgForAe2$getEppMaxPages();
        for (int configuredPage = 0; configuredPage < maxPages; configuredPage++) {
            boolean active = configuredPage == page;
            for (int row = 0; row < InterfaceConfigHelper.EPP_ROWS_PER_PAGE; row++) {
                this.moreCfgForAe2$setSlotsActive(InterfaceConfigHelper.eppConfigSemantic(configuredPage, row), active);
                this.moreCfgForAe2$setSlotsActive(InterfaceConfigHelper.eppStorageSemantic(configuredPage, row), active);
            }
        }
    }

    private void moreCfgForAe2$setSlotsActive(SlotSemantic semantic, boolean active) {
        for (Slot slot : this.getSlots(semantic)) {
            if (slot instanceof AppEngSlot appEngSlot) {
                appEngSlot.setActive(active);
            }
        }
    }
}
