package com.sinetangent.fasteat;

import com.sinetangent.fasteat.network.NetworkHandler;

import net.minecraftforge.fml.common.Mod;

@Mod(FastEat.MODID)
public class FastEat {

    public static final String MODID = "fasteat";

    public FastEat() {

        NetworkHandler.register();

    }
}