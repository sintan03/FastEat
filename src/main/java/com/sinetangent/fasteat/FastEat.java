package com.sinetangent.fasteat;

import com.mojang.logging.LogUtils;
import com.sinetangent.fasteat.config.FastEatConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(FastEat.MODID)
public class FastEat {

    public static final String MODID = "fasteat";

    public static final Logger LOGGER = LogUtils.getLogger();

    public FastEat() {

        ModLoadingContext.get().registerConfig(
                ModConfig.Type.COMMON,
                FastEatConfig.SPEC
        );

        LOGGER.info("Fast Eat has been loaded!");
    }
}