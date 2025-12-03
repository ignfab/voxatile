package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.generation.heightmaps.computed.ConstantHeightmap;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Shape3dConvertibleModel;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.shape3d.iterator.IndexedPosition3d;
import com.ignfab.minalac.generator.voxelization.shape3d.voxelizer.ThickLinearIndexedVoxelizer2d5;

/**
 * A task rendering lines by placing structure along them.
 */
public class RenderLinesTask extends ModelTask<Shape3dConvertibleModel> {
    // In structure, X is for linear direction (starting at 0, looping over bbox max x), Y for both orthogonal directions (0 is central axis).
    private final PlaceableStructure structure;
    private final ReadableHeightmapSpec renderOnlyWhenAboveSpec;
    private final ThickLinearIndexedVoxelizer2d5 voxelizer;
    private static final ReadableHeightmap DEFAULT_HEIGHTMAP = new ConstantHeightmap(Integer.MIN_VALUE);
    /**
     * Creates a new {@code RenderLinesTask}.
     * <p>
     * Structure will be used as template and will be repeated along lines.
     * <p>
     * In structure x-axis will be along lines. Y-axis will be on sides, and z-axis will be used for height.
     *
     * @param selection selection of models to render
     * @param structure structure placed along the lines
     * @param heightmapSpec if not null, only parts of the lines over that heightmap are rendered
     */
    public RenderLinesTask(
        ModelSelection selection,
        PlaceableStructure structure,
        ReadableHeightmapSpec heightmapSpec
    ) {
        super(Shape3dConvertibleModel.class, selection);

        this.structure = structure;
        renderOnlyWhenAboveSpec = heightmapSpec;
        voxelizer = new ThickLinearIndexedVoxelizer2d5(structure.limits().sizeY());
    }

    @Override
    protected void run(Shape3dConvertibleModel model, GenerationTile tile) {
        ReadableHeightmap renderOnlyWhenAbove = renderOnlyWhenAboveSpec == null ? DEFAULT_HEIGHTMAP : tile.heightmaps().get(renderOnlyWhenAboveSpec);
        WorldBBox3d limits = structure.limits();

        // Always align structure center to the line axis.
        double yOffset = 0.5 * limits.sizeY() + limits.minY();

        for (IndexedPosition3d pos : tile.limits().filterInside(voxelizer.voxelize(model))) {

            if (renderOnlyWhenAbove.get(pos.coords().to2d()) > pos.coords().z())
                continue;

            // Get voxel in structure along x-axis, using eventual structure offset as a repeat offset
            int x = Math.floorMod((int) Math.round(pos.index()) - limits.minX(), limits.sizeX()) + limits.minX();
            // Much simpler for y-axis
            int y = (int) Math.floor(pos.distance() + yOffset);

            for (int z = limits.minZ(); z <= limits.maxZ(); z++)
                structure.get(x, y, z).place(tile.voxels(), pos.coords().x(), pos.coords().y(), pos.coords().z() + z);
        }
    }
}
