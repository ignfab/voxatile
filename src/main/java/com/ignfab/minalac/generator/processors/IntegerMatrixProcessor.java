package com.ignfab.minalac.generator.processors;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.inputs.IntegerGeographicDataMatrix2d;
import com.ignfab.minalac.generator.models.IntegerMatrixModel;
import com.ignfab.minalac.generator.utils.coordinates.CoordsConverterProvider;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;

/**
 * Processor transforming {@link IntegerGeographicDataMatrix2d} into a
 * {@link IntegerMatrixModel}.
 */
public class IntegerMatrixProcessor implements Processor<IntegerGeographicDataMatrix2d, IntegerMatrixModel> {
    private final CoordsConverterProvider converterProvider;
    private MapToWorldConverter converter;

    /**
     * Creates a new {@code IntegerMatrixProcessor}.
     * @param converterProvider converter provider to transform coordinates from map to world
     */
    public IntegerMatrixProcessor(CoordsConverterProvider converterProvider) {
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

    @Override
    public Class<IntegerGeographicDataMatrix2d> acceptedType() {
        return IntegerGeographicDataMatrix2d.class;
    }

    @Override
    public Class<IntegerMatrixModel> modelType() {
        return IntegerMatrixModel.class;
    }

    @Override
    public IntegerMatrixModel process(IntegerGeographicDataMatrix2d matrix) throws IgnorableException {
        try {
            return new IntegerMatrixModel(matrix, converter);
        } catch (TransformException e) {
            throw new IgnorableException(e);
        }
    }
}
