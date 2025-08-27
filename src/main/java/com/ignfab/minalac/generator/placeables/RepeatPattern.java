package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * A pattern that repeats a {@link PlaceableStructure}.
 */
public class RepeatPattern implements Pattern {
    private final PlaceableStructure structure;
    private final int xdy;
    private final int xdz;
    private final int ydx;
    private final int ydz;
    private final int zdx;
    private final int zdy;

    private final int minX;
    private final int minY;
    private final int minZ;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;

    /**
     * Create a new {@code RepeatPattern}.
     *
     * @param structure what to place
     * @param xdy Y-axis shift for each X-axis change
     * @param xdz Z-axis shift for each X-axis change
     * @param ydx X-axis shift for each Y-axis change
     * @param ydz Z-axis shift for each y-axis change
     * @param zdx X-axis shift for each Z-axis change
     * @param zdy Y-axis shift for each Z-axis change
     */
    public RepeatPattern(
        PlaceableStructure structure,
        int xdy,
        int xdz,
        int ydx,
        int ydz,
        int zdx,
        int zdy) {
        if (structure.limits().isEmpty()) {
            this.structure = new PlaceableStructure();
            this.structure.set(new WorldCoords3d(0, 0, 0), Nothing.INSTANCE);
        } else {
            this.structure = structure;
        }

        this.xdy = xdy;
        this.xdz = xdz;
        this.ydx = ydx;
        this.ydz = ydz;
        this.zdx = zdx;
        this.zdy = zdy;

        minX = structure.limits().minX();
        minY = structure.limits().minY();
        minZ = structure.limits().minZ();
        sizeX = structure.limits().sizeX();
        sizeY = structure.limits().sizeY();
        sizeZ = structure.limits().sizeZ();
    }

    @Override
    public Placeable get(int x, int y, int z) {
        int nx = Math.floorDiv(x, sizeX);
        int ny = Math.floorDiv(y, sizeY);
        int nz = Math.floorDiv(z, sizeZ);

        return structure.get(
            minX + Math.floorMod(x - minX + xdy * ny + xdz * nz, sizeX),
            minY + Math.floorMod(y - minY + ydx * nx + ydz * nz, sizeY),
            minZ + Math.floorMod(z - minZ + zdx * nx + zdy * ny, sizeZ)
        );
    }
}
