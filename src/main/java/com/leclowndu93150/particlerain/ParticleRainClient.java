package com.leclowndu93150.particlerain;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
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
        ParticleRegistry.register(modEventBus);
        if(FMLLoader.getDist() == Dist.CLIENT) {
            AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
            config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
            AutoConfig.getConfigHolder(ModConfig.class).registerSaveListener(ClientStuff::saveListener);
            modEventBus.addListener(ClientStuff::registerParticles);
            ClientStuff.setupClient();
        }
    }

}