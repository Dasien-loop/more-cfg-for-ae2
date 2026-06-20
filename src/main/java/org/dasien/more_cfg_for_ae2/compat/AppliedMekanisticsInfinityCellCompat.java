package org.dasien.more_cfg_for_ae2.compat;

import appeng.api.stacks.AEKey;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class AppliedMekanisticsInfinityCellCompat {
    private AppliedMekanisticsInfinityCellCompat() {
    }

    public static Optional<AEKey> resolve(InfinityCellRegistryTarget target, String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null) {
            return Optional.empty();
        }
        return createStack(target, location).map(MekanismKey::of);
    }

    public static List<InfinityCellRegistryEntry> entries(InfinityCellRegistryTarget target) {
        List<InfinityCellRegistryEntry> entries = new ArrayList<>();
        switch (target) {
            case GAS -> MekanismAPI.gasRegistry().getValues().forEach(gas -> addEntry(entries, target, gas));
            case INFUSE_TYPE -> MekanismAPI.infuseTypeRegistry().getValues().forEach(type -> addEntry(entries, target, type));
            case PIGMENT -> MekanismAPI.pigmentRegistry().getValues().forEach(pigment -> addEntry(entries, target, pigment));
            case SLURRY -> MekanismAPI.slurryRegistry().getValues().forEach(slurry -> addEntry(entries, target, slurry));
            default -> {
            }
        }
        return entries;
    }

    public static Component name(InfinityCellRegistryTarget target, String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null) {
            return Component.literal(id);
        }
        return chemical(target, location).map(Chemical::getTextComponent).orElse(Component.literal(id));
    }

    public static Optional<ResourceLocation> icon(InfinityCellRegistryTarget target, String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        return location == null ? Optional.empty() : chemical(target, location).map(Chemical::getIcon);
    }

    public static int tint(InfinityCellRegistryTarget target, String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        return location == null ? 0xFFFFFFFF : chemical(target, location)
                .map(Chemical::getTint)
                .orElse(0xFFFFFFFF);
    }

    private static Optional<ChemicalStack<?>> createStack(InfinityCellRegistryTarget target, ResourceLocation id) {
        return switch (target) {
            case GAS -> Optional.ofNullable(MekanismAPI.gasRegistry().getValue(id))
                    .filter(gas -> !gas.isEmptyType())
                    .map(gas -> new GasStack(gas, 1));
            case INFUSE_TYPE -> Optional.ofNullable(MekanismAPI.infuseTypeRegistry().getValue(id))
                    .filter(type -> !type.isEmptyType())
                    .map(type -> new InfusionStack(type, 1));
            case PIGMENT -> Optional.ofNullable(MekanismAPI.pigmentRegistry().getValue(id))
                    .filter(pigment -> !pigment.isEmptyType())
                    .map(pigment -> new PigmentStack(pigment, 1));
            case SLURRY -> Optional.ofNullable(MekanismAPI.slurryRegistry().getValue(id))
                    .filter(slurry -> !slurry.isEmptyType())
                    .map(slurry -> new SlurryStack(slurry, 1));
            default -> Optional.empty();
        };
    }

    private static Optional<Chemical<?>> chemical(InfinityCellRegistryTarget target, ResourceLocation id) {
        return switch (target) {
            case GAS -> Optional.ofNullable(MekanismAPI.gasRegistry().getValue(id));
            case INFUSE_TYPE -> Optional.ofNullable(MekanismAPI.infuseTypeRegistry().getValue(id));
            case PIGMENT -> Optional.ofNullable(MekanismAPI.pigmentRegistry().getValue(id));
            case SLURRY -> Optional.ofNullable(MekanismAPI.slurryRegistry().getValue(id));
            default -> Optional.empty();
        };
    }

    private static void addEntry(List<InfinityCellRegistryEntry> entries, InfinityCellRegistryTarget target,
                                 Chemical<?> chemical) {
        if (!chemical.isEmptyType() && !chemical.isHidden()) {
            entries.add(new InfinityCellRegistryEntry(target, chemical.getRegistryName().toString(),
                    chemical.getTextComponent()));
        }
    }
}
