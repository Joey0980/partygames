package com.devclub.partygames.item.custom;

import com.devclub.partygames.entity.custom.ThrownDartEntity;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DartItem extends Item {
    public DartItem(Properties properties) {
        super(properties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand); // <- Instead of throwing instantly, start "charging"
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (livingEntity instanceof Player player) {
            int useDuration = this.getUseDuration(stack, player) - timeLeft;

            // Optionally: only allow shooting after charging a little bit
            if (useDuration < 10) return; // Must charge at least 10 ticks (half a second)

            if (!level.isClientSide) {
                ThrownDartEntity dartEntity = new ThrownDartEntity(player, level);

                // Set power based on how long you charged (optional: more charge = faster dart)
                float velocity = getPowerForTime(useDuration);
                dartEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity * 2.5F, 1.0F);

                level.addFreshEntity(dartEntity);
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);

                if (!player.getAbilities().instabuild) {
                    stack.shrink(1); // Consume the dart
                }
            }

            player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(this));
        }
    }

    private static float getPowerForTime(int charge) {
        float f = (float) charge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }
}