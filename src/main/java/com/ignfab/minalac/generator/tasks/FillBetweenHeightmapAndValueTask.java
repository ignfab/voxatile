package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Shape2dConvertibleModel;
import com.ignfab.minalac.generator.models.values.ModelValue;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.SurfaceVoxelizer2d;

/**
 * A {@link TileTask} which, for each model in {@link ModelSelection},
 * fills with {@link Placeable} the gap between a heightmap and an altitude (given by a model value)
 * within the model's boundaries.
 */
public class FillBetweenHeightmapAndValueTask extends ModelTask<Shape2dConvertibleModel> {
    private final ReadableHeightmapSpec heightmapSpec;
    private final ModelValue altitudeValue;
    private final Placeable placeAbove;
    private final Placeable placeBelow;

    private static final SurfaceVoxelizer2d VOXELIZER = new SurfaceVoxelizer2d();

    /**
     * Creates a new {@code FillBetweenHeightmapAndValueTask}.
     *
     * @param selection {@code ModelSelection} to use
     * @param heightmapSpec {@code ReadableHeightmap} to use
     * @param altitudeValue model value to use as altitude
     * @param placeAbove {@code Placeable} used to render voxels above the altitude value
     * @param placeBelow {@code Placeable} used to render voxels below the altitude value
     */
    public FillBetweenHeightmapAndValueTask(
        ModelSelection selection,
        ReadableHeightmapSpec heightmapSpec,
        ModelValue altitudeValue,
        Placeable placeAbove,
        Placeable placeBelow
    ) {
        super(Shape2dConvertibleModel.class, selection);
        this.heightmapSpec = heightmapSpec;
        this.altitudeValue = altitudeValue;
        this.placeAbove = placeAbove;
        this.placeBelow = placeBelow;
    }

    @Override
    protected void run(Shape2dConvertibleModel model, GenerationTile tile) throws IgnorableException {
        ReadableHeightmap heightmap = tile.heightmaps().get(heightmapSpec);
        int altitude = altitudeValue.getAsInt(model).orElseThrow(() -> new IgnorableException("Missing altitude"));

        for (Positioned2d voxel : tile.clip2d(VOXELIZER.voxelize(model))) {
            WorldCoords2d c = voxel.coords();

            int height = heightmap.get(c);
            Placeable place = height <= altitude ? placeBelow : placeAbove;
            int min = Math.max(Math.min(height, altitude), tile.limits().minZ());
            int max = Math.min(Math.max(height, altitude), tile.limits().maxZ());
            for (int z = min; z <= max; z++)
                place.place(tile.voxels(), c.x(), c.y(), z);
        }
    }
}
