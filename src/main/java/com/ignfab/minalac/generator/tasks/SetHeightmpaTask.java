package com.ignfab.minalac.generator.tasks;

import java.util.Optional;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.WritableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.WritableHeightmapSpec;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Shape2dConvertibleModel;
import com.ignfab.minalac.generator.models.values.ModelValue;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.SurfaceVoxelizer2d;

/**
 * A {@link TileTask} setting a value at all coordinates within the model's shape.
 */
public class SetHeightmpaTask extends ModelTask<Shape2dConvertibleModel> {
    private final ModelValue value;
    private final WritableHeightmapSpec toSpec;
    private final SurfaceVoxelizer2d voxelizer;
    /**
     * Creates a new {@code CopyHeightmapTask}.
     *
     * @param selection the model selection containing the wanted models to use
     * @param value value to set
     * @param toSpec target writable heightmap spec
     */
    public SetHeightmpaTask(ModelSelection selection, ModelValue value, WritableHeightmapSpec toSpec) {
        super(Shape2dConvertibleModel.class, selection);
        this.value = value;
        this.toSpec = toSpec;
        voxelizer = new SurfaceVoxelizer2d();
    }

    @Override
    protected void run(Shape2dConvertibleModel model, GenerationTile tile) {
        Optional<Integer> option = this.value.getAsInt(model);
        if (!option.isPresent())
            return;

        WritableHeightmap to = tile.heightmaps().get(toSpec);

        WritableHeightmap buffered = to.copy();
        for (Positioned2d voxel : buffered.bbox().clip(voxelizer.voxelize(model)))
            buffered.set(voxel.coords(), option.get());

        to.copyValues(buffered);
    }
}
