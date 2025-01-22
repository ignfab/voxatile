package com.ignfab.minalac.generator.placeables;

import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code VoxelStructurePlacer} is the {@link Placer} for {@code VoxelStructure}.
 */
public class VoxelStructurePlacer implements Placer {
    private final Map<WorldCoords3d, Placer> placers = new HashMap<>();

    /**
     * Adds a placer to this structure placer at a given position.
     *
     * @param position the relative position of the placer
     * @param placer the placer to be added
     */
    public void set(WorldCoords3d position, Placer placer) {
        placers.put(position, placer);
    }

    @Override
    public void place(int x, int y, int z) {
        placers.forEach((c, placer) -> placer.place(c.x() + x, c.y() + y, c.z() + z));
    }
}
