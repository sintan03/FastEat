package com.sinetangent.fasteat.event;

import com.sinetangent.fasteat.FastEat;
import com.sinetangent.fasteat.client.FastEatClientSettings;
import com.sinetangent.fasteat.client.FastEatKeyBindings;
import com.sinetangent.fasteat.network.ConsumeOnceResetPacket;
import com.sinetangent.fasteat.network.NetworkHandler;
import com.sinetangent.fasteat.network.ToggleFastEatPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = FastEat.MODID,
        value = Dist.CLIENT
)
public class ClientEventHandler {

    /**
     * 現在の右クリック押下中に
     * すでに食料・飲料を1回消費したか
     */
    private static boolean consumedThisClick = false;

    /**
     * 前Tickで右クリックが押されていたか
     */
    private static boolean useWasDown = false;

    /**
     * 現在の右クリックを
     * FastEatによってブロックしているか
     */
    private static boolean blockedUseKey = false;


    // ========================
    // No Slowdown
    // ========================

    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {

        if (
                !FastEatClientSettings.isEnabled()
                        || !FastEatClientSettings.isNoSlowdown()
        ) {
            return;
        }

        Player player = event.getEntity();

        if (!player.isUsingItem()) {
            return;
        }

        ItemStack item = player.getUseItem();
        UseAnim useAnim = item.getUseAnimation();

        if (isConsumableAnimation(useAnim)) {

            event.getInput().forwardImpulse *= 5.0F;
            event.getInput().leftImpulse *= 5.0F;

            Minecraft minecraft = Minecraft.getInstance();

            if (
                    event.getInput().forwardImpulse > 0
                            && minecraft.options.keySprint.isDown()
            ) {
                player.setSprinting(true);
            }
        }
    }


    // ========================
    // Consume Once Per Click
    // ========================

    @SubscribeEvent
    public static void onItemUseStart(
            LivingEntityUseItemEvent.Start event
    ) {
        if (
                !FastEatClientSettings.isEnabled()
                        || !FastEatClientSettings.isConsumeOncePerClick()
        ) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (event.getEntity() != minecraft.player) {
            return;
        }

        if (!isConsumableAnimation(
                event.getItem().getUseAnimation()
        )) {
            return;
        }

        // すでに使用済みならキャンセル
        if (blockedUseKey) {
            event.setCanceled(true);
            return;
        }

        // 使用開始時点で使用済みにする
        consumedThisClick = true;
        blockedUseKey = true;
    }



    // ========================
    // Input state
    // ========================

    @SubscribeEvent
    public static void onClientTick(
            TickEvent.ClientTickEvent event
    ) {

        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return;
        }

        // ------------------------
        // FastEatの有効・無効切り替え
        // ------------------------

        while (FastEatKeyBindings.TOGGLE_FAST_EAT.consumeClick()) {

            NetworkHandler.CHANNEL.sendToServer(
                    new ToggleFastEatPacket()
            );
        }

        boolean useDown = minecraft.options.keyUse.isDown();

        // FastEatまたはConsume Once Per Clickが無効なら
        // クライアント側の制御状態を解除
        if (
                !FastEatClientSettings.isEnabled()
                        || !FastEatClientSettings.isConsumeOncePerClick()
        ) {
            consumedThisClick = false;
            blockedUseKey = false;
        }


        // ------------------------
        // 右クリックを離した
        // ------------------------

        if (!useDown) {

            if (useWasDown) {

                consumedThisClick = false;
                blockedUseKey = false;

                NetworkHandler.CHANNEL.sendToServer(
                        new ConsumeOnceResetPacket()
                );
            }

            useWasDown = false;

            return;
        }


        // ------------------------
        // 右クリックを押している
        // ------------------------

        useWasDown = true;


        // 1個消費済みなら、
        // この押下中は使用を開始させない
        if (
                FastEatClientSettings.isEnabled()
                        && FastEatClientSettings.isConsumeOncePerClick()
                        && consumedThisClick
        ) {
            blockedUseKey = true;
        }
    }


    // ========================
    // Utility
    // ========================

    private static boolean isConsumableAnimation(
            UseAnim useAnim
    ) {

        return useAnim == UseAnim.EAT
                || useAnim == UseAnim.DRINK;
    }
}
