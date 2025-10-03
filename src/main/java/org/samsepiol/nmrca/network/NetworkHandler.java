package org.samsepiol.nmrca.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.simple.SimpleChannel;
import org.samsepiol.nmrca.NOMORERECIPECONFLICTAGAIN;

/**
 * Handles network communication between client and server
 */
public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(NOMORERECIPECONFLICTAGAIN.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    
    private static int packetId = 0;
    
    public static void register() {
        CHANNEL.registerMessage(packetId++, 
                SelectRecipePacket.class,
                SelectRecipePacket::encode,
                SelectRecipePacket::decode,
                SelectRecipePacket::handle);
        
        CHANNEL.registerMessage(packetId++,
                SyncConflictsPacket.class,
                SyncConflictsPacket::encode,
                SyncConflictsPacket::decode,
                SyncConflictsPacket::handle);
        
        NOMORERECIPECONFLICTAGAIN.LOGGER.info("Registered {} network packets", packetId);
    }
    
    public static void sendToServer(Object packet) {
        CHANNEL.sendToServer(packet);
    }
    
    public static void sendToPlayer(Object packet, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
    
    public static void sendToAllPlayers(Object packet) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
    }
}