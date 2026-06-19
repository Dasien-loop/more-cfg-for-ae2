package org.dasien.more_cfg_for_ae2.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.dasien.more_cfg_for_ae2.compat.InfinityCellRegistryTarget;

public class RegistryTargetIconButton extends Button {
    private final InfinityCellRegistryTarget target;
    private final String id;

    public RegistryTargetIconButton(int x, int y, InfinityCellRegistryTarget target, String id, OnPress onPress) {
        super(x, y, 20, 20, Component.translatable("gui.more_cfg_for_ae2.infinity_cell.select"), onPress,
                DEFAULT_NARRATION);
        this.target = target;
        this.id = id;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RegistryEntryDisplayHelper.renderSlot(graphics, this.getX(), this.getY(), this.isHoveredOrFocused());
        RegistryEntryDisplayHelper.renderIcon(graphics, this.target,
                InfinityCellRegistryTarget.registryId(this.id), this.getX() + 2, this.getY() + 2);
    }

    public void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        if (this.isHoveredOrFocused()) {
            graphics.renderComponentTooltip(Minecraft.getInstance().font, java.util.List.of(
                    RegistryEntryDisplayHelper.name(this.target, InfinityCellRegistryTarget.registryId(this.id)),
                    Component.literal(this.id).withStyle(style -> style.withColor(0x808080))), mouseX, mouseY);
        }
    }
}
