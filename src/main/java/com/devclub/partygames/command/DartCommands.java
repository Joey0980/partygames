// src/main/java/com/devclub/partygames/command/DartCommands.java
package com.devclub.partygames.command;

import com.devclub.partygames.session.ChallengeManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class DartCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("dart")
                // /dart challenge <rounds> [player]
                .then(Commands.literal("challenge")
                        .then(Commands.argument("rounds", IntegerArgumentType.integer(1))
                                // solo if no target
                                .executes(ctx -> {
                                    CommandSourceStack src  = ctx.getSource();
                                    ServerPlayer player     = src.getPlayerOrException();
                                    int rounds              = IntegerArgumentType.getInteger(ctx, "rounds");

                                    // solo = pass same player twice
                                    ChallengeManager.startChallenge(player, player, rounds);

                                    src.sendSuccess(() -> Component.literal(
                                            "🎯 Started solo dart—" + rounds + " throws!"
                                    ), true);
                                    return 1;
                                })
                                // PvP if target provided
                                .then(Commands.argument("target", EntityArgument.player())
                                        .executes(ctx -> {
                                            CommandSourceStack src       = ctx.getSource();
                                            ServerPlayer challenger      = src.getPlayerOrException();
                                            ServerPlayer opponent        = EntityArgument.getPlayer(ctx, "target");
                                            int rounds                   = IntegerArgumentType.getInteger(ctx, "rounds");

                                            ChallengeManager.startChallenge(challenger, opponent, rounds);

                                            src.sendSuccess(() -> Component.literal(
                                                    "🎯 " + challenger.getName().getString() +
                                                            " challenged " + opponent.getName().getString() +
                                                            " to " + rounds + " throws each!"
                                            ), true);
                                            return 1;
                                        })
                                )
                        )
                )
                // /dart stop
                .then(Commands.literal("stop")
                        .executes(ctx -> {
                            CommandSourceStack src   = ctx.getSource();
                            ServerPlayer player      = src.getPlayerOrException();
                            boolean wasStopped       = ChallengeManager.stopChallenge(player);
                            String message = wasStopped
                                    ? "🎯 Dart challenge stopped."
                                    : "⚠️ No active dart game to stop.";

                            src.sendSuccess(() -> Component.literal(message), true);
                            return 1;
                        })
                )
        );
    }
}
