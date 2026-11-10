package com.sinetangent.fasteat.event;

import com.sinetangent.fasteat.FastEat;
import com.sinetangent.fasteat.data.FastEatPlayerData;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FastEat.MODID)
public class ConsumableEventHandler {

    @SubscribeEvent
    public static void onItemUseStart(
            LivingEntityUseItemEvent.Start event
    ) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // 論理サーバー側だけで処理する
        if (player.level().isClientSide) {
            return;
        }

        FastEatPlayerData.Settings settings =
                FastEatPlayerData.get(player.getUUID());

        if (!settings.isEnabled()) {
            return;
        }

        ItemStack item = event.getItem();
        UseAnim useAnim = item.getUseAnimation();

        if (!isConsumableAnimation(useAnim)) {
            return;
        }

        // Consume Once Per Click
        if (settings.isConsumeOncePerClick()) {

            if (settings.isConsumedThisClick()) {
                event.setCanceled(true);
                return;
            }

            settings.setConsumedThisClick(true);
        }

        // Consumption duration
        if (useAnim == UseAnim.EAT) {
            event.setDuration(settings.getEatDuration());
        } else if (useAnim == UseAnim.DRINK) {
            event.setDuration(settings.getDrinkDuration());
        }
    }



    private static boolean isConsumableAnimation(UseAnim useAnim) {
        return useAnim == UseAnim.EAT
                || useAnim == UseAnim.DRINK;
    }
}
