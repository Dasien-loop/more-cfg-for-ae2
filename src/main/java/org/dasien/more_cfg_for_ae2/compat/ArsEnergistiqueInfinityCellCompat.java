package org.dasien.more_cfg_for_ae2.compat;

import appeng.api.stacks.AEKey;
import gripe._90.arseng.me.key.SourceKey;
import gripe._90.arseng.me.key.SourceKeyType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public final class ArsEnergistiqueInfinityCellCompat {
    private static final String SOURCE_ID = "ars_nouveau:source";

    private ArsEnergistiqueInfinityCellCompat() {
    }

    public static Optional<AEKey> resolve(String id) {
        return SOURCE_ID.equals(id) ? Optional.of(SourceKey.KEY) : Optional.empty();
    }

    public static List<InfinityCellRegistryEntry> entries() {
        return List.of(new InfinityCellRegistryEntry(InfinityCellRegistryTarget.SOURCE, SOURCE_ID,
                SourceKeyType.SOURCE));
    }

    public static Component name(String id) {
        return SOURCE_ID.equals(id) ? SourceKeyType.SOURCE : Component.literal(id);
    }

    public static ItemStack creativeCell() {
        return SourceKey.KEY.wrapForDisplayOrFilter();
    }
}
