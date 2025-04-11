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
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

import static com.leclowndu93150.particlerain.ParticleRainClient.fogCount;
import static com.leclowndu93150.particlerain.ParticleRainClient.particleCount;

public class ClientStuff {

    public static final String MOD_ID = "particlerain";
    public static ModConfig config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    public static boolean previousBiomeTintOption;
    public static boolean previousUseResolutionOption;
    public static int previousResolutionOption;

    static void registerParticles(RegisterParticleProvidersEvent event) {
        event.register(ParticleRegistry.RAIN.get(), RainParticle.DefaultFactory::new);
        event.register(ParticleRegistry.SNOW.get(), SnowParticle.DefaultFactory::new);
        event.register(ParticleRegistry.DUST_MOTE.get(), DustMoteParticle.DefaultFactory::new);
        event.register(ParticleRegistry.DUST.get(), DustParticle.DefaultFactory::new);
        event.register(ParticleRegistry.SHRUB.get(), ShrubParticle.DefaultFactory::new);
        event.register(ParticleRegistry.FOG.get(), FogParticle.DefaultFactory::new);
        event.register(ParticleRegistry.GROUND_FOG.get(), GroundFogParticle.DefaultFactory::new);
        event.register(ParticleRegistry.RIPPLE.get(), RippleParticle.DefaultFactory::new);
        event.register(ParticleRegistry.STREAK.get(), StreakParticle.DefaultFactory::new);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE,value = Dist.CLIENT)
    public static class ModClientForgeEvents {
        @SubscribeEvent
        public static void registerClientCommands(RegisterClientCommandsEvent event) {
            event.getDispatcher().register(Commands.literal(MOD_ID)
                    .executes(ctx -> {
                        ctx.getSource().sendSuccess(Component.literal(String.format("Particle count: %d/%d", particleCount, ParticleRainClient.config.maxParticleAmount)), false);
                        ctx.getSource().sendSuccess(Component.literal(String.format("Fog density: %d/%d", fogCount, ParticleRainClient.config.groundFog.density)), false);
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
        public static void onPlayerJoin(ClientPlayerNetworkEvent.LoggingIn event) {
            particleCount = 0;
            fogCount = 0;
        }

        @SubscribeEvent
        public static void onPlayerClone(ClientPlayerNetworkEvent.Clone event) {
            particleCount = 0;
            fogCount = 0;
        }
    }

    @Mod.EventBusSubscriber(modid = ParticleRainClient.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public class ClientEvents {
        @SubscribeEvent
        public static void onTextureStitch(TextureStitchEvent.Pre event) {
            if (!event.getAtlas().location().equals(TextureAtlas.LOCATION_PARTICLES)) return;

            ParticleRainClient.particleCount = 0;
            ParticleRainClient.fogCount = 0;

            for (int idx = 0; idx < 4; idx++) {
                try {
                    if (ParticleRainClient.config.biomeTint) {
                        NativeImage rainTexture = ClientStuff.loadTexture(new ResourceLocation(ParticleRainClient.MOD_ID, "textures/particle/rain" + idx + ".png")
                        );
                        ClientStuff.applyToAllPixels(rainTexture, ClientStuff.desaturateOperation);
                    }
                    addSprite(event, "rain" + idx);
                    addSprite(event, "snow" + idx);
                    addSprite(event, "splash_" + idx);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //loop from 0 to 7
            for (int idx = 0; idx < 8; idx++) {
                addSprite(event, "ripple_"+idx);
            }

            for (String texture : new String[]{"streak", "ground_fog", "fog_dithered", "dust"}) {
                addSprite(event, texture);
            }
        }

        private static void addSprite(TextureStitchEvent.Pre event, String path) {
            event.addSprite(new ResourceLocation(ParticleRainClient.MOD_ID, "particle/" + path));
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

    public static void applyToAllPixels(NativeImage image, IntUnaryOperator operator) {
        for(int x = 0; x < image.getWidth(); x++) {
            for(int y = 0; y < image.getHeight(); y++) {
                int rgba = Integer.rotateLeft(image.getPixelRGBA(x, y), 8); // ABGR to RGBA
                rgba = operator.applyAsInt(rgba);
                image.setPixelRGBA(x, y, Integer.rotateRight(rgba, 8)); // RGBA back to ABGR
            }
        }
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

    public static TextureAtlasSprite.Info createInfo(ResourceLocation location, NativeImage image) {
        return new TextureAtlasSprite.Info(location, image.getWidth(), image.getHeight(), AnimationMetadataSection.EMPTY);
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

}
