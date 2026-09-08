package com.sinetangent.fasteat;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(FastEat.MODID)
public class FastEat {

    public static final String MODID = "fasteat";

    public static final Logger LOGGER = LogUtils.getLogger();

    public FastEat() {

        LOGGER.info("Fast Eat has been loaded!");
    }
}