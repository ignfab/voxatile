package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ShapesVoxelizable2d;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.shape2d.ShapesVoxelizer2d;
import com.ignfab.minalac.generator.world.VoxelType;

public class NatureRenderer extends ModelRenderer {
    private final Heightmap heightmap;

    private final VoxelType leaf;

    private final VoxelType wood;

    public NatureRenderer(
            ModelSelection selection,
            Heightmap heightmap,
            VoxelType leaf,
            VoxelType wood) {
        super(selection);
        this.heightmap = heightmap;
        this.leaf = leaf;
        this.wood = wood;
    }

    @Override
    protected void render(Model model, WorldBBox3d bbox) {
        if (!(model instanceof ShapesVoxelizable2d voxelizable)) {
            // TODO: Better warning about not possible to render a non voxelizable model
            System.err.println("Ignoring non shapesvoxelizable model. Type: " + model.getClass());
            return;
        }
        ShapesVoxelizer2d voxelizer = voxelizable.voxelize2d(bbox.to2d());
        
        System.out.println((String) model.getMetadata("nature"));
        // Iterate over objects and place voxels on map at heightmap altitude
        for (Positioned2d voxel : voxelizer) {
            WorldCoords2d c = voxel.coords();
            
            if (Math.random() > 0.9)
                leaf.place(c.x(), c.y(), heightmap.get(c) + 1);

            if (Math.random() > 0.9) {
                wood.place(c.x(), c.y(), heightmap.get(c) + 1);
                wood.place(c.x(), c.y(), heightmap.get(c) + 2);
                wood.place(c.x(), c.y(), heightmap.get(c) + 3);
                wood.place(c.x(), c.y(), heightmap.get(c) + 4);
                leaf.place(c.x(), c.y(), heightmap.get(c) + 5);
            }
        }
    }
    
}
