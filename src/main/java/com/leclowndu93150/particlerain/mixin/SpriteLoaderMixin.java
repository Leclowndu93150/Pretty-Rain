package com.leclowndu93150.particlerain.mixin;

import com.leclowndu93150.particlerain.ClientStuff;
import com.leclowndu93150.particlerain.ParticleRainClient;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@Mixin(SpriteLoader.class)
public abstract class SpriteLoaderMixin {
    @Shadow @Final private int minHeight;
    @Shadow @Final private int minWidth;
    @Shadow @Final private ResourceLocation location;
    @Shadow @Final private int maxSupportedTextureSize;

    @Shadow protected abstract Map<ResourceLocation, TextureAtlasSprite> getStitchedSprites(Stitcher<SpriteContents> stitcher, int width, int height);

    @ModifyVariable(method = "stitch", at = @At("HEAD"), argsOnly = true)
    public List<SpriteContents> modifySpriteContents(List<SpriteContents> list) {
        List<SpriteContents> newList = new ArrayList<>(list);

        if (this.location.equals(ResourceLocation.tryParse("textures/atlas/particles.png"))) {
            System.out.println("Registering weather particles");
            ParticleRainClient.particleCount = 0;
            ParticleRainClient.fogCount = 0;

            NativeImage rainImage = null;
            NativeImage snowImage = null;
            try {
                rainImage = ClientStuff.loadTexture(new ResourceLocation("minecraft", "textures/environment/rain.png"));
                snowImage = ClientStuff.loadTexture(new ResourceLocation("minecraft", "textures/environment/snow.png"));
                if (ParticleRainClient.config.biomeTint) rainImage.applyToAllPixels(ClientStuff.desaturateOperation);
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (int j = 0; j < 4; j++) {
                newList.add(ClientStuff.splitImage(rainImage, j, "rain"));
            }
            for (int j = 0; j < 4; j++) {
                newList.add(ClientStuff.splitImage(snowImage, j, "snow"));
            }

            int rippleResolution = ClientStuff.getRippleResolution(newList);
            for (int j = 0; j < 8; j++) {
                newList.add(ClientStuff.generateRipple(j, rippleResolution));
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
                    newList.add(new SpriteContents(new ResourceLocation(ParticleRainClient.MOD_ID, "splash" + j),
                            new FrameSize(splashImage.getWidth(), splashImage.getHeight()),
                            splashImage,
                            AnimationMetadataSection.EMPTY));
                }
            }
        }

        return newList;
    }
}