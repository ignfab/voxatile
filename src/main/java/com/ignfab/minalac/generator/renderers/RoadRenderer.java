package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ShapesVoxelizable3d;
import com.ignfab.minalac.generator.models.selection.ModelFilter;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape3d.LineVoxel3d;
import com.ignfab.minalac.generator.voxelization.shape3d.ShapesVoxelizer3d;
import com.ignfab.minalac.generator.world.VoxelPattern;

/**
 * A basic road renderer intended to evolve.
 */
public class RoadRenderer extends ModelRenderer {
    private final Heightmap heightmap;
    private final VoxelPattern voxelPattern;

    /**
     * Creates a new {@code RoadRenderer}.
     *
     * @param models Models to be rendered (only ShapesVoxelizable3d ones will be)
     * @param heightmap Heightmap of the ground (on which features will be placed)
     * @param voxelPattern Pattern to place along the road
     */
    public RoadRenderer(ModelFilter models, Heightmap heightmap, VoxelPattern voxelPattern) {
        super(models);
        this.heightmap = heightmap;
        this.voxelPattern = voxelPattern;
    }

    @Override
    protected void render(Model model, WorldBBox3d bbox) {
        if (!(model instanceof ShapesVoxelizable3d voxelizable)) {
            // TODO: Better warning about not possible to render a non shapes-voxelizable model
            System.err.println("Ignoring non shapes-voxelizable model. Type: " + model.getClass());
            return;
        }
        ShapesVoxelizer3d voxelizer = voxelizable.voxelize3d(bbox);

        boolean onGround = model.getMetadata("position_par_rapport_au_sol").equals("0");

        for (LineVoxel3d voxel : voxelizer.borders()) {
            WorldCoords3d c = voxel.coords();
            WorldCoords2d c2d = c.to2d();
            if (heightmap.bbox().contains(c2d)) {
                int z = onGround ? heightmap.get(c2d) : Math.max(heightmap.get(c2d), c.z());
                voxelPattern.place(c.x(), c.y(), z);
            }
        }
    }
}
