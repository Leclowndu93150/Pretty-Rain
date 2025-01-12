package com.leclowndu93150.particlerain.particle.data;

import com.leclowndu93150.particlerain.ParticleRainClient;
import com.leclowndu93150.particlerain.particle.LavaSplashParticle;
import com.leclowndu93150.particlerain.particle.SplashParticle;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedList;
import java.util.List;

public class SpecialParticlesRegistry {

    public static final ParticleManager<SplashParticleData> SPLASH = new ParticleManager<>(
            new SplashParticleData(1.0F, 1.0F), serial("water_splash", 13),
            (data, sprites, level, x, y, z, xDelta, yDelta, zDelta) -> {
                return new SplashParticle(level, x, y, z, sprites, data.width, data.height);
            }
    );

    public static final ParticleManager<SplashParticleData> LAVA_SPLASH = new ParticleManager<>(
            new SplashParticleData(1.0F, 1.0F), serial("lava_splash", 13),
            (data, sprites, level, x, y, z, xDelta, yDelta, zDelta) -> {
                return new LavaSplashParticle(level, x, y, z, sprites, data.width, data.height);
            }
    );

    private static List<ResourceLocation> serial(String name, int size) {
        List<ResourceLocation> textures = new LinkedList<>();

        for (int i = 0; i < size; i++) {
            textures.add(new ResourceLocation(ParticleRainClient.MOD_ID, "particle/" + name + '_' + i));
        }

        return textures;
    }

}
