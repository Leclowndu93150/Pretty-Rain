package com.leclowndu93150.particlerain.mixin;

import com.leclowndu93150.particlerain.ClientStuff;
import com.leclowndu93150.particlerain.ParticleRainClient;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DripParticle.WaterHangProvider.class)
public abstract class DripParticleWaterHangProviderMixin {
    @Inject(method = "createParticle", at = @At("RETURN"))
    private void onCreateParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, CallbackInfoReturnable<Particle> cir) {
        if (ParticleRainClient.config.biomeTint) {
            ClientStuff.applyWaterTint((TextureSheetParticle)cir.getReturnValue(), level, new BlockPos(x, y, z));
        }
    }
}
