package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Voxelizable2d;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.Voxelizer2d;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * A {@link TileTask} leveling floor under models.
 * The final floor height is equals to the greatest floor height over all the
 * model's surface.
 */
public class LevelGroundTask extends ModelTask<Voxelizable2d> {
    /**
     * Heightmap of the ground, will be updated according to leveling.
     */
    private final Heightmap heightmap;

    /**
     * {@code Placeable} used to fill the space beneath the building geometry,
     * ensuring it connects to the ground and doesn't appear to float.
     */
    private final Placeable filling;

    /**
     * Creates a new {@code LevelGroundTask}.
     *
     * @param selection models selection to be leveled
     * @param heightmap heightmap of the ground
     * @param filling {@code Placeable} for fill leveled areas with
     */
    public LevelGroundTask(ModelSelection selection, Heightmap heightmap, Placeable filling) {
        super(Voxelizable2d.class, selection);
        this.heightmap = heightmap;
        this.filling = filling;
    }

    @Override
    protected void run(Voxelizable2d model, VoxelTile tile) {
        Voxelizer2d voxelizer = model.voxelize2d(tile.limits().to2d());

        // The highest coordinate of the model.
        int zMax = Integer.MIN_VALUE;
        for (Positioned2d voxel : voxelizer)
            zMax = Math.max(zMax, heightmap.get(voxel.coords()));

        for (Positioned2d voxel : voxelizer) {
            WorldCoords2d c = voxel.coords();
            // Flatten the floor for the model by placing filling voxels up to zMax.
            for (int z = heightmap.get(c); z <= zMax; z++)
                filling.place(tile, c.x(), c.y(), z);

            // Update the heightmap to reflect the leveling
            // and ensure the model is positioned above the new floor level.
            heightmap.set(c, zMax);
        }
    }
}
