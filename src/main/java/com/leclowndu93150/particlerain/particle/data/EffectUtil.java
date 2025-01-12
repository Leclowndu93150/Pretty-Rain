package com.leclowndu93150.particlerain.particle.data;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public final class EffectUtil {

    public static float splashWidth(Entity entity) {
        return entity.getBbWidth() * 2.0F;
    }

    public static float splashHeight(float width, Entity entity) {
        return Math.max(-(float)entity.getDeltaMovement().y() * width, 0.0F);
    }

    public static void splashSound(double x, double y, double z, float mag) {
        Minecraft.getInstance().getSoundManager().play(new LinearFadeSound(SplashSounds.AMBIENCE_SPLASH, SoundSource.AMBIENT, 20 + mag * 40, mag * 10.0F, x, y, z));
    }

    public static void smallSplashSound(double x, double y, double z, float mag) {
        Minecraft.getInstance().getSoundManager().play(new LinearFadeSound(SplashSounds.AMBIENCE_SMALL_SPLASH, SoundSource.AMBIENT, 10 + mag * 15, mag * 5.0F, x, y, z));
    }

    private EffectUtil() {}
}
