package com.ignfab.minalac.generator.voxelization.shape3d;

import com.ignfab.minalac.generator.utils.world3d.Bounded3d;
import com.ignfab.minalac.generator.utils.world3d.Vector3d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.voxelization.shape2d.Segment2d;

/**
 * Represents a 3d line segment in the voxel world.
 * <p>
 * The line is modelled using a parametric equation:
 * <pre>{@code
 *  x = direction.x() * index + start.x()
 *  y = direction.y() * index + start.y()
 *  z = direction.z() * index + start.z()
 * }</pre>
 * As {@code direction} is a unit vector, points of the segment correspond to {@code 0 <= index <= length}.
 */
public class Segment3d implements Bounded3d {
    private final WorldCoords3d start;
    private final WorldCoords3d end;
    private final WorldBBox3d bbox;
    private final Vector3d direction;
    private final double length;

    /**
     * Creates a new zero length segment but with a direction.
     * <p>
     * This sound weird but for some computation we need to asign direction to empty segments {@see com.ignfab.minalac.generator.tasks.RenderRoofTask}.
     *
     * @param pos start and end of segment
     * @param direction direction of that segment
     */

    public Segment3d(WorldCoords3d pos, Vector3d direction) {
        this.start = pos;
        this.end = pos;
        bbox = new WorldBBox3d(pos);
        length = 0;
        this.direction = direction;
    }

    /**
     * Creates a new segment between the given start and end points.
     *
     * @param start starting point of the segment
     * @param end ending point of the segment
     */
    public Segment3d(WorldCoords3d start, WorldCoords3d end) {
        this.start = start;
        this.end = end;
        bbox = new WorldBBox3d(start, end);

        if (start.equals(end)) {
            length = 0;
            direction = Vector3d.ZERO;
        } else {
            length = Vector3d.length(end.x() - start.x(), end.y() - start.y(), end.z() - start.z());
            direction = new Vector3d((end.x() - start.x()) / length, (end.y() - start.y()) / length,  (end.z() - start.z()) / length);
        }
    }

    /**
     * {@return the starting point of the segment}
     */
    public WorldCoords3d start() {
        return start;
    }

    /**
     * {@return the ending point of the segment}
     */
    public WorldCoords3d end() {
        return end;
    }

    /**
     * Returns segment direction unit vector or {@code Vector3d.ZERO} if segment is a single point.
     *
     * @return Segment direction unit vector.
     */
    public Vector3d direction() {
        return direction;
    }

    /**
     * {@return length of line segment in voxels along line axis}
     */
    public double length() {
        return length;
    }

    /**
     * Computes the coordinate of the point on the segment's line at the given index.
     *
     * @param index the index in the segment's line (can be outside segment).
     * @return the voxel coordinate for this index.
     */
    public WorldCoords3d atIndex(double index) {
        return WorldCoords3d.round(
            direction.x() * index + start.x(),
            direction.y() * index + start.y(),
            direction.z() * index + start.z()
        );
    }

    /**
     * Projects this segment on X-Y plane as a new {@link Segment2d}.
     *
     * @return projected 2d segment
     */
    public Segment2d to2d() {
        return new Segment2d(start.to2d(), end.to2d());
    }

    @Override
    public WorldBBox3d bbox() {
        return bbox;
    }

    @Override
    public String toString() {
        return "%s[start=%s, end=%s]".formatted(getClass().getSimpleName(), start, end);
    }

    @Override
    public int hashCode() {
        return 31 * start.hashCode() + end.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Segment3d that = (Segment3d) o;
        return start.equals(that.start) && end.equals(that.end);
    }
}

