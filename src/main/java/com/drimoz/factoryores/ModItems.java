package com.drimoz.factoryores;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModItems {
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, FactoryOres.MOD_ID);


    public static final RegistryObject<Item> SCANNER = ITEMS.register(
            "scanner",
            () -> new ScannerItem(new Item.Properties())
    );
}