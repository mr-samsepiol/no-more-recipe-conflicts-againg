package org.samsepiol.nmrca.network;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;
import org.samsepiol.nmrca.core.RecipeConflictManager;

import java.util.function.Supplier;

/**
 * Packet sent from server to client to sync recipe conflicts
 */
public class SyncConflictsPacket {
    private final String inputHash;
    private final int conflictCount;
    private final int selectedIndex;
    
    public SyncConflictsPacket(String inputHash, int conflictCount, int selectedIndex) {
        this.inputHash = inputHash;
        this.conflictCount = conflictCount;
        this.selectedIndex = selectedIndex;
    }
    
    public static void encode(SyncConflictsPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.inputHash);
        buffer.writeInt(packet.conflictCount);
        buffer.writeInt(packet.selectedIndex);
    }
    
    public static SyncConflictsPacket decode(FriendlyByteBuf buffer) {
        String inputHash = buffer.readUtf();
        int conflictCount = buffer.readInt();
        int selectedIndex = buffer.readInt();
        return new SyncConflictsPacket(inputHash, conflictCount, selectedIndex);
    }
    
    public static void handle(SyncConflictsPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Update client-side conflict information
            // This would be used to update the GUI widget
            // For now, we'll just store it in the manager
            RecipeConflictManager manager = RecipeConflictManager.getInstance();
            // We need to add a method to store client-side conflict info
            // This is a simplified version - you'd want more robust client-side handling
        });
        ctx.get().setPacketHandled(true);
    }
}