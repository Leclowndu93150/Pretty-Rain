package com.leclowndu93150.particlerain.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ParticleInstanceBuffer {
    private final List<ParticleRenderData> instances = new ArrayList<>();
    private final BufferBuilder builder;
    private final VertexFormat format = DefaultVertexFormat.PARTICLE;

    public ParticleInstanceBuffer() {
        this.builder = new BufferBuilder(256);
    }

    public void addInstance(ParticleRenderData data) {
        instances.add(data);
    }

    public void render(Camera camera) {
        if (instances.isEmpty()) return;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getParticleShader);

        Vec3 cameraPos = camera.getPosition();
        float cx = (float)cameraPos.x();
        float cy = (float)cameraPos.y();
        float cz = (float)cameraPos.z();

        builder.begin(VertexFormat.Mode.QUADS, format);

        for (ParticleRenderData data : instances) {
            float x = data.position().x() - cx;
            float y = data.position().y() - cy;
            float z = data.position().z() - cz;
            float size = data.size() / 2.0f;

            builder.vertex(x - size, y - size, z).uv(data.u1(), data.v1())
                    .color(data.r(), data.g(), data.b(), data.a())
                    .uv2(data.light()).endVertex();

            builder.vertex(x - size, y + size, z).uv(data.u1(), data.v0())
                    .color(data.r(), data.g(), data.b(), data.a())
                    .uv2(data.light()).endVertex();

            builder.vertex(x + size, y + size, z).uv(data.u0(), data.v0())
                    .color(data.r(), data.g(), data.b(), data.a())
                    .uv2(data.light()).endVertex();

            builder.vertex(x + size, y - size, z).uv(data.u0(), data.v1())
                    .color(data.r(), data.g(), data.b(), data.a())
                    .uv2(data.light()).endVertex();
        }

        BufferUploader.drawWithShader(builder.end());
        instances.clear();

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }
}