package org.samsepiol.nmrca;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.samsepiol.nmrca.network.NetworkHandler;

/**
 * No More Recipe Conflicts Again (NMRCA)
 * A NeoForge mod that resolves recipe conflicts by allowing players to choose
 * between multiple valid recipe outputs across various crafting systems.
 */
@Mod(NOMORERECIPECONFLICTAGAIN.MODID)
public class NOMORERECIPECONFLICTAGAIN {
    public static final String MODID = "nmrca";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NOMORERECIPECONFLICTAGAIN(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Initializing No More Recipe Conflicts Again");
        
        // Register common setup
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        
        // Register event handlers
        NeoForge.EVENT_BUS.register(this);
        
        // Register config
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("NMRCA Common Setup Starting");
        
        // Register network packets
        event.enqueueWork(() -> {
            NetworkHandler.register();
        });
        
        LOGGER.info("NMRCA Common Setup Complete");
    }
    
    private void clientSetup(FMLClientSetupEvent event) {
        LOGGER.info("NMRCA Client Setup Complete");
        // Client-side setup (GUI handlers, keybindings, etc.) will be added here
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("NMRCA Server Starting");
    }
}
