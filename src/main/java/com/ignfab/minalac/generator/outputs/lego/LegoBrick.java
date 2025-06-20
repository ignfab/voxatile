package com.ignfab.minalac.generator.outputs.lego;

import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * {@code LegoBrick} class implements a {@link Placeable} voxel for Lego.
 * A voxel in Lego, known as brick, consists of two parameters: reference and color.
 * @param ref The brick reference string
 * @param color The color ID
 * @param lduOffset The brick placement offset in LDraw Units (LDU)
 * @param rotationAroundY The rotation angle in degrees around the LDraw Y axis (vertical)
 */
public record LegoBrick(String ref, int color, WorldCoords3d lduOffset, double rotationAroundY) implements Placeable {
    public static final WorldCoords3d NO_OFFSET = new WorldCoords3d(0, 0, 0);
    public static final LegoBrick NO_BRICK = new LegoBrick("", -1, NO_OFFSET, 0);

    @Override
    public void place(VoxelTile tile, int x, int y, int z) {
        place(tile, new WorldCoords3d(x, y, z));
    }

    @Override
    public void place(VoxelTile tile, WorldCoords3d position) {
        ((LegoTile) tile).setBrick(position, this);
    }
}
