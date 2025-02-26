package com.ignfab.minalac.generator.renderers;

import java.util.Random;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ShapesVoxelizable2d;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.random.Seed;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineVoxel2d;
import com.ignfab.minalac.generator.voxelization.shape2d.ShapesVoxelizer2d;

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
     * @param seed random seed for this renderer
     * @param selection the model selection containing the wanted models to render (only ShapesVoxelizable2d ones will be)
     * @param heightmap Heightmap of the ground (on which features will be placed)
     * @param inside What to place inside geometries
     * @param edge What to place on geometries edges
     */
    public VectorRenderer(Seed seed, ModelSelection selection, Heightmap heightmap, Placeable inside, Placeable edge) {
        super(seed, ShapesVoxelizable2d.class, selection);
        this.heightmap = heightmap;
        this.inside = inside;
        this.edge = edge;
    }

    @Override
    protected void render(Seed seed, ShapesVoxelizable2d model, WorldBBox3d bbox) {
        Random random = seed.createRandom();

        ShapesVoxelizer2d voxelizer = model.voxelize2d(bbox.to2d());

        // Iterate over objects and place voxels on map at heightmap altitude
        for (Positioned2d voxel : voxelizer) {
            WorldCoords2d c = voxel.coords();
            inside.place(random, c.x(), c.y(), heightmap.get(c) + 1);
        }
        for (LineVoxel2d voxel : voxelizer.borders()) {
            WorldCoords2d c = voxel.coords();
            edge.place(random, c.x(), c.y(), heightmap.get(c) + 1);
        }
    }
}
