package com.leclowndu93150.particlerain.mixin;

import com.leclowndu93150.particlerain.ParticleRainClient;
import com.leclowndu93150.particlerain.ParticleRainConfig;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

@Mixin(SpriteLoader.class)
public abstract class SpriteLoaderMixin {

    @Shadow @Final private ResourceLocation location;

    @Inject(
            method = "stitch",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/texture/Stitcher;stitch()V"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void stitch(List<SpriteContents> contents, int mipLevel, Executor executor, CallbackInfoReturnable<SpriteLoader.Preparations> cir, int i, Stitcher stitcher, int j, int k, int j1, int k1, int l1) {
        if (this.location.equals(new ResourceLocation("textures/atlas/particles.png"))) {
            ParticleRainClient.particleCount = 0;
            ParticleRainClient.fogCount = 0;

            NativeImage rainImage = null;
            NativeImage snowImage = null;
            try {
                rainImage = ParticleRainClient.loadTexture(new ResourceLocation("minecraft","textures/environment/rain.png"));
                snowImage = ParticleRainClient.loadTexture(new ResourceLocation("minecraft","textures/environment/snow.png"));
                if (ParticleRainConfig.biomeTint) {
                    rainImage.applyToAllPixels(ParticleRainClient.desaturateOperation);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (int idx = 0; idx < 4; idx++) {
                ((Stitcher<SpriteContents>)stitcher).registerSprite(ParticleRainClient.splitImage(rainImage, idx, "rain"));
            }
            for (int idx = 0; idx < 4; idx++) {
                ((Stitcher<SpriteContents>)stitcher).registerSprite(ParticleRainClient.splitImage(snowImage, idx, "snow"));
            }

            int rippleResolution = ParticleRainClient.getRippleResolution(contents);
            for (int idx = 0; idx < 8; idx++) {
                ((Stitcher<SpriteContents>)stitcher).registerSprite(ParticleRainClient.generateRipple(idx, rippleResolution));
            }

            if (ParticleRainConfig.biomeTint) {
                for (int idx = 0; idx < 4; idx++) {
                    NativeImage splashImage = null;
                    try {
                        splashImage = ParticleRainClient.loadTexture(new ResourceLocation("textures/particle/splash_" + idx + ".png"));
                        splashImage.applyToAllPixels(ParticleRainClient.desaturateOperation);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ((Stitcher<SpriteContents>)stitcher).registerSprite(new SpriteContents(
                            new ResourceLocation(ParticleRainClient.MODID, "splash" + idx),
                            new FrameSize(splashImage.getWidth(), splashImage.getHeight()),
                            splashImage,
                            AnimationMetadataSection.EMPTY
                    ));
                }
            }
        }
    }
}