package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.Model;
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
public class VectorRenderer extends ModelRenderer {
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
        super(selection);
        this.heightmap = heightmap;
        this.inside = inside;
        this.edge = edge;
    }

    @Override
    protected void render(Model model, WorldBBox3d bbox) {
        if (!(model instanceof ShapesVoxelizable2d voxelizable)) {
            // TODO: Better warning about not possible to render a non voxelizable model
            System.err.println("Ignoring non shapesvoxelizable model. Type: " + model.getClass());
            return;
        }
        ShapesVoxelizer2d voxelizer = voxelizable.voxelize2d(bbox.to2d());

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
