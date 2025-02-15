package com.leclowndu93150.particlerain.mixin.fix;

import com.leclowndu93150.particlerain.ModConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.gui.ModListScreen;
import net.minecraftforge.client.gui.widget.ModListWidget;
import net.minecraftforge.forgespi.language.IModInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModListScreen.class, remap = false)
public class ModListScreenMixin {
    @Shadow
    private ModListWidget.ModEntry selected;

    @Shadow
    private Button configButton;

    @Inject(method = "updateCache", at = @At("HEAD"), cancellable = true)
    private void onUpdateCache(CallbackInfo ci) {
        if (selected == null) {
            configButton.active = false;
            return;
        }

        IModInfo selectedMod = selected.getInfo();
        if ("particlerain".equals(selectedMod.getModId())) {
            configButton.active = true;
            ci.cancel();
        }
    }

    @Inject(method = "displayModConfig", at = @At("HEAD"), cancellable = true)
    private void onDisplayModConfig(CallbackInfo ci) {
        if (selected != null && "particlerain".equals(selected.getInfo().getModId())) {
            Screen configScreen = AutoConfig.getConfigScreen(ModConfig.class, (ModListScreen)(Object)this).get();
            Minecraft.getInstance().setScreen(configScreen);
            ci.cancel();
        }
    }
}
