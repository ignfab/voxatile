package com.ignfab.minalac.generator.processors;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.utils.coordinates.CoordsConverterProvider;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;

public abstract class ConvertingProcessor<T, M extends Model> implements Processor<T, M> {
    private final CoordsConverterProvider converterProvider;
    protected MapToWorldConverter converter;

    public ConvertingProcessor(CoordsConverterProvider converterProvider) {
        this.converterProvider = converterProvider;
    }

    @Override
    public void initialize(CoordinateReferenceSystem layerCrs) throws GenerationFailedException {
        try {
            converter = converterProvider.computeForCRS(layerCrs);
        } catch (FactoryException e) {
            throw new GenerationFailedException("Could not find a converter from layer to generation CRS", e);
        }
    }
}
