package org.samsepiol.nmrca.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.samsepiol.nmrca.NOMORERECIPECONFLICTAGAIN;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Manages recipe conflict detection and resolution
 */
public class RecipeConflictManager {
    private static final RecipeConflictManager INSTANCE = new RecipeConflictManager();
    
    // Cache of recipe conflicts by input hash
    private final Map<String, List<RecipeHolder<?>>> conflictCache = new ConcurrentHashMap<>();
    
    // Currently selected recipe index for each conflict group
    private final Map<String, Integer> selectedIndices = new ConcurrentHashMap<>();
    
    // Player-specific selections (for multiplayer)
    private final Map<UUID, Map<String, Integer>> playerSelections = new ConcurrentHashMap<>();
    
    private RecipeConflictManager() {}
    
    public static RecipeConflictManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Find all conflicting recipes for a given container and recipe type
     */
    public <C extends Container, T extends Recipe<C>> List<RecipeHolder<T>> findConflicts(
            RecipeType<T> recipeType, C container, Level level) {
        
        String inputHash = generateInputHash(container);
        
        // Check cache first
        if (conflictCache.containsKey(inputHash)) {
            @SuppressWarnings("unchecked")
            List<RecipeHolder<T>> cached = (List<RecipeHolder<T>>) (List<?>) conflictCache.get(inputHash);
            return cached;
        }
        
        // Find all matching recipes
        RecipeManager recipeManager = level.getRecipeManager();
        List<RecipeHolder<T>> matches = recipeManager.getAllRecipesFor(recipeType).stream()
                .filter(holder -> holder.value().matches(container, level))
                .collect(Collectors.toList());
        
        // Cache if there are conflicts (more than 1 match)
        if (matches.size() > 1) {
            conflictCache.put(inputHash, new ArrayList<>(matches));
            NOMORERECIPECONFLICTAGAIN.LOGGER.debug("Found {} conflicting recipes for input hash: {}", 
                    matches.size(), inputHash);
        }
        
        return matches;
    }
    
    /**
     * Get the currently selected recipe for a conflict group
     */
    public <T extends Recipe<?>> RecipeHolder<T> getSelectedRecipe(
            List<RecipeHolder<T>> conflicts, String inputHash, UUID playerId) {
        
        if (conflicts.isEmpty()) {
            return null;
        }
        
        if (conflicts.size() == 1) {
            return conflicts.get(0);
        }
        
        // Get player-specific selection if available
        Map<String, Integer> playerMap = playerSelections.get(playerId);
        Integer index = null;
        
        if (playerMap != null) {
            index = playerMap.get(inputHash);
        }
        
        // Fall back to global selection
        if (index == null) {
            index = selectedIndices.getOrDefault(inputHash, 0);
        }
        
        // Ensure index is valid
        index = Math.max(0, Math.min(index, conflicts.size() - 1));
        
        return conflicts.get(index);
    }
    
    /**
     * Set the selected recipe index for a conflict group
     */
    public void setSelectedIndex(String inputHash, int index, UUID playerId) {
        if (playerId != null) {
            playerSelections.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>())
                    .put(inputHash, index);
        } else {
            selectedIndices.put(inputHash, index);
        }
    }
    
    /**
     * Cycle to the next recipe in a conflict group
     */
    public int cycleSelection(String inputHash, int totalRecipes, UUID playerId) {
        Map<String, Integer> selections = playerId != null 
                ? playerSelections.computeIfAbsent(playerId, k -> new ConcurrentHashMap<>())
                : selectedIndices;
        
        int currentIndex = selections.getOrDefault(inputHash, 0);
        int nextIndex = (currentIndex + 1) % totalRecipes;
        selections.put(inputHash, nextIndex);
        
        return nextIndex;
    }
    
    /**
     * Generate a hash for the container inputs
     */
    public String generateInputHash(Container container) {
        StringBuilder hash = new StringBuilder();
        
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                hash.append(i).append(":")
                    .append(stack.getItem().toString())
                    .append("x").append(stack.getCount())
                    .append(";");
            }
        }
        
        return hash.toString();
    }
    
    /**
     * Clear cache (useful when recipes are reloaded)
     */
    public void clearCache() {
        conflictCache.clear();
        selectedIndices.clear();
        playerSelections.clear();
        NOMORERECIPECONFLICTAGAIN.LOGGER.info("Cleared recipe conflict cache");
    }
    
    /**
     * Get the number of conflicts for a given input
     */
    public int getConflictCount(String inputHash) {
        List<RecipeHolder<?>> conflicts = conflictCache.get(inputHash);
        return conflicts != null ? conflicts.size() : 0;
    }
    
    /**
     * Get the current selection index
     */
    public int getSelectedIndex(String inputHash, UUID playerId) {
        if (playerId != null) {
            Map<String, Integer> playerMap = playerSelections.get(playerId);
            if (playerMap != null && playerMap.containsKey(inputHash)) {
                return playerMap.get(inputHash);
            }
        }
        return selectedIndices.getOrDefault(inputHash, 0);
    }
}