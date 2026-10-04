package fr.ign.voxatile.core.parameters.processors;

import org.geotools.api.feature.simple.SimpleFeature;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.models.JTSGeometryModel;
import fr.ign.voxatile.core.processors.GeoToolsVectorProcessor;
import fr.ign.voxatile.core.processors.Processor;

/**
 * Parameters for GeoTools vector processors.
 */
public class GeoToolsVectorProcessorParams extends ProcessorParams {
    @Override
    public Processor<SimpleFeature, JTSGeometryModel> create(Generation generation) {
        return new GeoToolsVectorProcessor(generation::makeCoordsConverter);
    }
}
