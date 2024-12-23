package com.leclowndu93150.particlerain.rendering;

import com.leclowndu93150.particlerain.particle.WeatherParticle;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.TextureSheetParticle;

import java.util.EnumMap;
import java.util.Map;

public class ParticleManager {
    public static final ParticleManager instance = new ParticleManager();
    private final Map<ParticleRenderType, ParticleInstanceBuffer> buffers = new EnumMap<>(ParticleRenderType.class);

    private ParticleManager() {}

    public void addParticle(TextureSheetParticle particle, ParticleRenderData data) {
        if(particle instanceof WeatherParticle weatherParticle) {
            buffers.computeIfAbsent(weatherParticle.getParticleType(), k -> new ParticleInstanceBuffer())
                    .addInstance(data);
        }
    }

    public void renderParticles(Camera camera) {
        System.out.println("Rendering particles... Camera Position: " + camera.getPosition());
        buffers.values().forEach(buffer -> {
            System.out.println("Buffer contains: " + buffer + " instances");
            buffer.render(camera);
        });
    }
}