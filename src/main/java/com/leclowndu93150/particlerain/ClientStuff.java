package com.leclowndu93150.particlerain;

import com.leclowndu93150.particlerain.particle.*;
import com.mojang.blaze3d.platform.NativeImage;
import it.unimi.dsi.fastutil.ints.IntUnaryOperator;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;
import java.io.IOException;
import java.util.List;

import static com.leclowndu93150.particlerain.ParticleRainClient.fogCount;
import static com.leclowndu93150.particlerain.ParticleRainClient.particleCount;

public class ClientStuff {

    public static final String MOD_ID = "particlerain";
    public static ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    public static boolean previousBiomeTintOption;
    public static boolean previousUseResolutionOption;
    public static int previousResolutionOption;

    static void registerParticles(ParticleFactoryRegisterEvent event) {
        ParticleEngine engine = Minecraft.getInstance().particleEngine;

        engine.register(ParticleRegistry.RAIN.get(), RainParticle.DefaultFactory::new);
        engine.register(ParticleRegistry.SNOW.get(), SnowParticle.DefaultFactory::new);
        engine.register(ParticleRegistry.DUST_MOTE.get(), DustMoteParticle.DefaultFactory::new);
        engine.register(ParticleRegistry.DUST.get(), DustParticle.DefaultFactory::new);
        engine.register(ParticleRegistry.FOG.get(), FogParticle.DefaultFactory::new);
        engine.register(ParticleRegistry.GROUND_FOG.get(), GroundFogParticle.DefaultFactory::new);
        engine.register(ParticleRegistry.SHRUB.get(), ShrubParticle.DefaultFactory::new);
        engine.register(ParticleRegistry.RIPPLE.get(), RippleParticle.DefaultFactory::new);
        engine.register(ParticleRegistry.STREAK.get(), StreakParticle.DefaultFactory::new);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE,value = Dist.CLIENT)
    public static class ModClientForgeEvents {
        @SubscribeEvent
        public static void registerClientCommands(RegisterClientCommandsEvent event) {
            event.getDispatcher().register(Commands.literal(MOD_ID)
                    .executes(ctx -> {
                        ctx.getSource().sendSuccess(new net.minecraft.network.chat.TextComponent(String.format("Particle count: %d/%d", particleCount, ParticleRainClient.config.maxParticleAmount)), false);
                        ctx.getSource().sendSuccess(new TextComponent(String.format("Fog density: %d/%d", fogCount, ParticleRainClient.config.groundFog.density)), false);
                        return 0;
                    }));
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if(event.phase == TickEvent.Phase.END){
                Minecraft minecraft = Minecraft.getInstance();
                if (!minecraft.isPaused() && minecraft.level != null && minecraft.getCameraEntity() != null) {
                    WeatherParticleSpawner.update(minecraft.level, minecraft.getCameraEntity(), minecraft.getDeltaFrameTime());
                }
            }
        }

        @SubscribeEvent
        public void onPlayerJoin(ClientPlayerNetworkEvent.LoggedInEvent event) {
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

    public static NativeImage loadTexture(ResourceManager manager, ResourceLocation location) throws IOException {
        Resource resource = manager.getResource(location);
        return NativeImage.read(resource.getInputStream());
    }

    public static TextureAtlasSprite.Info createInfo(ResourceLocation location, NativeImage image) {
        return new TextureAtlasSprite.Info(location, image.getWidth(), image.getHeight(), AnimationMetadataSection.EMPTY);
    }

    public static TextureAtlasSprite.Info splitImage(NativeImage source, int index, String name) {
        int frameWidth = source.getWidth() / 4;
        int frameHeight = source.getHeight();
        NativeImage frame = new NativeImage(frameWidth, frameHeight, false);

        for(int x = 0; x < frameWidth; x++) {
            for(int y = 0; y < frameHeight; y++) {
                frame.setPixelRGBA(x, y, source.getPixelRGBA(x + index * frameWidth, y));
            }
        }

        return createInfo(new ResourceLocation(ParticleRainClient.MOD_ID, name + index), frame);
    }

    public static void desaturateImage(NativeImage image) {
        for(int x = 0; x < image.getWidth(); x++) {
            for(int y = 0; y < image.getHeight(); y++) {
                int pixel = image.getPixelRGBA(x, y);
                int r = NativeImage.getR(pixel);
                int g = NativeImage.getG(pixel);
                int b = NativeImage.getB(pixel);
                int a = NativeImage.getA(pixel);

                // Grayscale conversion using luminance formula
                int gray = (int)(r * 0.299 + g * 0.587 + b * 0.114);

                image.setPixelRGBA(x, y, NativeImage.combine(a, gray, gray, gray));
            }
        }
    }

    public static double yLevelWindAdjustment(double y) {
        return Mth.clamp(0.01, 1, (y - 64) / 40);
    }

    public static int getRippleResolution(List<TextureAtlasSprite> sprites) {
        if (config.ripple.useResourcepackResolution) {
            ResourceLocation resourceLocation = new ResourceLocation("big_smoke_0");
            for (TextureAtlasSprite sprite : sprites) {
                if (sprite.getName().equals(resourceLocation)) {
                    if (sprite.getWidth() < 256) {
                        return sprite.getWidth();
                    }
                }
            }
        }
        if (config.ripple.resolution < 4) config.ripple.resolution = 4;
        if (config.ripple.resolution > 256) config.ripple.resolution = 256;
        return config.ripple.resolution;
    }

    public static TextureAtlasSprite.Info generateRipple(int i, int size) {
        float radius = ((size / 2F) / 8) * (i + 1);
        NativeImage image = new NativeImage(size, size, true);
        Color color = Color.WHITE;
        int colorint = ((color.getAlpha() & 0xFF) << 24) |
                ((color.getRed() & 0xFF) << 16) |
                ((color.getGreen() & 0xFF) << 8)  |
                ((color.getBlue() & 0xFF));
        generateBresenhamCircle(image, size, (int) Mth.clamp(1, (size / 2F) - 1, radius), colorint);
        return new TextureAtlasSprite.Info(new ResourceLocation(ParticleRainClient.MOD_ID, "particle/ripple_" + i), size, size, AnimationMetadataSection.EMPTY);
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
