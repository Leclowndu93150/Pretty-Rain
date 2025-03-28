package com.leclowndu93150.particlerain.mixin;

import com.leclowndu93150.particlerain.ParticleRainClient;
import com.leclowndu93150.particlerain.ParticleRainConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DripParticle.class)
public abstract class DripParticleMixin {

    @Inject(method = "createWaterHangParticle", at = @At("TAIL"))
    private static void createWaterHangParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i, CallbackInfoReturnable<TextureSheetParticle> cir) {
        if (ParticleRainConfig.biomeTint) ParticleRainClient.applyWaterTint(cir.getReturnValue(), clientLevel, BlockPos.containing(d, e, f));
    }

    @Inject(method = "createWaterFallParticle", at = @At("TAIL"))
    private static void createWaterFallParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i, CallbackInfoReturnable<TextureSheetParticle> cir) {
        if (ParticleRainConfig.biomeTint) ParticleRainClient.applyWaterTint(cir.getReturnValue(), clientLevel, BlockPos.containing(d, e, f));
    }
}
