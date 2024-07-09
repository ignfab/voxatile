package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.HeightMap;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world2d.iterator.Chunk2dElement;
import com.ignfab.minalac.generator.world.VoxelType;

/**
 * Ground renderer renders a basic ground using altitude from given heightmap.
 */
public class GroundRenderer {
    private final HeightMap heightMap;
    // This is to be replaced by patterns:
    private final VoxelType[] voxelTypes;

    /**
     * Creates a new GroundRenderer.
     *
     * @param heightMap Height map of the ground (where heights will be written)
     * @param voxelTypes TODO: Should be a "voxel model"
     */
    public GroundRenderer(HeightMap heightMap, VoxelType[] voxelTypes) {
        this.heightMap = heightMap;
        this.voxelTypes = voxelTypes;
    }

    /**
     * Performs rendering.
     */
    public void render() {
        // Iterate over height map and draw ground
        for (Chunk2dElement element : heightMap) {
            WorldCoords2d c = element.getCoords();
            int z = heightMap.get(c);
            for (VoxelType voxelType : voxelTypes)
                voxelType.place(c.x(), c.y(), z--);
        }
    }
}
