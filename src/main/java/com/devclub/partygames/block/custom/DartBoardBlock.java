package com.devclub.partygames.block.custom;

import com.devclub.partygames.session.ChallengeManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class DartBoardBlock extends Block {
    private static final int DISPLAY_TICKS = 20;

    public DartBoardBlock(Properties properties) {
        super(properties);
    }

    // handle being hit by a dart (arrow)
    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (level instanceof ServerLevel server && projectile instanceof AbstractArrow arrow) {
            // 1) compute score
            int score = computeScore(hit, hit.getLocation());

            // 2) show floating number
            spawnFloatingNumber(server, hit.getBlockPos(), hit.getLocation(), score);

            // 3) record to scoreboard
            if (arrow.getOwner() instanceof ServerPlayer player) {
                ChallengeManager.recordHit(player, score);
            }

            // 4) schedule cleanup of the floating text
            server.scheduleTick(hit.getBlockPos(), this, DISPLAY_TICKS);
        }
    }

    // convert hit location into a 0–15 score
    private static int computeScore(BlockHitResult hit, Vec3 loc) {
        Direction dir = hit.getDirection();
        double dx = Math.abs(Mth.frac(loc.x) - 0.5);
        double dy = Math.abs(Mth.frac(loc.y) - 0.5);
        double dz = Math.abs(Mth.frac(loc.z) - 0.5);

        double d;
        switch (dir.getAxis()) {
            case Y -> d = Math.max(dx, dz);
            case Z -> d = Math.max(dx, dy);
            default /*X*/ -> d = Math.max(dy, dz);
        }

        // 2-pixel border on a 16×16 grid → inner radius = 6px/16
        double boardRadius = 6.0 / 16.0;
        if (d > boardRadius) {
            return 0;  // hit the wood
        }

        double norm = (boardRadius - d) / boardRadius;
        return Mth.ceil(15.0 * norm);
    }

    // spawn an invisible armor stand to display the score
    private void spawnFloatingNumber(ServerLevel level, BlockPos pos, Vec3 loc, int score) {
        ArmorStand stand = (ArmorStand) EntityType.ARMOR_STAND.create(level);
        if (stand == null) return;

        stand.setPos(loc.x, loc.y - 1.5, loc.z);
        stand.setInvisible(true);
        stand.setNoGravity(true);
        stand.setNoBasePlate(true);

        stand.setCustomName(Component.literal(Integer.toString(score)));
        stand.setCustomNameVisible(true);
        stand.getPersistentData().putBoolean("DartScore", true);

        level.addFreshEntity(stand);
    }

    // clean up old armor stands tagged "DartScore"
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        List<ArmorStand> stands = level.getEntitiesOfClass(
                ArmorStand.class,
                new AABB(pos).inflate(1)
        );
        for (ArmorStand s : stands) {
            if (s.getPersistentData().getBoolean("DartScore")) {
                s.kill();
            }
        }
    }
}
