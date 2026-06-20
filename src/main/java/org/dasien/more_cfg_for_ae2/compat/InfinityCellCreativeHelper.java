package org.dasien.more_cfg_for_ae2.compat;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.common.items.InfinityCell;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;
import org.dasien.more_cfg_for_ae2.Config;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class InfinityCellCreativeHelper {
    private static final Logger LOGGER = LogUtils.getLogger();

    private InfinityCellCreativeHelper() {
    }

    public static void addConfiguredCells(CreativeModeTab.Output output) {
        for (ItemStack stack : configuredCells()) {
            output.accept(stack);
        }
    }

    public static ItemStack createIcon() {
        List<ItemStack> cells = configuredCells();
        if (!cells.isEmpty()) {
            return cells.get(0);
        }

        InfinityCell infinityCell = EPPItemAndBlock.INFINITY_CELL;
        return infinityCell != null ? new ItemStack(infinityCell) : ItemStack.EMPTY;
    }

    private static List<ItemStack> configuredCells() {
        Config config = Config.get();
        if (!(config instanceof Config.WithExPatternProvider eppConfig)) {
            return List.of();
        }
        InfinityCell infinityCell = EPPItemAndBlock.INFINITY_CELL;
        if (infinityCell == null) {
            return List.of();
        }

        List<ItemStack> cells = new ArrayList<>();
        List<String> invalidEntries = new ArrayList<>();

        for (String entry : uniqueIds(eppConfig.infinityCells)) {
            InfinityCellRegistryTarget target = InfinityCellRegistryTarget.fromEntry(entry);
            String id = InfinityCellRegistryTarget.registryId(entry);
            if (target == InfinityCellRegistryTarget.ITEM) {
                Optional<Item> item = resolveItem(id);
                if (item.isPresent()) {
                    cells.add(infinityCell.getRecordCell(AEItemKey.of(item.get())));
                } else {
                    invalidEntries.add(entry);
                }
            } else if (target == InfinityCellRegistryTarget.FLUID) {
                Optional<Fluid> fluid = resolveFluid(id);
                if (fluid.isPresent()) {
                    cells.add(infinityCell.getRecordCell(AEFluidKey.of(fluid.get())));
                } else {
                    invalidEntries.add(entry);
                }
            } else {
                Optional<AEKey> key = resolveCompatKey(target, id);
                if (key.isPresent()) {
                    cells.add(infinityCell.getRecordCell(key.get()));
                } else {
                    invalidEntries.add(entry);
                }
            }
        }

        logInvalidEntries(invalidEntries);
        return cells;
    }

    private static Set<String> uniqueIds(String[] configuredIds) {
        Set<String> ids = new LinkedHashSet<>();
        if (configuredIds == null) {
            return ids;
        }
        for (String configuredId : configuredIds) {
            if (configuredId != null && !configuredId.isBlank()) {
                ids.add(configuredId.trim());
            }
        }
        return ids;
    }

    private static Optional<Item> resolveItem(String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null) {
            return Optional.empty();
        }

        Item item = ForgeRegistries.ITEMS.getValue(location);
        return item != null && item != Items.AIR ? Optional.of(item) : Optional.empty();
    }

    private static Optional<Fluid> resolveFluid(String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null) {
            return Optional.empty();
        }

        Fluid fluid = ForgeRegistries.FLUIDS.getValue(location);
        return fluid != null && fluid != Fluids.EMPTY ? Optional.of(fluid) : Optional.empty();
    }

    private static Optional<AEKey> resolveCompatKey(InfinityCellRegistryTarget target, String id) {
        if (target == null) {
            return Optional.empty();
        }
        return switch (target) {
            case FE -> LoadedMods.isAppliedFluxLoaded() ? AppFluxInfinityCellCompat.resolve(id) : Optional.empty();
            case MANA -> LoadedMods.isAppliedBotanicsLoaded() ? AppliedBotanicsInfinityCellCompat.resolve(id)
                    : Optional.empty();
            case GAS, INFUSE_TYPE, PIGMENT, SLURRY -> LoadedMods.isAppliedMekanisticsLoaded()
                    ? AppliedMekanisticsInfinityCellCompat.resolve(target, id) : Optional.empty();
            case SOURCE -> LoadedMods.isArsEnergistiqueLoaded() ? ArsEnergistiqueInfinityCellCompat.resolve(id)
                    : Optional.empty();
            default -> Optional.empty();
        };
    }

    private static void logInvalidEntries(List<String> invalidEntries) {
        if (!invalidEntries.isEmpty()) {
            LOGGER.warn("Skipping invalid Ex Pattern Provider infinity cell entries: {}", invalidEntries);
        }
    }
}
