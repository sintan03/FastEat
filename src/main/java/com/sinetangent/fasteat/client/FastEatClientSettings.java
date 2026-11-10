package com.sinetangent.fasteat.client;

public class FastEatClientSettings {

    private static boolean enabled = true;

    private static int eatDuration = 1;
    private static int drinkDuration = 1;

    private static boolean noSlowdown = true;
    private static boolean consumeOncePerClick = true;


    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        FastEatClientSettings.enabled = enabled;
    }


    public static int getEatDuration() {
        return eatDuration;
    }

    public static void setEatDuration(int eatDuration) {
        FastEatClientSettings.eatDuration = eatDuration;
    }


    public static int getDrinkDuration() {
        return drinkDuration;
    }

    public static void setDrinkDuration(int drinkDuration) {
        FastEatClientSettings.drinkDuration = drinkDuration;
    }


    public static boolean isNoSlowdown() {
        return noSlowdown;
    }

    public static void setNoSlowdown(boolean noSlowdown) {
        FastEatClientSettings.noSlowdown = noSlowdown;
    }


    public static boolean isConsumeOncePerClick() {
        return consumeOncePerClick;
    }

    public static void setConsumeOncePerClick(boolean consumeOncePerClick) {
        FastEatClientSettings.consumeOncePerClick = consumeOncePerClick;
    }
}
