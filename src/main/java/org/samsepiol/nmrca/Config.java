package org.samsepiol.nmrca;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = NOMORERECIPECONFLICTAGAIN.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    // Common Configuration
    private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
    
    public static final ModConfigSpec.BooleanValue ENABLE_MOD = COMMON_BUILDER
            .comment("Enable recipe conflict resolution")
            .define("enable_mod", true);
    
    public static final ModConfigSpec.BooleanValue REMEMBER_SELECTIONS = COMMON_BUILDER
            .comment("Remember recipe selections for the current session")
            .define("remember_selections", true);
    
    public static final ModConfigSpec.BooleanValue DEBUG_MODE = COMMON_BUILDER
            .comment("Enable debug logging for recipe conflicts")
            .define("debug_mode", false);
    
    public static final ModConfigSpec.ConfigValue<List<? extends String>> BLACKLISTED_RECIPES = COMMON_BUILDER
            .comment("List of recipe IDs to ignore for conflict resolution")
            .defineListAllowEmpty("blacklisted_recipes", List.of(), () -> "", Config::validateResourceLocation);
    
    public static final ModConfigSpec.ConfigValue<List<? extends String>> PRIORITY_MODS = COMMON_BUILDER
            .comment("Mod priority order for default recipe selection (first = highest priority)")
            .defineListAllowEmpty("priority_mods", List.of("minecraft"), () -> "", obj -> obj instanceof String);
    
    static final ModConfigSpec SPEC = COMMON_BUILDER.build();
    
    // Client Configuration
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();
    
    public static final ModConfigSpec.BooleanValue SHOW_TOOLTIPS = CLIENT_BUILDER
            .comment("Show tooltips for recipe sources")
            .define("show_tooltips", true);
    
    public static final ModConfigSpec.BooleanValue USE_ANIMATIONS = CLIENT_BUILDER
            .comment("Enable GUI animations")
            .define("use_animations", true);
    
    public static final ModConfigSpec.IntValue BUTTON_OFFSET_X = CLIENT_BUILDER
            .comment("Horizontal offset for conflict resolution buttons")
            .defineInRange("button_offset_x", 0, -50, 50);
    
    public static final ModConfigSpec.IntValue BUTTON_OFFSET_Y = CLIENT_BUILDER
            .comment("Vertical offset for conflict resolution buttons")
            .defineInRange("button_offset_y", 0, -50, 50);
    
    public static final ModConfigSpec.EnumValue<ButtonStyle> BUTTON_STYLE = CLIENT_BUILDER
            .comment("Visual style for conflict resolution buttons")
            .defineEnum("button_style", ButtonStyle.COMPACT);
    
    static final ModConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();
    
    public enum ButtonStyle {
        COMPACT,
        EXPANDED,
        MINIMAL
    }
    
    private static boolean validateResourceLocation(final Object obj) {
        if (!(obj instanceof String str)) return false;
        try {
            ResourceLocation.parse(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        // Config values will be loaded here when needed
    }
}
