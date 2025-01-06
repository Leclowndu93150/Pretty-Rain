package com.leclowndu93150.particlerain.mixin;

import com.leclowndu93150.particlerain.ClientStuff;
import com.leclowndu93150.particlerain.ParticleRainClient;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.PngInfo;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(TextureAtlas.class)
public abstract class TextureAtlasMixin {
    @Shadow @Final private ResourceLocation location;
    @Shadow @Final private int maxSupportedTextureSize;

    @Inject(method = "prepareToStitch", at = @At("HEAD"), cancellable = true)
    private void registerWeatherParticles(ResourceManager resourceManager, Stream<ResourceLocation> spriteNames, ProfilerFiller profiler, int mipmapLevel, CallbackInfoReturnable<TextureAtlas.Preparations> cir) {
        if (!this.location.equals(new ResourceLocation("textures/atlas/particles.png"))) return;

        profiler.push("weather_particles");

        Stitcher stitcher = new Stitcher(this.maxSupportedTextureSize, this.maxSupportedTextureSize, mipmapLevel);
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

        Set<ResourceLocation> sprites = spriteNames.collect(Collectors.toSet());

        for (int j = 0; j < 4; j++) {
            stitcher.registerSprite(ClientStuff.splitImage(rainImage, j, "rain"));
            stitcher.registerSprite(ClientStuff.splitImage(snowImage, j, "snow"));
        }

        if (ParticleRainClient.config.biomeTint) {
            for (int j = 0; j < 4; j++) {
                try {
                    NativeImage splashImage = ClientStuff.loadTexture(resourceManager,
                            new ResourceLocation("textures/particle/splash_" + j + ".png"));
                    ClientStuff.desaturateImage(splashImage);
                    stitcher.registerSprite(ClientStuff.createInfo(
                            new ResourceLocation(ParticleRainClient.MOD_ID, "splash" + j), splashImage));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Set<TextureAtlasSprite.Info> spriteInfos = sprites.stream()
                .map(location -> {
                    try {
                        Resource resource = resourceManager.getResource(location);
                        PngInfo pnginfo = new PngInfo(resource.toString(), resource.getInputStream());
                        AnimationMetadataSection anim = resource.getMetadata(AnimationMetadataSection.SERIALIZER);
                        if (anim == null) anim = AnimationMetadataSection.EMPTY;
                        return new TextureAtlasSprite.Info(location, pnginfo.width, pnginfo.height, anim);
                    } catch (IOException e) {
                        return null;
                    }
                })
                .filter(info -> info != null)
                .collect(Collectors.toSet());

        spriteInfos.forEach(stitcher::registerSprite);

        stitcher.registerSprite(MissingTextureAtlasSprite.info());

        try {
            stitcher.stitch();
        } catch (StitcherException ex) {
            throw new ReportedException(CrashReport.forThrowable(ex, "Stitching"));
        }

        profiler.push("loading");
        List<TextureAtlasSprite> regions = getLoadedSprites(resourceManager, stitcher, mipmapLevel);
        profiler.pop();

        profiler.pop();
        cir.setReturnValue(new TextureAtlas.Preparations(sprites, stitcher.getWidth(), stitcher.getHeight(), mipmapLevel, regions));
    }

    @Shadow protected abstract List<TextureAtlasSprite> getLoadedSprites(ResourceManager manager, Stitcher stitcher, int mipmap);
}