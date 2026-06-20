package org.dasien.more_cfg_for_ae2.compat;

import appbot.ae2.ManaKey;
import appbot.ae2.ManaKeyType;
import appeng.api.stacks.AEKey;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public final class AppliedBotanicsInfinityCellCompat {
    private static final String MANA_ID = "botania:mana";

    private AppliedBotanicsInfinityCellCompat() {
    }

    public static Optional<AEKey> resolve(String id) {
        return MANA_ID.equals(id) ? Optional.of(ManaKey.KEY) : Optional.empty();
    }

    public static List<InfinityCellRegistryEntry> entries() {
        return List.of(new InfinityCellRegistryEntry(InfinityCellRegistryTarget.MANA, MANA_ID, ManaKeyType.MANA));
    }

    public static Component name(String id) {
        return MANA_ID.equals(id) ? ManaKeyType.MANA : Component.literal(id);
    }

    public static ItemStack icon() {
        return ManaKey.KEY.wrapForDisplayOrFilter();
    }
}
