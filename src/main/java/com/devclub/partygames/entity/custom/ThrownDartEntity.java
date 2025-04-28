package com.devclub.partygames.entity.custom;

import com.devclub.partygames.entity.ModEntities;
import com.devclub.partygames.item.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.client.renderer.entity.ArrowRenderer;

public class ThrownDartEntity extends AbstractArrow {
    public ThrownDartEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownDartEntity(LivingEntity shooter, Level level) {
        super(ModEntities.THROWN_DART.get(), shooter, level, new ItemStack(ModItems.DART.get()), null);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ModItems.DART.get());
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        return;
    }
}
