package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ShapesVoxelizable2d;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineVoxel2d;
import com.ignfab.minalac.generator.voxelization.shape2d.ShapesVoxelizer2d;
import com.ignfab.minalac.generator.world.Placeable;

/**
 * A basic example of vector renderer intended to evolve.
 */
public class VectorRenderer extends ModelRenderer<ShapesVoxelizable2d> {
    private final Heightmap heightmap;

    // What to place inside and on edges of geometries
    private final Placeable inside;
    private final Placeable edge;

    /**
     * Creates a new VectorRenderer.
     *
     * @param selection the model selection containing the wanted models to render (only ShapesVoxelizable2d ones will be)
     * @param heightmap Heightmap of the ground (on which features will be placed)
     * @param inside What to place inside geometries
     * @param edge What to place on geometries edges
     */
    public VectorRenderer(ModelSelection selection, Heightmap heightmap, Placeable inside, Placeable edge) {
        super(ShapesVoxelizable2d.class, selection);
        this.heightmap = heightmap;
        this.inside = inside;
        this.edge = edge;
    }

    @Override
    protected void render(ShapesVoxelizable2d model, WorldBBox3d bbox) {
        ShapesVoxelizer2d voxelizer = model.voxelize2d(bbox.to2d());

        // Iterate over objects and place voxels on map at heightmap altitude
        for (Positioned2d voxel : voxelizer) {
            WorldCoords2d c = voxel.coords();
            inside.place(c.x(), c.y(), heightmap.get(c) + 1);
        }
        for (LineVoxel2d voxel : voxelizer.borders()) {
            WorldCoords2d c = voxel.coords();
            edge.place(c.x(), c.y(), heightmap.get(c) + 1);
        }
    }
}
