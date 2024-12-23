package com.leclowndu93150.particlerain;

import com.leclowndu93150.particlerain.particle.*;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.Mth;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import org.joml.Math;
import org.slf4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.IntUnaryOperator;

@Mod(ParticleRainClient.MODID)
public class ParticleRainClient {
    public static final String MODID = "particlerain";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static int particleCount;
    public static int fogCount;

    public ParticleRainClient(IEventBus modEventBus, ModContainer modContainer) {

        ParticleRegistry.SOUND_EVENTS.register(modEventBus);
        ParticleRegistry.PARTICLE_TYPES.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.CLIENT, ParticleRainConfig.SPEC);
        
        modEventBus.addListener(this::registerParticleFactories);
        NeoForge.EVENT_BUS.addListener(this::onClientTick);
        NeoForge.EVENT_BUS.addListener(this::registerClientCommands);
    }

    private void registerParticleFactories(RegisterParticleProvidersEvent event) {
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

    private void registerClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal(MODID)
                .executes(ctx -> {
                    ctx.getSource().sendSystemMessage(Component.literal(String.format("Particle count: %d/%d", particleCount, ParticleRainConfig.maxParticleAmount)));
                    ctx.getSource().sendSystemMessage(Component.literal(String.format("Fog density: %d/%d", fogCount, ParticleRainConfig.groundFogDensity)));
                    return 0;
                }));
    }

    private void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.isPaused() && minecraft.level != null && minecraft.getCameraEntity() != null) {
            WeatherParticleSpawner.update(minecraft.level, minecraft.getCameraEntity(), minecraft.getFrameTimeNs());
        }
    }

    public static final IntUnaryOperator desaturateOperation = (int rgba) -> {
        Color col = new Color(rgba, true);
        int gray = Math.max(Math.max(col.getRed(), col.getGreen()), col.getBlue());
        return ((col.getAlpha() & 0xFF) << 24) |
                ((gray & 0xFF) << 16) |
                ((gray & 0xFF) << 8) |
                ((gray & 0xFF));
    };

    public static void applyWaterTint(TextureSheetParticle particle, ClientLevel clientLevel, BlockPos blockPos) {
        final Color waterColor = new Color(BiomeColors.getAverageWaterColor(clientLevel, blockPos));
        final Color fogColor = new Color(clientLevel.getBiome(blockPos).value().getFogColor());
        float rCol = (Mth.lerp(ParticleRainConfig.tintMix / 100F, waterColor.getRed(), fogColor.getRed()) / 255F);
        float gCol = (Mth.lerp(ParticleRainConfig.tintMix / 100F, waterColor.getGreen(), fogColor.getGreen()) / 255F);
        float bCol = (Mth.lerp(ParticleRainConfig.tintMix / 100F, waterColor.getBlue(), fogColor.getBlue()) / 255F);
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
        return new SpriteContents(ResourceLocation.fromNamespaceAndPath(MODID, id + segment), new FrameSize(size, size), sprite, ResourceMetadata.EMPTY);
    }

    public static double yLevelWindAdjustment(double y) {
        return Math.clamp(0.01, 1, (y - 64) / 40);
    }

    public static int getRippleResolution(List<SpriteContents> contents) {
        if (ParticleRainConfig.useResourcepackResolution) {
            ResourceLocation resourceLocation = ResourceLocation.withDefaultNamespace("big_smoke_0");
            for (SpriteContents spriteContents : contents) {
                if (spriteContents.name().equals(resourceLocation)) {
                    if (spriteContents.width() < 256) {
                        return spriteContents.width();
                    }
                }
            }
        }
        int resolution = ParticleRainConfig.rippleResolution;
        if (resolution < 4) resolution = 4;
        if (resolution > 256) resolution = 256;
        return resolution;
    }

    public static SpriteContents generateRipple(int i, int size) {
        float radius = ((size / 2F) / 8) * (i + 1);
        NativeImage image = new NativeImage(size, size, true);
        Color color = Color.WHITE;
        int colorint = ((color.getAlpha() & 0xFF) << 24) |
                ((color.getRed() & 0xFF) << 16) |
                ((color.getGreen() & 0xFF) << 8) |
                ((color.getBlue() & 0xFF));
        generateBresenhamCircle(image, size, (int) Math.clamp(1, (size / 2F) - 1, radius), colorint);
        return new SpriteContents(ResourceLocation.fromNamespaceAndPath(MODID, "ripple" + i), new FrameSize(size, size), image, ResourceMetadata.EMPTY);
    }

    public static void generateBresenhamCircle(NativeImage image, int imgSize, int radius, int colorint) {
        int centerX = imgSize / 2;
        int centerY = imgSize / 2;
        int x = 0, y = radius;
        int d = 3 - 2 * radius;
        drawCirclePixel(centerX, centerY, x, y, image, colorint);
        while (y >= x) {
            if (d > 0) {
                y--;
                d = d + 4 * (x - y) + 10;
            } else
                d = d + 4 * x + 6;
            x++;
            drawCirclePixel(centerX, centerY, x, y, image, colorint);
        }
    }

    private static void drawCirclePixel(int xc, int yc, int x, int y, NativeImage img, int col) {
        img.setPixel(xc + x, yc + y, col);
        img.setPixel(xc - x, yc + y, col);
        img.setPixel(xc + x, yc - y, col);
        img.setPixel(xc - x, yc - y, col);
        img.setPixel(xc + y, yc + x, col);
        img.setPixel(xc - y, yc + x, col);
        img.setPixel(xc + y, yc - x, col);
        img.setPixel(xc - y, yc - x, col);
    }
}