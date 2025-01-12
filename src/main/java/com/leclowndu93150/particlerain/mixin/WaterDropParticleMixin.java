package com.leclowndu93150.particlerain.mixin;

import com.leclowndu93150.particlerain.ClientStuff;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WaterDropParticle.class)
public abstract class WaterDropParticleMixin extends TextureSheetParticle {

    private boolean tintApplied = false;

    private WaterDropParticleMixin(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (!tintApplied && ClientStuff.config.biomeTint) {
            ClientStuff.applyWaterTint(this, this.level, new BlockPos((int)this.x, (int)this.y, (int)this.z));
            tintApplied = true;
        }
    }
}