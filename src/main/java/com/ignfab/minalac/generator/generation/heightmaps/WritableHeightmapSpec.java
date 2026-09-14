package com.ignfab.minalac.generator.generation.heightmaps;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

/**
 * Spec for a writable {@link Heightmap}.
 * <p>
 * This is the specs for usage only, not for actual heightmap data creation.
 * Unlike computed heightmaps, writable (and so stored) heightmaps are not created on the fly.
 * Store should be populated first (it is done when instantiating a new {@link HeightmapStore}).
 * <p>
 * {@code WritableHeightmapSpec} is also a spec for corresponding {@link ReadableHeightmap} so
 * it can be stored the same way as other readable heightmap specs in {@link HeightmapStore}.
 */
public class WritableHeightmapSpec extends ReadableHeightmapSpec {
    @Override
    protected WritableHeightmap create(HeightmapStore store) {
        // We will get here if we try to reach an unknown readable stored heightmap
        throw new IndexOutOfBoundsException("Stored heightmap not found");
    }

    @Override
    public AreaNeeds neededAreas(WorldBBox2d area) {
        // We suppose writable heightmaps needs only its own area
        // TODO: Clarify that comment!
        return new AreaNeeds(this, area);
    }
}
