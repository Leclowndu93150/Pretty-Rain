package com.leclowndu93150.particlerain.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;

public class TextureProcessor {
    public static void main(String[] args) {
        String outputPath = "assets/particlerain/textures/particle/";
        processTexture("rain.png", "rain", outputPath, 4);
        processTexture("snow.png", "snow", outputPath, 4);
    }

    private static void processTexture(String inputFile, String baseName, String outputPath, int frames) {
        try {
            File input = new File(inputFile);
            System.out.println("Processing: " + input.getAbsolutePath());

            BufferedImage source = ImageIO.read(input);
            int frameWidth = source.getWidth();
            int frameHeight = source.getHeight() / frames;

            Files.createDirectories(Paths.get(outputPath));

            for(int i = 0; i < frames; i++) {
                // Create ARGB image explicitly
                BufferedImage frame = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_ARGB);

                // Copy pixels manually to ensure proper format
                for(int x = 0; x < frameWidth; x++) {
                    for(int y = 0; y < frameHeight; y++) {
                        int rgb = source.getRGB(x, i * frameHeight + y);
                        frame.setRGB(x, y, rgb);
                    }
                }

                File output = new File(outputPath + baseName + i + ".png");
                // Use PNG-specific writer with no compression
                ImageIO.write(frame, "PNG", output);
                System.out.println("Created: " + output.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}