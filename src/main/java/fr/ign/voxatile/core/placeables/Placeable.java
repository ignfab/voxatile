package fr.ign.voxatile.core.placeables;

import fr.ign.voxatile.core.utils.world3d.WorldCoords3d;
import fr.ign.voxatile.core.world.VoxelTile;

/**
 * The {@code Placeable} interface represents something placeable in voxel world.
 */
public interface Placeable {
    /**
     * Places the placeable at given position in {@link VoxelTile}.
     * Coordinates are expressed using the system used in {@link WorldCoords3d}.
     *
     * @param tile tile to place into
     * @param x position x-coordinate
     * @param y position y-coordinate
     * @param z position z-coordinate
     */
    void place(VoxelTile tile, int x, int y, int z);

    /**
     * Places the placeable at given position in {@link VoxelTile}.
     *
     * @param tile tile to place into
     * @param position position where to place the placeable
     */
    default void place(VoxelTile tile, WorldCoords3d position) {
        place(tile, position.x(), position.y(), position.z());
    }
}
