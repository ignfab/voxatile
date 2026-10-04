package fr.ign.voxatile.core.generation.heightmaps.computed;

import fr.ign.voxatile.core.generation.heightmaps.HeightmapStore;
import fr.ign.voxatile.core.generation.heightmaps.ReadableHeightmap;
import fr.ign.voxatile.core.generation.heightmaps.ReadableHeightmapSpec;
import fr.ign.voxatile.core.utils.world2d.WorldBBox2d;

/**
 * A readable heightmap that always returns the specified value.
 */
public class ConstantHeightmap extends ReadableHeightmapSpec implements ReadableHeightmap {
    private final int value;

    /**
     * Creates a new {@code ConstantHeightmap}.
     *
     * @param value the constant value
     */
    public ConstantHeightmap(int value) {
        this.value = value;
    }

    @Override
    public int get(int x, int y) {
        return value;
    }

    @Override
    public WorldBBox2d bbox() {
        return WorldBBox2d.INFINITE;
    }

    @Override
    protected ReadableHeightmap create(HeightmapStore store) {
        // This heightmap is its own spec (its instance will always be the same regardless of the context).
        return this;
    }
}
