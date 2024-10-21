package com.ignfab.minalac.generator.inputs;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.utils.iterator.Iterators;
import org.citygml4j.cityjson.CityJSONContext;
import org.citygml4j.cityjson.CityJSONContextException;
import org.citygml4j.cityjson.reader.CityJSONInputFactory;
import org.citygml4j.cityjson.reader.CityJSONReadException;
import org.citygml4j.cityjson.reader.CityJSONReader;
import org.citygml4j.core.model.core.AbstractCityObject;
import org.citygml4j.core.model.core.AbstractCityObjectProperty;
import org.citygml4j.core.model.core.CityModel;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.xmlobjects.gml.model.geometry.DirectPosition;
import org.xmlobjects.gml.model.geometry.Envelope;

import java.io.File;
import java.util.Iterator;

// TODO Handle CRS
public class CityJSONDataProvider implements Provider<AbstractCityObject> {
    private final File file;
    private final ReferencedEnvelope envelope;

    public CityJSONDataProvider(File file, ReferencedEnvelope envelope) {
        this.file = file;
        this.envelope = envelope;
    }

    @Override
    public CoordinateReferenceSystem crs() {
        return envelope.getCoordinateReferenceSystem();
    }

    @Override
    public Class<AbstractCityObject> providedType() {
        return AbstractCityObject.class;
    }

    @Override
    public Result<AbstractCityObject> provide() throws GenerationFailedException, RetryableException {
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

        return new CityResult(cityModel, new Envelope(
            new DirectPosition(envelope.getMinX(), envelope.getMinY()),
            new DirectPosition(envelope.getMaxX(), envelope.getMaxY())
        ));
    }

    private record CityResult(CityModel cityModel, Envelope cityEnvelope) implements Result<AbstractCityObject> {
        @Override
        public void close() {}

        @Override
        public Iterator<AbstractCityObject> iterator() {
            return Iterators.filter(
                Iterators.remap(cityModel.getCityObjectMembers().iterator(), AbstractCityObjectProperty::getObject),
                cityObject -> cityObject.getBoundedBy().getEnvelope().intersects(cityEnvelope)
            );
        }
    }
}
