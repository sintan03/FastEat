package com.sinetangent.fasteat.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FastEatPlayerData {

    private static final Map<UUID, Settings> PLAYERS = new HashMap<>();

    private static final String DATA_KEY = "FastEat";

    private static final String ENABLED_KEY = "enabled";
    private static final String EAT_DURATION_KEY = "eatDuration";
    private static final String DRINK_DURATION_KEY = "drinkDuration";
    private static final String NO_SLOWDOWN_KEY = "noSlowdown";
    private static final String CONSUME_ONCE_PER_CLICK_KEY = "consumeOncePerClick";

    public static class Settings {

        private boolean enabled = true;

        private int eatDuration = 2;
        private int drinkDuration = 2;

        private boolean noSlowdown = true;
        private boolean consumeOncePerClick = true;

        // 現在の右クリックで既に消費したか
        // 一時的な状態なのでNBTには保存しない
        private boolean consumedThisClick = false;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getEatDuration() {
            return eatDuration;
        }

        public void setEatDuration(int eatDuration) {
            this.eatDuration = eatDuration;
        }

        public int getDrinkDuration() {
            return drinkDuration;
        }

        public void setDrinkDuration(int drinkDuration) {
            this.drinkDuration = drinkDuration;
        }

        public boolean isNoSlowdown() {
            return noSlowdown;
        }

        public void setNoSlowdown(boolean noSlowdown) {
            this.noSlowdown = noSlowdown;
        }

        public boolean isConsumeOncePerClick() {
            return consumeOncePerClick;
        }

        public void setConsumeOncePerClick(boolean consumeOncePerClick) {
            this.consumeOncePerClick = consumeOncePerClick;
        }

        public boolean isConsumedThisClick() {
            return consumedThisClick;
        }

        public void setConsumedThisClick(boolean consumedThisClick) {
            this.consumedThisClick = consumedThisClick;
        }
    }

    public static Settings get(UUID uuid) {
        return PLAYERS.computeIfAbsent(
                uuid,
                ignored -> new Settings()
        );
    }

    /**
     * プレイヤーの永続データから設定を読み込む。
     * 保存データが存在しない場合はSettingsのデフォルト値を使用する。
     */
    public static void load(ServerPlayer player) {

        CompoundTag persistentData = player.getPersistentData()
                .getCompound(Player.PERSISTED_NBT_TAG);

        Settings settings = new Settings();

        if (persistentData.contains(DATA_KEY, Tag.TAG_COMPOUND)) {

            CompoundTag data = persistentData.getCompound(DATA_KEY);

            if (data.contains(ENABLED_KEY, Tag.TAG_BYTE)) {
                settings.setEnabled(data.getBoolean(ENABLED_KEY));
            }

            if (data.contains(EAT_DURATION_KEY, Tag.TAG_INT)) {
                int duration = data.getInt(EAT_DURATION_KEY);

                if (duration >= 1 && duration <= 72000) {
                    settings.setEatDuration(duration);
                }
            }

            if (data.contains(DRINK_DURATION_KEY, Tag.TAG_INT)) {
                int duration = data.getInt(DRINK_DURATION_KEY);

                if (duration >= 1 && duration <= 72000) {
                    settings.setDrinkDuration(duration);
                }
            }

            if (data.contains(NO_SLOWDOWN_KEY, Tag.TAG_BYTE)) {
                settings.setNoSlowdown(data.getBoolean(NO_SLOWDOWN_KEY));
            }

            if (data.contains(CONSUME_ONCE_PER_CLICK_KEY, Tag.TAG_BYTE)) {
                settings.setConsumeOncePerClick(
                        data.getBoolean(CONSUME_ONCE_PER_CLICK_KEY)
                );
            }
        }

        // ログイン・プレイヤー複製時に、読み込んだ値でキャッシュを更新
        PLAYERS.put(player.getUUID(), settings);
    }

    /**
     * 現在の設定をプレイヤーの永続データに保存する。
     */
    public static void save(ServerPlayer player) {

        Settings settings = get(player.getUUID());

        CompoundTag playerData = player.getPersistentData();

        CompoundTag persistentData = playerData.getCompound(
                Player.PERSISTED_NBT_TAG
        );

        CompoundTag data = new CompoundTag();

        data.putBoolean(ENABLED_KEY, settings.isEnabled());
        data.putInt(EAT_DURATION_KEY, settings.getEatDuration());
        data.putInt(DRINK_DURATION_KEY, settings.getDrinkDuration());
        data.putBoolean(NO_SLOWDOWN_KEY, settings.isNoSlowdown());
        data.putBoolean(
                CONSUME_ONCE_PER_CLICK_KEY,
                settings.isConsumeOncePerClick()
        );

        persistentData.put(DATA_KEY, data);

        playerData.put(Player.PERSISTED_NBT_TAG, persistentData);
    }

    public static void remove(UUID uuid) {
        PLAYERS.remove(uuid);
    }
}
