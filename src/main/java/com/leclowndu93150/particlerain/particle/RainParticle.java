package com.leclowndu93150.particlerain.particle;

import com.leclowndu93150.particlerain.ParticleRainClient;
import com.leclowndu93150.particlerain.ParticleRainConfig;
import com.leclowndu93150.particlerain.ParticleRegistry;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.*;
import org.joml.Math;


public class RainParticle extends WeatherParticle {

    protected RainParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);

        if (ParticleRainConfig.biomeTint) ParticleRainClient.applyWaterTint(this, level, this.pos);

        this.quadSize = ParticleRainConfig.RainOptions.size;
        this.gravity = ParticleRainConfig.RainOptions.gravity;
        this.yd = -gravity;
        this.setSprite(Minecraft.getInstance().particleEngine.textureAtlas.getSprite(new ResourceLocation(ParticleRainClient.MODID, "rain" + random.nextInt(4))));

        if (level.isThundering()) {
            this.xd = gravity * ParticleRainConfig.RainOptions.stormWindStrength;
        } else {
            this.xd = gravity * ParticleRainConfig.RainOptions.windStrength;
        }
        if (ParticleRainConfig.yLevelWindAdjustment) {
            this.xd = this.xd * ParticleRainClient.yLevelWindAdjustment(y);
        }
        this.zd = this.xd;

        this.lifetime = ParticleRainConfig.particleRadius * 5;
        Vec3 vec3 = Minecraft.getInstance().cameraEntity.position();
        this.roll = (float) (Math.atan2(x - vec3.x, z - vec3.z) + Mth.HALF_PI);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age < 10) this.alpha = Math.clamp(0, ParticleRainConfig.RainOptions.opacity / 100F, this.alpha);
        if (this.onGround || !this.level.getFluidState(this.pos).isEmpty()) {
            // TODO: rewrite this whole bit
            if ((ParticleRainConfig.doSplashParticles || ParticleRainConfig.doSmokeParticles || ParticleRainConfig.doRippleParticles) && Minecraft.getInstance().cameraEntity.position().distanceTo(this.pos.getCenter()) < ParticleRainConfig.particleRadius - (ParticleRainConfig.particleRadius / 2.0)) {
                for (int i = 0; i < ParticleRainConfig.RainOptions.splashDensity; i++) {
                    Vec3 spawnPos = Vec3.atLowerCornerWithOffset(this.pos, (random.nextFloat() * 3) - 1, 0, (random.nextFloat() * 3) - 1);
                    double d = random.nextDouble();
                    double e = random.nextDouble();
                    BlockPos blockPos = BlockPos.containing(spawnPos);
                    BlockState blockState = level.getBlockState(blockPos);
                    FluidState fluidState = level.getFluidState(blockPos);
                    VoxelShape voxelShape = blockState.getCollisionShape(level, blockPos);
                    double voxelHeight = voxelShape.max(Direction.Axis.Y, d, e);
                    double fluidHeight = fluidState.getHeight(level, blockPos);
                    double height = java.lang.Math.max(voxelHeight, fluidHeight);
                    Vec3 raycastStart = new Vec3(this.x, this.y, this.z);
                    Vec3 raycastEnd = new Vec3(spawnPos.x, this.y, spawnPos.z);
                    BlockHitResult hit = level.clip(new ClipContext(raycastStart, raycastEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, Minecraft.getInstance().player));
                    Vec2 raycastHit = new Vec2((float)hit.getLocation().x, (float)hit.getLocation().z);
                    // this is SUCH a god damn mess
                    if (height != 0 && raycastHit.distanceToSqr(new Vec2((float) spawnPos.x, (float) spawnPos.z)) < 0.01) {
                        if (ParticleRainConfig.doRippleParticles && fluidState.isSourceOfType(Fluids.WATER)) {
                            if (height != 1) { // lazy workaround
                                Minecraft.getInstance().particleEngine.createParticle(ParticleRegistry.RIPPLE.get(), spawnPos.x, spawnPos.y + height, spawnPos.z, 0, 0, 0);
                                if (level.isThundering() && ParticleRainConfig.doSplashParticles) Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.RAIN, spawnPos.x, spawnPos.y + height, spawnPos.z, 0, 0, 0);
                            }
                        } else if (ParticleRainConfig.doSmokeParticles && (blockState.is(BlockTags.INFINIBURN_OVERWORLD) || blockState.is(BlockTags.STRIDER_WARM_BLOCKS))) {
                            //TODO: config option for warm block tags
                            Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.SMOKE, spawnPos.x, spawnPos.y + height, spawnPos.z, 0, 0, 0);
                            if (level.isThundering()) Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.LARGE_SMOKE, spawnPos.x, spawnPos.y + height, spawnPos.z, 0, 0, 0);
                        } else if (ParticleRainConfig.doSplashParticles) {
                            Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.RAIN, spawnPos.x, spawnPos.y + height, spawnPos.z, 0, 0, 0);
                        }
                    }
                }
            }
            this.remove();
        } else if (this.removeIfObstructed()) {
            Vec3 raycastStart = new Vec3(this.x, this.y, this.z);
            Vec3 raycastEnd = new Vec3(this.x + ParticleRainConfig.RainOptions.windStrength, this.y, this.z + ParticleRainConfig.RainOptions.windStrength);
            BlockHitResult hit = level.clip(new ClipContext(raycastStart, raycastEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, Minecraft.getInstance().player));
            if (hit.getType().equals(HitResult.Type.BLOCK)) {
                if (ParticleRainConfig.doStreakParticles && Minecraft.getInstance().cameraEntity.position().distanceTo(this.pos.getCenter()) < ParticleRainConfig.particleRadius - (ParticleRainConfig.particleRadius / 2.0)) {
                    BlockState state = level.getBlockState(hit.getBlockPos());
                    if (state.is(BlockTags.IMPERMEABLE) || state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
                        Minecraft.getInstance().particleEngine.createParticle(ParticleRegistry.STREAK.get(), this.x, this.y, this.z, hit.getDirection().get2DDataValue(), 0, 0);
                        Minecraft.getInstance().particleEngine.createParticle(ParticleTypes.RAIN, this.x, this.y, this.z, 0, 0, 0);
                    }
                }
                this.remove();
            }
        }
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickPercentage) {
        Vec3 camPos = camera.getPosition();
        float x = (float) (Mth.lerp(tickPercentage, this.xo, this.x) - camPos.x());
        float y = (float) (Mth.lerp(tickPercentage, this.yo, this.y) - camPos.y());
        float z = (float) (Mth.lerp(tickPercentage, this.zo, this.z) - camPos.z());

        Vector3f delta = new Vector3f((float) this.xd, (float) this.yd, (float) this.zd);
        final float angle = (float) Math.acos(delta.normalize().y);
        Vector3f axis = new Vector3f(-delta.z(), 0, delta.x()).normalize();
        Quaternionf quaternion = new Quaternionf().rotateAxis(-angle, axis.x(), axis.y(), axis.z());

        quaternion.mul(new Quaternionf().rotateY(this.roll));
        quaternion = this.flipItTurnwaysIfBackfaced(quaternion, new Vector3f(x, y, z));

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

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {

        public DefaultFactory(SpriteSet provider) {
        }

        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new RainParticle(level, x, y, z);
        }
    }
}