package org.dasien.more_cfg_for_ae2.compat;

import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;

public final class SlotSemanticsForPages {
    public static final SlotSemantic[] CONFIG_PAGE = new SlotSemantic[InterfaceConfigHelper.MAX_NORMAL_PAGES];
    public static final SlotSemantic[] STORAGE_PAGE = new SlotSemantic[InterfaceConfigHelper.MAX_NORMAL_PAGES];

    static {
        for (int slot = 1; slot < InterfaceConfigHelper.MAX_NORMAL_PAGES * InterfaceConfigHelper.NORMAL_SLOTS_PER_PAGE; slot++) {
            InterfaceConfigHelper.normalConfigSemantic(slot);
            InterfaceConfigHelper.normalStorageSemantic(slot);
        }

        CONFIG_PAGE[0] = SlotSemantics.CONFIG;
        STORAGE_PAGE[0] = SlotSemantics.STORAGE;
        for (int page = 1; page < CONFIG_PAGE.length; page++) {
            int firstSlot = page * InterfaceConfigHelper.NORMAL_SLOTS_PER_PAGE;
            CONFIG_PAGE[page] = InterfaceConfigHelper.normalConfigSemantic(firstSlot);
            STORAGE_PAGE[page] = InterfaceConfigHelper.normalStorageSemantic(firstSlot);
        }
    }

    public static SlotSemantic config(int slot) {
        int page = Math.max(0, slot / InterfaceConfigHelper.NORMAL_SLOTS_PER_PAGE);
        return page < CONFIG_PAGE.length ? CONFIG_PAGE[page] : InterfaceConfigHelper.normalConfigSemantic(slot);
    }

    public static SlotSemantic storage(int slot) {
        int page = Math.max(0, slot / InterfaceConfigHelper.NORMAL_SLOTS_PER_PAGE);
        return page < STORAGE_PAGE.length ? STORAGE_PAGE[page] : InterfaceConfigHelper.normalStorageSemantic(slot);
    }

    private SlotSemanticsForPages() {
    }
}
