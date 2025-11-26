package com.ignfab.minalac.generator.tasks;

import java.awt.Color;
import java.util.Map;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.minimaps.Minimap;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.PlacedVoxel;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * A {@link TileTask} that populates a {@link Minimap}.
 *
* <p>
 * For each (x, y) world coordinate, {@link PlacedVoxel}s are processed from top to bottom,
 * get the corresponding color of the {@link PlacedVoxel}, and composite them using source-over
 * alpha compositing until a fully opaque color is reached.
 */
public class PopulateMinimapTask implements TileTask {

    private final Minimap minimap;
    private final Map<String, Color> colors;

    /**
     * Creates a new {@code PopulateMinimapTask}.
     *
     * @param minimap minimap to populate
     * @param colors mapping of voxel identifiers to their display colors
     */
    public PopulateMinimapTask(Minimap minimap, Map<String, Color> colors) {
        this.minimap = minimap;
        this.colors = colors;
    }

    /**
     * Performs source-over alpha compositing between two colors.
     *
     * <p>
     * This method blends semi-transparent voxel layers (e.g., water, glass)
     * into a single 2D pixel color. By accumulating these layers, it enables the
     * representation of depth (such as water transparency or varying terrain details).
     *
     * @param previousColor destination (background)
     * @param currentColor source (foreground)
     * @return blended color using alpha compositing
     */
    private Color blend(Color previousColor, Color currentColor) {
        double cAlpha = currentColor.getAlpha() / 255d;
        double pAlpha = previousColor.getAlpha() / 255d;

        // Alpha compositing formula
        double red = pAlpha * (previousColor.getRed() / 255d)  + (1 - pAlpha) * cAlpha * (currentColor.getRed() / 255d);
        double green = pAlpha * (previousColor.getGreen() / 255d) + (1 - pAlpha) * cAlpha * (currentColor.getGreen() / 255d);
        double blue = pAlpha * (previousColor.getBlue() / 255d) + (1 - pAlpha) * cAlpha * (currentColor.getBlue() / 255d);
        double alpha = pAlpha + (1 - pAlpha) * cAlpha;

        return new Color(
            (int) (red * 255),
            (int) (green * 255),
            (int) (blue * 255),
            (int) (alpha * 255)
        );
    }

    @Override
    public void run(GenerationTile tile) {
        VoxelTile voxels = tile.voxels();
        for (WorldCoords2d pos : tile.limits().intersection(voxels.limits()).to2d()) {
            WorldCoords3d coords = null;
            Color computedColor = new Color(0, 0, 0, 0);
            for (PlacedVoxel voxel : voxels.voxels(pos.x(), pos.y())) {
                Color voxelColor = colors.get(voxel.voxel().getTypeIdentifier());
                if (voxelColor == null || voxelColor.getAlpha() == 0) continue;

                coords = voxel.coords();
                computedColor = blend(computedColor, voxelColor);

                // Stop once the accumulated color is fully opaque.
                if (computedColor.getAlpha() == 255) break;
            }

            if (coords != null)
                minimap.add(computedColor, coords);
        }
    }
}
