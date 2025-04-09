package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ShapesVoxelizable2d;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineVoxel2d;
import com.ignfab.minalac.generator.voxelization.shape2d.ShapesVoxelizer2d;
import com.ignfab.minalac.generator.world.VoxelWorld;

/**
 * Rendering of buildings.
 *
 * The generated buildings have a custom height, floors and windows.
 */
public class BuildingRenderer extends ModelRenderer<ShapesVoxelizable2d> {
    /**
     * Heightmap of the ground.
     */
    private final ReadableHeightmap heightmap;

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
     * Creates a new {@code BuildingRenderer}.
     *
     * @param selection building models selection
     * @param heightmap heightmap of the ground (on which features will be placed)
     * @param roof {@code Placeable} for roofs
     * @param wall {@code Placeable} for walls
     * @param window {@code Placeable} for windows
     */
    public BuildingRenderer(
        ModelSelection selection,
        ReadableHeightmap heightmap,
        Placeable roof,
        Placeable wall,
        Placeable window
    ) {
        super(ShapesVoxelizable2d.class, selection);
        this.heightmap = heightmap;
        this.roof = roof;
        this.wall = wall;
        this.window = window;
    }

    @Override
    protected void render(ShapesVoxelizable2d model, VoxelWorld world) {
        // TODO: Implement a post-processor for value rounding to rollback this change
        int height = (int) Math.round(
            /* Casting to Number is needed to avoid a cast exception in BuildingRendererTest */
            ((Number) model.getMetadata("height")).doubleValue()
        );

        ShapesVoxelizer2d voxelizer = model.voxelize2d(world.limits().to2d());
        // Build walls and place windows of the building
        for (LineVoxel2d voxel : voxelizer.borders()) {
            WorldCoords2d c = voxel.coords();
            int zMin = heightmap.get(c);

            for (int z = 1; z < height; z++)
                ((z % 4 == 0) ? window : wall).place(world, c.x(), c.y(), zMin + z);
            // Build the border of the roof of the building
            roof.place(world, c.x(), c.y(), zMin + height);
        }

        // Build the floors of the building
        for (Positioned2d voxel : voxelizer.inside()) {
            WorldCoords2d c = voxel.coords();
            int zMin = heightmap.get(c);

            for (int z = 2; z < height; z = z + 4)
                roof.place(world, c.x(), c.y(), zMin + z);
            // Build the inside of the roof of the building
            roof.place(world, c.x(), c.y(), zMin + height);
        }
    }
}
