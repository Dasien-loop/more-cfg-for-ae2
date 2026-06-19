package org.dasien.more_cfg_for_ae2.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.dasien.more_cfg_for_ae2.compat.InfinityCellRegistryTarget;
import org.dasien.more_cfg_for_ae2.compat.JeCharactersCompat;
import org.dasien.more_cfg_for_ae2.compat.LoadedMods;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class InfinityCellRegistrySelectionScreen extends Screen {
    private static final int CELL = 22;
    private static final int ICON = 16;
    private static final int TOP = 64;

    private final Screen parent;
    private final Consumer<String> selector;
    private final List<Entry> allEntries;
    private final List<Entry> visibleEntries = new ArrayList<>();

    private EditBox searchBox;
    private InfinityCellRegistryTarget target;
    private int scroll;

    public InfinityCellRegistrySelectionScreen(Screen parent, InfinityCellRegistryTarget target, Consumer<String> selector) {
        super(Component.translatable("gui.more_cfg_for_ae2.infinity_cell.select"));
        this.parent = parent;
        this.target = target;
        this.selector = selector;
        this.allEntries = new ArrayList<>();
        collectEntries(target, this.allEntries);
        this.visibleEntries.addAll(this.allEntries);
    }

    @Override
    protected void init() {
        this.searchBox = new EditBox(this.font, this.width / 2 - 160, 38, 320, 20,
                Component.translatable("gui.more_cfg_for_ae2.infinity_cell.search"));
        this.searchBox.setResponder(this::filter);
        this.addRenderableWidget(this.searchBox);
        this.addRenderableWidget(net.minecraft.client.gui.components.Button.builder(
                        Component.translatable("gui.more_cfg_for_ae2.infinity_cell.type.item"),
                        button -> switchTarget(InfinityCellRegistryTarget.ITEM))
                .bounds(this.width / 2 + 166, 38, 42, 20)
                .build());
        this.addRenderableWidget(net.minecraft.client.gui.components.Button.builder(
                        Component.translatable("gui.more_cfg_for_ae2.infinity_cell.type.fluid"),
                        button -> switchTarget(InfinityCellRegistryTarget.FLUID))
                .bounds(this.width / 2 + 210, 38, 42, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 16, 0xFFFFFF);
        super.render(graphics, mouseX, mouseY, partialTick);

        int columns = columns();
        int left = left(columns);
        int rows = rows();
        int maxVisible = columns * rows;
        int start = this.scroll * columns;
        int end = Math.min(this.visibleEntries.size(), start + maxVisible);

        for (int index = start; index < end; index++) {
            int visibleIndex = index - start;
            int x = left + (visibleIndex % columns) * CELL;
            int y = TOP + (visibleIndex / columns) * CELL;
            Entry entry = this.visibleEntries.get(index);
            boolean hovered = mouseX >= x && mouseX < x + CELL && mouseY >= y && mouseY < y + CELL;
            RegistryEntryDisplayHelper.renderSlot(graphics, x, y, hovered);
            entry.renderIcon(graphics, x + 2, y + 2);
        }

        Entry hovered = entryAt(mouseX, mouseY);
        if (hovered != null) {
            graphics.renderComponentTooltip(this.font, hovered.tooltip(), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Entry entry = entryAt((int) mouseX, (int) mouseY);
        if (entry != null) {
            this.selector.accept(entry.id.toString());
            Minecraft.getInstance().setScreen(this.parent);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int maxScroll = maxScroll();
        if (maxScroll <= 0) {
            return false;
        }
        this.scroll = Math.max(0, Math.min(maxScroll, this.scroll - (int) Math.signum(delta)));
        return true;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.parent);
    }

    private void filter(String query) {
        String normalized = normalizeSearchText(query);
        this.visibleEntries.clear();
        for (Entry entry : this.allEntries) {
            if (normalized.isEmpty()
                    || entry.searchText().contains(normalized)
                    || matchesWithJeCharacters(entry, normalized)) {
                this.visibleEntries.add(entry);
            }
        }
        this.scroll = 0;
    }

    private static String normalizeSearchText(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).trim();
    }

    private boolean matchesWithJeCharacters(Entry entry, String query) {
        if (!LoadedMods.isJustEnoughCharactersLoaded()) {
            return false;
        }
        return JeCharactersCompat.matches(entry.name().getString(), query)
                || JeCharactersCompat.matches(entry.id(), query);
    }

    private void switchTarget(InfinityCellRegistryTarget target) {
        this.target = target;
        this.allEntries.clear();
        collectEntries(target, this.allEntries);
        this.searchBox.setValue(this.searchBox.getValue());
        filter(this.searchBox.getValue());
    }

    private Entry entryAt(int mouseX, int mouseY) {
        int columns = columns();
        int left = left(columns);
        int rows = rows();
        if (mouseX < left || mouseY < TOP) {
            return null;
        }
        int column = (mouseX - left) / CELL;
        int row = (mouseY - TOP) / CELL;
        if (column < 0 || column >= columns || row < 0 || row >= rows) {
            return null;
        }
        int index = this.scroll * columns + row * columns + column;
        return index >= 0 && index < this.visibleEntries.size() ? this.visibleEntries.get(index) : null;
    }

    private int columns() {
        return Math.max(1, Math.min(18, (this.width - 40) / CELL));
    }

    private int rows() {
        return Math.max(1, (this.height - TOP - 20) / CELL);
    }

    private int left(int columns) {
        return (this.width - columns * CELL) / 2;
    }

    private int maxScroll() {
        int columns = columns();
        int rows = rows();
        int totalRows = (int) Math.ceil(this.visibleEntries.size() / (double) columns);
        return Math.max(0, totalRows - rows);
    }

    private static void collectEntries(InfinityCellRegistryTarget target, List<Entry> entries) {
        if (target == InfinityCellRegistryTarget.ITEM) {
            for (Item item : BuiltInRegistries.ITEM) {
                if (item == Items.AIR) {
                    continue;
                }
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
                entries.add(Entry.item(target.encode(id.toString()), item));
            }
        } else {
            for (Fluid fluid : BuiltInRegistries.FLUID) {
                if (fluid == Fluids.EMPTY || fluid.defaultFluidState().isSource() == false) {
                    continue;
                }
                ResourceLocation id = BuiltInRegistries.FLUID.getKey(fluid);
                entries.add(Entry.fluid(target.encode(id.toString()), fluid));
            }
        }
        entries.sort(Comparator.comparing(entry -> entry.name.getString().toLowerCase(Locale.ROOT)));
    }

    private record Entry(String id, InfinityCellRegistryTarget target, String registryId, Component name,
                         String searchText) {
        static Entry item(String id, Item item) {
            return new Entry(id, InfinityCellRegistryTarget.ITEM,
                    InfinityCellRegistryTarget.registryId(id), item.getDescription(),
                    normalizeSearchText(id + " " + item.getDescription().getString()));
        }

        static Entry fluid(String id, Fluid fluid) {
            String registryId = InfinityCellRegistryTarget.registryId(id);
            Component name = RegistryEntryDisplayHelper.name(InfinityCellRegistryTarget.FLUID, registryId);
            return new Entry(id, InfinityCellRegistryTarget.FLUID, registryId, name,
                    normalizeSearchText(id + " " + name.getString()));
        }

        void renderIcon(GuiGraphics graphics, int x, int y) {
            RegistryEntryDisplayHelper.renderIcon(graphics, this.target, this.registryId, x, y);
        }

        List<Component> tooltip() {
            return List.of(this.name, Component.literal(this.id).withStyle(style -> style.withColor(0x808080)));
        }
    }
}
