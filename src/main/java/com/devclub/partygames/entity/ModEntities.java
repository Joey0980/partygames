package com.devclub.partygames.entity;

import com.devclub.partygames.PartyGames;
import com.devclub.partygames.entity.custom.ThrownDartEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, PartyGames.MOD_ID);

    public static final Supplier<EntityType<ThrownDartEntity>> THROWN_DART =
        ENTITY_TYPES.register(
            "thrown_dart",
            () -> EntityType.Builder.<ThrownDartEntity>of(ThrownDartEntity::new, MobCategory.MISC)
                .sized(1.0f, 1.0f)
                .build("thrown_dart")
        );


    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }
}
