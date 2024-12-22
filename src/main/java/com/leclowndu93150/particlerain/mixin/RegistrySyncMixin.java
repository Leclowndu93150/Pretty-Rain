package com.leclowndu93150.particlerain.mixin;

import com.leclowndu93150.particlerain.ParticleRainConfig;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.configuration.SyncRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(SyncRegistries.class)
public class RegistrySyncMixin {
    @Inject(method = "run", at = @At("HEAD"), cancellable = true)
    private void onRun(Consumer<CustomPacketPayload> sender, CallbackInfo ci) {
        if (!ParticleRainConfig.syncRegistry) {
            ci.cancel();
        }
    }
}