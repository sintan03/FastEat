package com.sinetangent.fasteat.network;

import com.sinetangent.fasteat.data.FastEatPlayerData;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ConsumeOnceResetPacket {

    public ConsumeOnceResetPacket() {
    }

    public static void encode(
            ConsumeOnceResetPacket packet,
            FriendlyByteBuf buffer
    ) {
    }

    public static ConsumeOnceResetPacket decode(
            FriendlyByteBuf buffer
    ) {
        return new ConsumeOnceResetPacket();
    }

    public static void handle(
            ConsumeOnceResetPacket packet,
            Supplier<NetworkEvent.Context> supplier
    ) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {

            ServerPlayer player = context.getSender();

            if (player == null) {
                return;
            }

            FastEatPlayerData.Settings settings =
                    FastEatPlayerData.get(player.getUUID());

            settings.setConsumedThisClick(false);
        });

        context.setPacketHandled(true);
    }
}
