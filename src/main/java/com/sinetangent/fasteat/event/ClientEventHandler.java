package com.sinetangent.fasteat.event;

import com.sinetangent.fasteat.FastEat;
import com.sinetangent.fasteat.config.FastEatConfig;

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

    private static boolean consumedThisClick = false;
    private static boolean useWasDown = false;
    private static boolean blockedUseKey = false;

    // ========================
    // No Slowdown
    // ========================

    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {

        if (!FastEatConfig.NO_SLOWDOWN.get()) {
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
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {

        if (!FastEatConfig.CONSUME_ONCE_PER_CLICK.get()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (event.getEntity() != minecraft.player) {
            return;
        }

        ItemStack item = event.getItem();
        UseAnim useAnim = item.getUseAnimation();

        if (isConsumableAnimation(useAnim)) {
            consumedThisClick = true;
        }
    }

    // ========================
    // Reset when right-click released
    // ========================

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {

        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return;
        }

        boolean physicalUseDown = minecraft.mouseHandler.isRightPressed();

        // 実際に右クリックを離した
        if (!physicalUseDown) {

            consumedThisClick = false;
            blockedUseKey = false;

            return;
        }

        // 1個消費した後、まだ右クリックを押している
        if (
                FastEatConfig.CONSUME_ONCE_PER_CLICK.get()
                        && consumedThisClick
        ) {

            minecraft.options.keyUse.setDown(false);

            blockedUseKey = true;
        }

        useWasDown = physicalUseDown;
    }


    // ========================
    // Utility
    // ========================

    private static boolean isConsumableAnimation(UseAnim useAnim) {

        return useAnim == UseAnim.EAT
                || useAnim == UseAnim.DRINK;
    }
}