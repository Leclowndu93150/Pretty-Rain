package com.leclowndu93150.particlerain.particle;

import com.leclowndu93150.particlerain.ParticleRainClient;
import com.leclowndu93150.particlerain.ParticleRainConfig;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.AxisAngle4d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class StreakParticle extends WeatherParticle {

    Direction direction;

    private StreakParticle(ClientLevel level, double x, double y, double z, int direction2D, SpriteSet provider) {
        super(level, x, y, z);

        if (ParticleRainConfig.biomeTint) {
            ParticleRainClient.applyWaterTint(this, level, this.pos);
        } else {
            this.setColor(0.2f, 0.3f, 1.0f);
        }

        this.setSprite(provider.get(level.getRandom()));
        this.quadSize = 0.5F;
        this.gravity = random.nextFloat() / 10;

        this.roll = direction2D * Mth.HALF_PI;
        direction = Direction.from2DDataValue(direction2D);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age % 10 == 0) {
            if (random.nextBoolean()) {
                this.gravity = random.nextFloat() / 10;
            } else {
                this.gravity = 0;
            }
        }
        BlockState state = level.getBlockState(this.pos.relative(direction.getOpposite()));
        FluidState fluidState = level.getFluidState(this.pos);
        if (!this.shouldFadeOut && (this.onGround || !(state.is(BlockTags.IMPERMEABLE) || state.is(BlockTags.MINEABLE_WITH_PICKAXE)) || !fluidState.isEmpty())) {
            if (state.isAir()) Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.DRIPPING_WATER, this.x, this.y - 0.25F, this.z, 0, 0, 0);
            this.gravity = 0;
            this.yd = 0;
            this.shouldFadeOut = true;
        }
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float f) {
        Vec3 camPos = camera.getPosition();
        float x = (float) (Mth.lerp(f, this.xo, this.x) - camPos.x());
        float y = (float) (Mth.lerp(f, this.yo, this.y) - camPos.y());
        float z = (float) (Mth.lerp(f, this.zo, this.z) - camPos.z());

        Quaternionf quaternion = new Quaternionf().rotateY((float) Math.toRadians(this.roll));
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
            corner.add(x, y + 0.25F, z);
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
            return new StreakParticle(level, x, y, z, (int) velocityX, this.provider);
        }
    }
}
