package com.devclub.partygames.item;

import com.devclub.partygames.PartyGames;
import com.devclub.partygames.item.custom.DartItem;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(
        PartyGames.MOD_ID
    );

    public static final DeferredItem<Item> DART = ITEMS.registerItem(
        "dart",
        DartItem::new,
        new Item.Properties().stacksTo(16)
    );

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
