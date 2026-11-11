package com.sinetangent.fasteat.network;

import com.sinetangent.fasteat.client.FastEatClientSettings;
import com.sinetangent.fasteat.data.FastEatPlayerData;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientSettingsSyncPacket {

    private final boolean enabled;
    private final int eatDuration;
    private final int drinkDuration;
    private final boolean noSlowdown;
    private final boolean consumeOncePerClick;

    public ClientSettingsSyncPacket(
            FastEatPlayerData.Settings settings
    ) {
        this.enabled = settings.isEnabled();
        this.eatDuration = settings.getEatDuration();
        this.drinkDuration = settings.getDrinkDuration();
        this.noSlowdown = settings.isNoSlowdown();
        this.consumeOncePerClick = settings.isConsumeOncePerClick();
    }

    public ClientSettingsSyncPacket(
            boolean enabled,
            int eatDuration,
            int drinkDuration,
            boolean noSlowdown,
            boolean consumeOncePerClick
    ) {
        this.enabled = enabled;
        this.eatDuration = eatDuration;
        this.drinkDuration = drinkDuration;
        this.noSlowdown = noSlowdown;
        this.consumeOncePerClick = consumeOncePerClick;
    }

    public static void encode(
            ClientSettingsSyncPacket packet,
            FriendlyByteBuf buffer
    ) {
        buffer.writeBoolean(packet.enabled);
        buffer.writeInt(packet.eatDuration);
        buffer.writeInt(packet.drinkDuration);
        buffer.writeBoolean(packet.noSlowdown);
        buffer.writeBoolean(packet.consumeOncePerClick);
    }

    public static ClientSettingsSyncPacket decode(
            FriendlyByteBuf buffer
    ) {
        return new ClientSettingsSyncPacket(
                buffer.readBoolean(),
                buffer.readInt(),
                buffer.readInt(),
                buffer.readBoolean(),
                buffer.readBoolean()
        );
    }

    public static void handle(
            ClientSettingsSyncPacket packet,
            Supplier<NetworkEvent.Context> supplier
    ) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(
                        Dist.CLIENT,
                        () -> () -> {
                            FastEatClientSettings.setEnabled(
                                    packet.enabled
                            );

                            FastEatClientSettings.setEatDuration(
                                    packet.eatDuration
                            );

                            FastEatClientSettings.setDrinkDuration(
                                    packet.drinkDuration
                            );

                            FastEatClientSettings.setNoSlowdown(
                                    packet.noSlowdown
                            );

                            FastEatClientSettings.setConsumeOncePerClick(
                                    packet.consumeOncePerClick
                            );
                        }
                )
        );

        context.setPacketHandled(true);
    }
}
