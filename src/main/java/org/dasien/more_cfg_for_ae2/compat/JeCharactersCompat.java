package org.dasien.more_cfg_for_ae2.compat;

import me.towdium.pinin.PinIn;

public final class JeCharactersCompat {
    private static final PinIn PIN_IN = new PinIn();

    private JeCharactersCompat() {
    }

    public static boolean matches(String text, String query) {
        if (query == null || query.isBlank()) {
            return true;
        }
        if (text == null || text.isBlank()) {
            return false;
        }
        return PIN_IN.contains(text, query) || PIN_IN.begins(text, query) || PIN_IN.matches(text, query);
    }
}
