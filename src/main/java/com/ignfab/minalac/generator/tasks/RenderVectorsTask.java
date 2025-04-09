package com.ignfab.minalac.generator.tasks;

import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;
import com.ignfab.minalac.generator.models.ModelSelection;
import com.ignfab.minalac.generator.models.ShapesVoxelizable2d;
import com.ignfab.minalac.generator.placeables.Nothing;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.voxelization.shape2d.LineVoxel2d;
import com.ignfab.minalac.generator.voxelization.shape2d.ShapesVoxelizer2d;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * A {@link TileTask} placing things on vector models.
 * <p>
 * Placement is done on a heightmap, ignoring Z component of model geometries.
 * Placement can be done on borders (for all sorts of geometry) and/or inside (for polygon geometries) or everywhere.
 */
public class RenderVectorsTask extends ModelTask<ShapesVoxelizable2d> {
    private final ReadableHeightmap heightmap;

    // What to place inside and on borders of geometries
    private final Placeable inside;
    private final Placeable borders;

    /**
     * Creates a new {@code RenderVectorsTask}.
     *
     * @param selection the model selection containing the wanted models to render (only ShapesVoxelizable2d ones will be)
     * @param heightmap Heightmap of the ground (on which features will be placed)
     * @param inside What to place inside geometries
     * @param borders What to place on geometries borders
     */
    public RenderVectorsTask(ModelSelection selection, ReadableHeightmap heightmap, Placeable inside, Placeable borders) {
        super(ShapesVoxelizable2d.class, selection);
        this.heightmap = heightmap;
        this.inside = inside;
        this.borders = borders;
    }

    @Override
    protected void run(ShapesVoxelizable2d model, VoxelTile tile) {
        ShapesVoxelizer2d voxelizer = model.voxelize2d(tile.limits().to2d());

        if (inside != Nothing.INSTANCE) {
            // In case of same border & inside, don't perform two iterations
            if (inside == borders) {
                for (Positioned2d voxel : voxelizer) {
                    WorldCoords2d c = voxel.coords();
                    inside.place(tile, c.x(), c.y(), heightmap.get(c));
                }
                return;
            }

            // Iteration over inside voxels
            for (Positioned2d voxel : voxelizer.inside()) {
                WorldCoords2d c = voxel.coords();
                inside.place(tile, c.x(), c.y(), heightmap.get(c));
            }
        }

        if (borders != Nothing.INSTANCE)
            // Iteration over border voxels
            for (LineVoxel2d voxel : voxelizer.borders()) {
                WorldCoords2d c = voxel.coords();
                borders.place(tile, c.x(), c.y(), heightmap.get(c));
            }
    }
}
