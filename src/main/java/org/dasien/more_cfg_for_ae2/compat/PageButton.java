package org.dasien.more_cfg_for_ae2.compat;

import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;
import net.minecraft.network.chat.Component;

public final class PageButton extends IconButton {
    private final boolean next;

    public PageButton(boolean next, OnPress onPress) {
        super(onPress);
        this.next = next;
        Component message = Component.translatable(next
                ? "gui.more_cfg_for_ae2.interface.next"
                : "gui.more_cfg_for_ae2.interface.previous");
        this.setMessage(message);
    }

    @Override
    protected Icon getIcon() {
        return this.next ? Icon.ARROW_RIGHT : Icon.ARROW_LEFT;
    }
}
