package org.samsepiol.nmrca.compat.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.samsepiol.nmrca.NOMORERECIPECONFLICTAGAIN;
import org.samsepiol.nmrca.core.RecipeConflictManager;

import java.util.*;
import java.util.stream.Collectors;

/**
 * REI Plugin for displaying recipe conflicts
 */
public class NMRCAREIPlugin implements REIClientPlugin {
    
    @Override
    public String getPluginProviderName() {
        return "No More Recipe Conflicts Again";
    }
    
    @Override
    public void registerCategories(CategoryRegistry registry) {
        NOMORERECIPECONFLICTAGAIN.LOGGER.info("Registering REI categories for NMRCA");
        // We could add a custom category for viewing all conflicts if needed
    }
    
    @Override
    public void registerDisplays(DisplayRegistry registry) {
        NOMORERECIPECONFLICTAGAIN.LOGGER.info("Registering REI displays for NMRCA");
        
        // Detect and mark recipe conflicts
        detectAndMarkConflicts();
    }
    
    @Override
    public void registerScreens(ScreenRegistry registry) {
        // Register screen handlers if we need custom overlays
    }
    
    @Override
    public void registerEntries(EntryRegistry registry) {
        // Register any custom entries if needed
    }
    
    private void detectAndMarkConflicts() {
        if (Minecraft.getInstance().level == null) return;
        
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        RecipeConflictManager conflictManager = RecipeConflictManager.getInstance();
        
        // Group recipes by their inputs to find conflicts
        Map<String, List<RecipeHolder<CraftingRecipe>>> recipeGroups = new HashMap<>();
        
        recipeManager.getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING)
                .forEach(holder -> {
                    String patternHash = createPatternHash(holder.value());
                    recipeGroups.computeIfAbsent(patternHash, k -> new ArrayList<>()).add(holder);
                });
        
        // Log conflicts for debugging
        recipeGroups.values().stream()
                .filter(list -> list.size() > 1)
                .forEach(conflicts -> {
                    NOMORERECIPECONFLICTAGAIN.LOGGER.debug("REI: Found {} conflicting recipes with outputs: {}",
                            conflicts.size(),
                            conflicts.stream()
                                    .map(h -> h.value().getResultItem(Minecraft.getInstance().level.registryAccess()).toString())
                                    .collect(Collectors.joining(", ")));
                    
                    // Here we would add visual indicators to REI's displays
                    // This would require more complex integration with REI's rendering system
                });
    }
    
    private String createPatternHash(CraftingRecipe recipe) {
        // Create a hash of the recipe pattern for grouping
        return recipe.getIngredients().stream()
                .map(ing -> ing.getItems().length > 0 ? ing.getItems()[0].getItem().toString() : "empty")
                .sorted()
                .collect(Collectors.joining(","));
    }
    
    /**
     * Helper method to check if a recipe has conflicts
     */
    public static boolean hasConflicts(RecipeHolder<?> recipe) {
        // This would check against our conflict manager
        // Implementation would depend on how we store conflict information
        return false; // Placeholder
    }
    
    /**
     * Get conflict count for a recipe
     */
    public static int getConflictCount(RecipeHolder<?> recipe) {
        // Return the number of conflicting recipes
        return 0; // Placeholder
    }
}