package com.devclub.partygames.item.custom;

import com.devclub.partygames.entity.ModEntities;
import com.devclub.partygames.entity.custom.ThrownDart;
import com.devclub.partygames.item.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class DartItem extends TridentItem {

    public DartItem(Properties properties) {
        super(properties);
    }

//    @Override
//    public InteractionResult useOn(UseOnContext context) {
//        // When the player right-clicks with this item, throw it as a trident
//        Level level = context.getLevel();
//        Player player = context.getPlayer();
//
//        if (player != null && !level.isClientSide) {
////            ThrownTrident thrownTrident = new ThrownTrident(level, player, new ItemStack(ModItems.DART.get()));
////            thrownTrident.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F); // Trident physics
////            level.addFreshEntity(thrownTrident); // Spawn the projectile in the world
//
////            ThrownDart thrownDart = new ThrownDart(level, player, new ItemStack(ModItems.DART.get()));
////            thrownDart.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F); // Trident physics
////            level.addFreshEntity(thrownDart); // Spawn the projectile in the world
//
//            //ThrownDart thrownDart = (ThrownDart) Projectile.spawnProjectileFromRotation(ThrownDart::new, context.getLevel().getServer().getLevel(context.getLevel().dimension()), new ItemStack(ModItems.DART.get()), player, 0.0F, 2.5F, 1.0F);
//        }
//        return InteractionResult.SUCCESS;
//    }

//    @Override
//    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
//        if (!(entity instanceof Player player)) return false;
//
//        int chargeTime = this.getUseDuration(stack, entity) - timeLeft;
//        if (chargeTime < 10) return false;
//
//        if (stack.nextDamageWillBreak()) return false;
//
//        if (level instanceof ServerLevel serverLevel) {
//            stack.hurtWithoutBreaking(1, player); // Reduce durability by 1
//
//            // Create a new ThrownTrident entity
//            ThrownDart throwndart = new ThrownDart(serverLevel, player, stack.copy());
//            throwndart.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F, 1.0F);
//
//            // Set pickup mode depending on player mode
//            if (player.hasInfiniteMaterials()) {
//                throwndart.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
//            } else {
//                player.getInventory().removeItem(stack);
//            }
//
//            // Add the trident to the world
//            serverLevel.addFreshEntity(throwndart);
//
//            return true;
//        }
//
//        return false;
//    }
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        // Begin “charging” just like a trident
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }


    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        if (user instanceof Player player) {
            if (!level.isClientSide) {
                ThrownDart dart = new ThrownDart(ModEntities.THROWN_DART.get(), player, level);
                dart.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F, 1.0F);
                level.addFreshEntity(dart);
                level.playSound(null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.TRIDENT_THROW,
                    SoundSource.PLAYERS,
                    1.0F, 1.0F
                );

                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            player.awardStat(Stats.ITEM_USED.get(this));
        }
        return stack;
    }

}