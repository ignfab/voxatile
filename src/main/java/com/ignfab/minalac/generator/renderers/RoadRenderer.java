package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ShapesVoxelizable3d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape3d.LineVoxel3d;
import com.ignfab.minalac.generator.voxelization.shape3d.ShapesVoxelizer3d;
import com.ignfab.minalac.generator.world.Placeable;

/**
 * A basic road renderer intended to evolve.
 */
public class RoadRenderer extends ModelRenderer<ShapesVoxelizable3d> {
    private final Heightmap heightmap;
    private final Placeable place;

    /**
     * Creates a new {@code RoadRenderer}.
     *
     * @param models Models to be rendered (only ShapesVoxelizable3d ones will be)
     * @param heightmap Heightmap of the ground (on which features will be placed)
     * @param place Placeable to place along the road
     */
    public RoadRenderer(ModelSelection models, Heightmap heightmap, Placeable place) {
        super(ShapesVoxelizable3d.class, models);
        this.heightmap = heightmap;
        this.place = place;
    }

    @Override
    protected void render(ShapesVoxelizable3d model, WorldBBox3d bbox) {
        ShapesVoxelizer3d voxelizer = model.voxelize3d(bbox);

        for (LineVoxel3d voxel : voxelizer.borders()) {
            WorldCoords3d c = voxel.coords();
            WorldCoords2d c2d = c.to2d();
            if (heightmap.bbox().contains(c2d))
                place.place(c.x(), c.y(), Math.max(heightmap.get(c2d), c.z()));
        }
    }
}
