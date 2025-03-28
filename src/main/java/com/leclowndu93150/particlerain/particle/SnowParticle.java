package com.leclowndu93150.particlerain.particle;

import com.leclowndu93150.particlerain.ParticleRainClient;
import com.leclowndu93150.particlerain.ParticleRainConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class SnowParticle extends WeatherParticle {

    float rotationAmount;

    protected SnowParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.quadSize = ParticleRainConfig.SnowOptions.size;
        this.gravity = ParticleRainConfig.SnowOptions.gravity;
        this.yd = -gravity;
        this.setSprite(Minecraft.getInstance().particleEngine.textureAtlas.getSprite(ResourceLocation.fromNamespaceAndPath(ParticleRainClient.MODID, "snow" + random.nextInt(4))));

        if (level.isThundering()) {
            this.xd = gravity * ParticleRainConfig.SnowOptions.stormWindStrength;
        } else {
            this.xd = gravity * ParticleRainConfig.SnowOptions.windStrength;
        }
        if (ParticleRainConfig.yLevelWindAdjustment) {
            this.xd = this.xd * ParticleRainClient.yLevelWindAdjustment(y);
        }
        this.zd = this.xd;

        if (level.getRandom().nextBoolean()) {
            this.rotationAmount = 1;
        } else {
            this.rotationAmount = -1;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.getFluidState(this.pos.below()).isEmpty()) {
            this.alpha = 0;
        } else if (!this.level.getFluidState(this.pos.below(2)).isEmpty()) {
            double distanceToWater = this.pos.below(2).getY() - this.pos.getY();
            this.alpha = (float) Math.abs(distanceToWater) / 2.0f;
        }
        this.oRoll = this.roll;
        this.roll = this.oRoll + (level.isThundering() ? ParticleRainConfig.SnowOptions.stormRotationAmount : ParticleRainConfig.SnowOptions.rotationAmount) * this.rotationAmount;
        if (this.onGround || this.removeIfObstructed()) {
            this.remove();
        }
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
            return new SnowParticle(level, x, y, z);
        }
    }
}
