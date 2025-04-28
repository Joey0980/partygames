package com.devclub.partygames.item;

import com.devclub.partygames.PartyGames;
import com.devclub.partygames.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PartyGames.MOD_ID);

    public static final Supplier<CreativeModeTab> DARTS_TAB = CREATIVE_MODE_TAB.register("darts", () -> CreativeModeTab.builder()
        .title(Component.translatable("creativetab." + PartyGames.MOD_ID + ".darts"))
        .icon(() -> new ItemStack(ModItems.DART.get()))
        .displayItems((params, output) -> {
            output.accept(ModItems.DART.get());
            output.accept(ModBlocks.DART_BOARD_BLOCK);
        })
        .build()
    );

    public static void register(IEventBus bus) {
        CREATIVE_MODE_TAB.register(bus);
    }
}
