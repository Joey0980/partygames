package com.devclub.partygames.block.custom;

import com.mojang.serialization.MapCodec;
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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DartBoardBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<DartBoardBlock> CODEC = simpleCodec(DartBoardBlock::new);
    private static final int DISPLAY_TICKS = 20;

    // 1px-thick faces on each side
    private static final VoxelShape NORTH_SHAPE = Block.box(0, 0, 0, 16, 16, 1);
    private static final VoxelShape SOUTH_SHAPE = Block.box(0, 0, 15, 16, 16, 16);
    private static final VoxelShape WEST_SHAPE  = Block.box(0, 0, 0, 1, 16, 16);
    private static final VoxelShape EAST_SHAPE  = Block.box(15, 0, 0, 16, 16, 16);

    public DartBoardBlock(Properties properties) {
        super(properties);
        // default facing so getShape never sees null
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.SOUTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return switch (state.getValue(FACING)) {
            case NORTH -> SOUTH_SHAPE;
            case SOUTH -> NORTH_SHAPE;
            case WEST  -> EAST_SHAPE;
            case EAST  -> WEST_SHAPE;
            default    -> NORTH_SHAPE;   // covers any other Direction
        };
    }

    public void onProjectileHit(BlockState state, Level world, BlockHitResult hit, Projectile projectile) {
        if (!world.isClientSide && projectile instanceof AbstractArrow) {
            int score = computeScore(hit, hit.getLocation());
            spawnFloatingNumber((ServerLevel) world, hit.getBlockPos(), hit.getLocation(), score);
            world.scheduleTick(hit.getBlockPos(), this, DISPLAY_TICKS);
        }
    }

    private static int computeScore(BlockHitResult hit, Vec3 loc) {
        Direction dir = hit.getDirection();
        double dx = Math.abs(Mth.frac(loc.x) - 0.5);
        double dy = Math.abs(Mth.frac(loc.y) - 0.5);
        double dz = Math.abs(Mth.frac(loc.z) - 0.5);

        double d = switch (dir.getAxis()) {
            case Y -> Math.max(dx, dz);
            case Z -> Math.max(dx, dy);
            default -> Math.max(dy, dz);
        };

        double clamped = Mth.clamp((0.5 - d) / 0.5, 0.0, 1.0);
        return Math.max(1, Mth.ceil(15.0 * clamped));
    }

    private void spawnFloatingNumber(ServerLevel level, BlockPos pos, Vec3 loc, int score) {
        ArmorStand stand = EntityType.ARMOR_STAND.create(level);
        if (stand == null) return;

        stand.setPos(loc.x, loc.y - 1, loc.z);
        stand.setInvisible(true);
        stand.setNoGravity(true);
        //stand.setSmall(true);
        stand.setNoBasePlate(true);

        stand.setCustomName(Component.literal(Integer.toString(score)));
        stand.setCustomNameVisible(true);
        stand.getPersistentData().putBoolean("DartScore", true);

        level.addFreshEntity(stand);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // use AABB(BlockPos) + inflate, so BlockPos is allowed
        List<ArmorStand> stands = level.getEntitiesOfClass(
                ArmorStand.class,
                new AABB(pos).inflate(1.0D)
        );
        for (ArmorStand s : stands) {
            if (s.getPersistentData().getBoolean("DartScore")) {
                s.kill();
            }
        }
    }
}
