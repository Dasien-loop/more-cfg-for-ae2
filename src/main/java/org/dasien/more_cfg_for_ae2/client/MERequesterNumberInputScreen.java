package org.dasien.more_cfg_for_ae2.client;

import appeng.client.gui.MathExpressionParser;
import appeng.client.gui.NumberEntryType;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.AESubScreen;
import appeng.menu.AEBaseMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.dasien.more_cfg_for_ae2.compat.MERequesterNumberFieldBridge;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Optional;

public class MERequesterNumberInputScreen extends AESubScreen<AEBaseMenu, AEBaseScreen<AEBaseMenu>> {
    private static final int PANEL_WIDTH = 248;
    private static final int PANEL_HEIGHT = 104;
    private static final int TEXT_COLOR = 0xE0E0E0;
    private static final int ERROR_COLOR = 0xFF5555;

    private final MERequesterNumberFieldBridge field;
    private final DecimalFormat decimalFormat;
    private final Component dialogTitle;
    private EditBox input;
    private Button confirm;
    private Component error = Component.empty();

    public MERequesterNumberInputScreen(AEBaseScreen<AEBaseMenu> parent, MERequesterNumberFieldBridge field) {
        super(parent, "/screens/more_cfg_for_ae2_me_requester_number.json");
        this.field = field;
        this.dialogTitle = titleFor(field);
        this.decimalFormat = new DecimalFormat("#.######", new DecimalFormatSymbols());
        this.decimalFormat.setParseBigDecimal(true);
        this.decimalFormat.setNegativePrefix("-");
    }

    @Override
    protected void init() {
        super.init();
        int left = left();
        int top = top();
        this.input = new EditBox(this.font, left + 18, top + 34, PANEL_WIDTH - 36, 20, this.dialogTitle);
        this.input.setMaxLength(32);
        this.addRenderableWidget(this.input);

        this.confirm = Button.builder(Component.translatable("gui.more_cfg_for_ae2.me_requester_number.confirm"),
                        button -> confirm())
                .bounds(left + 56, top + 72, 64, 20)
                .build();
        this.addRenderableWidget(this.confirm);
        this.addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> onClose())
                .bounds(left + 128, top + 72, 64, 20)
                .build());

        this.input.setResponder(value -> validateInput());
        this.input.setValue(formatExternal(this.field.moreCfgForAe2$getValue()));
        this.setInitialFocus(this.input);
        this.input.setFocused(true);
        validateInput();
    }

    @Override
    public void drawBG(GuiGraphics graphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        int left = left();
        int top = top();
        graphics.fill(left, top, left + PANEL_WIDTH, top + PANEL_HEIGHT, 0xE0101010);
        graphics.fill(left + 1, top + 1, left + PANEL_WIDTH - 1, top + PANEL_HEIGHT - 1, 0xF0202020);
    }

    @Override
    public void drawFG(GuiGraphics graphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        int left = left() - offsetX;
        int top = top() - offsetY;
        graphics.drawCenteredString(this.font, this.dialogTitle, this.width / 2 - offsetX, top + 12, 0xFFFFFF);
        if (!this.error.getString().isEmpty()) {
            graphics.drawString(this.font, this.error, left + 18, top + 58, ERROR_COLOR, false);
        } else if (this.field.moreCfgForAe2$getType().unit() != null) {
            graphics.drawString(this.font, this.field.moreCfgForAe2$getType().unit(), left + 18, top + 58, 0x808080,
                    false);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335) {
            confirm();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        this.returnToParent();
    }

    private void confirm() {
        Optional<Long> parsed = parseValue();
        if (parsed.isEmpty()) {
            validateInput();
            return;
        }

        long value = parsed.get();
        this.field.moreCfgForAe2$setValue(value);
        this.field.moreCfgForAe2$submit(value);
        this.returnToParent();
    }

    private void validateInput() {
        Optional<Long> parsed = parseValue();
        this.confirm.active = parsed.isPresent();
        this.input.setTextColor(parsed.isPresent() ? TEXT_COLOR : ERROR_COLOR);
    }

    private Optional<Long> parseValue() {
        Optional<BigDecimal> parsed = MathExpressionParser.parse(this.input.getValue(), this.decimalFormat);
        if (parsed.isEmpty()) {
            this.error = Component.translatable("gui.more_cfg_for_ae2.me_requester_number.invalid");
            return Optional.empty();
        }

        long value = convertToExternalValue(parsed.get());
        if (value < 0) {
            this.error = Component.translatable("gui.more_cfg_for_ae2.me_requester_number.too_small");
            return Optional.empty();
        }

        long limit = this.field.moreCfgForAe2$getLimit();
        if (value > limit) {
            this.error = Component.translatable("gui.more_cfg_for_ae2.me_requester_number.too_large",
                    formatExternal(limit));
            return Optional.empty();
        }

        this.error = Component.empty();
        return Optional.of(value);
    }

    private long convertToExternalValue(BigDecimal value) {
        BigDecimal unit = BigDecimal.valueOf(this.field.moreCfgForAe2$getType().amountPerUnit());
        BigDecimal external = value.multiply(unit, MathContext.DECIMAL128).setScale(0, RoundingMode.UP);
        return external.longValue();
    }

    private String formatExternal(long value) {
        NumberEntryType type = this.field.moreCfgForAe2$getType();
        BigDecimal unit = BigDecimal.valueOf(type.amountPerUnit());
        BigDecimal internal = BigDecimal.valueOf(Math.max(0L, value)).divide(unit, MathContext.DECIMAL128);
        return this.decimalFormat.format(internal);
    }

    private int left() {
        return (this.width - PANEL_WIDTH) / 2;
    }

    private int top() {
        return (this.height - PANEL_HEIGHT) / 2;
    }

    private static Component titleFor(MERequesterNumberFieldBridge field) {
        return Component.translatable("gui.more_cfg_for_ae2.me_requester_number." + field.moreCfgForAe2$getName());
    }
}
