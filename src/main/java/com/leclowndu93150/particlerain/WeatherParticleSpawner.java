package com.leclowndu93150.particlerain;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public final class WeatherParticleSpawner {

    private static final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    private static void spawnParticle(ClientLevel level, Holder<Biome> biome, double x, double y, double z) {
        if (!FMLEnvironment.dist.isClient()) return;

        if (ParticleRainClient.particleCount > ParticleRainClient.config.maxParticleAmount) {
            return;
        } else if (!ParticleRainClient.config.spawnAboveClouds && y > ParticleRainClient.config.cloudHeight) {
            y = ParticleRainClient.config.cloudHeight;
        }
        if (ParticleRainClient.config.doFogParticles && level.random.nextFloat() < ParticleRainClient.config.fog.density / 100F) {
            level.addParticle(ParticleRegistry.FOG.get(), x, y, z, 0, 0, 0);
        }
        Precipitation precipitation = biome.value().getPrecipitationAt(level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos));
        //biome.value().hasPrecipitation() isn't reliable for modded biomes and seasons
        if (precipitation == Precipitation.RAIN) {
            if (ParticleRainClient.config.doGroundFogParticles && ParticleRainClient.fogCount < ParticleRainClient.config.groundFog.density) {
                int height = level.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) x, (int) z);
                if (height <= ParticleRainClient.config.groundFog.spawnHeight && height >= ParticleRainClient.config.groundFog.spawnHeight - 4 && level.getFluidState(BlockPos.containing(x, height - 1, z)).isEmpty()) {
                    level.addParticle(ParticleRegistry.GROUND_FOG.get(), x, height + level.random.nextFloat(), z, 0, 0, 0);
                }
            }
            if (ParticleRainClient.config.doRainParticles && level.random.nextFloat() < ParticleRainClient.config.rain.density / 100F) {
                level.addParticle(ParticleRegistry.RAIN.get(), x, y, z, 0, 0, 0);
            }
        } else if (precipitation == Precipitation.SNOW && ParticleRainClient.config.doSnowParticles) {
            if (level.random.nextFloat() < ParticleRainClient.config.snow.density / 100F) {
                level.addParticle(ParticleRegistry.SNOW.get(), x, y, z, 0, 0, 0);
            }
        } else if (doesThisBlockHaveDustBlowing(precipitation, level, BlockPos.containing(x, y, z), biome)) {
            if (ParticleRainClient.config.sand.spawnOnGround) y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) x, (int) z);
            if (ParticleRainClient.config.doSandParticles) {
                if (level.random.nextFloat() < ParticleRainClient.config.sand.density / 100F) {
                    level.addParticle(ParticleRegistry.DUST.get(), x, y, z, 0, 0, 0);
                }
            }
            if (ParticleRainClient.config.doShrubParticles) {
                if (level.random.nextFloat() < ParticleRainClient.config.shrub.density / 100F) {
                    level.addParticle(ParticleRegistry.SHRUB.get(), x, y, z, 0, 0, 0);
                }
            }
        }
    }

    public static void update(ClientLevel level, Entity entity, float partialTicks) {
        if (!FMLEnvironment.dist.isClient()) return;

        if (level.isRaining() || ParticleRainClient.config.alwaysRaining) {
            int density;
            
            // A bug in TerraFirmaCraft causes level.getRainLevel() to return unreasonable values.
            // To prevent other mods from breaking our particle system, clamp the value between 0 and 1.
            float rainLevel = level.getRainLevel(partialTicks);
            if (rainLevel < 0 || rainLevel > 1) {
                ParticleRainClient.LOGGER.warn("World's rain level is out of bounds: " + rainLevel);
                rainLevel = Mth.clamp(rainLevel, 0, 1);
            }

            if (level.isThundering())
                if (ParticleRainClient.config.alwaysRaining) {
                    density = ParticleRainClient.config.particleStormDensity;
                } else {
                    density = (int) (ParticleRainClient.config.particleStormDensity * rainLevel);
                }
            else if (ParticleRainClient.config.alwaysRaining) {
                density = ParticleRainClient.config.particleDensity;
            } else {
                density = (int) (ParticleRainClient.config.particleDensity * rainLevel);
            }

            RandomSource rand = RandomSource.create();
            
            ProfilerFiller profiler = Minecraft.getInstance().getProfiler();
            profiler.push("spawnRainParticles");
            for (int pass = 0; pass < density; pass++) {
                profiler.push("randomizeParticle");

                float theta = (float) (2 * Math.PI * rand.nextFloat());
                float phi = (float) Math.acos(2 * rand.nextFloat() - 1);
                double x = ParticleRainClient.config.particleRadius * Mth.sin(phi) * Math.cos(theta);
                double y = ParticleRainClient.config.particleRadius * Mth.sin(phi) * Math.sin(theta);
                double z = ParticleRainClient.config.particleRadius * Mth.cos(phi);

                pos.set(x + entity.getX(), y + entity.getY(), z + entity.getZ());
                if (level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()) > pos.getY()) {
                    profiler.pop();
                    continue;
                }
                
                profiler.popPush("spawnParticle");
                spawnParticle(level, level.getBiome(pos), pos.getX() + rand.nextFloat(), pos.getY() + rand.nextFloat(), pos.getZ() + rand.nextFloat());
                profiler.pop();
            }
            profiler.pop();
        }
    }

    @Nullable
    public static SoundEvent getBiomeSound(BlockPos blockPos, boolean above) {
        Holder<Biome> biome = Minecraft.getInstance().level.getBiome(blockPos);
        Precipitation precipitation = biome.value().getPrecipitationAt(blockPos);
        if (precipitation == Precipitation.RAIN && ParticleRainClient.config.doRainSounds) {
            return above ? SoundEvents.WEATHER_RAIN_ABOVE : SoundEvents.WEATHER_RAIN;
        } else if (precipitation == Precipitation.SNOW && ParticleRainClient.config.doSnowSounds) {
            return above ? ParticleRegistry.WEATHER_SNOW_ABOVE.get() : ParticleRegistry.WEATHER_SNOW.get();
        } else if (doesThisBlockHaveDustBlowing(precipitation, Minecraft.getInstance().level, blockPos, biome) && ParticleRainClient.config.doSandSounds) {
            return above ? ParticleRegistry.WEATHER_SANDSTORM_ABOVE.get() : ParticleRegistry.WEATHER_SANDSTORM.get();
        }
        return null;
    }

    public static boolean doesThisBlockHaveDustBlowing(Precipitation precipitation, ClientLevel level, BlockPos blockPos, Holder<Biome> biome) {
        return precipitation == Precipitation.NONE && level.getBlockState(level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos).below()).is(TagKey.create(Registries.BLOCK, ResourceLocation.tryParse(ParticleRainClient.config.sand.matchTags))) && biome.value().getBaseTemperature() > 0.25;
    }
}