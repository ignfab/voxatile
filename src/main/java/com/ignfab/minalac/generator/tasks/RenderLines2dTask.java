package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Shape2dConvertibleModel;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.shape2d.iterator.IndexedPosition2d;
import com.ignfab.minalac.generator.voxelization.shape2d.voxelizer.ThickLinearIndexedVoxelizer2d;

/**
 * A task rendering lines by placing structure along them.
 */
public class RenderLines2dTask extends ModelTask<Shape2dConvertibleModel> {
    // In structure, X is for linear direction (starting at 0, looping over bbox max x), Y for both orthogonal directions (0 is central axis).
    private final PlaceableStructure structure;
    private final ReadableHeightmapSpec heightmapSpec;
    private final ThickLinearIndexedVoxelizer2d voxelizer;

    /**
     * Creates a new {@code RenderLinesTask}.
     * <p>
     * Structure will be used as template and will be repeated along lines.
     * <p>
     * In structure x-axis will be along lines. Y-axis will be on sides, and z-axis will be used for height.
     *
     * @param selection selection of models to render
     * @param structure structure placed along the lines
     * @param heightmapSpec heightmap on which draw lines
     */
    public RenderLines2dTask(
        ModelSelection selection,
        PlaceableStructure structure,
        ReadableHeightmapSpec heightmapSpec
    ) {
        super(Shape2dConvertibleModel.class, selection);

        this.structure = structure;
        this.heightmapSpec = heightmapSpec;
        voxelizer = new ThickLinearIndexedVoxelizer2d(structure.limits().sizeY());
    }

    @Override
    protected void run(Shape2dConvertibleModel model, GenerationTile tile) {
        ReadableHeightmap heightmap = tile.heightmap(heightmapSpec);
        WorldBBox3d limits = structure.limits();

        // Always align structure center to the line axis.
        double yOffset = 0.5 * limits.sizeY() + limits.minY();

        for (IndexedPosition2d pos : tile.limits().to2d().filterInside(voxelizer.voxelize(model))) {

            // Get voxel in structure along x-axis, using eventual structure offset as a repeat offset
            int x = Math.floorMod((int) Math.round(pos.index()) - limits.minX(), limits.sizeX()) + limits.minX();
            // Much simpler for y-axis
            int y = (int) Math.floor(pos.distance() + yOffset);

            for (int z = limits.minZ(); z <= limits.maxZ(); z++)
                structure.get(x, y, z).place(tile.voxels(), pos.coords().x(), pos.coords().y(), heightmap.get(pos.coords()) + z);
        }
    }
}
