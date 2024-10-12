package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.models.IntegerMatrixModel;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.Matrix2d;
import com.ignfab.minalac.generator.world.VoxelTypeFactory;

/**
 * Ground renderer renders a basic ground using altitude from given heightmap.
 */
public class ColoredGroundRenderer extends ModelRenderer {
    private final Heightmap heightmap;
    private final VoxelTypeFactory factory;

    public ColoredGroundRenderer(ModelSelection selection, Heightmap heightmap, VoxelTypeFactory factory) {
        super(selection);
        this.heightmap = heightmap;
        this.factory = factory;
    }

    @Override
    protected void render(Model model, WorldBBox3d bbox) {
        if (!(model instanceof IntegerMatrixModel matrix)) {
            // TODO: Better warning about not possible to render a non integer matrix model
            System.out.println("Ignoring non integer matrix model.");
            return;
        }

        WorldBBox2d intersection = heightmap.bbox().intersection(bbox);
        for (Matrix2d.Value<Integer> value : matrix) {
            WorldCoords2d c = value.coords();
            if (intersection.contains(c)) {
                int z = heightmap.get(c);
                for (int i = z - 20; i <= z; i++)
                    factory.createColor(value.value()).place(c.x(), c.y(), i);
            }
        }
    }
}
