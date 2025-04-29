// src/main/java/com/devclub/partygames/session/ChallengeManager.java
package com.devclub.partygames.session;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;

import java.util.UUID;

public class ChallengeManager {
    // Single Objective for all challenges
    private static Objective scoreObjective;

    // Session state
    private static UUID playerA;
    private static UUID playerB;
    private static int maxRounds;
    private static int throwsA;
    private static int throwsB;

    /**
     * Starts the sidebar, resets both players to 0, and sets up round limits.
     */
    public static void startChallenge(ServerPlayer a, ServerPlayer b, int rounds) {
        playerA = a.getUUID();
        playerB = b.getUUID();
        maxRounds = rounds;
        throwsA = 0;
        throwsB = 0;

        MinecraftServer server = a.getServer();
        Scoreboard sb = server.getScoreboard();

        // get or create the objective
        scoreObjective = sb.getObjective("dartScore");
        if (scoreObjective == null) {
            scoreObjective = sb.addObjective(
                    "dartScore",
                    ObjectiveCriteria.DUMMY,
                    Component.literal("Dart Score"),
                    ObjectiveCriteria.RenderType.INTEGER,
                    false,
                    null
            );
        }

        // show in sidebar
        sb.setDisplayObjective(DisplaySlot.SIDEBAR, scoreObjective);

        // reset both players' scores
        ScoreHolder ha = ScoreHolder.forNameOnly(a.getScoreboardName());
        sb.getOrCreatePlayerScore(ha, scoreObjective).set(0);
        if (!playerA.equals(playerB)) {
            ScoreHolder hb = ScoreHolder.forNameOnly(b.getScoreboardName());
            sb.getOrCreatePlayerScore(hb, scoreObjective).set(0);
        }

        // notify players
        if (playerA.equals(playerB)) {
            a.sendSystemMessage(Component.literal("🎯 Solo game started: " + rounds + " throws."));
        } else {
            a.sendSystemMessage(Component.literal("🎯 Game started vs " + b.getScoreboardName() + ": " + rounds + " throws each."));
            b.sendSystemMessage(Component.literal("🎯 Game started vs " + a.getScoreboardName() + ": " + rounds + " throws each."));
        }
    }

    /**
     * Adds 'pts' to the given player's score, enforces round limit,
     * and ends the game when both have thrown maxRounds times.
     */
    public static void recordHit(ServerPlayer player, int pts) {
        if (scoreObjective == null) return;

        UUID id = player.getUUID();
        boolean isA = id.equals(playerA);
        boolean isB = id.equals(playerB);

        // ignore if not in this session
        if (!isA && !isB) return;

        // enforce throw limits
        if (isA && throwsA >= maxRounds) {
            player.sendSystemMessage(Component.literal("🚫 No throws left."));
            return;
        }
        if (isB && throwsB >= maxRounds) {
            player.sendSystemMessage(Component.literal("🚫 No throws left."));
            return;
        }

        // increment throws count
        if (isA) throwsA++; else throwsB++;

        // update scoreboard
        MinecraftServer server = player.getServer();
        Scoreboard sb = server.getScoreboard();
        ScoreHolder holder = ScoreHolder.forNameOnly(player.getScoreboardName());
        ScoreAccess access = sb.getOrCreatePlayerScore(holder, scoreObjective);
        access.set(access.get() + pts);

        player.sendSystemMessage(Component.literal("You scored " + pts + " points! (" +
                (isA ? throwsA : throwsB) + "/" + maxRounds + " throws)"));

        // if both have finished all rounds, end game
        if (throwsA >= maxRounds && throwsB >= maxRounds) {
            // fetch final scores
            ScoreAccess scoreA = sb.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player.getServer()
                    .getPlayerList().getPlayer(playerA)
                    .getScoreboardName()), scoreObjective);
            ScoreAccess scoreB = sb.getOrCreatePlayerScore(ScoreHolder.forNameOnly(player.getServer()
                    .getPlayerList().getPlayer(playerB)
                    .getScoreboardName()), scoreObjective);

            int sA = scoreA.get();
            int sB = scoreB.get();
            String nameA = player.getServer().getPlayerList().getPlayer(playerA).getScoreboardName();
            String nameB = player.getServer().getPlayerList().getPlayer(playerB).getScoreboardName();

            String winner = sA > sB ? nameA : sB > sA ? nameB : "No one, it's a tie";
            server.getPlayerList()
                    .broadcastSystemMessage(Component.literal("🏆 " + winner +
                            " wins! Final score: " + sA + "–" + sB), false);

            // clean up
            stopChallenge(player);
        }
    }

    /**
     * Hides and removes the scoreboard objective and clears session state.
     */
    public static boolean stopChallenge(ServerPlayer who) {
        if (scoreObjective == null) return false;

        // remove objective
        MinecraftServer server = who.getServer();
        Scoreboard sb = server.getScoreboard();
        sb.setDisplayObjective(DisplaySlot.SIDEBAR, null);
        sb.removeObjective(scoreObjective);
        scoreObjective = null;

        // reset session state
        playerA = null;
        playerB = null;
        maxRounds = 0;
        throwsA = throwsB = 0;

        who.sendSystemMessage(Component.literal("🎯 Game ended."));
        return true;
    }
}
