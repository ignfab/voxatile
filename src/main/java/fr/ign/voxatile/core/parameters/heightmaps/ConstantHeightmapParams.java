package fr.ign.voxatile.core.parameters.heightmaps;

import java.beans.ConstructorProperties;

import fr.ign.voxatile.core.generation.heightmaps.HeightmapDeclarationStore;
import fr.ign.voxatile.core.generation.heightmaps.ReadableHeightmapSpec;
import fr.ign.voxatile.core.generation.heightmaps.computed.ConstantHeightmap;

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
    public void validate() {}

    @Override
    public ReadableHeightmapSpec create(HeightmapDeclarationStore store) {
        return new ConstantHeightmap(constant);
    }
}
