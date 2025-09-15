package com.ignfab.minalac.generator.parameters.heightmaps;

import java.beans.ConstructorProperties;

import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclarationStore;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.generation.heightmaps.computed.ConstantHeightmap;

/**
 * Parameters for a {@link ConstantHeightmap}.
 */
public class ConstantHeightmapParams implements ReadableHeightmapParams {
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
    public ReadableHeightmapSpec create(HeightmapDeclarationStore store) {
        return new ConstantHeightmap(constant);
    }
}
