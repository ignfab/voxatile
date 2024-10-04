package com.ignfab.minalac.generator.parameters.processors;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.PolymorphicParams;
import com.ignfab.minalac.generator.processors.Processor;

/**
 * Represents the parameters of a type of {@link Processor}.
 */
public abstract class ProcessorParams extends PolymorphicParams {
    /**
     * Creates the corresponding {@code Processor}.
     *
     * @param generation the generation context
     * @param layerCrs CRS of the layer to be processed
     * @return the resulting processor
     */
    public abstract Processor<?, ?> create(Generation generation, CoordinateReferenceSystem layerCrs);
}
