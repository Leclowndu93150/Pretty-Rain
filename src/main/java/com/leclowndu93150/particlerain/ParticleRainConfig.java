package com.leclowndu93150.particlerain;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = ParticleRainClient.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ParticleRainConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // General
    private static final ModConfigSpec.IntValue MAX_PARTICLE_AMOUNT = BUILDER
            .comment("Maximum number of particles allowed")
            .defineInRange("maxParticleAmount", 1500, 0, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue PARTICLE_DENSITY = BUILDER
            .defineInRange("particleDensity", 100, 0, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue PARTICLE_STORM_DENSITY = BUILDER
            .defineInRange("particleStormDensity", 200, 0, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue PARTICLE_RADIUS = BUILDER
            .defineInRange("particleRadius", 25, 0, Integer.MAX_VALUE);

    // Particle toggles
    private static final ModConfigSpec.BooleanValue DO_RAIN_PARTICLES = BUILDER
            .define("doRainParticles", true);
    private static final ModConfigSpec.BooleanValue DO_SPLASH_PARTICLES = BUILDER
            .define("doSplashParticles", true);
    private static final ModConfigSpec.BooleanValue DO_SMOKE_PARTICLES = BUILDER
            .define("doSmokeParticles", true);
    private static final ModConfigSpec.BooleanValue DO_RIPPLE_PARTICLES = BUILDER
            .define("doRippleParticles", true);
    private static final ModConfigSpec.BooleanValue DO_STREAK_PARTICLES = BUILDER
            .define("doStreakParticles", true);
    private static final ModConfigSpec.BooleanValue DO_SNOW_PARTICLES = BUILDER
            .define("doSnowParticles", true);
    private static final ModConfigSpec.BooleanValue DO_SAND_PARTICLES = BUILDER
            .define("doSandParticles", true);
    private static final ModConfigSpec.BooleanValue DO_SHRUB_PARTICLES = BUILDER
            .define("doShrubParticles", true);
    private static final ModConfigSpec.BooleanValue DO_FOG_PARTICLES = BUILDER
            .define("doFogParticles", false);
    private static final ModConfigSpec.BooleanValue DO_GROUND_FOG_PARTICLES = BUILDER
            .define("doGroundFogParticles", true);

    // Rain options
    private static final ModConfigSpec.IntValue RAIN_DENSITY = BUILDER
            .comment("Rain particle density")
            .defineInRange("rain.density", 100, 1, 100);
    private static final ModConfigSpec.DoubleValue RAIN_GRAVITY = BUILDER
            .defineInRange("rain.gravity", 1.0, 0.0, Double.MAX_VALUE);
    private static final ModConfigSpec.DoubleValue RAIN_WIND_STRENGTH = BUILDER
            .defineInRange("rain.windStrength", 0.3, 0.0, Double.MAX_VALUE);
    private static final ModConfigSpec.DoubleValue RAIN_STORM_WIND_STRENGTH = BUILDER
            .defineInRange("rain.stormWindStrength", 0.5, 0.0, Double.MAX_VALUE);
    private static final ModConfigSpec.IntValue RAIN_OPACITY = BUILDER
            .defineInRange("rain.opacity", 100, 1, 100);
    private static final ModConfigSpec.IntValue RAIN_SPLASH_DENSITY = BUILDER
            .defineInRange("rain.splashDensity", 5, 0, Integer.MAX_VALUE);
    private static final ModConfigSpec.DoubleValue RAIN_SIZE = BUILDER
            .defineInRange("rain.size", 2.0, 0.0, Double.MAX_VALUE);

    // Snow options
    private static final ModConfigSpec.IntValue SNOW_DENSITY = BUILDER
            .defineInRange("snow.density", 40, 1, 100);
    private static final ModConfigSpec.DoubleValue SNOW_GRAVITY = BUILDER
            .defineInRange("snow.gravity", 0.08, 0.0, Double.MAX_VALUE);
    private static final ModConfigSpec.DoubleValue SNOW_ROTATION_AMOUNT = BUILDER
            .defineInRange("snow.rotationAmount", 0.03, 0.0, Double.MAX_VALUE);
    private static final ModConfigSpec.DoubleValue SNOW_STORM_ROTATION_AMOUNT = BUILDER
            .defineInRange("snow.stormRotationAmount", 0.05, 0.0, Double.MAX_VALUE);
    private static final ModConfigSpec.DoubleValue SNOW_WIND_STRENGTH = BUILDER
            .defineInRange("snow.windStrength", 1.0, 0.0, Double.MAX_VALUE);
    private static final ModConfigSpec.DoubleValue SNOW_STORM_WIND_STRENGTH = BUILDER
            .defineInRange("snow.stormWindStrength", 3.0, 0.0, Double.MAX_VALUE);
    private static final ModConfigSpec.DoubleValue SNOW_SIZE = BUILDER
            .defineInRange("snow.size", 2.0, 0.0, Double.MAX_VALUE);

    // Ripple options
    private static final ModConfigSpec.IntValue RIPPLE_RESOLUTION = BUILDER
            .defineInRange("ripple.resolution", 16, 4, 256);
    private static final ModConfigSpec.BooleanValue USE_RESOURCEPACK_RESOLUTION = BUILDER
            .define("ripple.useResourcepackResolution", true);

    // Sand options
    private static final ModConfigSpec.IntValue SAND_DENSITY = BUILDER
            .defineInRange("sand.density", 80, 1, 100);
    private static final ModConfigSpec.DoubleValue SAND_GRAVITY = BUILDER
            .defineInRange("sand.gravity", 0.2, 0.0, Double.MAX_VALUE);
    private static final ModConfigSpec.DoubleValue SAND_WIND_STRENGTH = BUILDER
            .defineInRange("sand.windStrength", 0.3, 0.0, Double.MAX_VALUE);
    private static final ModConfigSpec.DoubleValue SAND_MOTE_SIZE = BUILDER
            .defineInRange("sand.moteSize", 0.1, 0.0, Double.MAX_VALUE);
    private static final ModConfigSpec.DoubleValue SAND_SIZE = BUILDER
            .defineInRange("sand.size", 2.0, 0.0, Double.MAX_VALUE);
    private static final ModConfigSpec.BooleanValue SAND_SPAWN_ON_GROUND = BUILDER
            .define("sand.spawnOnGround", true);
    private static final ModConfigSpec.ConfigValue<String> SAND_MATCH_TAGS = BUILDER
            .define("sand.matchTags", "minecraft:camel_sand_step_sound_blocks");

    // Shrub options
    private static final ModConfigSpec.IntValue SHRUB_DENSITY = BUILDER
            .defineInRange("shrub.density", 2, 1, 100);
    private static final ModConfigSpec.DoubleValue SHRUB_GRAVITY = BUILDER
            .defineInRange("shrub.gravity", 0.2, 0.0, Double.MAX_VALUE);
    private static final ModConfigSpec.DoubleValue SHRUB_ROTATION_AMOUNT = BUILDER
            .defineInRange("shrub.rotationAmount", 0.2, 0.0, Double.MAX_VALUE);
    private static final ModConfigSpec.DoubleValue SHRUB_BOUNCINESS = BUILDER
            .defineInRange("shrub.bounciness", 0.2, 0.0, Double.MAX_VALUE);

    // Fog options
    private static final ModConfigSpec.IntValue FOG_DENSITY = BUILDER
            .defineInRange("fog.density", 20, 1, 100);
    private static final ModConfigSpec.DoubleValue FOG_GRAVITY = BUILDER
            .defineInRange("fog.gravity", 0.2, 0.0, Double.MAX_VALUE);
    private static final ModConfigSpec.DoubleValue FOG_SIZE = BUILDER
            .defineInRange("fog.size", 0.5, 0.0, Double.MAX_VALUE);

    // Ground fog options
    private static final ModConfigSpec.IntValue GROUND_FOG_DENSITY = BUILDER
            .defineInRange("groundFog.density", 20, 1, 100);
    private static final ModConfigSpec.IntValue GROUND_FOG_SPAWN_HEIGHT = BUILDER
            .defineInRange("groundFog.spawnHeight", 64, 0, 256);
    private static final ModConfigSpec.DoubleValue GROUND_FOG_SIZE = BUILDER
            .defineInRange("groundFog.size", 8.0, 0.0, Double.MAX_VALUE);

    // Sound toggles
    private static final ModConfigSpec.BooleanValue DO_RAIN_SOUNDS = BUILDER
            .define("doRainSounds", true);
    private static final ModConfigSpec.BooleanValue DO_SNOW_SOUNDS = BUILDER
            .define("doSnowSounds", true);
    private static final ModConfigSpec.BooleanValue DO_SAND_SOUNDS = BUILDER
            .define("doSandSounds", true);

    // Additional settings
    private static final ModConfigSpec.BooleanValue RENDER_VANILLA_WEATHER = BUILDER
            .define("renderVanillaWeather", false);
    private static final ModConfigSpec.BooleanValue TICK_VANILLA_WEATHER = BUILDER
            .define("tickVanillaWeather", false);
    private static final ModConfigSpec.BooleanValue BIOME_TINT = BUILDER
            .define("biomeTint", true);
    private static final ModConfigSpec.IntValue TINT_MIX = BUILDER
            .defineInRange("tintMix", 50, 0, 100);
    private static final ModConfigSpec.BooleanValue SPAWN_ABOVE_CLOUDS = BUILDER
            .define("spawnAboveClouds", false);
    private static final ModConfigSpec.IntValue CLOUD_HEIGHT = BUILDER
            .defineInRange("cloudHeight", 191, 0, 256);
    private static final ModConfigSpec.BooleanValue ALWAYS_RAINING = BUILDER
            .define("alwaysRaining", false);
    private static final ModConfigSpec.BooleanValue Y_LEVEL_WIND_ADJUSTMENT = BUILDER
            .define("yLevelWindAdjustment", true);
    private static final ModConfigSpec.BooleanValue SYNC_REGISTRY = BUILDER
            .define("syncRegistry", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    // Public fields for access
    public static int maxParticleAmount;
    public static int particleDensity;
    public static int particleStormDensity;
    public static int particleRadius;
    public static boolean doRainParticles;
    public static boolean doSplashParticles;
    public static boolean doSmokeParticles;
    public static boolean doRippleParticles;
    public static boolean doStreakParticles;
    public static boolean doSnowParticles;
    public static boolean doSandParticles;
    public static boolean doShrubParticles;
    public static boolean doFogParticles;
    public static boolean doGroundFogParticles;
    public static boolean doRainSounds;
    public static boolean doSnowSounds;
    public static boolean doSandSounds;
    public static int rippleResolution;
    public static boolean useResourcepackResolution;
    public static int groundFogDensity;
    public static boolean renderVanillaWeather;
    public static boolean tickVanillaWeather;
    public static boolean biomeTint;
    public static int tintMix;
    public static boolean spawnAboveClouds;
    public static int cloudHeight;
    public static boolean alwaysRaining;
    public static boolean yLevelWindAdjustment;
    public static boolean syncRegistry;

    public static class RainOptions {
        public static int density;
        public static float gravity;
        public static float windStrength;
        public static float stormWindStrength;
        public static int opacity;
        public static int splashDensity;
        public static float size;
    }

    public static class SnowOptions {
        public static int density;
        public static float gravity;
        public static float rotationAmount;
        public static float stormRotationAmount;
        public static float windStrength;
        public static float stormWindStrength;
        public static float size;
    }

    public static class SandOptions {
        public static int density;
        public static float gravity;
        public static float windStrength;
        public static float moteSize;
        public static float size;
        public static boolean spawnOnGround;
        public static String matchTags;
    }

    public static class ShrubOptions {
        public static int density;
        public static float gravity;
        public static float rotationAmount;
        public static float bounciness;
    }

    public static class FogOptions {
        public static int density;
        public static float gravity;
        public static float size;
    }

    public static class GroundFogOptions {
        public static int density;
        public static int spawnHeight;
        public static float size;
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        maxParticleAmount = MAX_PARTICLE_AMOUNT.get();
        particleDensity = PARTICLE_DENSITY.get();
        particleStormDensity = PARTICLE_STORM_DENSITY.get();
        particleRadius = PARTICLE_RADIUS.get();

        doRainParticles = DO_RAIN_PARTICLES.get();
        doSplashParticles = DO_SPLASH_PARTICLES.get();
        doSmokeParticles = DO_SMOKE_PARTICLES.get();
        doRippleParticles = DO_RIPPLE_PARTICLES.get();
        doStreakParticles = DO_STREAK_PARTICLES.get();
        doSnowParticles = DO_SNOW_PARTICLES.get();
        doSandParticles = DO_SAND_PARTICLES.get();
        doShrubParticles = DO_SHRUB_PARTICLES.get();
        doFogParticles = DO_FOG_PARTICLES.get();
        doGroundFogParticles = DO_GROUND_FOG_PARTICLES.get();

        doRainSounds = DO_RAIN_SOUNDS.get();
        doSnowSounds = DO_SNOW_SOUNDS.get();
        doSandSounds = DO_SAND_SOUNDS.get();

        rippleResolution = RIPPLE_RESOLUTION.get();
        useResourcepackResolution = USE_RESOURCEPACK_RESOLUTION.get();
        groundFogDensity = GROUND_FOG_DENSITY.get();

        RainOptions.density = RAIN_DENSITY.get();
        RainOptions.gravity = RAIN_GRAVITY.get().floatValue();
        RainOptions.windStrength = RAIN_WIND_STRENGTH.get().floatValue();
        RainOptions.stormWindStrength = RAIN_STORM_WIND_STRENGTH.get().floatValue();
        RainOptions.opacity = RAIN_OPACITY.get();
        RainOptions.splashDensity = RAIN_SPLASH_DENSITY.get();
        RainOptions.size = RAIN_SIZE.get().floatValue();

        SnowOptions.density = SNOW_DENSITY.get();
        SnowOptions.gravity = SNOW_GRAVITY.get().floatValue();
        SnowOptions.rotationAmount = SNOW_ROTATION_AMOUNT.get().floatValue();
        SnowOptions.stormRotationAmount = SNOW_STORM_ROTATION_AMOUNT.get().floatValue();
        SnowOptions.windStrength = SNOW_WIND_STRENGTH.get().floatValue();
        SnowOptions.stormWindStrength = SNOW_STORM_WIND_STRENGTH.get().floatValue();
        SnowOptions.size = SNOW_SIZE.get().floatValue();

        SandOptions.density = SAND_DENSITY.get();
        SandOptions.gravity = SAND_GRAVITY.get().floatValue();
        SandOptions.windStrength = SAND_WIND_STRENGTH.get().floatValue();
        SandOptions.moteSize = SAND_MOTE_SIZE.get().floatValue();
        SandOptions.size = SAND_SIZE.get().floatValue();
        SandOptions.spawnOnGround = SAND_SPAWN_ON_GROUND.get();
        SandOptions.matchTags = SAND_MATCH_TAGS.get();

        ShrubOptions.density = SHRUB_DENSITY.get();
        ShrubOptions.gravity = SHRUB_GRAVITY.get().floatValue();
        ShrubOptions.rotationAmount = SHRUB_ROTATION_AMOUNT.get().floatValue();
        ShrubOptions.bounciness = SHRUB_BOUNCINESS.get().floatValue();

        FogOptions.density = FOG_DENSITY.get();
        FogOptions.gravity = FOG_GRAVITY.get().floatValue();
        FogOptions.size = FOG_SIZE.get().floatValue();

        GroundFogOptions.density = GROUND_FOG_DENSITY.get();
        GroundFogOptions.spawnHeight = GROUND_FOG_SPAWN_HEIGHT.get();
        GroundFogOptions.size = GROUND_FOG_SIZE.get().floatValue();

        renderVanillaWeather = RENDER_VANILLA_WEATHER.get();
        tickVanillaWeather = TICK_VANILLA_WEATHER.get();
        biomeTint = BIOME_TINT.get();
        tintMix = TINT_MIX.get();
        spawnAboveClouds = SPAWN_ABOVE_CLOUDS.get();
        cloudHeight = CLOUD_HEIGHT.get();
        alwaysRaining = ALWAYS_RAINING.get();
        yLevelWindAdjustment = Y_LEVEL_WIND_ADJUSTMENT.get();
        syncRegistry = SYNC_REGISTRY.get();

    }
}