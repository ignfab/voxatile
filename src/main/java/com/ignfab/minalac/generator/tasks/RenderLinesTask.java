package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.Shape3dConvertibleModel;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.utils.world2d.Vector2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.voxelization.shape3d.iterator.Indexed2dPosition3d;
import com.ignfab.minalac.generator.voxelization.shape3d.voxelizer.ThickLinearIndexedVoxelizer3d;

/**
 * A task rendering lines using a structure as template.
 */
public class RenderLinesTask extends ModelTask<Shape3dConvertibleModel> {
    // In structure, X is for linear direction (starting at 0, looping over bbox max x), Y for both orthogonal directions (0 is central axis).
    private final StructureGenerator structureGenerator;
    private final GetZ stickToZ;
    private final ReadableHeightmapSpec renderOnlyWhenAboveSpec;

    /**
     * Creates a new {@code RenderLinesTask}.
     *
     * Stucture will be used as template and will be repeated along lines.
     *
     * In structure x-axis will be along lines. Y-axis will be on sides, and z-axis will be used for height.
     *
     * @param selection Selection of models to render
     * @param structureGenerator Structure to use as template
     * @param stickToHeightmapSpec If not null, lines will be rendered at that heightmap
     * @param renderOnlyWhenAboveSpec If not null, lines will be rendered only if they have a part over that heightmap
     */
    public RenderLinesTask(
        ModelSelection selection,
        StructureGenerator structureGenerator,
        GetZ stickToZ,
        ReadableHeightmapSpec renderOnlyWhenAboveSpec
    ) {
        super(Shape3dConvertibleModel.class, selection);

        this.structureGenerator = structureGenerator;
        this.renderOnlyWhenAboveSpec = renderOnlyWhenAboveSpec;
        this.stickToZ = stickToZ;
    }

    @Override
    protected void run(Shape3dConvertibleModel model, GenerationTile tile) throws IgnorableException {
        ReadableHeightmap renderOnlyWhenAbove = renderOnlyWhenAboveSpec == null ? null : tile.heightmaps().get(renderOnlyWhenAboveSpec);

        PlaceableStructure structure = structureGenerator.generate(model);
        ThickLinearIndexedVoxelizer3d voxelizer = new ThickLinearIndexedVoxelizer3d(structure.limits().maxY() * 2 + 1); // 1 for central position and * 2 for each side

        WorldBBox3d limits = structure.limits();

        for (Indexed2dPosition3d pos : tile.clip3d(voxelizer.voxelize(model))) {

            if (renderOnlyWhenAbove != null && renderOnlyWhenAbove.get(pos.coords().to2d()) > pos.coords().z())
                continue;

            Vector2d index = pos.index();
            int x = Math.floorMod((int) Math.round(index.x()), limits.sizeX()) + limits.minX();
            int y = (int) Math.round(Math.abs(index.y()));
            int zOffset = stickToZ == null ? pos.coords().z() : stickToZ.get(tile, model, pos.coords().to2d());

            for (int z = limits.minZ(); z <= limits.maxZ(); z++) {
                Placeable placeable = structure.get(x, y, z);
                if (placeable != null)
                    placeable.place(tile.voxels(), pos.coords().x(), pos.coords().y(), zOffset + z);
            }
        }
    }

    @FunctionalInterface
    public interface StructureGenerator {
        PlaceableStructure generate(Model model) throws IgnorableException;
    }

    @FunctionalInterface
    public interface GetZ {
        int get(GenerationTile tile, Model model, WorldCoords2d coords) throws IgnorableException;
    }
}
