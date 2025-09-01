package com.ignfab.minalac.generator.generation.heightmaps;

import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

/**
 * A stored heightmap declaration. Describes how a {@link Heightmap} should be created.
 */
public class HeightmapDeclaration {
    private final String name;
    private final int defaultValue;

    private final WritableHeightmapSpec spec = new WritableHeightmapSpec();

    /**
     * Creates a new {@code HeightmapDeclaration}.
     *
     * @param name Name of corresponding heightmap in heightmap store
     * @param defaultValue Default value of the heightmap when instantiating
     */
    public HeightmapDeclaration(String name, int defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the name of this declaration.
     *
     * @return name
     */
    public String name() {
        return name;
    }

    /**
     * Returns spec object to use as key in readable/writable heightmap stores in order to retrieve it.
     *
     * @return {@link WritableHeightmapSpec} object
     */
    public WritableHeightmapSpec spec() {
        return spec;
    }

    /**
     * Creates {@link WritableHeightmap} corresponding to this declaration for a given bounding box.
     * When margins will be implemented, the resulting heightmap bbox may be different from that given as argument.
     *
     * @param bbox Bounding box of the heightmap to instantiate
     * @return Created heightmap.
     */
    public WritableHeightmap create(WorldBBox2d bbox) {
        return new Heightmap(bbox, defaultValue);
    }
}

