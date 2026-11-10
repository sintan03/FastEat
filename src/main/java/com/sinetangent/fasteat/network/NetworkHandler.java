package com.sinetangent.fasteat.network;

import com.sinetangent.fasteat.FastEat;
import com.sinetangent.fasteat.data.FastEatPlayerData;
import com.sinetangent.fasteat.network.ToggleFastEatPacket;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL =
            NetworkRegistry.newSimpleChannel(
                    new ResourceLocation(
                            FastEat.MODID,
                            "main"
                    ),
                    () -> PROTOCOL_VERSION,
                    PROTOCOL_VERSION::equals,
                    PROTOCOL_VERSION::equals
            );

    private static int packetId = 0;

    private static int nextId() {
        return packetId++;
    }

    public static void register() {
        CHANNEL.registerMessage(
                nextId(),
                ClientSettingsSyncPacket.class,
                ClientSettingsSyncPacket::encode,
                ClientSettingsSyncPacket::decode,
                ClientSettingsSyncPacket::handle
        );

        CHANNEL.registerMessage(
                nextId(),
                ConsumeOnceResetPacket.class,
                ConsumeOnceResetPacket::encode,
                ConsumeOnceResetPacket::decode,
                ConsumeOnceResetPacket::handle
        );

        CHANNEL.registerMessage(
                nextId(),
                ToggleFastEatPacket.class,
                ToggleFastEatPacket::encode,
                ToggleFastEatPacket::decode,
                ToggleFastEatPacket::handle
        );
    }

    /**
     * プレイヤー自身の設定を、そのプレイヤーのClientへ同期する。
     */
    public static void syncSettings(ServerPlayer player) {

        // 設定変更をプレイヤーの永続データにも保存する
        FastEatPlayerData.save(player);

        FastEatPlayerData.Settings settings =
                FastEatPlayerData.get(player.getUUID());

        CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new ClientSettingsSyncPacket(settings)
        );
    }
}
