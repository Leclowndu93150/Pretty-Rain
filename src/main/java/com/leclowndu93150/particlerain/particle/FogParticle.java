package com.leclowndu93150.particlerain.particle;

import com.leclowndu93150.particlerain.ParticleRainConfig;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;

public class FogParticle extends WeatherParticle {

    private FogParticle(ClientLevel level, double x, double y, double z, SpriteSet provider) {
        super(level, x, y, z);
        this.setSprite(provider.get(level.getRandom()));
        this.lifetime = ParticleRainConfig.particleRadius * 5;
        final double distance = Minecraft.getInstance().cameraEntity.position().distanceTo(new Vec3(x, y, z));
        this.quadSize = (float) (ParticleRainConfig.FogOptions.size / distance);

        Color color = new Color(this.level.getBiome(this.pos).value().getFogColor()).darker();
        this.rCol = color.getRed() / 255F;
        this.gCol = color.getGreen() / 255F;
        this.bCol = color.getBlue() / 255F;

        this.roll = level.random.nextFloat() * Mth.PI;
        this.oRoll = this.roll;

        this.xd = gravity / 3;
        this.zd = gravity / 3;
        this.gravity = ParticleRainConfig.FogOptions.gravity;
    }

    public void tick() {
        super.tick();
        final double camdist = Minecraft.getInstance().cameraEntity.position().distanceTo(new Vec3(x, y, z));
        this.quadSize = (float) camdist / 2;
        BlockState fallingTowards = level.getBlockState(this.pos.offset(3, -1, 3));
        BlockPos blockPos = this.pos.offset(2, -4, 2);
        if (level.getHeight(Heightmap.Types.MOTION_BLOCKING, blockPos.getX(), blockPos.getZ()) >= blockPos.getY() || !fallingTowards.getFluidState().isEmpty()) {
            if (!shouldFadeOut) {
                shouldFadeOut = true;
            }
        }
        if (onGround) {
            remove();
        }
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float f) {
        Vec3 camPos = camera.getPosition();
        float x = (float) (Mth.lerp(f, this.xo, this.x) - camPos.x());
        float y = (float) (Mth.lerp(f, this.yo, this.y) - camPos.y());
        float z = (float) (Mth.lerp(f, this.zo, this.z) - camPos.z());
        Vector3f localPos = new Vector3f(x, y, z);

        // rotate particle around y axis to face player
        Quaternionf quaternion = new Quaternionf().rotateY((float) Math.atan2(x, z) + Mth.PI);
        // rotate particle by angle between y axis and camera location
        float yAngle = (float) Math.asin(y / localPos.length());
        quaternion.rotateX(yAngle)
                .rotateZ((float) Math.atan2(x, z))
                .rotateZ(Mth.lerp(f, this.oRoll, this.roll));

        // the z rotation doubles up on the -y axis instead of negating it like the positive axis. idk how to fix
        // for now we remove them before it gets to look too weird
        if (yAngle < -1) shouldFadeOut = true;

        Vector3f[] corners = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
        };

        float scale = this.getQuadSize(f);
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
        int light = this.getLightColor(f);

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

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet provider;

        public DefaultFactory(SpriteSet provider) {
            this.provider = provider;
        }

        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new FogParticle(level, x, y, z, this.provider);
        }
    }
}
