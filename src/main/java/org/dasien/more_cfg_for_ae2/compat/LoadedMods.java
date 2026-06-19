package org.dasien.more_cfg_for_ae2.compat;

import net.minecraftforge.fml.ModList;

public final class LoadedMods {
    public static final String EX_PATTERN_PROVIDER = "expatternprovider";
    public static final String JUST_ENOUGH_CHARACTERS = "jecharacters";
    public static final String ME_REQUESTER = "merequester";

    private LoadedMods() {
    }

    public static boolean isExPatternProviderLoaded() {
        return ModList.get().isLoaded(EX_PATTERN_PROVIDER);
    }

    public static boolean isJustEnoughCharactersLoaded() {
        return ModList.get().isLoaded(JUST_ENOUGH_CHARACTERS);
    }

    public static boolean isMERequesterLoaded() {
        return ModList.get().isLoaded(ME_REQUESTER);
    }
}
