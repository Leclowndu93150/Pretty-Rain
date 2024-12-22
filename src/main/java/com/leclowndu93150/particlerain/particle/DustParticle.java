package com.leclowndu93150.particlerain.particle;

import com.leclowndu93150.particlerain.ParticleRainConfig;
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
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickPercentage) {
        Vec3 camPos = camera.getPosition();
        float x = (float) (Mth.lerp(tickPercentage, this.xo, this.x) - camPos.x());
        float y = (float) (Mth.lerp(tickPercentage, this.yo, this.y) - camPos.y());
        float z = (float) (Mth.lerp(tickPercentage, this.zo, this.z) - camPos.z());

        Quaternionf quaternion = camera.rotation();
        y = y + Mth.sin((Mth.lerp(tickPercentage, this.age - 1.0F, this.age)) / 20) + 1.5F;

        Vector3f[] corners = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
        };

        float scale = this.getQuadSize(tickPercentage);
        for (int i = 0; i < 4; i++) {
            Vector3f corner = corners[i];
            corner.rotate(quaternion);
            corner.mul(scale);
            corner.add(x, y, z);
        }

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int light = this.getLightColor(tickPercentage);

        vertexConsumer.vertex(corners[0].x(), corners[0].y(), corners[0].z())
                .uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(light).endVertex();
        vertexConsumer.vertex(corners[1].x(), corners[1].y(), corners[1].z())
                .uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(light).endVertex();
        vertexConsumer.vertex(corners[2].x(), corners[2].y(), corners[2].z())
                .uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(light).endVertex();
        vertexConsumer.vertex(corners[3].x(), corners[3].y(), corners[3].z())
                .uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv2(light).endVertex();
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
