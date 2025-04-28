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

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class DartBoardBlock extends HorizontalDirectionalBlock {
    private static final int DISPLAY_TICKS = 20;
    public static final MapCodec<DartBoardBlock> CODEC = simpleCodec(DartBoardBlock::new);
    private static final VoxelShape SHAPE = Block.box(7, 0, -5, 9, 26, 21);

    public DartBoardBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
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

    // convert redstone strength into getting score
    private static int computeScore(BlockHitResult hit, Vec3 loc) {
        Direction dir = hit.getDirection();
        double dx = Math.abs(Mth.frac(loc.x) - 0.5);
        double dy = Math.abs(Mth.frac(loc.y) - 0.5);
        double dz = Math.abs(Mth.frac(loc.z) - 0.5);

        Direction.Axis axis = dir.getAxis();
        double d;
        if (axis == Direction.Axis.Y) {
            d = Math.max(dx, dz);
        } else if (axis == Direction.Axis.Z) {
            d = Math.max(dx, dy);
        } else {
            d = Math.max(dy, dz);
        }

        double clamped = Mth.clamp((0.5 - d) / 0.5, 0.0, 1.0);
        return Math.max(1, Mth.ceil(15.0 * clamped));
    }

    // create invisible armor stand
    private void spawnFloatingNumber(ServerLevel level, BlockPos pos, Vec3 loc, int score) {
        ArmorStand stand = (ArmorStand) EntityType.ARMOR_STAND.create(level);
        if (stand == null) return;

        // spawn stand at projectile point
        stand.setPos(loc.x, loc.y-1, loc.z);
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
