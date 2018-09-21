package com.github.blarosen95.ArtMap.IO.Protocol;

import com.comphenix.protocol.ProtocolLibrary;
import com.github.blarosen95.ArtMap.ArtMap;
import com.github.blarosen95.ArtMap.IO.Protocol.In.GenericPacketReceiver;
import com.github.blarosen95.ArtMap.IO.Protocol.In.PacketReceiver;
import com.github.blarosen95.ArtMap.IO.Protocol.In.ProtocolLibReceiver;
import com.github.blarosen95.ArtMap.IO.Protocol.Out.GenericPacketSender;
import com.github.blarosen95.ArtMap.IO.Protocol.Out.PacketSender;
import com.github.blarosen95.ArtMap.IO.Protocol.Out.ProtocolLibSender;
import org.bukkit.Bukkit;

public class ProtocolHandler {

    public final PacketReceiver PACKET_RECIEVER;
    public final PacketSender PACKET_SENDER;

    public ProtocolHandler() {
        boolean useProtocolLib = ArtMap.getCompatManager().isPluginLoaded("ProtocolLib");
        try {
            ProtocolLibrary.getProtocolManager();
        } catch (Exception | NoClassDefFoundError e) {
            useProtocolLib = false;
        }
        if (useProtocolLib) {
            PACKET_RECIEVER = new ProtocolLibReceiver();
            PACKET_SENDER = new ProtocolLibSender();
            Bukkit.getLogger().info("[ArtMap] ProtocolLib hooks enabled.");
        } else {
            PACKET_RECIEVER = new GenericPacketReceiver();
            PACKET_SENDER = new GenericPacketSender();
        }
    }
}
