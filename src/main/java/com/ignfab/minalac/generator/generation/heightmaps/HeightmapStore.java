package com.ignfab.minalac.generator.generation.heightmaps;

import java.util.HashMap;
import java.util.Map;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

/**
 * A store of heightmaps, indexed by their specs.
 */
public class HeightmapStore {
    /**
     * Known heightmaps indexed by their specs.
     *
     * (protected visibility required for tests)
     */
    protected final Map<ReadableHeightmapSpec, ReadableHeightmap> heightmaps = new HashMap<>();

    /**
     * Creates a new {@code HeightmapStore} populated with stored heightmaps created from given {@link HeightmapDeclaration}.
     *
     * @param heightmaps A store of heightmap declarations
     * @param bbox The 2d bbox of created heightmaps
     */
    public HeightmapStore(HeightmapDeclarationStore heightmaps, WorldBBox2d bbox) {
        heightmaps.declarations().forEach((declaration) -> {
            this.heightmaps.put(declaration.spec(), declaration.create(bbox));
        });
    }

    /**
     * Retrieves or creates a {@link ReadableHeightmap} corresponding to {@code ReadableHeightmapSpec}.
     * Created heightmap will be stored for later use.
     *
     * @param spec Specification of heightmap to get.
     * @return resulting {@link ReadableHeightmap}
     */
    public ReadableHeightmap get(ReadableHeightmapSpec spec) {
        ReadableHeightmap heightmap;
        synchronized (heightmaps) {
            heightmap = heightmaps.get(spec);
            if (heightmap == null) {
                heightmap = spec.create(this);
                heightmaps.put(spec, heightmap);
            }
        }
        return heightmap;
    }

    /**
     * Retrieves a {@link Heightmap} corresponding to {@code WritableHeightmapSpec}.
     *
     * @param spec Specification of stored heightmap to get.
     * @return resulting {@link Heightmap}
     */
    public WritableHeightmap get(WritableHeightmapSpec spec) {
        ReadableHeightmap heightmap;
        synchronized (heightmaps) {
            heightmap = heightmaps.get(spec);
        }
        if (heightmap == null)
            throw new IndexOutOfBoundsException("Writable heightmap not found");

        // ReadableHeightmap associated to a WritableHeightmapSpec is always a WritableHeightmap
        return (WritableHeightmap) heightmap;
    }
}
