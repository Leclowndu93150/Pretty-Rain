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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.levelgen.Heightmap;

import javax.annotation.Nullable;


public final class WeatherParticleSpawner {

    private static final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    private static void spawnParticle(ClientLevel level, Holder<Biome> biome, double x, double y, double z) {
        if (ParticleRainClient.particleCount > ParticleRainConfig.maxParticleAmount) {
            return;
        } else if (!ParticleRainConfig.spawnAboveClouds && y > ParticleRainConfig.cloudHeight) {
            y = ParticleRainConfig.cloudHeight;
        }
        if (ParticleRainConfig.doFogParticles && level.random.nextFloat() < ParticleRainConfig.FogOptions.density / 100F) {
            level.addParticle(ParticleRegistry.FOG.get(), x, y, z, 0, 0, 0);
        }
        Precipitation precipitation = biome.value().getPrecipitationAt(level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos));
        //biome.value().hasPrecipitation() isn't reliable for modded biomes and seasons
        if (precipitation == Precipitation.RAIN) {
            if (ParticleRainConfig.doGroundFogParticles && ParticleRainClient.fogCount < ParticleRainConfig.GroundFogOptions.density) {
                int height = level.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) x, (int) z);
                if (height <= ParticleRainConfig.GroundFogOptions.spawnHeight && height >= ParticleRainConfig.GroundFogOptions.spawnHeight - 4 && level.getFluidState(BlockPos.containing(x, height - 1, z)).isEmpty()) {
                    level.addParticle(ParticleRegistry.GROUND_FOG.get(), x, height + level.random.nextFloat(), z, 0, 0, 0);
                }
            }
            if (ParticleRainConfig.doRainParticles && level.random.nextFloat() < ParticleRainConfig.RainOptions.density / 100F) {
                level.addParticle(ParticleRegistry.RAIN.get(), x, y, z, 0, 0, 0);
            }
        } else if (precipitation == Precipitation.SNOW && ParticleRainConfig.doSnowParticles) {
            if (level.random.nextFloat() < ParticleRainConfig.SnowOptions.density / 100F) {
                level.addParticle(ParticleRegistry.SNOW.get(), x, y, z, 0, 0, 0);
            }
        } else if (doesThisBlockHaveDustBlowing(precipitation, level, BlockPos.containing(x, y, z), biome)) {
            if (ParticleRainConfig.SandOptions.spawnOnGround) y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) x, (int) z);
            if (ParticleRainConfig.doSandParticles) {
                if (level.random.nextFloat() < ParticleRainConfig.SandOptions.density / 100F) {
                    level.addParticle(ParticleRegistry.DUST.get(), x, y, z, 0, 0, 0);
                }
            }
            if (ParticleRainConfig.doShrubParticles) {
                if (level.random.nextFloat() < ParticleRainConfig.ShrubOptions.density / 100F) {
                    level.addParticle(ParticleRegistry.SHRUB.get(), x, y, z, 0, 0, 0);
                }
            }
        }
    }

    public static void update(ClientLevel level, Entity entity, float partialTicks) {
        if (level.isRaining() || ParticleRainConfig.alwaysRaining) {
            int density;
            if (level.isThundering())
                if (ParticleRainConfig.alwaysRaining) {
                    density = ParticleRainConfig.particleStormDensity;
                } else {
                    density = (int) (ParticleRainConfig.particleStormDensity * level.getRainLevel(partialTicks));
                }
            else if (ParticleRainConfig.alwaysRaining) {
                density = ParticleRainConfig.particleDensity;
            } else {
                density = (int) (ParticleRainConfig.particleDensity * level.getRainLevel(partialTicks));
            }


            RandomSource rand = RandomSource.create();

            for (int pass = 0; pass < density; pass++) {

                float theta = (float) (2 * Math.PI * rand.nextFloat());
                float phi = (float) Math.acos(2 * rand.nextFloat() - 1);
                double x = ParticleRainConfig.particleRadius * Mth.sin(phi) * Math.cos(theta);
                double y = ParticleRainConfig.particleRadius * Mth.sin(phi) * Math.sin(theta);
                double z = ParticleRainConfig.particleRadius * Mth.cos(phi);

                pos.set(x + entity.getX(), y + entity.getY(), z + entity.getZ());
                if (level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()) > pos.getY())
                    continue;

                spawnParticle(level, level.getBiome(pos), pos.getX() + rand.nextFloat(), pos.getY() + rand.nextFloat(), pos.getZ() + rand.nextFloat());
            }
        }
    }

    @Nullable
    public static SoundEvent getBiomeSound(BlockPos blockPos, boolean above) {
        Holder<Biome> biome = Minecraft.getInstance().level.getBiome(blockPos);
        Precipitation precipitation = biome.value().getPrecipitationAt(blockPos);
        if (precipitation == Precipitation.RAIN && ParticleRainConfig.doRainSounds) {
            return above ? SoundEvents.WEATHER_RAIN_ABOVE : SoundEvents.WEATHER_RAIN;
        } else if (precipitation == Precipitation.SNOW && ParticleRainConfig.doSnowSounds) {
            return above ? ParticleRegistry.WEATHER_SNOW_ABOVE.get() : ParticleRegistry.WEATHER_SNOW.get();
        } else if (doesThisBlockHaveDustBlowing(precipitation, Minecraft.getInstance().level, blockPos, biome) && ParticleRainConfig.doSandSounds) {
            return above ? ParticleRegistry.WEATHER_SANDSTORM_ABOVE.get() : ParticleRegistry.WEATHER_SANDSTORM.get();
        }
        return null;
    }

    public static boolean doesThisBlockHaveDustBlowing(Precipitation precipitation, ClientLevel level, BlockPos blockPos, Holder<Biome> biome) {
        return precipitation == Precipitation.NONE && level.getBlockState(level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos).below()).is(TagKey.create(Registries.BLOCK, ResourceLocation.tryParse(ParticleRainConfig.SandOptions.matchTags))) && biome.value().getBaseTemperature() > 0.25;
    }
}