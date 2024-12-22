package com.leclowndu93150.particlerain;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, ParticleRainClient.MODID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, ParticleRainClient.MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RAIN = PARTICLE_TYPES.register("rain",
            () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SNOW = PARTICLE_TYPES.register("snow",
            () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DUST_MOTE = PARTICLE_TYPES.register("dust_mote",
            () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> DUST = PARTICLE_TYPES.register("dust",
            () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FOG = PARTICLE_TYPES.register("fog",
            () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GROUND_FOG = PARTICLE_TYPES.register("ground_fog",
            () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SHRUB = PARTICLE_TYPES.register("shrub",
            () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RIPPLE = PARTICLE_TYPES.register("ripple",
            () -> new SimpleParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> STREAK = PARTICLE_TYPES.register("streak",
            () -> new SimpleParticleType(true));

    public static final DeferredHolder<SoundEvent,SoundEvent> WEATHER_SNOW = SOUND_EVENTS.register("weather.snow",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ParticleRainClient.MODID, "weather.snow")));
    public static final DeferredHolder<SoundEvent,SoundEvent> WEATHER_SNOW_ABOVE = SOUND_EVENTS.register("weather.snow.above",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ParticleRainClient.MODID, "weather.snow.above")));
    public static final DeferredHolder<SoundEvent,SoundEvent> WEATHER_SANDSTORM = SOUND_EVENTS.register("weather.sandstorm",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ParticleRainClient.MODID, "weather.sandstorm")));
    public static final DeferredHolder<SoundEvent,SoundEvent> WEATHER_SANDSTORM_ABOVE = SOUND_EVENTS.register("weather.sandstorm.above",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(ParticleRainClient.MODID, "weather.sandstorm.above")));
}