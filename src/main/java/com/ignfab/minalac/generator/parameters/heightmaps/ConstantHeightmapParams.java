package com.ignfab.minalac.generator.parameters.heightmaps;

import java.beans.ConstructorProperties;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.heightmaps.ConstantHeightmap;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmap;

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
    public ReadableHeightmap create(Generation generation) {
        return new ConstantHeightmap(constant, generation.world().limits().to2d());
    }
}
