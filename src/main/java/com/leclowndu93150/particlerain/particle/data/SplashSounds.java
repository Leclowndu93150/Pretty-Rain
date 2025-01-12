package com.leclowndu93150.particlerain.particle.data;

import com.leclowndu93150.particlerain.ParticleRainClient;
import net.minecraft.resources.ResourceLocation;

public class SplashSounds {

    public static final ResourceLocation AMBIENCE_SPLASH = effectiveLoc("ambience.splash");
    public static final ResourceLocation AMBIENCE_SMALL_SPLASH = effectiveLoc("ambience.small_splash");

    private static ResourceLocation effectiveLoc(String path) {
        return new ResourceLocation(ParticleRainClient.MOD_ID, path);
    }

}
