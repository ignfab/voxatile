package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ShapesVoxelizable2d;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.shape2d.ShapesVoxelizer2d;
import com.ignfab.minalac.generator.voxelization.shape2d.iterator.Polygon2dIterator;

/**
 * {@code WaterHeightmapRenderer} populates a heightmap in order to represent the depth of water sources.
 * This renderer uses models that has vector data.
 */
public class WaterHeightmapRenderer extends ModelRenderer {
    private Heightmap heightmap;
    private int depth;

    /**
     * Creates a new {@code WaterHeightmapRenderer}.
     *
     * @param selection the model selection containing the wanted models to render (only {@code ShapesVoxelizable2d} will be)
     * @param heightmap the heightmap where heights will be written
     * @param depth the depth that will be written on the heightmap.
     */
    public WaterHeightmapRenderer(ModelSelection selection, Heightmap heightmap, int depth) {
        super(selection);
        this.heightmap = heightmap;
        this.depth = depth;
    }

    @Override
    protected void render(Model model, WorldBBox3d bbox) {
        if (!(model instanceof ShapesVoxelizable2d voxelizable)) {
            // TODO: Better warning about not possible to render a non voxelizable model
            System.err.println("Ignoring non shapesvoxelizable model. Type: " + model.getClass());
            return;
        }
        ShapesVoxelizer2d voxelizer = voxelizable.voxelize2d(bbox.to2d());

        for (Positioned2d voxel : voxelizer) {
            heightmap.set(voxel.coords(), 1 + (int) Math.round(3 * Math.sqrt(((Polygon2dIterator.Positioned2dWithSDF) voxel).sdf())));
        }
    }
}
