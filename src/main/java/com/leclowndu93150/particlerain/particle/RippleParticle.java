package com.leclowndu93150.particlerain.particle;

import com.leclowndu93150.particlerain.ClientStuff;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RippleParticle extends WeatherParticle {

    protected SpriteSet spriteSet;

    private RippleParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        super(level, x, y, z);
        //this.setSprite(Minecraft.getInstance().particleEngine.textureAtlas.getSprite(new ResourceLocation(ParticleRainClient.MOD_ID, "ripple_0")));
        this.quadSize = 0.1f;
        this.alpha = 0.1F;
        this.x = Math.round(this.x / (1F / 16F)) * (1F / 16F);
        this.z = Math.round(this.z / (1F / 16F)) * (1F / 16F);
        this.spriteSet = spriteSet;
    }

    @Override
    public void tick() {
        super.tick();
        this.alpha = Mth.lerp(this.age / 9F, 0.3F, 0F);
        if (this.age > 8) this.remove();
        this.setSprite(this.spriteSet.get(level.random));
    }

    @Override
    public void fadeIn() {
        //dont
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float f) {
        Vec3 camPos = camera.getPosition();
        float x = (float) (Mth.lerp(f, this.xo, this.x) - camPos.x());
        float y = (float) (Mth.lerp(f, this.yo, this.y) - camPos.y());
        float z = (float) (Mth.lerp(f, this.zo, this.z) - camPos.z());

        Quaternion quaternion = new Quaternion(-1.6f, 0, 0, Mth.HALF_PI);
        this.renderRotatedQuad(vertexConsumer, quaternion, x, y, z, f);
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
            RippleParticle rippleParticle = new RippleParticle(level, x, y, z, this.spriteSet);
            rippleParticle.setSprite(this.spriteSet.get(level.random));
            if(ClientStuff.config.biomeTint) ClientStuff.applyWaterTint(rippleParticle, level, new BlockPos(x, y, z));
            return rippleParticle;
        }
    }
}