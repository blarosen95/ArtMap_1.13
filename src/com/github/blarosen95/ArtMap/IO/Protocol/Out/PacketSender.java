package com.github.blarosen95.ArtMap.IO.Protocol.Out;

public interface PacketSender {
    WrappedPacket buildChatPacket(String message);
}
