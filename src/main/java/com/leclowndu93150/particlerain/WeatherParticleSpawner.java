package com.leclowndu93150.particlerain;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;
import javax.xml.ws.Holder;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public final class WeatherParticleSpawner {

    private static final BlockPos.Mutable pos = new BlockPos.Mutable();

    private static void spawnParticle(ClientWorld level, Holder<Biome> biome, double x, double y, double z) {
        if (!FMLEnvironment.dist.isClient()) return;

        if (ParticleRainClient.particleCount > ParticleRainClient.config.maxParticleAmount) {
            return;
        } else if (!ParticleRainClient.config.spawnAboveClouds && y > ParticleRainClient.config.cloudHeight) {
            y = ParticleRainClient.config.cloudHeight;
        }
        if (ParticleRainClient.config.doFogParticles && level.random.nextFloat() < ParticleRainClient.config.fog.density / 100F) {
            level.addParticle(ParticleRegistry.FOG.get(), x, y, z, 0, 0, 0);
        }
        Precipitation precipitation = biome.value().getPrecipitation();
        //biome.value().hasPrecipitation() isn't reliable for modded biomes and seasons
        if (precipitation == Precipitation.RAIN) {
            if (ParticleRainClient.config.doGroundFogParticles && ParticleRainClient.fogCount < ParticleRainClient.config.groundFog.density) {
                int height = level.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) x, (int) z);
                if (height <= ParticleRainClient.config.groundFog.spawnHeight && height >= ParticleRainClient.config.groundFog.spawnHeight - 4 && level.getFluidState(new BlockPos(x, height - 1, z)).isEmpty()) {
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
        } else if (doesThisBlockHaveDustBlowing(precipitation, level, new BlockPos(x,y,z), biome)) {
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

    public static void update(ClientWorld level, Entity entity, float partialTicks) {
        if (!FMLEnvironment.dist.isClient()) return;

        if (level.isRaining() || ParticleRainClient.config.alwaysRaining) {
            int density;
            if (level.isThundering())
                if (ParticleRainClient.config.alwaysRaining) {
                    density = ParticleRainClient.config.particleStormDensity;
                } else {
                    density = (int) (ParticleRainClient.config.particleStormDensity * level.getRainLevel(partialTicks));
                }
            else if (ParticleRainClient.config.alwaysRaining) {
                density = ParticleRainClient.config.particleDensity;
            } else {
                density = (int) (ParticleRainClient.config.particleDensity * level.getRainLevel(partialTicks));
            }


            Random rand = new Random();

            for (int pass = 0; pass < density; pass++) {

                float theta = (float) (2 * Math.PI * rand.nextFloat());
                float phi = (float) Math.acos(2 * rand.nextFloat() - 1);
                double x = ParticleRainClient.config.particleRadius * MathHelper.sin(phi) * Math.cos(theta);
                double y = ParticleRainClient.config.particleRadius * MathHelper.sin(phi) * Math.sin(theta);
                double z = ParticleRainClient.config.particleRadius * MathHelper.cos(phi);

                pos.set(x + entity.getX(), y + entity.getY(), z + entity.getZ());
                if (level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()) > pos.getY())
                    continue;

                spawnParticle(level, level.getBiome(pos), pos.getX() + rand.nextFloat(), pos.getY() + rand.nextFloat(), pos.getZ() + rand.nextFloat());
            }
        }
    }

    @Nullable
    public static SoundEvent getBiomeSound(BlockPos blockPos, boolean above) {
        Biome biome = Minecraft.getInstance().level.getBiome(blockPos);
        Biome.RainType rainType = biome.getPrecipitation();
        if (rainType == Biome.RainType.RAIN && ParticleRainClient.config.doRainSounds) {
            return above ? SoundEvents.WEATHER_RAIN_ABOVE : SoundEvents.WEATHER_RAIN;
        } else if (rainType == Biome.RainType.SNOW && ParticleRainClient.config.doSnowSounds) {
            return above ? ParticleRegistry.WEATHER_SNOW_ABOVE.get() : ParticleRegistry.WEATHER_SNOW.get();
        } else if (doesThisBlockHaveDustBlowing(rainType, Minecraft.getInstance().level, blockPos, biome) && ParticleRainClient.config.doSandSounds) {
            return above ? ParticleRegistry.WEATHER_SANDSTORM_ABOVE.get() : ParticleRegistry.WEATHER_SANDSTORM.get();
        }
        return null;
    }

    public static boolean doesThisBlockHaveDustBlowing(Biome.RainType rainType, ClientWorld world, BlockPos blockPos, Biome biome) {
        return rainType == Biome.RainType.NONE && world.getBlockState(world.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, blockPos).below()).is(BlockTags.SAND) && biome.getTemperature(blockPos) > 0.25;
    }
}