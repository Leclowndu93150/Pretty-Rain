package com.leclowndu93150.particlerain.particle;

import com.leclowndu93150.particlerain.ParticleRainConfig;
import com.leclowndu93150.particlerain.rendering.ParticleManager;
import com.leclowndu93150.particlerain.rendering.ParticleRenderData;
import com.leclowndu93150.particlerain.rendering.ParticleRenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class DustParticle extends DustMoteParticle {

    protected DustParticle(ClientLevel clientWorld, double x, double y, double z, SpriteSet provider) {
        super(clientWorld, x, y, z, provider);
        this.quadSize = ParticleRainConfig.SandOptions.size;
        this.gravity = ParticleRainConfig.SandOptions.gravity - 0.1F;
        if (ParticleRainConfig.SandOptions.spawnOnGround) this.yd = 0.1F;
    }
    @Override
    public void tick() {
        super.tick();
        if (this.onGround) {
            this.yd = 0.01F;
        }
    }
    @Override
    public void render(VertexConsumer consumer, Camera camera, float partialTick) {
        Vec3 camPos = camera.getPosition();
        Vector3f pos = new Vector3f(
                (float) (Mth.lerp(partialTick, this.xo, this.x) - camPos.x()),
                (float) (Mth.lerp(partialTick, this.yo, this.y) - camPos.y() + Mth.sin((Mth.lerp(partialTick, this.age - 1.0F, this.age)) / 20) + 1.5F),
                (float) (Mth.lerp(partialTick, this.zo, this.z) - camPos.z())
        );

        ParticleRenderData data = new ParticleRenderData(
                pos,
                this.getQuadSize(partialTick),
                this.getU0(), this.getV0(),
                this.getU1(), this.getV1(),
                this.getLightColor(partialTick),
                this.rCol, this.gCol, this.bCol, this.alpha
        );

        ParticleManager.instance.addParticle(this, data);
    }

    @Override
    public ParticleRenderType getParticleType() {
        return ParticleRenderType.DUST;
    }

    public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet provider;

        public DefaultFactory(SpriteSet provider) {
            this.provider = provider;
        }

        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new DustParticle(level, x, y, z, this.provider);
        }
    }
}
