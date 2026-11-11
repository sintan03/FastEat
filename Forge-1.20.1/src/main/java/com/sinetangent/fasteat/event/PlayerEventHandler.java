package com.sinetangent.fasteat.event;

import com.sinetangent.fasteat.FastEat;
import com.sinetangent.fasteat.data.FastEatPlayerData;
import com.sinetangent.fasteat.network.NetworkHandler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FastEat.MODID)
public class PlayerEventHandler {

    /**
     * ログイン時に保存データを読み込み、クライアントへ同期する。
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        FastEatPlayerData.load(player);
        NetworkHandler.syncSettings(player);
    }

    /**
     * ログアウト時に設定を保存して、メモリ上のキャッシュを破棄する。
     */
    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        FastEatPlayerData.save(player);
        FastEatPlayerData.remove(player.getUUID());
    }

    /**
     * 死亡時のリスポーンやディメンション移動でプレイヤーが複製された際、
     * Forgeの永続データ領域を新しいプレイヤーへ引き継ぐ。
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {

        Player original = event.getOriginal();
        Player replacement = event.getEntity();

        CompoundTag originalPersistentData = original.getPersistentData();

        if (originalPersistentData.contains(Player.PERSISTED_NBT_TAG)) {
            replacement.getPersistentData().put(
                    Player.PERSISTED_NBT_TAG,
                    originalPersistentData
                            .getCompound(Player.PERSISTED_NBT_TAG)
                            .copy()
            );
        }

        if (replacement instanceof ServerPlayer player) {
            FastEatPlayerData.load(player);
        }
    }
}
