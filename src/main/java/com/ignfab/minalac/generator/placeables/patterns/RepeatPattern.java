package com.ignfab.minalac.generator.placeables.patterns;

import com.ignfab.minalac.generator.placeables.Pattern;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;
import com.ignfab.minalac.generator.utils.world3d.WorldSize3d;

/**
 * A pattern that repeats a {@link PlaceableStructure}.
 */
public class RepeatPattern implements Pattern {
    private final PlaceableStructure structure;
    private final WorldCoords3d xd;
    private final WorldCoords3d yd;
    private final WorldCoords3d zd;

    private final WorldSize3d size;
    private final WorldBBox3d bbox;

    /**
     * Create a new {@code RepeatPattern}.
     *
     * @param structure what to repeat
     * @param xd X, Y or/and Z axes shift for each X-axis change
     * @param yd X, Y or/and Z axes shift for each Y-axis change
     * @param zd X, Y or/and Z axes shift for each Z-axis change
     */
    public RepeatPattern(
        PlaceableStructure structure,
        WorldCoords3d xd,
        WorldCoords3d yd,
        WorldCoords3d zd
    ) {
        this.structure = structure;
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;

        size = new WorldSize3d(
            structure.limits().sizeX() + xd.x(),
            structure.limits().sizeY() + yd.y(),
            structure.limits().sizeZ() + zd.z()
        );

        // BBox is the largest bbox of underlying placables.
        // Actual bbox depends on position but we have none.
        bbox = WorldBBox3d.surrounding(Iterables.remap(structure.limits(), (pos) -> structure.get(pos).bbox()));
    }

    @Override
    public Placeable get(int x, int y, int z) {
        // Structure index
        int nx = Math.floorDiv(x, size.x());
        int ny = Math.floorDiv(y, size.y());
        int nz = Math.floorDiv(z, size.z());

        WorldCoords3d min = structure.limits().min();
        // Map world coordinates to the corresponding position inside the structure
        return structure.get(
            min.x() + Math.floorMod(x - min.x() - yd.x() * ny - zd.x() * nz, size.x()),
            min.y() + Math.floorMod(y - min.y() - xd.y() * nx - zd.y() * nz, size.y()),
            min.z() + Math.floorMod(z - min.z() - xd.z() * nx - yd.z() * ny, size.z())
        );
    }

    @Override
    public WorldBBox3d bbox() {
        return bbox;
    }
}
