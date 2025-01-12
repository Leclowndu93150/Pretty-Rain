package com.leclowndu93150.particlerain.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(WaterFluid.class)
public abstract class WaterFluidMixin {
    @Inject(method = "animateTick", at = @At("TAIL"))
    protected void animateTick(Level level, BlockPos pos, FluidState fluidState, Random random, CallbackInfo ci) {
        if (!(level instanceof ClientLevel)) return;

        Vec3 vec3d = fluidState.getFlow(level, pos);
        int amount = random.nextInt(10);

        if (!fluidState.isSource() && shouldSplashie((ClientLevel) level, pos, fluidState)) {
            for (int i = 0; i <= amount; i++) {
                level.addParticle(ParticleTypes.SPLASH,
                        pos.getX() + .5 + random.nextGaussian() / 2f,
                        pos.getY() + 1 + random.nextFloat(),
                        pos.getZ() + .5 + random.nextGaussian() / 2f,
                        vec3d.x * random.nextFloat(),
                        random.nextFloat() / 10f,
                        vec3d.z * random.nextFloat());
            }
        }
    }

    private static boolean shouldSplashie(ClientLevel level, BlockPos pos, FluidState fluidState) {
        if (!fluidState.isSource()) {
            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            for (Direction direction : Direction.values()) {
                mutable.setWithOffset(pos, direction);
                if (direction.getAxis() != Direction.Axis.Y && level.getBlockState(mutable).isAir()) {
                    return true;
                }
            }
        }
        return false;
    }
}
