package com.ignfab.minalac.generator.generation.minimaps;

import java.awt.Color;

import com.ignfab.minalac.generator.tasks.ApplyShadingMinimapTask;
import com.ignfab.minalac.generator.tasks.PopulateMinimapTask;
import com.ignfab.minalac.generator.tasks.SaveMinimapTask;
import com.ignfab.minalac.generator.utils.world2d.Bounded2d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.Voxel;
import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * Builds a scaled 2D representation of a {@link VoxelWorld} for use as an in-game minimap.
 *
 * <p>
 * Although the stored data is 2D, this {@code Minimap} is designed to support 2.5D effects
 * through height-based tasks (e.g. shading, lighting).
 *
 * <p>
 * This class is purely a data structure. Population, processing, and rendering
 * are performed by dedicated external tasks.
 *
 * @see PopulateMinimapTask
 * @see ApplyShadingMinimapTask
 * @see SaveMinimapTask
 */
public class Minimap {

    // Cells composing the minimap, stored in row-major order.
    private final MinimapCell[] cells;

    private final Bounded2d bbox;
    private final double samplingRate;

    // Size in pixels of the minimap.
    private final int width;
    private final int height;

    /**
     * Creates a {@code Minimap} covering the specified world bounding box.
     *
     * @param bbox the world bounds
     * @param size the maximum size of the longest side of the minimap in pixels
     */
    public Minimap(Bounded2d bbox, int size) {
        this.bbox = bbox;

        samplingRate = Math.min((double) size / bbox.bbox().sizeX(), (double) size / bbox.bbox().sizeY());

        width = (int) Math.ceil(samplingRate * bbox.bbox().sizeX());
        height = (int) Math.ceil(samplingRate * bbox.bbox().sizeY());

        cells = new MinimapCell[width * height];
    }

    // Returns the index of a cell at the specified projected coordinates
    private int index(int x, int y) {
        return y * width + x;
    }

    /**
     * Projects a {@link Voxel} onto the {@code Minimap} using a box blur algorithm.
     *
     * <p>
     * The {@link VoxelWorld} and the {@code Minimap} usually do not share the same
     * size, so a voxel rarely maps cleanly to a single {@link MinimapCell}.
     * A direct one-to-one mapping would therefore produce visual artifacts on the
     * resulting {@code Minimap}.
     *
     * <p>
     * To avoid this, each {@link Voxel} contribution is distributed
     * across all overlapping {@link MinimapCell}s. The weight assigned to each
     * cell corresponds to the area of intersection between the voxel's projected
     * footprint and the cell itself.
     *
     * @param color the voxel color
     * @param coords the voxel world coordinates
     */
    public void add(Color color, WorldCoords3d coords) {
        // Project world coordinates into minimap coordinates.
        double x = (coords.x() - bbox.bbox().minX()) * samplingRate;
        double y = (coords.y() - bbox.bbox().minY()) * samplingRate;
        double xEnd = x + samplingRate;
        double yEnd = y + samplingRate;

        // Compute the range of cells that may be covered by the sample.
        int xMin = Math.max(0, (int) Math.floor(x));
        int yMin = Math.max(0, (int) Math.floor(y));
        int xMax = Math.min(width - 1, (int) Math.floor(xEnd));
        int yMax = Math.min(height - 1, (int) Math.floor(yEnd));

        for (int py = yMin; py <= yMax; py++) {
            // Compute how much the sample overlaps this cell vertically.
            double overlapY = Math.max(0.0,
                Math.min(yEnd, py + 1.0) - Math.max(y, py));

            for (int px = xMin; px <= xMax; px++) {
                // Compute how much the sample overlaps this cell horizontally.
                double overlapX = Math.max(0.0,
                        Math.min(xEnd, px + 1.0) - Math.max(x, px));

                // The contribution of the sample is proportional
                // to the overlapping area with the current cell.
                double weight = overlapX * overlapY;
                if (weight > 0.0) {
                    int index = index(px, py);
                    if (cells[index] == null)
                        cells[index] = new MinimapCell();
                    cells[index].add(
                        color.getRed(),
                        color.getGreen(),
                        color.getBlue(),
                        color.getAlpha(),
                        coords.z(),
                        weight
                    );
                }
            }
        }
    }

    /**
     * Returns the {@link MinimapCell} at the specified {@code Minimap} coordinates.
     *
     * @param x the cell x-coordinate
     * @param y the cell y-coordinate
     * @return the {@link MinimapCell}, or {@code null} if the coordinates are outside the {@code Minimap}
     * or if no voxel has been projected onto this cell.
     */
    public MinimapCell get(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height)
            return null;
        return cells[index(x, y)];
    }

    /**
     * {@return the width of the {@code Minimap}}
     */
    public int getWidth() {
        return width;
    }

    /**
     * {@return the height of the {@code Minimap}}
     */
    public int getHeight() {
        return height;
    }
}
