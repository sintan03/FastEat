package com.sinetangent.fasteat.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.sinetangent.fasteat.FastEat;

import net.minecraft.client.KeyMapping;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = FastEat.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class FastEatKeyBindings {

    public static final KeyMapping TOGGLE_FAST_EAT =
            new KeyMapping(
                    "key.fasteat.toggle",
                    InputConstants.UNKNOWN.getValue(),
                    "key.categories.fasteat"
            );

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_FAST_EAT);
    }
}