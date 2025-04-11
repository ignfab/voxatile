package com.ignfab.minalac.generator.generation.heightmaps;

import com.ignfab.minalac.generator.generation.GenerationTile;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

/**
 * An unbound 2d readable and writeable heightmap (in voxel world units) that should be bound to a {@link GenerationTile} before being used.
 *
 * This is also a template for creating underlying data containing {@link Heightmap} object.
 *
 * TODO: Mixing both roles is quite bad and should be improved by separating them in different interfaces/classes.
 * TODO: Nothing ensures given name corresponds to the store key (should always be)
 */
public class UnboundHeightmap implements UnboundReadableHeightmap {
    private String name;
    private int defaultValue;

    /**
     * Creates a new {@code UnboundHeightmap}.
     *
     * @param name Name of corresponding heightmap in heightmap store (refers to "unbound" role)
     * @param defaultValue Default value of the heightmap when instanciating (refers to "template" role)
     */
    public UnboundHeightmap(String name, int defaultValue) {
        this.name = name;
    }

    @Override
    public Heightmap bind(GenerationTile tile) {
        return tile.heightmaps().get(name);
    }

    /**
     * Creates a {@code Heightmap} from given specification (only default value for now but we could also have margins later).
     *
     * @param bbox Bounding box of the heightmap to instanciate
     * @return created heightmap
     */
    public Heightmap createHeightmap(WorldBBox2d bbox) {
        return new Heightmap(bbox, defaultValue);
    }
}

