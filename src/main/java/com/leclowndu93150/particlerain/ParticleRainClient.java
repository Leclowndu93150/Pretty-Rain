package com.leclowndu93150.particlerain;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(ParticleRainClient.MOD_ID)
public class ParticleRainClient{

    public static final String MOD_ID = "particlerain";
    public static int particleCount;
    public static int fogCount;
    public static ModConfig config;

    public ParticleRainClient() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        if(FMLLoader.getDist() == Dist.CLIENT) {
            modEventBus.addListener(ClientStuff::registerParticles);
            AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
            config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            AutoConfig.getConfigHolder(ModConfig.class).registerSaveListener(ClientStuff::saveListener);
        }
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        ParticleRegistry.register(modEventBus);
    }

    //do cloud height config in 1.21.1

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(NetworkHandler::init);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        WeatherParticleSpawner.isTFCloaded = ModList.get().isLoaded("tfc");
    }
}