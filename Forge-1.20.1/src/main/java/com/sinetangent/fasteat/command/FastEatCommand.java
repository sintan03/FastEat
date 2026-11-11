package com.sinetangent.fasteat.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import com.sinetangent.fasteat.data.FastEatPlayerData;
import com.sinetangent.fasteat.network.NetworkHandler;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Locale;

public class FastEatCommand {

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher
    ) {

        dispatcher.register(
                Commands.literal("fasteat")
                        .requires(source -> source.getEntity() instanceof ServerPlayer)

                        // ========================
                        // ON / OFF / TOGGLE
                        // ========================

                        .then(Commands.literal("on")
                                .executes(context -> {
                                    ServerPlayer player = getPlayer(context.getSource());
                                    FastEatPlayerData.Settings settings = getSettings(player);

                                    settings.setEnabled(true);
                                    sync(player);

                                    sendMessage(
                                            context.getSource(),
                                            Component.translatable("message.fasteat.enabled")
                                    );
                                    return 1;
                                })
                        )

                        .then(Commands.literal("off")
                                .executes(context -> {
                                    ServerPlayer player = getPlayer(context.getSource());
                                    FastEatPlayerData.Settings settings = getSettings(player);

                                    settings.setEnabled(false);
                                    sync(player);

                                    sendMessage(
                                            context.getSource(),
                                            Component.translatable("message.fasteat.disabled")
                                    );
                                    return 1;
                                })
                        )

                        .then(Commands.literal("toggle")
                                .executes(context -> {
                                    ServerPlayer player = getPlayer(context.getSource());
                                    FastEatPlayerData.Settings settings = getSettings(player);

                                    boolean enabled = !settings.isEnabled();

                                    settings.setEnabled(enabled);
                                    sync(player);

                                    sendMessage(
                                            context.getSource(),
                                            Component.translatable(
                                                    "message.fasteat.toggled",
                                                    getEnabledText(enabled)
                                            )
                                    );

                                    return 1;
                                })
                        )

                        // ========================
                        // STATUS
                        // ========================

                        .then(Commands.literal("status")
                                .executes(context -> {
                                    ServerPlayer player = getPlayer(context.getSource());
                                    FastEatPlayerData.Settings settings = getSettings(player);

                                    sendMessage(
                                            context.getSource(),
                                            Component.translatable("message.fasteat.status.title")
                                    );

                                    sendMessage(
                                            context.getSource(),
                                            Component.translatable(
                                                    "message.fasteat.status.enabled",
                                                    getEnabledText(settings.isEnabled())
                                            )
                                    );

                                    sendMessage(
                                            context.getSource(),
                                            Component.translatable(
                                                    "message.fasteat.duration.food",
                                                    formatDuration(settings.getEatDuration())
                                            )
                                    );

                                    sendMessage(
                                            context.getSource(),
                                            Component.translatable(
                                                    "message.fasteat.duration.drink",
                                                    formatDuration(settings.getDrinkDuration())
                                            )
                                    );

                                    sendMessage(
                                            context.getSource(),
                                            Component.translatable(
                                                    "message.fasteat.no_slowdown",
                                                    getEnabledText(settings.isNoSlowdown())
                                            )
                                    );

                                    sendMessage(
                                            context.getSource(),
                                            Component.translatable(
                                                    "message.fasteat.once_per_click",
                                                    getEnabledText(settings.isConsumeOncePerClick())
                                            )
                                    );

                                    return 1;
                                })
                        )

                        // ========================
                        // SETTINGS
                        // ========================

                        .then(Commands.literal("settings")

                                // ------------------------
                                // DURATION
                                // ------------------------

                                .then(Commands.literal("duration")

                                        // FOOD
                                        .then(Commands.literal("food")
                                                .executes(context -> {
                                                    ServerPlayer player = getPlayer(context.getSource());
                                                    FastEatPlayerData.Settings settings = getSettings(player);

                                                    sendMessage(
                                                            context.getSource(),
                                                            Component.translatable(
                                                                    "message.fasteat.duration.food",
                                                                    formatDuration(settings.getEatDuration())
                                                            )
                                                    );

                                                    return 1;
                                                })

                                                .then(Commands.argument(
                                                                        "ticks",
                                                                        IntegerArgumentType.integer(1, 72000)
                                                                )
                                                                .executes(context -> {
                                                                    ServerPlayer player = getPlayer(context.getSource());
                                                                    FastEatPlayerData.Settings settings = getSettings(player);

                                                                    int ticks = IntegerArgumentType.getInteger(context, "ticks");

                                                                    settings.setEatDuration(ticks);
                                                                    sync(player);

                                                                    sendMessage(
                                                                            context.getSource(),
                                                                            Component.translatable(
                                                                                    "message.fasteat.duration.food.changed",
                                                                                    formatDuration(ticks)
                                                                            )
                                                                    );

                                                                    return 1;
                                                                })
                                                )
                                        )

                                        // DRINK
                                        .then(Commands.literal("drink")
                                                .executes(context -> {
                                                    ServerPlayer player = getPlayer(context.getSource());
                                                    FastEatPlayerData.Settings settings = getSettings(player);

                                                    sendMessage(
                                                            context.getSource(),
                                                            Component.translatable(
                                                                    "message.fasteat.duration.drink",
                                                                    formatDuration(settings.getDrinkDuration())
                                                            )
                                                    );

                                                    return 1;
                                                })

                                                .then(Commands.argument(
                                                                        "ticks",
                                                                        IntegerArgumentType.integer(1, 72000)
                                                                )
                                                                .executes(context -> {
                                                                    ServerPlayer player = getPlayer(context.getSource());
                                                                    FastEatPlayerData.Settings settings = getSettings(player);

                                                                    int ticks = IntegerArgumentType.getInteger(context, "ticks");

                                                                    settings.setDrinkDuration(ticks);
                                                                    sync(player);

                                                                    sendMessage(
                                                                            context.getSource(),
                                                                            Component.translatable(
                                                                                    "message.fasteat.duration.drink.changed",
                                                                                    formatDuration(ticks)
                                                                            )
                                                                    );

                                                                    return 1;
                                                                })
                                                )
                                        )

                                        // ALL
                                        .then(Commands.literal("all")
                                                .executes(context -> {
                                                    ServerPlayer player = getPlayer(context.getSource());
                                                    FastEatPlayerData.Settings settings = getSettings(player);

                                                    sendMessage(
                                                            context.getSource(),
                                                            Component.translatable(
                                                                    "message.fasteat.duration.food",
                                                                    formatDuration(settings.getEatDuration())
                                                            )
                                                    );

                                                    sendMessage(
                                                            context.getSource(),
                                                            Component.translatable(
                                                                    "message.fasteat.duration.drink",
                                                                    formatDuration(settings.getDrinkDuration())
                                                            )
                                                    );

                                                    return 1;
                                                })

                                                .then(Commands.argument(
                                                                        "ticks",
                                                                        IntegerArgumentType.integer(1, 72000)
                                                                )
                                                                .executes(context -> {
                                                                    ServerPlayer player = getPlayer(context.getSource());
                                                                    FastEatPlayerData.Settings settings = getSettings(player);

                                                                    int ticks = IntegerArgumentType.getInteger(context, "ticks");

                                                                    settings.setEatDuration(ticks);
                                                                    settings.setDrinkDuration(ticks);
                                                                    sync(player);

                                                                    sendMessage(
                                                                            context.getSource(),
                                                                            Component.translatable(
                                                                                    "message.fasteat.duration.all.changed",
                                                                                    formatDuration(ticks)
                                                                            )
                                                                    );

                                                                    return 1;
                                                                })
                                                )
                                        )
                                )

                                // ------------------------
                                // NO SLOWDOWN
                                // ------------------------

                                .then(Commands.literal("noSlowdown")
                                        .executes(context -> {
                                            ServerPlayer player = getPlayer(context.getSource());
                                            FastEatPlayerData.Settings settings = getSettings(player);

                                            sendMessage(
                                                    context.getSource(),
                                                    Component.translatable(
                                                            "message.fasteat.no_slowdown",
                                                            getEnabledText(settings.isNoSlowdown())
                                                    )
                                            );

                                            return 1;
                                        })

                                        .then(Commands.argument("value", BoolArgumentType.bool())
                                                .executes(context -> {
                                                    ServerPlayer player = getPlayer(context.getSource());
                                                    FastEatPlayerData.Settings settings = getSettings(player);

                                                    boolean value = BoolArgumentType.getBool(context, "value");

                                                    settings.setNoSlowdown(value);
                                                    sync(player);

                                                    sendMessage(
                                                            context.getSource(),
                                                            Component.translatable(
                                                                    "message.fasteat.no_slowdown.changed",
                                                                    getEnabledText(value)
                                                            )
                                                    );

                                                    return 1;
                                                })
                                        )
                                )

                                // ------------------------
                                // ONCE PER CLICK
                                // ------------------------

                                .then(Commands.literal("oncePerClick")
                                        .executes(context -> {
                                            ServerPlayer player = getPlayer(context.getSource());
                                            FastEatPlayerData.Settings settings = getSettings(player);

                                            sendMessage(
                                                    context.getSource(),
                                                    Component.translatable(
                                                            "message.fasteat.once_per_click",
                                                            getEnabledText(settings.isConsumeOncePerClick())
                                                    )
                                            );

                                            return 1;
                                        })

                                        .then(Commands.argument("value", BoolArgumentType.bool())
                                                .executes(context -> {
                                                    ServerPlayer player = getPlayer(context.getSource());
                                                    FastEatPlayerData.Settings settings = getSettings(player);

                                                    boolean value = BoolArgumentType.getBool(context, "value");

                                                    settings.setConsumeOncePerClick(value);
                                                    sync(player);

                                                    sendMessage(
                                                            context.getSource(),
                                                            Component.translatable(
                                                                    "message.fasteat.once_per_click.changed",
                                                                    getEnabledText(value)
                                                            )
                                                    );

                                                    return 1;
                                                })
                                        )
                                )
                        )
        );
    }


    // ========================
    // Utility
    // ========================

    private static ServerPlayer getPlayer(CommandSourceStack source) {
        return (ServerPlayer) source.getEntity();
    }

    private static FastEatPlayerData.Settings getSettings(ServerPlayer player) {
        return FastEatPlayerData.get(player.getUUID());
    }

    private static void sync(ServerPlayer player) {
        NetworkHandler.syncSettings(player);
    }

    private static void sendMessage(
            CommandSourceStack source,
            Component message
    ) {
        source.sendSuccess(() -> message, false);
    }

    private static Component getEnabledText(boolean enabled) {
        return Component.translatable(
                enabled
                        ? "message.fasteat.enabled_text"
                        : "message.fasteat.disabled_text"
        );
    }

    private static Component formatDuration(int ticks) {
        return Component.translatable(
                "message.fasteat.duration.value",
                ticks,
                String.format(Locale.ROOT, "%.2f", ticks / 20.0)
        );
    }
}