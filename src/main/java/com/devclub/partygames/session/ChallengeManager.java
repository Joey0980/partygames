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

public class ChallengeManager {
    // Single Objective for all challenges
    private static Objective scoreObjective;

    /**
     * Starts the sidebar and resets both players to 0.
     * 'rounds' parameter is ignored in this minimal version.
     */
    public static void startChallenge(ServerPlayer a, ServerPlayer b, int rounds) {
        MinecraftServer server = a.getServer();
        Scoreboard sb = server.getScoreboard();

        // get or create
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

        // reset both
        if (a.getUUID().equals(b.getUUID())) {
            // solo play: only one player
            ScoreHolder h = ScoreHolder.forNameOnly(a.getScoreboardName());
            sb.getOrCreatePlayerScore(h, scoreObjective).set(0);
            a.sendSystemMessage(Component.literal("🎯 Solo scoreboard started!"));
        } else {
            // PvP: reset both and message both
            ScoreHolder ha = ScoreHolder.forNameOnly(a.getScoreboardName());
            ScoreHolder hb = ScoreHolder.forNameOnly(b.getScoreboardName());
            sb.getOrCreatePlayerScore(ha, scoreObjective).set(0);
            sb.getOrCreatePlayerScore(hb, scoreObjective).set(0);

            a.sendSystemMessage(Component.literal("🎯 Scoreboard started! You vs. " + b.getScoreboardName()));
            b.sendSystemMessage(Component.literal("🎯 Scoreboard started! You vs. " + a.getScoreboardName()));
        }
    }

    /**
     * Adds 'pts' to the given player's score.
     */
    public static void recordHit(ServerPlayer player, int pts) {
        if (scoreObjective == null) return;
        Scoreboard sb = player.getServer().getScoreboard();
        ScoreHolder holder = ScoreHolder.forNameOnly(player.getScoreboardName());
        ScoreAccess access = sb.getOrCreatePlayerScore(holder, scoreObjective);

        access.set(access.get() + pts);
        player.sendSystemMessage(Component.literal("You scored " + pts + " points!"));
    }

    /**
     * Hides and removes the scoreboard objective.
     */
    public static boolean stopChallenge(ServerPlayer who) {
        if (scoreObjective == null) return false;
        MinecraftServer server = who.getServer();
        Scoreboard sb = server.getScoreboard();

        sb.setDisplayObjective(DisplaySlot.SIDEBAR, null);
        sb.removeObjective(scoreObjective);
        scoreObjective = null;

        who.sendSystemMessage(Component.literal("🎯 Scoreboard removed."));
        return true;
    }
}
