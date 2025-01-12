package com.leclowndu93150.particlerain.particle;

import com.leclowndu93150.particlerain.ClientStuff;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class RippleParticle extends TextureSheetParticle {

    protected SpriteSet spriteProvider;

    public RippleParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd, SpriteSet spriteProvider) {
        super(world, x, y, z, xd, yd, zd);

        this.xd = 0;
        this.yd = 0;
        this.zd = 0;

        this.spriteProvider = spriteProvider;

        int scaleAgeModifier = 1 + new Random().nextInt(10);
        this.quadSize *= 1.2f + random.nextFloat() / 10f * scaleAgeModifier;
        this.lifetime = 10 + new Random().nextInt(scaleAgeModifier);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }



    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        this.setSpriteFromAge(spriteProvider);

        Vec3 vec3d = camera.getPosition();
        float f = (float) (Mth.lerp(tickDelta, this.xo, this.x) - vec3d.x());
        float g = (float) (Mth.lerp(tickDelta, this.yo, this.y) - vec3d.y());
        float h = (float) (Mth.lerp(tickDelta, this.zo, this.z) - vec3d.z());
        Quaternion quaternion2;
        if (this.roll == 0.0F) {
            quaternion2 = camera.rotation();
        } else {
            quaternion2 = new Quaternion(camera.rotation());
            float i = Mth.lerp(tickDelta, this.oRoll, this.roll);
            quaternion2.mul(Vector3f.ZP.rotationDegrees(i));
        }

        Vector3f Vector3f = new Vector3f(-1.0F, -1.0F, 0.0F);
        Vector3f.transform(quaternion2);
        Vector3f[] Vector3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float j = this.getQuadSize(tickDelta);

        for (int k = 0; k < 4; ++k) {
            Vector3f Vector3f2 = Vector3fs[k];
            Vector3f2.transform(new Quaternion(90f, 0f, 0f, true));
            Vector3f2.mul(j);
            Vector3f2.add(f, g, h);
        }

        float minU = this.getU0();
        float maxU = this.getU1();
        float minV = this.getV0();
        float maxV = this.getV1();
        int l = this.getLightColor(tickDelta);

        vertexConsumer.vertex(Vector3fs[0].x(), Vector3fs[0].y(), Vector3fs[0].z()).uv(maxU, maxV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
        vertexConsumer.vertex(Vector3fs[1].x(), Vector3fs[1].y(), Vector3fs[1].z()).uv(maxU, minV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
        vertexConsumer.vertex(Vector3fs[2].x(), Vector3fs[2].y(), Vector3fs[2].z()).uv(minU, minV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
        vertexConsumer.vertex(Vector3fs[3].x(), Vector3fs[3].y(), Vector3fs[3].z()).uv(minU, maxV).color(rCol, gCol, bCol, alpha).uv2(l).endVertex();
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
            RippleParticle rippleParticle = new RippleParticle(level, x, y, z, velocityX, velocityY, velocityZ, this.spriteSet);
            rippleParticle.setSprite(this.spriteSet.get(level.random));
            if(ClientStuff.config.biomeTint) ClientStuff.applyWaterTint(rippleParticle, level, new BlockPos(x, y, z));
            return rippleParticle;
        }
    }
}