package org.dasien.more_cfg_for_ae2;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.dasien.more_cfg_for_ae2.compat.LoadedMods;
import org.slf4j.Logger;

@Mod(More_cfg_for_ae2.MODID)
public class More_cfg_for_ae2 {

    public static final String MODID = "more_cfg_for_ae2";
    private static final Logger LOGGER = LogUtils.getLogger();

    public More_cfg_for_ae2() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        boolean exPatternProviderLoaded = LoadedMods.isExPatternProviderLoaded();
        boolean meRequesterLoaded = LoadedMods.isMERequesterLoaded();

        if (exPatternProviderLoaded) {
            MoreCfgCreativeTabs.TABS.register(modEventBus);
        }

        if (exPatternProviderLoaded && meRequesterLoaded) {
            Config.registerWithExPatternProviderAndMERequester();
        } else if (exPatternProviderLoaded) {
            Config.registerWithExPatternProvider();
        } else if (meRequesterLoaded) {
            Config.registerWithMERequester();
        } else {
            Config.register();
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Loaded configurable AE2 interface limits");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
