package org.dasien.more_cfg_for_ae2.compat;

import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.AEKeyTypes;
import appeng.blockentity.misc.InterfaceBlockEntity;
import appeng.helpers.InterfaceLogic;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import appeng.parts.misc.InterfacePart;
import appeng.util.ConfigMenuInventory;
import appeng.util.ConfigInventory;
import net.minecraft.world.inventory.Slot;
import org.dasien.more_cfg_for_ae2.Config;

public final class InterfaceConfigHelper {
    public static final int NORMAL_SLOTS_PER_PAGE = 9;
    public static final int MAX_NORMAL_PAGES = 10;
    public static final int EPP_SLOTS_PER_PAGE = 18;
    public static final int EPP_ROWS_PER_PAGE = 2;
    public static final int EPP_SLOTS_PER_ROW = 9;
    public static final int MAX_EPP_PAGES = 20;

    private static final String EPP_TILE_EX_INTERFACE = "com.glodblock.github.extendedae.common.tileentities.TileExInterface";
    private static final String EPP_PART_EX_INTERFACE = "com.glodblock.github.extendedae.common.parts.PartExInterface";
    private static final String EPP_TILE_OVERSIZE_INTERFACE =
            "com.glodblock.github.extendedae.common.tileentities.TileOversizeInterface";
    private static final String EPP_PART_OVERSIZE_INTERFACE =
            "com.glodblock.github.extendedae.common.parts.PartOversizeInterface";

    private static final SlotSemantic[] EPP_CONFIG_PATTERN = createEppConfigPattern();
    private static final SlotSemantic[] EPP_STORAGE_PATTERN = createEppStoragePattern();

    private InterfaceConfigHelper() {
    }

    public static int slotCountForHost(Object host, int originalSlotCount) {
        if (isNormalInterface(host)) {
            return normalPageCount() * NORMAL_SLOTS_PER_PAGE;
        }
        if (isExtendedInterface(host) || isOversizeInterface(host)) {
            return eppPageCount(host) * EPP_SLOTS_PER_PAGE;
        }
        return originalSlotCount;
    }

    public static int slotLimitForHost(Object host) {
        if (isOversizeInterface(host)) {
            return clamp(eppConfig().oversizeInterfaceSlotLimit, Config.ME_INTERFACE_DEFAULT_SLOT_LIMIT,
                    Config.OVERSIZE_INTERFACE_MAX_SLOT_LIMIT);
        }
        if (isExtendedInterface(host)) {
            return clamp(eppConfig().extendedInterfaceSlotLimit, Config.ME_INTERFACE_DEFAULT_SLOT_LIMIT,
                    Config.EXTENDED_INTERFACE_MAX_SLOT_LIMIT);
        }
        return clamp(Config.get().meInterfaceSlotLimit, Config.ME_INTERFACE_DEFAULT_SLOT_LIMIT,
                Config.ME_INTERFACE_MAX_SLOT_LIMIT);
    }

    public static int maxPagesForHost(Object host) {
        if (isNormalInterface(host)) {
            return normalPageCount();
        }
        if (isExtendedInterface(host) || isOversizeInterface(host)) {
            return eppPageCount(host);
        }
        return 1;
    }

    public static int normalPageCount() {
        return clamp(Config.get().meInterfacePages, 1, MAX_NORMAL_PAGES);
    }

    public static int eppPageCount(Object host) {
        if (isOversizeInterface(host)) {
            return clamp(eppConfig().oversizeInterfacePages, 1, Config.OVERSIZE_INTERFACE_MAX_PAGES);
        }
        if (isExtendedInterface(host)) {
            return clamp(eppConfig().extendedInterfacePages, 1, Config.EXTENDED_INTERFACE_MAX_PAGES);
        }
        return 1;
    }

    public static int eppPatternCount(Object host) {
        return eppPageCount(host) * EPP_ROWS_PER_PAGE;
    }

    public static SlotSemantic[] eppConfigPattern() {
        return EPP_CONFIG_PATTERN;
    }

    public static SlotSemantic[] eppStoragePattern() {
        return EPP_STORAGE_PATTERN;
    }

    public static SlotSemantic eppConfigSemantic(int page, int row) {
        return EPP_CONFIG_PATTERN[eppPatternIndex(page, row)];
    }

    public static SlotSemantic eppStorageSemantic(int page, int row) {
        return EPP_STORAGE_PATTERN[eppPatternIndex(page, row)];
    }

    public static void applySlotLimit(InterfaceLogic logic, Object host) {
        if (!isConfigurableInterface(host)) {
            return;
        }
        int limit = slotLimitForHost(host);
        applySlotLimit(logic.getConfig(), limit);
        applySlotLimit(logic.getStorage(), limit);
    }

    public static void applySlotLimit(ConfigInventory inventory, long limit) {
        for (AEKeyType keyType : AEKeyTypes.getAll()) {
            inventory.setCapacity(keyType, capacityForKeyType(keyType, limit));
        }
        ((ConfiguredSlotLimitInventory) inventory).moreCfgForAe2$setUsesConfiguredSlotLimit(true);
    }

    private static long capacityForKeyType(AEKeyType keyType, long itemLimit) {
        return saturatedMultiply(itemLimit, keyType.getAmountPerUnit());
    }

    private static long saturatedMultiply(long value, long multiplier) {
        if (value > 0 && multiplier > Long.MAX_VALUE / value) {
            return Long.MAX_VALUE;
        }
        return value * multiplier;
    }

    public static boolean hasConfiguredStack(Slot slot) {
        if (slot instanceof AppEngSlot appEngSlot && appEngSlot.getInventory() instanceof ConfigMenuInventory inventory) {
            return inventory.getDelegate().getStack(slot.getContainerSlot()) != null;
        }
        return slot != null && !slot.getItem().isEmpty();
    }

    public static int clampPage(Object host, int page) {
        int maxPages = Math.max(1, maxPagesForHost(host));
        if (page < 0) {
            return 0;
        }
        if (page >= maxPages) {
            return maxPages - 1;
        }
        return page;
    }

    public static String normalConfigSemanticId(int slot) {
        return slot == 0 ? "CONFIG" : "MORE_CFG_FOR_AE2_CONFIG_" + slot;
    }

    public static String normalStorageSemanticId(int slot) {
        return slot == 0 ? "STORAGE" : "MORE_CFG_FOR_AE2_STORAGE_" + slot;
    }

    public static SlotSemantic normalConfigSemantic(int slot) {
        return slot == 0 ? SlotSemantics.CONFIG : getOrRegister(normalConfigSemanticId(slot));
    }

    public static SlotSemantic normalStorageSemantic(int slot) {
        return slot == 0 ? SlotSemantics.STORAGE : getOrRegister(normalStorageSemanticId(slot));
    }

    private static SlotSemantic getOrRegister(String id) {
        SlotSemantic existing = SlotSemantics.get(id);
        return existing != null ? existing : SlotSemantics.register(id, false);
    }

    private static SlotSemantic[] createEppConfigPattern() {
        SlotSemantic[] pattern = new SlotSemantic[MAX_EPP_PAGES * EPP_ROWS_PER_PAGE];
        pattern[0] = getOrRegister("EX_1");
        pattern[1] = getOrRegister("EX_3");
        pattern[2] = getOrRegister("EX_5");
        pattern[3] = getOrRegister("EX_7");
        for (int index = 4; index < pattern.length; index++) {
            pattern[index] = getOrRegister("MORE_CFG_FOR_AE2_EX_CONFIG_" + index);
        }
        return pattern;
    }

    private static SlotSemantic[] createEppStoragePattern() {
        SlotSemantic[] pattern = new SlotSemantic[MAX_EPP_PAGES * EPP_ROWS_PER_PAGE];
        pattern[0] = getOrRegister("EX_2");
        pattern[1] = getOrRegister("EX_4");
        pattern[2] = getOrRegister("EX_6");
        pattern[3] = getOrRegister("EX_8");
        for (int index = 4; index < pattern.length; index++) {
            pattern[index] = getOrRegister("MORE_CFG_FOR_AE2_EX_STORAGE_" + index);
        }
        return pattern;
    }

    private static int eppPatternIndex(int page, int row) {
        return clamp(page, 0, MAX_EPP_PAGES - 1) * EPP_ROWS_PER_PAGE
                + clamp(row, 0, EPP_ROWS_PER_PAGE - 1);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static boolean isNormalInterface(Object host) {
        return host != null && (host.getClass() == InterfaceBlockEntity.class || host.getClass() == InterfacePart.class);
    }

    private static boolean isConfigurableInterface(Object host) {
        return isNormalInterface(host) || isExtendedInterface(host) || isOversizeInterface(host);
    }

    private static boolean isExtendedInterface(Object host) {
        return hasExactClassName(host, EPP_TILE_EX_INTERFACE) || hasExactClassName(host, EPP_PART_EX_INTERFACE);
    }

    private static boolean isOversizeInterface(Object host) {
        return hasExactClassName(host, EPP_TILE_OVERSIZE_INTERFACE)
                || hasExactClassName(host, EPP_PART_OVERSIZE_INTERFACE);
    }

    private static boolean hasExactClassName(Object host, String className) {
        return host != null && host.getClass().getName().equals(className);
    }

    private static Config.WithExPatternProvider eppConfig() {
        Config config = Config.get();
        return config instanceof Config.WithExPatternProvider eppConfig ? eppConfig : new Config.WithExPatternProvider();
    }
}
