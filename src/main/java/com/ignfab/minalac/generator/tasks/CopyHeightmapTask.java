package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.generation.heightmaps.WritableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.WritableHeightmapSpec;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ShapesVoxelizable2d;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.voxelization.shape2d.ShapesVoxelizer2d;

/**
 * A {@link TileTask} copying values of a heightmap to another at all coordinates within the model's shape.
 */
public class CopyHeightmapTask extends ModelTask<ShapesVoxelizable2d> {
    private final ReadableHeightmapSpec fromSpec;
    private final WritableHeightmapSpec toSpec;

    /**
     * Creates a new {@code CopyHeightmapTask}.
     *
     * @param selection the model selection containing the wanted models to use
     * @param fromSpec source readable heightmap spec
     * @param toSpec target writable heightmap spec
     */
    public CopyHeightmapTask(ModelSelection selection, ReadableHeightmapSpec fromSpec, WritableHeightmapSpec toSpec) {
        super(ShapesVoxelizable2d.class, selection);
        this.fromSpec = fromSpec;
        this.toSpec = toSpec;
    }

    @Override
    protected void run(ShapesVoxelizable2d model, GenerationTile tile) {
        ReadableHeightmap from = tile.heightmaps().get(fromSpec);
        WritableHeightmap to = tile.heightmaps().get(toSpec);

        ShapesVoxelizer2d voxelizer = model.voxelize2d(tile.limits().to2d().intersection(from.bbox()).intersection(to.bbox()));
        WritableHeightmap buffered = to.copy();

        for (Positioned2d voxel : voxelizer)
            buffered.set(voxel.coords(), from.get(voxel.coords()));

        to.copyValues(buffered);
    }
}
