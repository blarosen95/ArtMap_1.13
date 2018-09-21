package com.github.blarosen95.ArtMap.IO.Protocol.Out;

import io.netty.channel.Channel;
import com.github.blarosen95.ArtMap.ArtMap;
import com.github.blarosen95.ArtMap.IO.ErrorLogger;
import com.github.blarosen95.ArtMap.Utils.Reflection;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.github.blarosen95.ArtMap.Utils.VersionHandler.BukkitVersion.v1_12;

public class GenericPacketSender implements PacketSender {

    private PacketBuilder builder = ArtMap.getBukkitVersion().getVersion().isGreaterOrEqualTo(v1_12)
            ? new ChatPacketBuilder(Reflection.NMS) : new ChatPacketBuilderLegacy(Reflection.NMS);

    private static void logFailure(Exception e) {
        ErrorLogger.log(e, "Failed to instantiate protocol! Is this version supported?");
    }

    @Override
    public WrappedPacket buildChatPacket(String message) {
        return new WrappedPacket<Object>(builder.buildChatPacket(message)) {
            private String rawMessage = message;

            @Override
            public void send(Player player) {
                Channel channel;
                try {
                    channel = ArtMap.getCacheManager().getChannel(player.getUniqueId());
                } catch (Exception e) {
                    ErrorLogger.log(e, String.format("Error binding player channel for '%s'!", player.getName()));
                    channel = null;
                }
                if (channel != null) channel.writeAndFlush(this.rawPacket);
                else player.sendMessage(rawMessage);
            }
        };
    }

    interface PacketBuilder {
        Object buildChatPacket(String message);
    }

    private static class ChatPacketBuilderLegacy implements PacketBuilder {
        protected Constructor packetCons;
        protected Method chatSerializer;
        protected Class chatSerializerClass;

        public ChatPacketBuilderLegacy(String NMS_Prefix) {
            String packetClassName = NMS_Prefix + ".PacketPlayOutChat";
            String chatComponentName = NMS_Prefix + ".IChatBaseComponent";
            String chatSerializerName = chatComponentName + "$ChatSerializer";

            try {
                Class chatPacketClass = Class.forName(packetClassName);
                Class chatComponentClass = Class.forName(chatComponentName);
                chatSerializerClass = Class.forName(chatSerializerName);

                packetCons = chatPacketClass.getDeclaredConstructor(chatComponentClass, byte.class);
                chatSerializer = chatSerializerClass.getDeclaredMethod("a", String.class);

            } catch (ClassNotFoundException | NoSuchMethodException e) {
                logFailure(e);
            }
        }

        @Override
        public Object buildChatPacket(String message) {
            try {
                Object chatComponent = chatSerializer.invoke(chatSerializerClass, "{\"text\": \"" + message + "\"}");
                return packetCons.newInstance(chatComponent, (byte) 2);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                logFailure(e);
                return null;
            }
        }
    }

    private static class ChatPacketBuilder implements PacketBuilder {
        protected Constructor packetCons;
        protected Method chatSerializer;
        protected Class chatSerializerClass;
        protected Object chatType;

        public ChatPacketBuilder(String NMS_Prefix) {
            String packetClassName = NMS_Prefix + ".PacketPlayOutChat";
            String chatComponentName = NMS_Prefix + ".IChatBaseComponent";
            String chatSerializerName = chatComponentName + "$ChatSerializer";
            String chatTypeClassName = NMS_Prefix + ".ChatMessageType";

            try {
                Class chatPacketClass = Class.forName(packetClassName);
                Class chatComponentClass = Class.forName(chatComponentName);
                chatSerializerClass = Class.forName(chatSerializerName);
                Class chatTypeClass = Class.forName(chatTypeClassName);

                packetCons = chatPacketClass.getDeclaredConstructor(chatComponentClass, chatTypeClass);
                chatSerializer = chatSerializerClass.getDeclaredMethod("a", String.class);
                Field chatTypeField = chatTypeClass.getDeclaredField("GAME_INFO");
                chatType = chatTypeField.get(null);

            } catch (ClassNotFoundException | NoSuchMethodException |
                    IllegalAccessException | NoSuchFieldException e) {
                logFailure(e);
            }
        }

        @Override
        public Object buildChatPacket(String message) {
            try {
                Object chatComponent = chatSerializer.invoke(chatSerializerClass, "{\"text\": \"" + message + "\"}");
                return packetCons.newInstance(chatComponent, chatType);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                logFailure(e);
                return null;
            }
        }
    }
}
