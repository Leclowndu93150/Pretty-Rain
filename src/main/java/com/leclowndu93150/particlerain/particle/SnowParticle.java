package com.leclowndu93150.particlerain.particle;

import com.leclowndu93150.particlerain.ClientStuff;
import com.leclowndu93150.particlerain.ParticleRainClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public class SnowParticle extends WeatherParticle {

    float rotationAmount;

    protected SnowParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.quadSize = ParticleRainClient.config.snow.size;
        this.gravity = ParticleRainClient.config.snow.gravity;
        this.yd = -gravity;
        //this.setSprite(Minecraft.getInstance().particleEngine.textureAtlas.getSprite(new ResourceLocation(ParticleRainClient.MOD_ID, "snow" + random.nextInt(4))));

        if (level.isThundering()) {
            this.xd = gravity * ParticleRainClient.config.snow.stormWindStrength;
        } else {
            this.xd = gravity * ParticleRainClient.config.snow.windStrength;
        }
        if (ParticleRainClient.config.yLevelWindAdjustment) {
            this.xd = this.xd * ClientStuff.yLevelWindAdjustment(y);
        }
        this.zd = this.xd;

        if (level.getRandom().nextBoolean()) {
            this.rotationAmount = 1;
        } else {
            this.rotationAmount = -1;
        }
    }

    public void tick() {
        super.tick();
        this.oRoll = this.roll;
        this.roll = this.oRoll + (level.isThundering() ? ParticleRainClient.config.snow.stormRotationAmount : ParticleRainClient.config.snow.rotationAmount) * this.rotationAmount;
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
        private final SpriteSet spriteSet;

        public DefaultFactory(SpriteSet provider) {
            this.spriteSet = provider;
        }

        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            SnowParticle particle = new SnowParticle(level, x, y, z);
            particle.setSprite(this.spriteSet.get(level.random));
            return particle;
        }
    }
}