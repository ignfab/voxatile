package com.ignfab.minalac.generator.inputs;

import java.io.File;
import java.util.List;

import org.citygml4j.cityjson.CityJSONContext;
import org.citygml4j.cityjson.CityJSONContextException;
import org.citygml4j.cityjson.reader.CityJSONInputFactory;
import org.citygml4j.cityjson.reader.CityJSONReadException;
import org.citygml4j.cityjson.reader.CityJSONReader;
import org.citygml4j.core.model.core.AbstractCityObject;
import org.citygml4j.core.model.core.AbstractCityObjectProperty;
import org.citygml4j.core.model.core.CityModel;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.xmlobjects.gml.model.geometry.DirectPosition;
import org.xmlobjects.gml.model.geometry.Envelope;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.coordinates.EnvelopeProvider;
import com.ignfab.minalac.generator.utils.iterator.Iterators;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

public class CityJSONDataProvider implements Provider<AbstractCityObject> {
    private final File file;
    private final CoordinateReferenceSystem crsOverride;
    private final EnvelopeProvider envelopeProvider;

    public CityJSONDataProvider(File file, CoordinateReferenceSystem crsOverride, EnvelopeProvider envelopeProvider) {
        this.file = file;
        this.crsOverride = crsOverride;
        this.envelopeProvider = envelopeProvider;
    }

    @Override
    public Class<AbstractCityObject> providedType() {
        return AbstractCityObject.class;
    }

    @Override
    public Result<AbstractCityObject> provide(WorldBBox3d bbox) throws GenerationFailedException, RetryableException {

        CityJSONContext context;
        try {
            context = CityJSONContext.newInstance();
        } catch (CityJSONContextException e) {
            throw new GenerationFailedException(e);
        }

        CityJSONInputFactory in = context.createCityJSONInputFactory();

        CityModel cityModel;
        try (CityJSONReader reader = in.createCityJSONReader(file)) {
            cityModel = (CityModel) reader.next();
        } catch (CityJSONReadException e) {
            throw new RetryableException(e);
        }

        CoordinateReferenceSystem crs = crsOverride;
        if (crs == null)
            try {
                crs = CRS.decode(cityModel.getBoundedBy().getEnvelope().getSrsName());
            } catch (FactoryException e) {
                throw new GenerationFailedException(e);
            }

        ReferencedEnvelope envelope;
        try {
            envelope = envelopeProvider.computeForCRS(crs, bbox);
        } catch (FactoryException | TransformException e) {
            throw new GenerationFailedException(e);
        }

        final Envelope cityEnvelope = new Envelope(
                    new DirectPosition(envelope.getMinX(), envelope.getMinY()),
                    new DirectPosition(envelope.getMaxX(), envelope.getMaxY())
        );

        return new SimpleResult<AbstractCityObject>(
            crs,
            Iterators.filter(
                Iterators.remap(
                    cityModel.getCityObjectMembers().iterator(),
                    AbstractCityObjectProperty::getObject
                ),
                object -> object.getBoundedBy().getEnvelope().intersects(cityEnvelope)
            ),
            List.of()
        );
    }
}
