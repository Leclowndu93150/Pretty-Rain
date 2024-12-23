package com.leclowndu93150.particlerain.rendering;

import org.joml.Vector3f;

public record ParticleRenderData(
        Vector3f position,
        float size,
        float u0, float v0,
        float u1, float v1,
        int light,
        float r, float g, float b, float a
) {}