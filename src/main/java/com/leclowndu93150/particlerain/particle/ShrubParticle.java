package com.leclowndu93150.particlerain.particle;

import com.leclowndu93150.particlerain.ParticleRainClient;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class ShrubParticle extends WeatherParticle {

    protected ShrubParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.quadSize = 0.5F;
        this.lifetime = 80;
        this.gravity = ParticleRainClient.config.shrub.gravity;
        this.xd = ParticleRainClient.config.sand.windStrength;
        this.zd = ParticleRainClient.config.sand.windStrength;
        if (ParticleRainClient.config.sand.spawnOnGround) {
            this.yd = 0.1F;
        }
        this.hasPhysics = true;

        BlockState blockState = level.getBlockState(level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(x, y, z)));
        if (blockState.is(Blocks.DEAD_BUSH) || blockState.is(BlockTags.SMALL_FLOWERS) || blockState.is(BlockTags.TALL_FLOWERS)) {
            ItemStack itemStack = blockState.getBlock().asItem().getDefaultInstance();
            this.setSprite(Minecraft.getInstance().getItemRenderer().getModel(itemStack, level, null, 0).getParticleIcon());

            int colorInt = BiomeColors.getAverageFoliageColor(level, new BlockPos(x, y, z));
            Color color = new Color(colorInt);
            this.setColor(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F);
        } else {
            if (level.random.nextFloat() < 0.9) {
                this.remove();
            }
            this.setSprite(Minecraft.getInstance().getItemRenderer().getModel(new ItemStack(Items.DEAD_BUSH), level, null, 0).getParticleIcon());
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.removeIfObstructed();
        if (!this.level.getFluidState(this.pos).isEmpty()) {
            this.shouldFadeOut = true;
            this.gravity = 0;
        } else {
            this.xd = 0.2;
            this.zd = 0.2;
        }
        this.oRoll = this.roll;
        this.roll = this.roll + ParticleRainClient.config.shrub.rotationAmount;
        if (this.onGround) {
            this.yd = ParticleRainClient.config.shrub.bounciness;
        }
    }

    @Override
    public void fadeIn() {
        if (age < 10) {
            this.alpha = (age * 1.0f) / 10;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickPercentage) {
        Vec3 camPos = camera.getPosition();
        float x = (float) (Mth.lerp(tickPercentage, this.xo, this.x) - camPos.x());
        float y = (float) (Mth.lerp(tickPercentage, this.yo, this.y) - camPos.y());
        float z = (float) (Mth.lerp(tickPercentage, this.zo, this.z) - camPos.z());

        float angle = (float) Math.atan2(this.xd, this.zd);
        Quaternion quaternion = Vector3f.YP.rotation(angle);

        Quaternion quat1 = new Quaternion(0, 0, 1, 0);
        Quaternion quat2 = Vector3f.YP.rotationDegrees(90);

        quat1.mul(quaternion);
        quat1.mul(Vector3f.XP.rotation(Mth.lerp(tickPercentage, this.oRoll, this.roll)));

        quat2.mul(quaternion);
        quat2.mul(Vector3f.ZP.rotation(Mth.lerp(tickPercentage, this.oRoll, this.roll)));

        quat1 = this.flipItTurnwaysIfBackfaced(quat1, new Vector3f(x, y, z));
        quat2 = this.flipItTurnwaysIfBackfaced(quat2, new Vector3f(x, y, z));

        this.renderRotatedQuad(vertexConsumer, quat1, x, y, z, tickPercentage);
        this.renderRotatedQuad(vertexConsumer, quat2, x, y, z, tickPercentage);
    }

    @OnlyIn(Dist.CLIENT)
    public static class DefaultFactory implements ParticleProvider<SimpleParticleType> {

        public DefaultFactory(SpriteSet provider) {
        }

        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new ShrubParticle(level, x, y, z);
        }
    }
}