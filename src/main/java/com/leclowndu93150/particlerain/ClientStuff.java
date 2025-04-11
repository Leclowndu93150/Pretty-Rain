package com.leclowndu93150.particlerain;

import com.leclowndu93150.particlerain.particle.*;
import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.ints.IntUnaryOperator;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Math;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.leclowndu93150.particlerain.ParticleRainClient.fogCount;
import static com.leclowndu93150.particlerain.ParticleRainClient.particleCount;

public class ClientStuff {

    public static final String MOD_ID = "particlerain";
    public static ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    public static boolean previousBiomeTintOption;
    public static boolean previousUseResolutionOption;
    public static int previousResolutionOption;

    static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleRegistry.RAIN.get(), RainParticle.DefaultFactory::new);
        event.registerSpriteSet(ParticleRegistry.SNOW.get(), SnowParticle.DefaultFactory::new);
        event.registerSpriteSet(ParticleRegistry.DUST_MOTE.get(), DustMoteParticle.DefaultFactory::new);
        event.registerSpriteSet(ParticleRegistry.DUST.get(), DustParticle.DefaultFactory::new);
        event.registerSpriteSet(ParticleRegistry.SHRUB.get(), ShrubParticle.DefaultFactory::new);
        event.registerSpriteSet(ParticleRegistry.FOG.get(), FogParticle.DefaultFactory::new);
        event.registerSpriteSet(ParticleRegistry.GROUND_FOG.get(), GroundFogParticle.DefaultFactory::new);
        event.registerSpriteSet(ParticleRegistry.RIPPLE.get(), RippleParticle.DefaultFactory::new);
        event.registerSpriteSet(ParticleRegistry.STREAK.get(), StreakParticle.DefaultFactory::new);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE,value = Dist.CLIENT)
    public static class ModClientForgeEvents {
        @SubscribeEvent
        public static void registerClientCommands(RegisterClientCommandsEvent event) {
            event.getDispatcher().register(Commands.literal(MOD_ID)
                    .executes(ctx -> {
                        ctx.getSource().sendSystemMessage(Component.literal(String.format("Particle count: %d/%d", particleCount, ParticleRainClient.config.maxParticleAmount)));
                        ctx.getSource().sendSystemMessage(Component.literal(String.format("Fog density: %d/%d", fogCount, ParticleRainClient.config.groundFog.density)));
                        return 0;
                    }));
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if(event.phase == TickEvent.Phase.END){
                Minecraft minecraft = Minecraft.getInstance();
                if (!minecraft.isPaused() && minecraft.level != null && minecraft.getCameraEntity() != null) {
                    WeatherParticleSpawner.update(minecraft.level, minecraft.getCameraEntity(), minecraft.getFrameTimeNs());
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerJoin(ClientPlayerNetworkEvent.LoggingIn event) {
            particleCount = 0;
            fogCount = 0;
        }

        @SubscribeEvent
        public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event){
            particleCount = 0;
            fogCount = 0;
        }
    }

    static InteractionResult saveListener(ConfigHolder<ModConfig> modConfigConfigHolder, ModConfig modConfig) {
        if (config.biomeTint != previousBiomeTintOption || config.ripple.useResourcepackResolution != previousUseResolutionOption || config.ripple.resolution != previousResolutionOption) {
            Minecraft.getInstance().reloadResourcePacks();
        }
        return InteractionResult.PASS;
    }

    public static IntUnaryOperator desaturateOperation = (int rgba) -> {
        Color col = new Color(rgba, true);
        int gray = Math.max(Math.max(col.getRed(), col.getGreen()), col.getBlue());
        return ((col.getAlpha() & 0xFF) << 24) |
                ((gray & 0xFF) << 16) |
                ((gray & 0xFF) << 8)  |
                ((gray & 0xFF));
    };

    public static void applyWaterTint(TextureSheetParticle particle, ClientLevel clientLevel, BlockPos blockPos) {
        final Color waterColor = new Color(BiomeColors.getAverageWaterColor(clientLevel, blockPos));
        final Color fogColor = new Color(clientLevel.getBiome(blockPos).value().getFogColor());
        float rCol = (Mth.lerp(config.tintMix / 100F, waterColor.getRed(), fogColor.getRed()) / 255F);
        float gCol = (Mth.lerp(config.tintMix / 100F, waterColor.getGreen(), fogColor.getGreen()) / 255F);
        float bCol = (Mth.lerp(config.tintMix / 100F, waterColor.getBlue(), fogColor.getBlue()) / 255F);
        particle.setColor(rCol, gCol, bCol);
    }

    public static NativeImage loadTexture(ResourceLocation resourceLocation) throws IOException {
        Resource resource = Minecraft.getInstance().getResourceManager().getResourceOrThrow(resourceLocation);
        InputStream inputStream = resource.open();
        NativeImage nativeImage;
        try {
            nativeImage = NativeImage.read(inputStream);
        } catch (Throwable owo) {
            try {
                inputStream.close();
            } catch (Throwable uwu) {
                owo.addSuppressed(uwu);
            }
            throw owo;
        }
        inputStream.close();
        return nativeImage;
    }

    public static SpriteContents splitImage(NativeImage image, int segment, String id) {
        int size = image.getWidth();
        NativeImage sprite = new NativeImage(size, size, false);
        image.copyRect(sprite, 0, size * segment, 0, 0, size, size, true, true);
        return(new SpriteContents(new ResourceLocation(ParticleRainClient.MOD_ID, id + segment), new FrameSize(size, size), sprite, AnimationMetadataSection.EMPTY));
    }

    public static double yLevelWindAdjustment(double y) {
        return Math.clamp(0.01, 1, (y - 64) / 40);
    }

    public static int getRippleResolution(List<SpriteContents> contents) {
        if (config.ripple.useResourcepackResolution) {
            ResourceLocation resourceLocation = new ResourceLocation("big_smoke_0");
            for (SpriteContents spriteContents : contents) {
                if (spriteContents.name().equals(resourceLocation)) {
                    if (spriteContents.width() < 256) {
                        return spriteContents.width();
                    }
                }
            }
        }
        if (config.ripple.resolution < 4) config.ripple.resolution = 4;
        if (config.ripple.resolution > 256) config.ripple.resolution = 256;
        return config.ripple.resolution;
    }

    public static SpriteContents generateRipple(int i, int size) {
        float radius = ((size / 2F) / 8) * (i + 1);
        NativeImage image = new NativeImage(size, size, true);
        Color color = Color.WHITE;
        int colorint = ((color.getAlpha() & 0xFF) << 24) |
                ((color.getRed() & 0xFF) << 16) |
                ((color.getGreen() & 0xFF) << 8)  |
                ((color.getBlue() & 0xFF));
        generateBresenhamCircle(image, size, (int) Math.clamp(1, (size / 2F) - 1, radius), colorint);
        return(new SpriteContents(new ResourceLocation(ParticleRainClient.MOD_ID, "ripple" + i), new FrameSize(size, size), image, AnimationMetadataSection.EMPTY));
    }

    public static void generateBresenhamCircle(NativeImage image, int imgSize, int radius, int colorint) {
        int centerX = imgSize / 2;
        int centerY = imgSize / 2;
        int x = 0, y = radius;
        int d = 3 - 2 * radius;
        drawCirclePixel(centerX, centerY, x, y, image, colorint);
        while (y >= x){
            if (d > 0) {
                y--;
                d = d + 4 * (x - y) + 10;
            }
            else
                d = d + 4 * x + 6;
            x++;
            drawCirclePixel(centerX, centerY, x, y, image, colorint);
        }
    }

    static void drawCirclePixel(int xc, int yc, int x, int y, NativeImage img, int col){
        img.setPixelRGBA(xc+x, yc+y, col);
        img.setPixelRGBA(xc-x, yc+y, col);
        img.setPixelRGBA(xc+x, yc-y, col);
        img.setPixelRGBA(xc-x, yc-y, col);
        img.setPixelRGBA(xc+y, yc+x, col);
        img.setPixelRGBA(xc-y, yc+x, col);
        img.setPixelRGBA(xc+y, yc-x, col);
        img.setPixelRGBA(xc-y, yc-x, col);
    }

}
