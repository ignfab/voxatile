package fr.ign.voxatile.core.world;

import fr.ign.voxatile.core.placeables.Placeable;

/**
 * The {@code Voxel} interface represents a {@link Placeable} that can be identified in the voxel world.
 */
public interface Voxel extends Placeable {
    /**
     * {@return the type identifier of the voxel}
     * It is a unique and persistent identifier for each type of voxel.
     * Only the type matters, any additional property of the voxel has no impact on this identifier.
     */
    String getTypeIdentifier();
}
