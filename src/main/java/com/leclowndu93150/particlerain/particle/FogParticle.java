package com.leclowndu93150.particlerain.particle;

import com.leclowndu93150.particlerain.ParticleRainClient;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
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

import java.awt.*;

public class FogParticle extends WeatherParticle {

    private FogParticle(ClientLevel level, double x, double y, double z, SpriteSet provider) {
        super(level, x, y, z);
        this.setSprite(provider.get(level.getRandom()));
        this.lifetime = ParticleRainClient.config.particleRadius * 5;
        final double distance = Minecraft.getInstance().cameraEntity.position().distanceTo(new Vec3(x, y, z));
        this.quadSize = (float) (ParticleRainClient.config.fog.size / distance);

        Color color = new Color(this.level.getBiome(this.pos).value().getFogColor()).darker();
        this.rCol = color.getRed() / 255F;
        this.gCol = color.getGreen() / 255F;
        this.bCol = color.getBlue() / 255F;

        this.roll = level.random.nextFloat() * Mth.PI;
        this.oRoll = this.roll;

        this.xd = gravity / 3;
        this.zd = gravity / 3;
        this.gravity = ParticleRainClient.config.fog.gravity;
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

        float yRot = (float) Math.atan2(x, z) + Mth.PI;
        float xRot = (float) Math.asin(y / Math.sqrt(x * x + y * y + z * z));
        Quaternion quaternion = Vector3f.YP.rotation(yRot);
        quaternion.mul(new Quaternion(xRot, 0, 0, true));
        quaternion.mul(new Quaternion(0, 0, (float) Math.atan2(x, z), true));

        if (xRot < -1) shouldFadeOut = true;

        quaternion.mul(new Quaternion(0, 0, Mth.lerp(f, this.oRoll, this.roll), true));
        this.renderRotatedQuad(vertexConsumer, quaternion, x, y, z, f);
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