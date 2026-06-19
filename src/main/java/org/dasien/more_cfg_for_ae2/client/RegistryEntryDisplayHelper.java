package org.dasien.more_cfg_for_ae2.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.dasien.more_cfg_for_ae2.compat.InfinityCellRegistryTarget;

public final class RegistryEntryDisplayHelper {
    private RegistryEntryDisplayHelper() {
    }

    public static Component name(InfinityCellRegistryTarget target, String id) {
        if (target == InfinityCellRegistryTarget.ITEM) {
            return itemForId(id).getDescription();
        }
        Fluid fluid = fluidForId(id);
        if (fluid == Fluids.EMPTY) {
            return Component.translatable("block.minecraft.barrier");
        }
        return new FluidStack(fluid, 1).getDisplayName();
    }

    public static void renderIcon(GuiGraphics graphics, InfinityCellRegistryTarget target, String id, int x, int y) {
        if (target == InfinityCellRegistryTarget.ITEM) {
            renderItemIcon(graphics, id, x, y);
        } else {
            renderFluidIcon(graphics, id, x, y);
        }
    }

    public static Item itemForId(String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null) {
            return Items.BARRIER;
        }
        Item item = ForgeRegistries.ITEMS.getValue(location);
        return item != null && item != Items.AIR ? item : Items.BARRIER;
    }

    public static Fluid fluidForId(String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null) {
            return Fluids.EMPTY;
        }
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(location);
        return fluid != null ? fluid : Fluids.EMPTY;
    }

    public static void renderSlot(GuiGraphics graphics, int x, int y, boolean hovered) {
        graphics.fill(x, y, x + 20, y + 20, hovered ? 0xFFB8B8B8 : 0xFF6F6F6F);
        graphics.fill(x + 1, y + 1, x + 19, y + 19, 0xFF1D1D1D);
        graphics.fill(x + 1, y + 1, x + 19, y + 2, hovered ? 0xFFEEEEEE : 0xFF9A9A9A);
        graphics.fill(x + 1, y + 1, x + 2, y + 19, hovered ? 0xFFEEEEEE : 0xFF9A9A9A);
        graphics.fill(x + 18, y + 1, x + 19, y + 19, 0xFF2D2D2D);
        graphics.fill(x + 1, y + 18, x + 19, y + 19, 0xFF2D2D2D);
    }

    private static void renderItemIcon(GuiGraphics graphics, String id, int x, int y) {
        graphics.renderItem(new ItemStack(itemForId(id)), x, y);
    }

    private static void renderFluidIcon(GuiGraphics graphics, String id, int x, int y) {
        Fluid fluid = fluidForId(id);
        if (fluid == Fluids.EMPTY) {
            graphics.renderItem(new ItemStack(Items.BARRIER), x, y);
            return;
        }

        FluidStack stack = new FluidStack(fluid, 1);
        IClientFluidTypeExtensions extensions = IClientFluidTypeExtensions.of(fluid);
        ResourceLocation texture = extensions.getStillTexture(stack);
        if (texture == null) {
            graphics.renderItem(new ItemStack(Items.BARRIER), x, y);
            return;
        }

        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(texture);
        int tint = extensions.getTintColor(stack);
        float alpha = ((tint >> 24) & 0xFF) / 255.0F;
        if (alpha <= 0.0F) {
            alpha = 1.0F;
        }
        float red = ((tint >> 16) & 0xFF) / 255.0F;
        float green = ((tint >> 8) & 0xFF) / 255.0F;
        float blue = (tint & 0xFF) / 255.0F;

        RenderSystem.setShaderColor(red, green, blue, alpha);
        graphics.blit(x, y, 0, 16, 16, sprite);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
