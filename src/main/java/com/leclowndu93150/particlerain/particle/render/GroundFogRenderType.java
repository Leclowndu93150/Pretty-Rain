package com.leclowndu93150.particlerain.particle.render;

import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GroundFogRenderType {
    // In 1.21.5, ParticleRenderType is a record with name, renderType, and translucent fields
    // We simply create an instance of this record with the appropriate values
    public static final ParticleRenderType INSTANCE = new ParticleRenderType(
            "particlerain:ground_fog",
            RenderType.translucentParticle(TextureAtlas.LOCATION_PARTICLES),
            true
    );
}