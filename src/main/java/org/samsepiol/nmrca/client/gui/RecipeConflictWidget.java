package org.samsepiol.nmrca.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.samsepiol.nmrca.NOMORERECIPECONFLICTAGAIN;
import org.samsepiol.nmrca.core.RecipeConflictManager;
import org.samsepiol.nmrca.network.NetworkHandler;
import org.samsepiol.nmrca.network.SelectRecipePacket;

import java.util.List;

/**
 * Widget for displaying and selecting conflicting recipes
 */
public class RecipeConflictWidget extends AbstractWidget {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
            NOMORERECIPECONFLICTAGAIN.MODID, "textures/gui/recipe_selector.png");
    
    private final AbstractContainerScreen<?> parentScreen;
    private final int outputSlotX;
    private final int outputSlotY;
    
    private Button prevButton;
    private Button nextButton;
    private Component indicatorText;
    
    private List<RecipeHolder<CraftingRecipe>> conflicts;
    private String currentInputHash;
    private int currentIndex = 0;
    private int totalRecipes = 0;
    
    public RecipeConflictWidget(AbstractContainerScreen<?> parentScreen, int x, int y) {
        super(x, y, 60, 20, Component.empty());
        this.parentScreen = parentScreen;
        this.outputSlotX = x;
        this.outputSlotY = y;
        
        // Create navigation buttons
        this.prevButton = Button.builder(
                Component.literal("<"),
                button -> cycleRecipe(-1))
                .bounds(x - 25, y, 20, 20)
                .build();
        
        this.nextButton = Button.builder(
                Component.literal(">"),
                button -> cycleRecipe(1))
                .bounds(x + 25, y, 20, 20)
                .build();
    }
    
    public void updateConflicts(CraftingContainer craftingContainer) {
        if (craftingContainer == null) return;
        
        RecipeConflictManager manager = RecipeConflictManager.getInstance();
        String inputHash = manager.generateInputHash(craftingContainer);
        
        // Check if input has changed
        if (!inputHash.equals(currentInputHash)) {
            currentInputHash = inputHash;
            
            // Get conflicts from manager (this would need client-side caching)
            // For now, we'll need to implement client-side conflict detection
            updateConflictDisplay();
        }
    }
    
    private void updateConflictDisplay() {
        RecipeConflictManager manager = RecipeConflictManager.getInstance();
        totalRecipes = manager.getConflictCount(currentInputHash);
        
        if (totalRecipes > 1) {
            currentIndex = manager.getSelectedIndex(currentInputHash, 
                    Minecraft.getInstance().player.getUUID());
            indicatorText = Component.literal(String.format("Recipe %d/%d", 
                    currentIndex + 1, totalRecipes));
            this.visible = true;
            this.prevButton.visible = true;
            this.nextButton.visible = true;
        } else {
            this.visible = false;
            this.prevButton.visible = false;
            this.nextButton.visible = false;
        }
    }
    
    private void cycleRecipe(int direction) {
        if (totalRecipes <= 1) return;
        
        currentIndex = (currentIndex + direction + totalRecipes) % totalRecipes;
        
        // Send packet to server to update selection
        NetworkHandler.sendToServer(new SelectRecipePacket(currentInputHash, currentIndex));
        
        // Update display
        indicatorText = Component.literal(String.format("Recipe %d/%d", 
                currentIndex + 1, totalRecipes));
    }
    
    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible || totalRecipes <= 1) return;
        
        // Render indicator text
        if (indicatorText != null) {
            int textX = getX() - graphics.getFont().width(indicatorText) / 2;
            int textY = getY() - 12;
            graphics.drawString(graphics.getFont(), indicatorText, 
                    textX, textY, 0xFFFFFF, true);
        }
        
        // Render buttons
        prevButton.render(graphics, mouseX, mouseY, partialTick);
        nextButton.render(graphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        if (indicatorText != null) {
            output.add(NarrationElementOutput.Type.TITLE, indicatorText);
        }
    }
    
    public Button getPrevButton() {
        return prevButton;
    }
    
    public Button getNextButton() {
        return nextButton;
    }
    
    public boolean hasConflicts() {
        return totalRecipes > 1;
    }
}