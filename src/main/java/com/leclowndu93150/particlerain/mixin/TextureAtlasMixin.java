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

        // Load numbered textures first
        for (int idx = 0; idx < 8; idx++) {
            if (idx < 4) {
                addTextureToAtlas(resourceManager, stitcher, "rain" + idx, "rain" + idx);
                addTextureToAtlas(resourceManager, stitcher, "snow" + idx, "snow" + idx);
                addTextureToAtlas(resourceManager, stitcher, "splash_" + idx, "splash_" + idx);
            }

        }

        addTextureToAtlas(resourceManager, stitcher, "ripple_" + 1, "ripple_" + 1);

        // Load single textures
        String[] singles = {"streak", "ground_fog", "fog_dithered", "dust"};
        for (String texture : singles) {
            addTextureToAtlas(resourceManager, stitcher, texture, texture);
        }

        profiler.pop();
    }

    private void addTextureToAtlas(ResourceManager resourceManager, Stitcher stitcher, String texturePath, String particlePath) {
        try {
            NativeImage texture = ClientStuff.loadTexture(
                    new ResourceLocation(ParticleRainClient.MOD_ID, "textures/particle/" + texturePath + ".png"));
            stitcher.registerSprite(ClientStuff.createInfo(
                    new ResourceLocation(ParticleRainClient.MOD_ID, "particle/" + particlePath), texture));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}