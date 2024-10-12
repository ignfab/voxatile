package com.ignfab.minalac.generator.outputs.minetest.mod;

import com.ignfab.minalac.generator.outputs.minetest.MTVoxelWorld;
import com.ignfab.minalac.generator.world.MapWriteException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ColoredVoxelsLuaMod implements LuaMod {
    private final Set<Integer> colors = new HashSet<>();

    public int registerColor(int color, int downsampling) {
        int downsampled = downsampling > 0 ? downsample(color, downsampling) : color;
        colors.add(downsampled);
        return downsampled;
    }

    public int downsample(int color, int downsampling) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return ((r - r % downsampling) << 16) | ((g - g % downsampling) << 8) | (b - b % downsampling);
    }

    public String nodeId(int color) {
        return "colors:rgb_%06X".formatted(color & 0xFFFFFF);
    }

    @Override
    public void save(File directory) throws MapWriteException {
        if (colors.isEmpty())
            return;

        StringBuilder code = new StringBuilder();
        for (int color : colors) {
            code.append("""
                minetest.register_node("%s", {
                  tiles = {"colors_empty.png^[colorize:#%06X"}
                })
                """.formatted(nodeId(color), color & 0xFFFFFF));
        }
        MTVoxelWorld.createFile(new File(directory, "init.lua"), code.toString());
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, 0xFFFFFFFF);
        try {
            File textures = new File(directory, "textures");
            textures.mkdir();
            ImageIO.write(image, "png", new File(textures, "colors_empty.png"));
        } catch (IOException e) {
            throw new MapWriteException(e);
        }
    }
}
