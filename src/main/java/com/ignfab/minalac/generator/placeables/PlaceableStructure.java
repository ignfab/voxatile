package com.ignfab.minalac.generator.placeables;

import java.util.Map;

import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.world.VoxelTile;

/**
 * {@code PlaceableStructure} is a {@link Placeable} consisting of placeables at given coordinate offsets.
 * This structure may be extendable by repeating certain elements at specified axis coordinates.
 */
public class PlaceableStructure implements Placeable {
    private final Map<WorldCoords3d, Placeable> placeables;
    private final WorldBBox3d limits;
    // TODO-PR: Should this class holds the capabilities? See -> ElasticPlaceableStructureParams
    private final SegmentCapability axisX;
    private final SegmentCapability axisY;
    private final SegmentCapability axisZ;

    /**
     * Creates a new {@code PlaceableStructure} from a mapping.
     * Each key and value must be not null.
     *
     * @param placeables the mapping associating position with placeable.
     */
    public PlaceableStructure(Map<WorldCoords3d, Placeable> placeables) {
        this.placeables = placeables;
        limits = placeables.isEmpty() ? WorldBBox3d.EMPTY : new WorldBBox3d(placeables.keySet().toArray(new WorldCoords3d[0]));
        axisX = new SegmentCapability.NonExpendable(limits.minX(), limits.sizeX());
        axisY = new SegmentCapability.NonExpendable(limits.minY(), limits.sizeY());
        axisZ = new SegmentCapability.NonExpendable(limits.minZ(), limits.sizeZ());

    }

    // TODO-PR: Weird that null is used as a way to tell if axis is elastic
    /**
     * Creates a new {@code PlaceableStructure} from a mapping.
     * Each key and value must be not null.
     * Placeables that match one of the specified coordinates will be marked as repeatable to expand this structure.
     *
     * @param placeables the mapping associating position with placeable.
     * @param xCoordinate the x-coordinate where repetition would happen or {code null} to make this structure unexpendable at x-axis.
     * @param yCoordinate the y-coordinate where repetition would happen or {code null} to make this structure unexpendable at y-axis.
     * @param zCoordinate the z-coordinate where repetition would happen or {code null} to make this structure unexpendable at z-axis.
     */
    public PlaceableStructure(Map<WorldCoords3d, Placeable> placeables, Integer xCoordinate, Integer yCoordinate, Integer zCoordinate) {
        this.placeables = placeables;
        limits = placeables.isEmpty() ? WorldBBox3d.EMPTY : new WorldBBox3d(placeables.keySet().toArray(new WorldCoords3d[0]));

        axisX = (xCoordinate == null) ? new SegmentCapability.NonExpendable(limits.minX(), limits.sizeX()) : new SegmentCapability.Expendable(limits.minX(), xCoordinate, limits.sizeX() - 1);
        axisY = (yCoordinate == null) ? new SegmentCapability.NonExpendable(limits.minY(), limits.sizeY()) : new SegmentCapability.Expendable(limits.minY(), yCoordinate, limits.sizeY() - 1);
        axisZ = (zCoordinate == null) ? new SegmentCapability.NonExpendable(limits.minZ(), limits.sizeZ()) : new SegmentCapability.Expendable(limits.minZ(), zCoordinate, limits.sizeZ() - 1);
    }

    @Override
    public void place(VoxelTile tile, int x, int y, int z) {
        for (Map.Entry<WorldCoords3d, Placeable> entry : placeables.entrySet()) {
            WorldCoords3d c = entry.getKey();
            entry.getValue().place(tile, c.x() + x, c.y() + y, c.z() + z);
        }
    }

    /**
     * Returns the placeable at the specified coordinates.
     *
     * @param x relative x-coordinate
     * @param y relative y-coordinate
     * @param z relative z-coordinate
     *
     * @return placeable at relative structure coordinates or {@code NoVoxel.INSTANCE} if none
     */
    public Placeable get(int x, int y, int z) {
        Placeable placeable = placeables.get(new WorldCoords3d(x, y, z));
        return placeable == null ? Nothing.INSTANCE : placeable;
    }

    /**
     * Tells the limits of this structure in relative coordinates.
     * <p>
     * This is not the bounding box of all that would be placed.
     * Limits will only contain origin coordinates of contained placeables.
     * <p>
     * In other words, limits contains every position for which {@link #get} returns something other than {@link Nothing#INSTANCE}.
     * This may be used to know how to repeat this structure.
     *
     * @return limits of the structure in relative coordinates.
     */
    public WorldBBox3d limits() {
        return limits;
    }

    /**
     * Returns the capability of the x-axis.
     *
     * @return {@link SegmentCapability} of the x-axis of this structure.
     */
    public SegmentCapability axisX() {
        return axisX;
    }

    /**
     * Returns the capability of the y-axis.
     *
     * @return {@link SegmentCapability} of the y-axis of this structure.
     */
    public SegmentCapability axisY() {
        return axisY;
    }

    /**
     * Returns the capability of the z-axis.
     *
     * @return {@link SegmentCapability} of the z-axis of this structure.
     */
    public SegmentCapability axisZ() {
        return axisZ;
    }
}
