package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.generation.heightmaps.WritableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.WritableHeightmapSpec;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Shape2dConvertibleModel;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.Shape2dVoxelizer;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.SurfaceVoxelizer2d;

/**
 * A {@link TileTask} copying values of a heightmap to another at all coordinates within the model's shape.
 */
public class CopyHeightmapTask extends ModelTask<Shape2dConvertibleModel> {
    private final ReadableHeightmapSpec fromSpec;
    private final WritableHeightmapSpec toSpec;
    private final Shape2dVoxelizer voxelizer;
    /**
     * Creates a new {@code CopyHeightmapTask}.
     *
     * @param selection the model selection containing the wanted models to use
     * @param fromSpec source readable heightmap spec
     * @param toSpec target writable heightmap spec
     */
    public CopyHeightmapTask(ModelSelection selection, ReadableHeightmapSpec fromSpec, WritableHeightmapSpec toSpec) {
        super(Shape2dConvertibleModel.class, selection);
        this.fromSpec = fromSpec;
        this.toSpec = toSpec;
        voxelizer = new SurfaceVoxelizer2d();
    }

    @Override
    protected void run(Shape2dConvertibleModel model, GenerationTile tile) {
        ReadableHeightmap from = tile.heightmaps().get(fromSpec);
        WritableHeightmap to = tile.heightmaps().get(toSpec);

        WritableHeightmap buffered = to.copy();
        for (Positioned2d voxel : buffered.bbox().filterInside(voxelizer.voxelize(model)))
            buffered.set(voxel.coords(), from.get(voxel.coords()));

        to.copyValues(buffered);
    }
}
