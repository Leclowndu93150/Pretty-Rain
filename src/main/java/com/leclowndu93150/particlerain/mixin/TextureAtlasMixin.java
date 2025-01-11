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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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
    private void registerWeatherParticles(ResourceManager resourceManager, Stream<ResourceLocation> spriteNames, ProfilerFiller profiler, int mipLevel,
                                          CallbackInfoReturnable<TextureAtlas.Preparations> cir, Set<ResourceLocation> set, int i, Stitcher stitcher, int j, int k) {
        if (!this.location.equals(TextureAtlas.LOCATION_PARTICLES)) return;

        profiler.push("weather_particles");
        ParticleRainClient.particleCount = 0;
        ParticleRainClient.fogCount = 0;

        // Load pre-split textures for rain and snow
        for (int j2 = 0; j2 < 4; j2++) {
            try {
                NativeImage rainFrame = ClientStuff.loadTexture(resourceManager,
                        new ResourceLocation(ParticleRainClient.MOD_ID, "textures/particle/rain" + j2 + ".png"));
                NativeImage snowFrame = ClientStuff.loadTexture(resourceManager,
                        new ResourceLocation(ParticleRainClient.MOD_ID, "textures/particle/snow" + j2 + ".png"));

                if (ParticleRainClient.config.biomeTint) {
                    ClientStuff.desaturateImage(rainFrame);
                }

                stitcher.registerSprite(ClientStuff.createInfo(
                        new ResourceLocation(ParticleRainClient.MOD_ID, "particle/rain" + j2), rainFrame));
                stitcher.registerSprite(ClientStuff.createInfo(
                        new ResourceLocation(ParticleRainClient.MOD_ID, "particle/snow" + j2), snowFrame));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (ParticleRainClient.config.biomeTint) {
            for (int j2 = 0; j2 < 4; j2++) {
                try {
                    NativeImage splashImage = ClientStuff.loadTexture(resourceManager,
                            new ResourceLocation("minecraft", "textures/particle/splash_" + j2 + ".png"));
                    ClientStuff.desaturateImage(splashImage);
                    stitcher.registerSprite(ClientStuff.createInfo(
                            new ResourceLocation("minecraft", "particle/splash_" + j2), splashImage));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        for (int j2 = 0; j2 < 8; j2++) {
            try {
                NativeImage rippleFrame = ClientStuff.loadTexture(resourceManager,
                        new ResourceLocation(ParticleRainClient.MOD_ID, "textures/particle/ripple_" + j2 + ".png"));
                stitcher.registerSprite(ClientStuff.createInfo(
                        new ResourceLocation(ParticleRainClient.MOD_ID, "particle/ripple_" + j2), rippleFrame));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        profiler.pop();
    }
}