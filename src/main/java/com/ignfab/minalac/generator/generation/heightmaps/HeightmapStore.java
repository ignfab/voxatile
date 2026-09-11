package com.ignfab.minalac.generator.generation.heightmaps;

import java.util.HashMap;
import java.util.Map;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

/**
 * A store of heightmaps, indexed by their specs.
 */
public class HeightmapStore {

    private final HeightmapDeclarationStore declarations;

    private final Map<WritableHeightmapSpec, WorldBBox2d> limits = new HashMap<>();

    /**
     * Known heightmaps indexed by their specs.
     *
     * (protected visibility required for tests)
     */
    protected final Map<ReadableHeightmapSpec, ReadableHeightmap> heightmaps = new HashMap<>();

    /**
     * Creates a new {@code HeightmapStore} for given {@link HeightmapDeclaration}.
     *
     * @param declarations A store of heightmap declarations
     * @param bbox The 2d bbox of created heightmaps
     */
    public HeightmapStore(HeightmapDeclarationStore declarations, WorldBBox2d bbox) {
        this.declarations = declarations;

        declarations.declarations().forEach((declaration) -> {
            this.limits.put(declaration.spec(), bbox);
        });
    }

    private WritableHeightmap instanciate(WritableHeightmapSpec spec) {
        WritableHeightmap heightmap;
        HeightmapDeclaration declaration = declarations.get(spec);
        if (declaration == null)
            throw new IndexOutOfBoundsException("No writable heightmap corresponding to this spec");

        heightmap = declaration.create(limits.get(spec));
        heightmaps.put(declaration.spec(), heightmap);

        return heightmap;
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
                if (spec instanceof WritableHeightmapSpec writableSpec) {
                    heightmap = instanciate(writableSpec);
                } else {
                    heightmap = spec.create(this);
                    heightmaps.put(spec, heightmap);
                }
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
            if (heightmap == null)
                heightmap = instanciate(spec);
        }

        // ReadableHeightmap associated to a WritableHeightmapSpec is always a WritableHeightmap
        return (WritableHeightmap) heightmap;
    }
}
