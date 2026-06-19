package org.dasien.more_cfg_for_ae2.compat;

public enum InfinityCellRegistryTarget {
    ITEM("item"),
    FLUID("fluid");

    public static final String CONFIG_KEY = "infinityCells";

    private final String prefix;

    InfinityCellRegistryTarget(String prefix) {
        this.prefix = prefix;
    }

    public String prefix() {
        return this.prefix;
    }

    public String encode(String registryId) {
        return this.prefix + ":" + registryId;
    }

    public static InfinityCellRegistryTarget fromEntry(String entry) {
        int separator = entry == null ? -1 : entry.indexOf(':');
        if (separator <= 0) {
            return ITEM;
        }
        String prefix = entry.substring(0, separator);
        for (InfinityCellRegistryTarget target : values()) {
            if (target.prefix.equals(prefix)) {
                return target;
            }
        }
        return null;
    }

    public static String registryId(String entry) {
        InfinityCellRegistryTarget target = fromEntry(entry);
        if (target == null || entry == null) {
            return "";
        }
        String prefix = target.prefix + ":";
        return entry.startsWith(prefix) ? entry.substring(prefix.length()) : entry;
    }
}
