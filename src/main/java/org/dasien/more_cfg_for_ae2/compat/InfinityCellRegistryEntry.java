package org.dasien.more_cfg_for_ae2.compat;

import net.minecraft.network.chat.Component;

public record InfinityCellRegistryEntry(InfinityCellRegistryTarget target, String registryId, Component name) {
    public String encodedId() {
        return this.target.encode(this.registryId);
    }
}
