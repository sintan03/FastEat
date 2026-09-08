package com.sinetangent.fasteat.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class FastEatConfig {

    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue EAT_DURATION;
    public static final ForgeConfigSpec.IntValue DRINK_DURATION;
    public static final ForgeConfigSpec.BooleanValue NO_SLOWDOWN;
    public static final ForgeConfigSpec.BooleanValue CONSUME_ONCE_PER_CLICK;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("consumption");

        EAT_DURATION = builder
                .comment("Time required to eat food, in ticks.")
                .comment("Minimum: 1")
                .defineInRange("eatDuration", 2, 1, 72000);

        DRINK_DURATION = builder
                .comment("Time required to drink items, in ticks.")
                .comment("Minimum: 1")
                .defineInRange("drinkDuration", 2, 1, 72000);

        NO_SLOWDOWN = builder
                .comment("Prevent movement slowdown while consuming items.")
                .define("noSlowdown", true);

        CONSUME_ONCE_PER_CLICK = builder
                .comment("Only consume one item per right-click.")
                .define("consumeOncePerClick", true);

        builder.pop();

        SPEC = builder.build();
    }
}