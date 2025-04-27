package com.devclub.partygames.entity;

import com.devclub.partygames.PartyGames;
import com.devclub.partygames.entity.custom.ThrownDart;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {

    public static final DeferredRegister.Entities ENTITY_TYPES =
        DeferredRegister.createEntities(PartyGames.MOD_ID);

    public static final Supplier<EntityType<ThrownDart>> THROWN_DART =
        ENTITY_TYPES.register("thrown_dart",
            () -> EntityType.Builder.<ThrownDart>of(ThrownDart::new, MobCategory.MISC)
                .sized(0.5f, 0.5f)
                .clientTrackingRange(4)
                .updateInterval(20)
                .build(ResourceKey.create(
                    Registries.ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath(PartyGames.MOD_ID, "thrown_dart")
                ))
        );

    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }
}
