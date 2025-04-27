package com.devclub.partygames.entity.custom;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ThrownDart extends ThrownTrident {
    public ThrownDart(EntityType<? extends ThrownTrident> type, Level world) {
        super(type, world);
    }

    public ThrownDart(Level world, LivingEntity owner, ItemStack stack) {
        super(EntityType.TRIDENT, world);
        this.setOwner(owner);
        //this.tridentItem = stack.copy();
    }

    @Override
    protected ItemStack getPickupItem() {
        return this.tridentItem.copy();
    }
}
