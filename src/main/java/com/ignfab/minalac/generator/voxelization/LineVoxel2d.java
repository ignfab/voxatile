package com.ignfab.minalac.generator.voxelization;

import com.ignfab.minalac.generator.utils.shape2d.Line2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

/**
 * A 2d voxel from a line with additional information about this line.
 */
public class LineVoxel2d implements Voxel2d {
    private WorldCoords2d coords;
    private Line2d line;
    private int index;

    /**
     * Create a line voxel without line information.
     *
     * @param coords position of the voxel
     */
    public LineVoxel2d(WorldCoords2d coords) {
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
    public LineVoxel2d(Line2d line, int index) {
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
    public Line2d line() {
        return line;
    }

    @Override
    public WorldCoords2d coords() {
        return coords;
    }
}
