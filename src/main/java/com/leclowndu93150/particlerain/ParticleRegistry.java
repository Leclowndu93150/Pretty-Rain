package com.leclowndu93150.particlerain;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, ParticleRainClient.MODID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, ParticleRainClient.MODID);

    public static final RegistryObject<SimpleParticleType> RAIN = PARTICLE_TYPES.register("rain",
            () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SNOW = PARTICLE_TYPES.register("snow",
            () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> DUST_MOTE = PARTICLE_TYPES.register("dust_mote",
            () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> DUST = PARTICLE_TYPES.register("dust",
            () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> FOG = PARTICLE_TYPES.register("fog",
            () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> GROUND_FOG = PARTICLE_TYPES.register("ground_fog",
            () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> SHRUB = PARTICLE_TYPES.register("shrub",
            () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> RIPPLE = PARTICLE_TYPES.register("ripple",
            () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> STREAK = PARTICLE_TYPES.register("streak",
            () -> new SimpleParticleType(true));

    public static final RegistryObject<SoundEvent> WEATHER_SNOW = SOUND_EVENTS.register("weather.snow",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ParticleRainClient.MODID, "weather.snow")));
    public static final RegistryObject<SoundEvent> WEATHER_SNOW_ABOVE = SOUND_EVENTS.register("weather.snow.above",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ParticleRainClient.MODID, "weather.snow.above")));
    public static final RegistryObject<SoundEvent> WEATHER_SANDSTORM = SOUND_EVENTS.register("weather.sandstorm",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ParticleRainClient.MODID, "weather.sandstorm")));
    public static final RegistryObject<SoundEvent> WEATHER_SANDSTORM_ABOVE = SOUND_EVENTS.register("weather.sandstorm.above",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(ParticleRainClient.MODID, "weather.sandstorm.above")));
}