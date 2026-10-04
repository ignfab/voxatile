package fr.ign.voxatile.core.parameters.processors;

import org.geotools.api.referencing.FactoryException;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.inputs.OsmData;
import fr.ign.voxatile.core.models.JTSGeometryModel;
import fr.ign.voxatile.core.processors.OsmProcessor;
import fr.ign.voxatile.core.processors.Processor;

/**
 * Parameters for OSM processor.
 */
public class OsmProcessorParams extends ProcessorParams {
    @Override
    public Processor<OsmData, JTSGeometryModel> create(Generation generation) {
        try {
            return new OsmProcessor(generation.makeCoordsConverter(OsmData.CRS));
        } catch (FactoryException e) {
            throw new RuntimeException("Cannot convert OSM CRS to target CRS", e);
        }
    }
}
