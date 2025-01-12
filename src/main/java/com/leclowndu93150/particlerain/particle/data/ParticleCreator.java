package com.leclowndu93150.particlerain.particle.data;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;

public interface ParticleCreator<E extends ParticleData> {
    Particle createParticle(E data, SpriteSet sprites, ClientLevel level, double x, double y, double z, double xDelta, double yDelta, double zDelta);
}
