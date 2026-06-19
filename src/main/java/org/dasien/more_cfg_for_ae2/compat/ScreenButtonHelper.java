package org.dasien.more_cfg_for_ae2.compat;

import appeng.client.gui.widgets.IconButton;
import net.minecraft.client.gui.components.Button;

import java.lang.reflect.Constructor;

public final class ScreenButtonHelper {
    private ScreenButtonHelper() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends Button> T createButton(Class<?> owner, Button.OnPress onPress) {
        try {
            Class<?> buttonClass = Class.forName(owner.getName() + "$SetAmountButton");
            Constructor<?> constructor = buttonClass.getDeclaredConstructor(Button.OnPress.class);
            constructor.setAccessible(true);
            Button button = (Button) constructor.newInstance(onPress);
            if (button instanceof IconButton iconButton) {
                iconButton.setDisableBackground(true);
            }
            return (T) button;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to create interface amount button", e);
        }
    }
}
