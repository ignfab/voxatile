package com.ignfab.minalac.generator.parameters.heightmaps;

import java.beans.ConstructorProperties;

import com.ignfab.minalac.generator.generation.Store;
import com.ignfab.minalac.generator.generation.heightmaps.ConstantHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.UnboundHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.UnboundReadableHeightmap;

/**
 * Parameters for a {@link ConstantHeightmap}.
 */
public class ConstantHeightmapParams extends CustomReadableHeightmapParams {
    /**
     * The constant value (required).
     */
    public int constant;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param constant the constant value of the heightmap
     */
    @ConstructorProperties("constant")
    public ConstantHeightmapParams(int constant) {
        this.constant = constant;
    }

    @Override
    public void validate() {}

    @Override
    public UnboundReadableHeightmap create(Store<UnboundHeightmap> store) {
        return new ConstantHeightmap(constant);
    }
}
