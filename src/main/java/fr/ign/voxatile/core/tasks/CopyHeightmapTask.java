package fr.ign.voxatile.core.tasks;

import fr.ign.voxatile.core.generation.GenerationTile;
import fr.ign.voxatile.core.generation.heightmaps.ReadableHeightmap;
import fr.ign.voxatile.core.generation.heightmaps.ReadableHeightmapSpec;
import fr.ign.voxatile.core.generation.heightmaps.WritableHeightmap;
import fr.ign.voxatile.core.generation.heightmaps.WritableHeightmapSpec;
import fr.ign.voxatile.core.models.ModelSelection;
import fr.ign.voxatile.core.models.Shape2dConvertibleModel;
import fr.ign.voxatile.core.utils.world2d.Positioned2d;
import fr.ign.voxatile.core.voxelization.shape2d.voxelizer.Shape2dVoxelizer;
import fr.ign.voxatile.core.voxelization.shape2d.voxelizer.SurfaceVoxelizer2d;

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
        ReadableHeightmap from = tile.heightmap(fromSpec);
        WritableHeightmap to = tile.heightmap(toSpec);

        WritableHeightmap buffered = to.copy();
        for (Positioned2d voxel : buffered.bbox().filterInside(voxelizer.voxelize(model)))
            buffered.set(voxel.coords(), from.get(voxel.coords()));

        to.copyValues(buffered);
    }
}
