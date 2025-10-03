package org.samsepiol.nmrca.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;
import org.samsepiol.nmrca.core.RecipeConflictManager;

import java.util.function.Supplier;

/**
 * Packet sent from client to server to select a recipe from conflicts
 */
public class SelectRecipePacket {
    private final String inputHash;
    private final int selectedIndex;
    
    public SelectRecipePacket(String inputHash, int selectedIndex) {
        this.inputHash = inputHash;
        this.selectedIndex = selectedIndex;
    }
    
    public static void encode(SelectRecipePacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.inputHash);
        buffer.writeInt(packet.selectedIndex);
    }
    
    public static SelectRecipePacket decode(FriendlyByteBuf buffer) {
        String inputHash = buffer.readUtf();
        int selectedIndex = buffer.readInt();
        return new SelectRecipePacket(inputHash, selectedIndex);
    }
    
    public static void handle(SelectRecipePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                // Update the selected recipe on the server
                RecipeConflictManager.getInstance().setSelectedIndex(
                        packet.inputHash, 
                        packet.selectedIndex, 
                        player.getUUID()
                );
                
                // Force the crafting menu to update
                if (player.containerMenu != null) {
                    player.containerMenu.slotsChanged(null);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}