package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ShapesVoxelizable2d;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.shape2d.ShapesVoxelizer2d;

/**
 * A {@link TileTask} copying values of a heightmap to another at all coordinates within the model's shape.
 */
public class CopyHeightmapTask extends ModelTask<ShapesVoxelizable2d> {
    private final ReadableHeightmap from;
    private final Heightmap to;

    /**
     * Creates a new {@code CopyHeightmapTask}.
     *
     * @param selection the model selection containing the wanted models to use
     * @param from the giving heightmap
     * @param to the receiving heightmap
     */
    public CopyHeightmapTask(ModelSelection selection, ReadableHeightmap from, Heightmap to) {
        super(ShapesVoxelizable2d.class, selection);
        this.from = from;
        this.to = to;
    }

    @Override
    protected void run(ShapesVoxelizable2d model, WorldBBox3d bbox) {
        ShapesVoxelizer2d voxelizer = model.voxelize2d(bbox.to2d().intersection(from.bbox()).intersection(to.bbox()));
        Heightmap buffered = to.copy();
        for (Positioned2d voxel : voxelizer)
            buffered.set(voxel.coords(), from.get(voxel.coords()));
        to.swap(buffered);
    }
}
