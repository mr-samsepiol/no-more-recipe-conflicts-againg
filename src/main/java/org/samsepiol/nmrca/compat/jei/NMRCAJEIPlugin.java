package org.samsepiol.nmrca.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.samsepiol.nmrca.NOMORERECIPECONFLICTAGAIN;
import org.samsepiol.nmrca.core.RecipeConflictManager;

import java.util.*;
import java.util.stream.Collectors;

/**
 * JEI Plugin for displaying recipe conflicts
 */
@JeiPlugin
public class NMRCAJEIPlugin implements IModPlugin {
    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(
            NOMORERECIPECONFLICTAGAIN.MODID, "jei_plugin");
    
    private IJeiRuntime jeiRuntime;
    
    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }
    
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        NOMORERECIPECONFLICTAGAIN.LOGGER.info("Registering JEI recipe conflict information");
        
        // We can add custom recipe info here if needed
        // For now, we'll just log that we're active
    }
    
    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        // Register custom transfer handlers if needed for conflict resolution
    }
    
    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        // Register GUI handlers for showing conflict indicators
    }
    
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // Register any custom recipe catalysts if needed
    }
    
    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        this.jeiRuntime = jeiRuntime;
        NOMORERECIPECONFLICTAGAIN.LOGGER.info("JEI Runtime available for NMRCA");
        
        // We can use this to modify JEI's behavior at runtime
        detectAndMarkConflicts();
    }
    
    private void detectAndMarkConflicts() {
        if (jeiRuntime == null) return;
        
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        RecipeConflictManager conflictManager = RecipeConflictManager.getInstance();
        
        // Group recipes by their inputs to find conflicts
        Map<String, List<RecipeHolder<CraftingRecipe>>> recipeGroups = new HashMap<>();
        
        recipeManager.getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING)
                .forEach(holder -> {
                    // Create a simple hash of the recipe pattern for grouping
                    // This is simplified - in reality, we'd need more complex matching
                    String patternHash = createPatternHash(holder.value());
                    recipeGroups.computeIfAbsent(patternHash, k -> new ArrayList<>()).add(holder);
                });
        
        // Log conflicts for debugging
        recipeGroups.values().stream()
                .filter(list -> list.size() > 1)
                .forEach(conflicts -> {
                    NOMORERECIPECONFLICTAGAIN.LOGGER.debug("JEI: Found {} conflicting recipes with outputs: {}",
                            conflicts.size(),
                            conflicts.stream()
                                    .map(h -> h.value().getResultItem(Minecraft.getInstance().level.registryAccess()).toString())
                                    .collect(Collectors.joining(", ")));
                });
    }
    
    private String createPatternHash(CraftingRecipe recipe) {
        // This is a simplified version - you'd want more robust pattern matching
        // For shaped recipes, we'd hash the pattern
        // For shapeless, we'd hash the sorted ingredients
        return recipe.getIngredients().stream()
                .map(ing -> ing.getItems().length > 0 ? ing.getItems()[0].getItem().toString() : "empty")
                .sorted()
                .collect(Collectors.joining(","));
    }
}