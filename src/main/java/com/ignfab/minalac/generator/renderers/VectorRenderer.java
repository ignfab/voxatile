package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.Voxelizable2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.LineVoxel2d;
import com.ignfab.minalac.generator.voxelization.Voxel2d;
import com.ignfab.minalac.generator.voxelization.Voxelizer2d;
import com.ignfab.minalac.generator.world.VoxelType;

/**
 * A basic example of vector renderer intended to evolve.
 */
public class VectorRenderer {
    private final Heightmap heightmap;
    private final Iterable<Model> models;

    // This may evolve in something more elaborated, like edge and inside voxel patterns,
    // or even closures (one for inside, one for edge and another for init)
    private final VoxelType inside;
    private final VoxelType edge;

    /**
     * Creates a new VectorRenderer.
     *
     * @param heightmap Heightmap of the ground (on which features will be placed)
     * @param models Models to be rendered (only Voxelizable2d ones will be)
     * @param inside Voxel type to draw inside geometries
     * @param edge Voxel type to draw on edges of geometries (including points and lines)
     */
    public VectorRenderer(Heightmap heightmap, Iterable<Model> models, VoxelType inside, VoxelType edge) {
        this.heightmap = heightmap;
        this.models = models;
        this.inside = inside;
        this.edge = edge;
    }

    /**
     * Performs rendering.
     *
     * @param bbox the limits of the rendering area.
     */
    public void render(WorldBBox3d bbox) {
        for (Model model : models) {
            if (!(model instanceof Voxelizable2d voxelizable)) {
                // TODO: Better warning about not possible to render a non voxelizable model
                System.err.println("Ignoring non voxelizable model. Type: " + model.getClass());
                continue;
            }
            Voxelizer2d voxelizer = voxelizable.voxelize2d(bbox.to2d());

            // Iterate over objects and place voxels on map at heightmap altitude
            for (Voxel2d voxel : voxelizer) {
                WorldCoords2d c = voxel.coords();
                inside.place(c.x(), c.y(), heightmap.get(c) + 1);
            }
            for (LineVoxel2d voxel : voxelizer.borders()) {
                WorldCoords2d c = voxel.coords();
                edge.place(c.x(), c.y(), heightmap.get(c) + 1);
            }
        }
    }
}
