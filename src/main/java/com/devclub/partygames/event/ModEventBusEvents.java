package com.devclub.partygames.event;

import com.devclub.partygames.PartyGames;
import com.devclub.partygames.entity.client.ThrownDartModel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = PartyGames.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ThrownDartModel.LAYER_LOCATION, ThrownDartModel::createBodyLayer);
    }
}
