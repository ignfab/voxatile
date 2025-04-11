package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.Heightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.UnboundHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.UnboundReadableHeightmap;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ShapesVoxelizable2d;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.voxelization.shape2d.ShapesVoxelizer2d;

// TODO: Rename this class as it is not a renderer per se. Should be be an "operation" or "task".
/**
 * Copies the values of a heightmap to another at all coordinates within the model's shape.
 */
public class CopyHeightmapRenderer extends ModelRenderer<ShapesVoxelizable2d> {
    private final UnboundReadableHeightmap from;
    private final UnboundHeightmap to;

    /**
     * Creates a new {@code CopyHeightmapRenderer}.
     *
     * @param selection the model selection containing the wanted models to render
     * @param from the giving heightmap
     * @param to the receiving heightmap
     */
    public CopyHeightmapRenderer(ModelSelection selection, UnboundReadableHeightmap from, UnboundHeightmap to) {
        super(ShapesVoxelizable2d.class, selection);
        this.from = from;
        this.to = to;
    }

    @Override
    protected void render(ShapesVoxelizable2d model, GenerationTile tile) {
        ReadableHeightmap from = this.from.bind(tile);
        Heightmap to = this.to.bind(tile);
        ShapesVoxelizer2d voxelizer = model.voxelize2d(tile.limits().to2d().intersection(from.bbox()).intersection(to.bbox()));

        Heightmap buffered = to.copy();
        for (Positioned2d voxel : voxelizer)
            buffered.set(voxel.coords(), from.get(voxel.coords()));
        to.swap(buffered);
    }
}
