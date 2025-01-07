package com.leclowndu93150.particlerain.mixin;

import com.leclowndu93150.particlerain.ClientStuff;
import com.leclowndu93150.particlerain.ParticleRainClient;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;
import java.util.List;

@Mixin(TextureAtlas.class)
public abstract class TextureAtlasMixin {
    @Shadow @Final private ResourceLocation location;
    @Shadow @Final private int maxSupportedTextureSize;

    @Inject(
            method = "prepareToStitch",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/texture/Stitcher;registerSprite(Lnet/minecraft/client/renderer/texture/TextureAtlasSprite$Info;)V",
                    ordinal = 0
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void registerWeatherParticles(ResourceManager resourceManager, Stream<ResourceLocation> spriteNames, ProfilerFiller profiler, int mipLevel, CallbackInfoReturnable<TextureAtlas.Preparations> cir, Set set, int i, Stitcher stitcher, int j, int k, Iterator var10, TextureAtlasSprite.Info textureatlassprite$info, int l) {
        if (!this.location.equals(new ResourceLocation("textures/atlas/particles.png"))) return;

        profiler.push("weather_particles");
        ParticleRainClient.particleCount = 0;
        ParticleRainClient.fogCount = 0;

        NativeImage rainImage = null;
        NativeImage snowImage = null;
        try {
            rainImage = ClientStuff.loadTexture(resourceManager, new ResourceLocation("minecraft","textures/environment/rain.png"));
            snowImage = ClientStuff.loadTexture(resourceManager, new ResourceLocation("minecraft","textures/environment/snow.png"));
            if (ParticleRainClient.config.biomeTint) ClientStuff.desaturateImage(rainImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int j2 = 0; j2 < 4; j2++) {
            stitcher.registerSprite(ClientStuff.splitImage(rainImage, j2, "rain"));
            stitcher.registerSprite(ClientStuff.splitImage(snowImage, j2, "snow"));
        }

        if (ParticleRainClient.config.biomeTint) {
            for (int j2 = 0; j2 < 4; j2++) {
                try {
                    NativeImage splashImage = ClientStuff.loadTexture(resourceManager,
                            new ResourceLocation("minecraft","textures/particle/splash_" + j2 + ".png"));
                    ClientStuff.desaturateImage(splashImage);
                    stitcher.registerSprite(ClientStuff.createInfo(
                            new ResourceLocation("minecraft", "particle/splash_" + j2), splashImage));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        int rippleResolution = ClientStuff.getRippleResolution(List.of());
        for (int j2 = 0; j2 < 8; j2++) {
            stitcher.registerSprite(ClientStuff.generateRipple(j2, rippleResolution));
        }

        profiler.pop();
    }
}