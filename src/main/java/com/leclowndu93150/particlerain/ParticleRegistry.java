package com.leclowndu93150.particlerain;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ParticleRainClient.MOD_ID);

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ParticleRainClient.MOD_ID);

    public static final RegistryObject<BasicParticleType> RAIN = PARTICLE_TYPES.register("rain",
            () -> new BasicParticleType(true));
    public static final RegistryObject<BasicParticleType> SNOW = PARTICLE_TYPES.register("snow",
            () -> new BasicParticleType(true));
    public static final RegistryObject<BasicParticleType> DUST_MOTE = PARTICLE_TYPES.register("dust_mote",
            () -> new BasicParticleType(true));
    public static final RegistryObject<BasicParticleType> DUST = PARTICLE_TYPES.register("dust",
            () -> new BasicParticleType(true));
    public static final RegistryObject<BasicParticleType> FOG = PARTICLE_TYPES.register("fog",
            () -> new BasicParticleType(true));
    public static final RegistryObject<BasicParticleType> GROUND_FOG = PARTICLE_TYPES.register("ground_fog",
            () -> new BasicParticleType(true));
    public static final RegistryObject<BasicParticleType> SHRUB = PARTICLE_TYPES.register("shrub",
            () -> new BasicParticleType(true));
    public static final RegistryObject<BasicParticleType> RIPPLE = PARTICLE_TYPES.register("ripple",
            () -> new BasicParticleType(true));
    public static final RegistryObject<BasicParticleType> STREAK = PARTICLE_TYPES.register("streak",
            () -> new BasicParticleType(true));

    public static final RegistryObject<SoundEvent> WEATHER_SNOW = SOUND_EVENTS.register("weather.snow",
            () -> new SoundEvent(new ResourceLocation(ParticleRainClient.MOD_ID, "weather.snow")));
    public static final RegistryObject<SoundEvent> WEATHER_SNOW_ABOVE = SOUND_EVENTS.register("weather.snow.above",
            () -> new SoundEvent(new ResourceLocation(ParticleRainClient.MOD_ID, "weather.snow.above")));
    public static final RegistryObject<SoundEvent> WEATHER_SANDSTORM = SOUND_EVENTS.register("weather.sandstorm",
            () -> new SoundEvent(new ResourceLocation(ParticleRainClient.MOD_ID, "weather.sandstorm")));
    public static final RegistryObject<SoundEvent> WEATHER_SANDSTORM_ABOVE = SOUND_EVENTS.register("weather.sandstorm.above",
            () -> new SoundEvent(new ResourceLocation(ParticleRainClient.MOD_ID, "weather.sandstorm.above")));

    public static void register(IEventBus modEventBus) {
        PARTICLE_TYPES.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);
    }
}