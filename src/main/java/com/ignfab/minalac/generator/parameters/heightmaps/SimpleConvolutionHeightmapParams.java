package com.ignfab.minalac.generator.parameters.heightmaps;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclarationStore;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.generation.heightmaps.computed.UnaryOperationHeightmapSpec;
import com.ignfab.minalac.generator.generation.heightmaps.computed.operators.ConvolutionHeightmapOperator;

/**
 * Parameters for a {@link ConvolutionHeightmapOperator.Simple} {@link UnaryOperationHeightmapSpec}.
 */
public class SimpleConvolutionHeightmapParams implements ReadableHeightmapParams {
    /**
     * The base heightmap (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ReadableHeightmapParams convolution;
    /**
     * The convolution kernel (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public double[][] kernel;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param convolution the base heightmap.
     * @param kernel the convolution kernel.
     */
    @ConstructorProperties({ "convolution", "kernel" })
    public SimpleConvolutionHeightmapParams(ReadableHeightmapParams convolution, double[][] kernel) {
        this.convolution = convolution;
        this.kernel = kernel;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        convolution.validate();
        if (kernel.length % 2 == 0)
            throw new IllegalArgumentException("Invalid kernel (empty or not odd)");
        for (double[] row : kernel)
            if (row.length != kernel.length)
                throw new IllegalArgumentException("Invalid kernel length (not a square)");
    }

    @Override
    public ReadableHeightmapSpec create(HeightmapDeclarationStore store) {
        return new UnaryOperationHeightmapSpec(
            convolution.create(store),
            new ConvolutionHeightmapOperator.Simple(kernel)
        );
    }
}
