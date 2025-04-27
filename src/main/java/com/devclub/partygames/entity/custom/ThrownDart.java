package com.devclub.partygames.entity.custom;

import com.devclub.partygames.entity.ModEntities;
import com.devclub.partygames.item.ModItems;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.TridentItem;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import org.lwjgl.system.macosx.LibC;

public class ThrownDart extends AbstractArrow {
    public ThrownDart(EntityType<? extends ThrownDart> type, Level level) {
        super(type, level);
    }

    public ThrownDart(EntityType<? extends ThrownDart> type, LivingEntity shooter, Level level) {
        super(type, level);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        // What you get when you pick it up after it lands
        return new ItemStack(ModItems.DART.get());
    }
}
