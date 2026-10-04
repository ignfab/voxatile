package fr.ign.voxatile.core.parameters.heightmaps;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.generation.heightmaps.HeightmapDeclarationStore;
import fr.ign.voxatile.core.generation.heightmaps.ReadableHeightmapSpec;
import fr.ign.voxatile.core.generation.heightmaps.computed.UnaryOperationHeightmapSpec;
import fr.ign.voxatile.core.generation.heightmaps.computed.operators.LocalMinimumHeightmapOperator;

/**
 * Parameters for a {@link LocalMinimumHeightmapOperator} {@link UnaryOperationHeightmapSpec}.
 */
public class LocalMinimumHeightmapParams implements ReadableHeightmapParams {
    /**
     * The base heightmap (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ReadableHeightmapParams localMin;
    /**
     * The local minimum range (required).
     */
    public int range;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param localMin the base heightmap.
     * @param range the local minimum range.
     */
    @ConstructorProperties({"localMin", "range"})
    public LocalMinimumHeightmapParams(ReadableHeightmapParams localMin, int range) {
        this.localMin = localMin;
        this.range = range;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        localMin.validate();
        if (range < 0)
            throw new IllegalArgumentException("range can not be negative");
    }

    @Override
    public ReadableHeightmapSpec create(HeightmapDeclarationStore store) {
        return new UnaryOperationHeightmapSpec(
            localMin.create(store),
            new LocalMinimumHeightmapOperator(range)
        );
    }
}
