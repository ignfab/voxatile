package com.ignfab.minalac.generator.outputs.minetest;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.world.MapWriteException;
import com.ignfab.minalac.generator.world.PlacedVoxel;

public class MTMapper {

    private Map<String, Integer> colors = new HashMap<>();

    public MTMapper(File colorFile) {

        Pattern pattern = Pattern.compile("([a-zA-Z0-9:_-]+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)");

        try (BufferedReader reader = new BufferedReader(new FileReader(colorFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {

                    colors.put(matcher.group(1),
                        (new Color(
                            Integer.parseInt(matcher.group(2)),
                            Integer.parseInt(matcher.group(3)),
                            Integer.parseInt(matcher.group(4))
                        )).getRGB()
                    );
                }
            }
        } catch (IOException e) {
            // Display warning / create error / tax the rich / burn the whole world
        }
    }

    public void saveMinimap(MTVoxelTile tile) throws MapWriteException {
        BufferedImage image = new BufferedImage(tile.limits().sizeX(), tile.limits().sizeY(), BufferedImage.TYPE_INT_RGB);

        for (WorldCoords2d pos : tile.limits().to2d()) {
            for (PlacedVoxel pv : tile.voxels(pos.x(), pos.y())) {
                if (pv.voxel() instanceof MTVoxel voxel) {
                    if (voxel.type != "air") {
                        Integer color = colors.get(voxel.type);
                        if (color != null)
                            image.setRGB(pos.x() - tile.limits().minX(), pos.y() - tile.limits().minY(), color);
                        break;
                    }
                }
            }
        }

        try {
            javax.imageio.ImageIO.write(image, "png", new File("tilemap-" + tile.limits().minX() + "-" + tile.limits().maxX() + "," + tile.limits().minY() + "-" + tile.limits().maxY() + ".png"));
        } catch (IOException e) {
            throw new MapWriteException(e);
        }

//Pass the Graphics to JPanel.paintComponent()
    }

}
