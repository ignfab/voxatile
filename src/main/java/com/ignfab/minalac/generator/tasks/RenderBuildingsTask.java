package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ShapesVoxelizable2d;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineVoxel2d;
import com.ignfab.minalac.generator.voxelization.shape2d.ShapesVoxelizer2d;

/**
 * A {@link TileTask} rendering a {@link ModelSelection} as buildings.
 *
 * The generated buildings include a custom height, walls,
 * windows and a roof.
 */
public class RenderBuildingsTask extends ModelTask<ShapesVoxelizable2d> {
    private final Placeable roof;
    private final Placeable wall;
    private final Placeable window;

    /**
     * Creates a new {@code RenderBuildingsTask}.
     *
     * @param selection building models selection
     * @param roof {@code Placeable} for roofs
     * @param wall {@code Placeable} for walls
     * @param window {@code Placeable} for windows
     */
    public RenderBuildingsTask(
        ModelSelection selection,
        Placeable roof,
        Placeable wall,
        Placeable window
    ) {
        super(ShapesVoxelizable2d.class, selection);
        this.roof = roof;
        this.wall = wall;
        this.window = window;
    }

    @Override
    protected void run(ShapesVoxelizable2d model, GenerationTile tile) {
        // Metadata required to render a model as buildings
        // Obtain them by executing the 'computeHeightmapStats' task
        //
        // The metadata is hardcoded because this renderer will be deleted
        // All metadata must be in voxel units
        Integer zMinFoundation = model.getMetadata("minimum-ground-altitude");
        Integer zMaxFoundation = model.getMetadata("ground-floor-altitude");
        if (zMinFoundation == null || zMaxFoundation == null)
            return;

        if (!(model.getMetadata("height") instanceof Integer buildingHeight) || buildingHeight <= 0)
            return;

        /*
        The height of a building is calculated according to the largest side of its wall
        in order not to overestimate the height of the building it is necessary
        to subtract the height of the foundation from the height of the building

        The height of a building cannot be negative
        */
        buildingHeight = Math.max(0, buildingHeight - (zMaxFoundation - zMinFoundation));

        ShapesVoxelizer2d voxelizer = model.voxelize2d(tile.limits().to2d());
        // Place the facade and enhance it with windows
        for (LineVoxel2d voxel : voxelizer.borders())
            for (int z = zMaxFoundation + 1; z <= zMaxFoundation + buildingHeight; z++) // zMax + 1 to place walls/windows above the foundation
                ((z % 4 == 0) ? window : wall).place(tile.voxels(), voxel.coords().x(), voxel.coords().y(), z);

        // Place the roof of the building
        for (Positioned2d voxel : voxelizer)
            roof.place(tile.voxels(), voxel.coords().x(), voxel.coords().y(), zMaxFoundation + buildingHeight);
    }
}
