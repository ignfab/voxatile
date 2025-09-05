package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ShapesVoxelizable2d;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineVoxel2d;
import com.ignfab.minalac.generator.voxelization.shape2d.ShapesVoxelizer2d;

/**
 * A {@link TileTask} rendering a {@link ModelSelection} as buildings.
 *
 * The generated buildings have a custom height, floors and windows.
 */
public class RenderBuildingsTask extends ModelTask<ShapesVoxelizable2d> {
    /**
     * Heightmap of the ground.
     */
    private final ReadableHeightmapSpec heightmapSpec;

    /**
     * {@code Placeable} representing the roof of the building.
     */
    private final Placeable roof;

    /**
     * {@code Placeable} representing the walls of the building.
     */
    private final Placeable wall;

    /**
     * {@code Placeable} representing the windows of the building.
     */
    private final Placeable window;

    /**
     * Creates a new {@code RenderBuildingsTask}.
     *
     * @param selection building models selection
     * @param heightmapSpec heightmap spec for the ground (on which features will be placed)
     * @param roof {@code Placeable} for roofs
     * @param wall {@code Placeable} for walls
     * @param window {@code Placeable} for windows
     */
    public RenderBuildingsTask(
        ModelSelection selection,
        ReadableHeightmapSpec heightmapSpec,
        Placeable roof,
        Placeable wall,
        Placeable window
    ) {
        super(ShapesVoxelizable2d.class, selection);
        this.heightmapSpec = heightmapSpec;
        this.roof = roof;
        this.wall = wall;
        this.window = window;
    }

    @Override
    protected void run(ShapesVoxelizable2d model, GenerationTile tile) {
        // TODO: Implement a post-processor for value rounding to rollback this change
        int height = (int) Math.round(
            /* Casting to Number is needed to avoid a cast exception in RenderBuildingsTask */
            ((Number) model.getMetadata("height")).doubleValue()
        );

        ReadableHeightmap heightmap = tile.heightmaps().get(heightmapSpec);
        ShapesVoxelizer2d voxelizer = model.voxelize2d(tile.limits().to2d());
        // Build walls and place windows of the building
        for (LineVoxel2d voxel : voxelizer.borders()) {
            WorldCoords2d c = voxel.coords();
            int zMin = heightmap.get(c);

            for (int z = 1; z < height; z++)
                ((z % 4 == 0) ? window : wall).place(tile, c.x(), c.y(), zMin + z);
            // Build the border of the roof of the building
            roof.place(tile, c.x(), c.y(), zMin + height);
        }

        // Build the floors of the building
        for (Positioned2d voxel : voxelizer.inside()) {
            WorldCoords2d c = voxel.coords();
            int zMin = heightmap.get(c);

            for (int z = 2; z < height; z = z + 4)
                roof.place(tile, c.x(), c.y(), zMin + z);
            // Build the inside of the roof of the building
            roof.place(tile, c.x(), c.y(), zMin + height);
        }
    }
}
