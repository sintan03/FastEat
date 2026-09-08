package com.sinetangent.fasteat.event;

import com.sinetangent.fasteat.FastEat;
import com.sinetangent.fasteat.config.FastEatConfig;

import net.minecraft.client.Minecraft;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = FastEat.MODID,
        value = Dist.CLIENT
)
public class ClientEventHandler {

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

        if (useAnim == UseAnim.EAT || useAnim == UseAnim.DRINK) {

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
}