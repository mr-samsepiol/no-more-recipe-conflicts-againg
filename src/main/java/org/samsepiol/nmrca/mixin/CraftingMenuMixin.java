package org.samsepiol.nmrca.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.samsepiol.nmrca.core.RecipeConflictManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(CraftingMenu.class)
public abstract class CraftingMenuMixin {
    
    @Shadow @Final private CraftingContainer craftSlots;
    @Shadow @Final private ResultContainer resultSlots;
    @Shadow @Final private Player player;
    
    @Inject(method = "slotsChanged", at = @At("HEAD"), cancellable = true)
    private void onSlotsChanged(CallbackInfo ci) {
        Level level = player.level();
        if (!level.isClientSide()) {
            RecipeConflictManager manager = RecipeConflictManager.getInstance();
            
            // Find all matching recipes
            List<RecipeHolder<CraftingRecipe>> conflicts = manager.findConflicts(
                    RecipeType.CRAFTING, craftSlots, level);
            
            if (!conflicts.isEmpty()) {
                String inputHash = manager.generateInputHash(craftSlots);
                
                // Get the selected recipe
                RecipeHolder<CraftingRecipe> selected = manager.getSelectedRecipe(
                        conflicts, inputHash, player.getUUID());
                
                if (selected != null) {
                    // Set the result to the selected recipe's output
                    ItemStack result = selected.value().assemble(craftSlots, level.registryAccess());
                    resultSlots.setItem(0, result);
                    resultSlots.setRecipeUsed(selected);
                    
                    // Cancel the original method to prevent it from overwriting our result
                    ci.cancel();
                }
            }
        }
    }
}