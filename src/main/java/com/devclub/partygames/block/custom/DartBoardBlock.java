package com.devclub.partygames.block.custom;

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

    // handle being hit by arrow
    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (!level.isClientSide && projectile instanceof AbstractArrow) {
            int score = computeScore(hit, hit.getLocation());
            spawnFloatingNumber((ServerLevel) level, hit.getBlockPos(), hit.getLocation(), score);
            ((ServerLevel) level).scheduleTick(hit.getBlockPos(), this, DISPLAY_TICKS);
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

        // the board graphic has a 2-pixel border on all sides of a 16×16 grid → inner radius = 6px/16
        double boardRadius = 6.0 / 16.0;
        if (d > boardRadius) {
            return 0;  // you hit the wood
        }

        double norm = (boardRadius - d) / boardRadius;
        return Mth.ceil(15.0 * norm);
    }

    // create invisible armor stand
    private void spawnFloatingNumber(ServerLevel level, BlockPos pos, Vec3 loc, int score) {
        ArmorStand stand = (ArmorStand) EntityType.ARMOR_STAND.create(level);
        if (stand == null) return;

        // spawn stand at projectile point
        stand.setPos(loc.x, loc.y-1.5, loc.z);
        stand.setInvisible(true);
        stand.setNoGravity(true);
        //stand.setSmall(true); // PRIVATE?
        stand.setNoBasePlate(true);

        // display score
        stand.setCustomName(Component.literal(Integer.toString(score)));
        stand.setCustomNameVisible(true);
        stand.getPersistentData().putBoolean("DartScore", true);

        level.addFreshEntity(stand);
    }

    // clean up old (also tagged aboe) armor stands
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        List<ArmorStand> stands = level.getEntitiesOfClass(
                ArmorStand.class,
                new AABB(pos).inflate(1)
        );

        // kill each armor stand with DartScore tag
        for (ArmorStand s : stands) {
            if (s.getPersistentData().getBoolean("DartScore")) {
                s.kill();
            }
        }
    }
}