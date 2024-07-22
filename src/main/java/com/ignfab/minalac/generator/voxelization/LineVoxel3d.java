package com.ignfab.minalac.generator.voxelization;

import com.ignfab.minalac.generator.utils.shape3d.Line3d;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

/**
 * A 3d voxel from a line with additional information about this line.
 */
public class LineVoxel3d implements Voxel3d {
    private WorldCoords3d coords;
    private Line3d line;
    private int index;

    /**
     * Create a line voxel without line information.
     *
     * @param coords position of the voxel
     */
    public LineVoxel3d(WorldCoords3d coords) {
        this.coords = coords;
        this.line = null;
        this.index = 0;
    }

    /**
     * Create a line voxel with line information.
     * Voxel position is computed from this information.
     *
     * @param line line which voxel comes from
     * @param index index of the voxel in that line
     */
    public LineVoxel3d(Line3d line, int index) {
        this.coords = line.atIndex(index);
        this.line = line;
        this.index = index;
    }

    /**
     * Returns index of the voxel in the line.
     *
     * @return index of the voxel
     */
    public int index() {
        return index;
    }

    /**
     * Returns line which the voxel comes from.
     *
     * @return line which the voxel comes from
     */
    public Line3d line() {
        return line;
    }

    @Override
    public WorldCoords3d coords() {
        return coords;
    }
}
