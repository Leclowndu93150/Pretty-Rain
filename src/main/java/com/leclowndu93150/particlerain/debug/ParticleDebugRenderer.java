package com.leclowndu93150.particlerain.debug;

import com.leclowndu93150.particlerain.ParticleRainClient;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Queue;

@Mod.EventBusSubscriber(modid = ParticleRainClient.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ParticleDebugRenderer {

    private static Field particlesField;

    static {
        try {
            particlesField = ParticleEngine.class.getDeclaredField("particles");
            particlesField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes() && !SharedConstants.IS_RUNNING_IN_IDE) return;

        ParticleEngine particleEngine = minecraft.particleEngine;
        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = event.getCamera().getPosition();

        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());

        renderParticleBoundingBoxes(particleEngine, poseStack, vertexConsumer, cameraPos);

        bufferSource.endBatch();
    }

    @SuppressWarnings("unchecked")
    private static void renderParticleBoundingBoxes(ParticleEngine particleEngine, PoseStack poseStack,
                                                    VertexConsumer vertexConsumer, Vec3 cameraPos) {
        if (particlesField == null) return;

        try {
            Map<ParticleRenderType, Queue<Particle>> particles = (Map<ParticleRenderType, Queue<Particle>>) particlesField.get(particleEngine);

            for (Queue<Particle> particleQueue : particles.values()) {
                for (Particle particle : particleQueue) {
                    if (particle != null && particle.isAlive()) {
                        AABB boundingBox = particle.getBoundingBox();
                        AABB offsetBox = boundingBox.move(-cameraPos.x, -cameraPos.y, -cameraPos.z);

                        LevelRenderer.renderLineBox(poseStack, vertexConsumer, offsetBox, 1.0f, 1.0f, 1.0f, 1.0f);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}