package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.models.IntegerMatrixModel;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.selection.ModelSelection;
import com.ignfab.minalac.generator.models.Voxelizable3d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.Positioned3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.VoxelTypeFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Ground renderer renders a basic ground using altitude from given heightmap.
 */
public class ColoredModelRenderer extends ModelRenderer {
    private final ModelSelection colorModels;
    private final VoxelTypeFactory factory;
    private List<IntegerMatrixModel> colors;

    public ColoredModelRenderer(ModelSelection selection, ModelSelection colorModels, VoxelTypeFactory factory) {
        super(selection);
        this.colorModels = colorModels;
        this.factory = factory;
    }

    @Override
    public void render(WorldBBox3d bbox) {
        colors = new ArrayList<>();

        for (Model colorModel : colorModels) {
            if (!(colorModel instanceof IntegerMatrixModel matrix)) {
                // TODO: Better warning about not possible to render a non integer matrix model
                System.out.println("Ignoring non integer matrix model.");
                return;
            }
            colors.add(matrix);
        }

        super.render(bbox);
    }

    @Override
    protected void render(Model model, WorldBBox3d bbox) {
        if (!(model instanceof Voxelizable3d voxelizable)) {
            // TODO: Better warning about not possible to render a non integer matrix model
            System.out.println("Ignoring non integer matrix model.");
            return;
        }

        for (Positioned3d p : voxelizable.voxelize3d(bbox)) {
            for (IntegerMatrixModel colorModel : colors) {
                WorldCoords3d c = p.coords();
                WorldCoords2d c2d = c.to2d();
                if (colorModel.bbox().contains(c2d)) {
                    factory.createColor(colorModel.get(c2d)).place(c.x(), c.y(), c.z());
                }
            }
        }
    }
}
