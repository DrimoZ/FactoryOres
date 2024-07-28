package com.drimoz.factoryores;

import com.drimoz.factoryores.core.utils.FO_Loggers;
import com.drimoz.factoryores.tests.NoiseMap;
import com.drimoz.factoryores.tests.OreGenerator;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.List;

@Mod(com.drimoz.factoryores.FactoryOres.MOD_ID)
public class FactoryOres
{
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final String MOD_ID = "factoryores";
    public static final String MOD_NAME = "Factory'Ores";

    private static final String CONFIG_PATH = "config/" + MOD_ID + "/config.json";
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);


    public static final RegistryObject<CreativeModeTab> MOD_CREATIVE_TAB = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.SCANNER.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.SCANNER.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());

    public FactoryOres()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the Deferred Register to the mod event bus so items get registered
        ModItems.ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, FactoryOres.MOD_ID + "/factoryores-common.toml");

        FO_Loggers.modLoadedInfo();
    }

    @SubscribeEvent
    public void onCommonSetup(final FMLCommonSetupEvent event)
    {

        FO_Loggers.eventCompletedInfo("onCommonSetup");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        FO_Loggers.eventCompletedInfo("onServerStarting");
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        main(event.getServer().getLevel(ServerLevel.OVERWORLD));

        FO_Loggers.eventCompletedInfo("onServerStarted");
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        FO_Loggers.eventCompletedInfo("onClientSetup");
    }

    @SubscribeEvent
    public void addCreative(BuildCreativeModeTabContentsEvent event)
    {

    }

    private void main(ServerLevel level) {
        int width = 512; // Width of the area to generate noise for
        int height = 512; // Height of the area to generate noise for
        int lowNoiseMapSize = 32; // Size of the low-level noise map

        // Initialize high-level noise maps for different ores
        NoiseMap coalHighNoiseMap = new NoiseMap(width, height, level.getSeed() + "coal".hashCode());
        NoiseMap ironHighNoiseMap = new NoiseMap(width, height, level.getSeed() + "iron".hashCode());
        NoiseMap goldHighNoiseMap = new NoiseMap(width, height, level.getSeed() + "gold".hashCode());
        NoiseMap diamondHighNoiseMap = new NoiseMap(width, height, level.getSeed() + "diamond".hashCode());

        // Initialize the ore generator
        OreGenerator generator = new OreGenerator(level, width, height);

        // Define high-level thresholds for each ore type
        double highThreshold = 0.9;

        // Apply high-level thresholds to generate patch locations
        List<int[]> coalPatchLocations = generator.applyThreshold(coalHighNoiseMap.getNoiseMap(), highThreshold);
        List<int[]> ironPatchLocations = generator.applyThreshold(ironHighNoiseMap.getNoiseMap(), highThreshold);
        List<int[]> goldPatchLocations = generator.applyThreshold(goldHighNoiseMap.getNoiseMap(), highThreshold);
        List<int[]> diamondPatchLocations = generator.applyThreshold(diamondHighNoiseMap.getNoiseMap(), highThreshold);

        int radius = 8; // Radius of the blob
        int richness = 50; // Richness of the ore

        // Place ore blocks within the smoothed high-level patches
        generator.placeOreBlocks(coalPatchLocations, Blocks.COAL_ORE, 6, richness);
        generator.placeOreBlocks(ironPatchLocations, Blocks.IRON_ORE, 5, richness);
        generator.placeOreBlocks(goldPatchLocations, Blocks.GOLD_ORE, 3, richness);
        generator.placeOreBlocks(diamondPatchLocations, Blocks.DIAMOND_ORE, 1, richness);

    }
}
