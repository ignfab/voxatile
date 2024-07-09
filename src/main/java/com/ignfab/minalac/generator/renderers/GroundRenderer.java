package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.VoxelPattern;

/**
 * Ground renderer renders a basic ground using altitude from given heightmap.
 */
public class GroundRenderer {
    private final Heightmap heightmap;
    private final VoxelPattern pattern;

    /**
     * Creates a new GroundRenderer.
     *
     * @param heightmap heightmap of the ground
     * @param pattern voxel pattern to be placed at each voxel of the given heightmap
     */
    public GroundRenderer(Heightmap heightmap, VoxelPattern pattern) {
        this.heightmap = heightmap;
        this.pattern = pattern;
    }

    /**
     * Performs rendering.
     *
     * @param bbox the limits of the rendering area.
     */
    public void render(WorldBBox3d bbox) {
        for (WorldCoords2d c : heightmap.bbox().intersection(bbox))
            pattern.place(c.x(), c.y(), heightmap.get(c));
    }
}
