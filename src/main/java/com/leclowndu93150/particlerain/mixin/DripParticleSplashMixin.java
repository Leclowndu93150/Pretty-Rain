package com.leclowndu93150.particlerain.mixin;

import com.leclowndu93150.particlerain.ClientStuff;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DripParticle.FallAndLandParticle.class)
public class DripParticleSplashMixin extends DripParticle {
    @Shadow @Final protected ParticleOptions landParticle;

    private DripParticleSplashMixin(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z, Fluids.WATER);
    }

    @Inject(method = "postMoveUpdate", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/ClientLevel;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
    private void beforeSplash(CallbackInfo ci) {
        if (ClientStuff.config.biomeTint && this.onGround) {
            BlockPos pos = new BlockPos(this.x, this.y, this.z);
            ClientStuff.applyWaterTint(this, this.level, pos);
        }
    }
}
