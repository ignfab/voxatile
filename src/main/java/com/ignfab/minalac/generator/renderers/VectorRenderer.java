package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ShapesVoxelizable2d;
import com.ignfab.minalac.generator.placeables.NoVoxel;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineVoxel2d;
import com.ignfab.minalac.generator.voxelization.shape2d.ShapesVoxelizer2d;

/**
 * A basic example of vector renderer intended to evolve.
 */
public class VectorRenderer extends ModelRenderer<ShapesVoxelizable2d> {
    private final ReadableHeightmap heightmap;

    // What to place inside and on edges of geometries
    private final Placeable inside;
    private final Placeable borders;

    /**
     * Creates a new VectorRenderer.
     *
     * @param selection the model selection containing the wanted models to render (only ShapesVoxelizable2d ones will be)
     * @param heightmap Heightmap of the ground (on which features will be placed)
     * @param inside What to place inside geometries
     * @param borders What to place on geometries borders
     */
    public VectorRenderer(ModelSelection selection, ReadableHeightmap heightmap, Placeable inside, Placeable borders) {
        super(ShapesVoxelizable2d.class, selection);
        this.heightmap = heightmap;
        this.inside = inside;
        this.borders = borders;
    }

    @Override
    protected void render(ShapesVoxelizable2d model, WorldBBox3d bbox) {
        ShapesVoxelizer2d voxelizer = model.voxelize2d(bbox.to2d());

        if (inside != NoVoxel.INSTANCE) {
            // In case of same border & inside, don't perform two iterations
            if (inside == borders) {
                for (Positioned2d voxel : voxelizer) {
                    WorldCoords2d c = voxel.coords();
                    inside.place(c.x(), c.y(), heightmap.get(c));
                }
                return;
            }

            // Iteration over inside voxels
            for (Positioned2d voxel : voxelizer.inside()) {
                WorldCoords2d c = voxel.coords();
                inside.place(c.x(), c.y(), heightmap.get(c));
            }
        }

        if (borders != NoVoxel.INSTANCE)
            // Iteration over border voxels
            for (LineVoxel2d voxel : voxelizer.borders()) {
                WorldCoords2d c = voxel.coords();
                borders.place(c.x(), c.y(), heightmap.get(c));
            }
    }
}
