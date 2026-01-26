package com.ignfab.minalac.generator.parameters.heightmaps;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclarationStore;
import com.ignfab.minalac.generator.generation.heightmaps.ReadableHeightmapSpec;
import com.ignfab.minalac.generator.generation.heightmaps.computed.UnaryOperationHeightmapSpec;
import com.ignfab.minalac.generator.generation.heightmaps.computed.operators.ConvolutionHeightmapOperator;

/**
 * Parameters for a {@link ConvolutionHeightmapOperator.Bilateral} {@link UnaryOperationHeightmapSpec}.
 */
public class BilateralConvolutionHeightmapParams implements ReadableHeightmapParams {
    /**
     * The base heightmap (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ReadableHeightmapParams convolutionBilateral;
    /**
     * The convolution kernel (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public double[][] kernel;
    /**
     * The bilateral filtering range (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public double range;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param convolutionBilateral the base heightmap.
     * @param kernel the convolution kernel.
     * @param range the bilateral filtering range.
     */
    @ConstructorProperties({ "convolutionBilateral", "kernel", "range" })
    public BilateralConvolutionHeightmapParams(ReadableHeightmapParams convolutionBilateral, double[][] kernel, double range) {
        this.convolutionBilateral = convolutionBilateral;
        this.kernel = kernel;
        this.range = range;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        convolutionBilateral.validate();
        if (kernel.length % 2 == 0)
            throw new IllegalArgumentException("Invalid kernel (empty or not odd)");
        for (double[] row : kernel)
            if (row.length != kernel.length)
                throw new IllegalArgumentException("Invalid kernel length (not a square)");
        if (range <= 0)
            throw new IllegalArgumentException("Invalid range (not positive)");
    }

    @Override
    public ReadableHeightmapSpec create(HeightmapDeclarationStore store) {
        return new UnaryOperationHeightmapSpec(
            convolutionBilateral.create(store),
            new ConvolutionHeightmapOperator.Bilateral(kernel, range)
        );
    }
}
