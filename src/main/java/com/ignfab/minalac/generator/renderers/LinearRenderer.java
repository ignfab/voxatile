package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ShapesVoxelizable3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape3d.LineVoxel3d;
import com.ignfab.minalac.generator.voxelization.shape3d.ShapesVoxelizer3d;
import com.ignfab.minalac.generator.world.Placeable;

/**
 * A basic example of vector renderer intended to evolve.
 */
public class LinearRenderer extends ModelRenderer {
    private final Heightmap heightmap;
    private final Placeable placeable;

    /**
     * Creates a new VectorRenderer.
     *
     * @param heightmap Heightmap of the ground (on which features will be placed)
     * @param models Models to be rendered (only Voxelizable2d ones will be)
     * @param inside Voxel type to draw inside geometries
     * @param edge Voxel type to draw on edges of geometries (including points and lines)
     */
    public LinearRenderer(ModelSelection selection, Heightmap heightmap, Placeable placeable) {
        super(selection);
        this.heightmap = heightmap;
        this.placeable = placeable;
    }

    private void place(int x, int y, int z) {
        if (heightmap.bbox().contains(x, y))
            placeable.place(x, y, Math.max(heightmap.get(x, y), z));
    }

    /**
     * Performs rendering.
     *
     * @param bbox the limits of the rendering area.
     */
    @Override
    protected void render(Model model, WorldBBox3d bbox) {
        if (!(model instanceof ShapesVoxelizable3d voxelizable)) {
            // TODO: Better warning about not possible to render a non voxelizable model
            System.err.println("Ignoring non voxelizable model. Type: " + model.getClass());
            return;
        }
        ShapesVoxelizer3d voxelizer = voxelizable.voxelize3d(bbox);

        for (LineVoxel3d voxel : voxelizer.borders()) {
            WorldCoords3d c = voxel.coords();
            place(c.x(), c.y(), c.z());
        }
    }
}
