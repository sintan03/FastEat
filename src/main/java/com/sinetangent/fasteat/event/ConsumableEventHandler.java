package com.sinetangent.fasteat.event;

import com.sinetangent.fasteat.FastEat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FastEat.MODID)
public class ConsumableEventHandler {

    @SubscribeEvent
    public static void onItemUseStart(LivingEntityUseItemEvent.Start event) {

        ItemStack item = event.getItem();

        UseAnim useAnim = item.getUseAnimation();

        if (useAnim == UseAnim.EAT || useAnim == UseAnim.DRINK) {

            event.setDuration(2);

            FastEat.LOGGER.info(
                    "Fast consuming: {}",
                    item.getDescriptionId()
            );
        }
    }
}