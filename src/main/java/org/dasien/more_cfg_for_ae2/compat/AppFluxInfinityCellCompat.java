package org.dasien.more_cfg_for_ae2.compat;

import appeng.api.stacks.AEKey;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public final class AppFluxInfinityCellCompat {
    private AppFluxInfinityCellCompat() {
    }

    public static Optional<AEKey> resolve(String id) {
        return isFeId(id) ? Optional.of(FluxKey.of(EnergyType.FE)) : Optional.empty();
    }

    public static List<InfinityCellRegistryEntry> entries() {
        return List.of(new InfinityCellRegistryEntry(InfinityCellRegistryTarget.FE, EnergyType.FE.id().toString(),
                EnergyType.FE.translate()));
    }

    public static Component name(String id) {
        return isFeId(id) ? EnergyType.FE.translate() : Component.literal(id);
    }

    public static Optional<ResourceLocation> icon(String id) {
        return isFeId(id) ? Optional.of(EnergyType.FE.getIcon()) : Optional.empty();
    }

    private static boolean isFeId(String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        return EnergyType.FE.id().equals(location);
    }
}
