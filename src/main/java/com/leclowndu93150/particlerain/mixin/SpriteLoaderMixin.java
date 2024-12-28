package com.leclowndu93150.particlerain.mixin;

import com.leclowndu93150.particlerain.ClientStuff;
import com.leclowndu93150.particlerain.ParticleRainClient;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(SpriteLoader.class)
public abstract class SpriteLoaderMixin {
    @Shadow @Final private int minHeight;
    @Shadow @Final private int minWidth;
    @Shadow @Final private ResourceLocation location;
    @Shadow @Final private int maxSupportedTextureSize;
    @Unique private List<SpriteContents> spriteContentsList;

    @Inject(method = "stitch", at = @At("HEAD"))
    public void stitch(List<SpriteContents> list, int i, Executor executor, CallbackInfoReturnable<SpriteLoader.Preparations> cir) {
        this.spriteContentsList = list;
    }

    @Inject(
            method = "stitch",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/client/renderer/texture/Stitcher",
                    ordinal = 0
            ),
            cancellable = true
    )
    private void registerWeatherParticles(List<SpriteContents> list, int i, Executor executor, CallbackInfoReturnable<SpriteLoader.Preparations> cir) {
        Stitcher<SpriteContents> stitcher = new Stitcher<>(this.maxSupportedTextureSize, this.maxSupportedTextureSize, i);
        System.out.println("Registering weather particles before the if statement");

        if (this.location.equals(ResourceLocation.tryParse("textures/atlas/particles.png"))) {
            System.out.println("Registering weather particles");
            ParticleRainClient.particleCount = 0;
            ParticleRainClient.fogCount = 0;

            NativeImage rainImage = null;
            NativeImage snowImage = null;
            try {
                rainImage = ClientStuff.loadTexture(new ResourceLocation("minecraft","textures/environment/rain.png"));
                snowImage = ClientStuff.loadTexture(new ResourceLocation("minecraft","textures/environment/snow.png"));
                if (ParticleRainClient.config.biomeTint) rainImage.applyToAllPixels(ClientStuff.desaturateOperation);
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (int j = 0; j < 4; j++) {
                stitcher.registerSprite(ClientStuff.splitImage(rainImage, j, "rain"));
            }
            for (int j = 0; j < 4; j++) {
                stitcher.registerSprite(ClientStuff.splitImage(snowImage, j, "snow"));
            }

            int rippleResolution = ClientStuff.getRippleResolution(this.spriteContentsList);
            for (int j = 0; j < 8; j++) {
                stitcher.registerSprite(ClientStuff.generateRipple(j, rippleResolution));
            }

            if (ParticleRainClient.config.biomeTint) {
                for (int j = 0; j < 4; j++) {
                    NativeImage splashImage = null;
                    try {
                        splashImage = ClientStuff.loadTexture(new ResourceLocation("textures/particle/splash_" + j + ".png"));
                        splashImage.applyToAllPixels(ClientStuff.desaturateOperation);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    stitcher.registerSprite(new SpriteContents(new ResourceLocation(ParticleRainClient.MOD_ID, "splash" + j),
                            new FrameSize(splashImage.getWidth(), splashImage.getHeight()),
                            splashImage,
                            AnimationMetadataSection.EMPTY));
                }
            }
        }

        for(SpriteContents contents : list) {
            stitcher.registerSprite(contents);
        }

        try {
            stitcher.stitch();
        } catch (StitcherException var16) {
            throw new ReportedException(CrashReport.forThrowable(var16, "Stitching"));
        }

        int width = Math.max(stitcher.getWidth(), this.minWidth);
        int height = Math.max(stitcher.getHeight(), this.minHeight);

        Map<ResourceLocation, TextureAtlasSprite> regions = this.getStitchedSprites(stitcher, width, height);
        TextureAtlasSprite missingSprite = regions.get(MissingTextureAtlasSprite.getLocation());

        CompletableFuture<Void> future;
        if (i > 0) {
            future = CompletableFuture.runAsync(() -> {
                regions.values().forEach(sprite -> sprite.contents().increaseMipLevel(i));
            }, executor);
        } else {
            future = CompletableFuture.completedFuture(null);
        }

        cir.setReturnValue(new SpriteLoader.Preparations(width, height, i, missingSprite, regions, future));
    }

    @Shadow protected abstract Map<ResourceLocation, TextureAtlasSprite> getStitchedSprites(Stitcher<SpriteContents> stitcher, int width, int height);
}