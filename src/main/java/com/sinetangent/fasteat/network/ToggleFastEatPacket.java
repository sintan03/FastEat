package com.sinetangent.fasteat.network;

import com.sinetangent.fasteat.data.FastEatPlayerData;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleFastEatPacket {

    public ToggleFastEatPacket() {
    }

    public static void encode(
            ToggleFastEatPacket packet,
            FriendlyByteBuf buffer
    ) {
    }

    public static ToggleFastEatPacket decode(
            FriendlyByteBuf buffer
    ) {
        return new ToggleFastEatPacket();
    }

    public static void handle(
            ToggleFastEatPacket packet,
            Supplier<NetworkEvent.Context> supplier
    ) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            FastEatPlayerData.Settings settings =
                    FastEatPlayerData.get(player.getUUID());

            settings.setEnabled(!settings.isEnabled());
            settings.setConsumedThisClick(false);

            // 設定をクライアントに同期
            NetworkHandler.syncSettings(player);

            // アクションバーに状態を表示
            boolean enabled = settings.isEnabled();

            player.displayClientMessage(
                    Component.translatable(
                            "message.fasteat.actionbar",
                            Component.translatable(
                                    enabled
                                            ? "message.fasteat.enabled_text"
                                            : "message.fasteat.disabled_text"
                            ).withStyle(
                                    enabled ? ChatFormatting.GREEN : ChatFormatting.RED
                            )
                    ),
                    true
            );
        });

        context.setPacketHandled(true);
    }
}