package com.ignfab.minalac.generator.renderers;

import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;
import com.ignfab.minalac.generator.world.VoxelType;

/**
 * {@code WaterRenderer} renders a basic water surface using ground and water heightmaps.
 */
public class WaterRenderer implements Renderer {
    private final Heightmap groundHeightmap;
    private final Heightmap waterHeightmap;
    private final VoxelType water;
    private final VoxelType air;

    /**
     * Creates a new {@code WaterRenderer}.
     *
     * @param groundHeightmap heightmap of the ground
     * @param waterHeightmap heightmap of the water
     * @param water the voxel used to represent water
     * @param air the voxel used to represent air (typically the small gap between the water source and ground surface)
     */
    public WaterRenderer(Heightmap groundHeightmap, Heightmap waterHeightmap, VoxelType water, VoxelType air) {
        this.groundHeightmap = groundHeightmap;
        this.waterHeightmap = waterHeightmap;
        this.water = water;
        this.air = air;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(WorldBBox3d bbox) {
        //HeightmapUtils.applyAverageFilter(waterHeightmap);
        for (WorldCoords2d c : waterHeightmap.bbox().intersection(bbox)) {
            int waterValue = waterHeightmap.get(c);
            if (waterValue > 0) {
                for (int p = 1; p < waterValue; p++)
                    air.place(c.x(), c.y(), groundHeightmap.get(c) - p);
                // Gap between water and ground surface
                air.place(c.x(), c.y(), groundHeightmap.get(c));
                water.place(c.x(), c.y(), groundHeightmap.get(c) - waterValue);
            }
        }
    }
}
