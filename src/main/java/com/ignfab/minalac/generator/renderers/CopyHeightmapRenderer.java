package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ShapesVoxelizable2d;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.voxelization.shape2d.ShapesVoxelizer2d;
import com.ignfab.minalac.generator.world.VoxelWorld;

// TODO: Rename this class as it is not a renderer per se. Should be be an "operation" or "task".
/**
 * Copies the values of a heightmap to another at all coordinates within the model's shape.
 */
public class CopyHeightmapRenderer extends ModelRenderer<ShapesVoxelizable2d> {
    private final ReadableHeightmap from;
    private final Heightmap to;

    /**
     * Creates a new {@code CopyHeightmapRenderer}.
     *
     * @param selection the model selection containing the wanted models to render
     * @param from the giving heightmap
     * @param to the receiving heightmap
     */
    public CopyHeightmapRenderer(ModelSelection selection, ReadableHeightmap from, Heightmap to) {
        super(ShapesVoxelizable2d.class, selection);
        this.from = from;
        this.to = to;
    }

    @Override
    protected void render(ShapesVoxelizable2d model, VoxelWorld world) {
        ShapesVoxelizer2d voxelizer = model.voxelize2d(world.limits().to2d().intersection(from.bbox()).intersection(to.bbox()));
        Heightmap buffered = to.copy();
        for (Positioned2d voxel : voxelizer)
            buffered.set(voxel.coords(), from.get(voxel.coords()));
        to.swap(buffered);
    }
}
