package com.devclub.partygames.item.custom;

import com.devclub.partygames.item.ModItems;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class DartItem extends TridentItem {

    public DartItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // When the player right-clicks with this item, throw it as a trident
        Level level = context.getLevel();
        Player player = context.getPlayer();

        if (player != null && !level.isClientSide) {
            ThrownTrident thrownTrident = new ThrownTrident(level, player, new ItemStack(ModItems.DART.get()));
            thrownTrident.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F); // Trident physics
            level.addFreshEntity(thrownTrident); // Spawn the projectile in the world
        }
        return InteractionResult.SUCCESS;
    }
}