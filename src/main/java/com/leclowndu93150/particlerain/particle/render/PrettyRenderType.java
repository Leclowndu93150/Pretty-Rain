package com.leclowndu93150.particlerain.particle.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;

public class PrettyRenderType implements ParticleRenderType {
    public static final ParticleRenderType INSTANCE = new PrettyRenderType();

    @Override
    public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
        RenderSystem.depthMask(true);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.3F);
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
    }

    @Override
    public void end(Tesselator tesselator) {
        tesselator.end();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}