package com.ignfab.minalac.generator.models.filters;

import java.util.function.Predicate;

import org.geotools.referencing.operation.transform.IdentityTransform;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.util.AffineTransformation;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.models.JTSGeometryModel;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;

import static org.junit.jupiter.api.Assertions.*;

public class ModelFilterEmptyGeometryTest {
    private static final MapToWorldConverter IDENTITY_CONVERTER = new MapToWorldConverter(IdentityTransform.create(2), new AffineTransformation());
    private static final GeometryFactory FACTORY = new GeometryFactory();

    @Test
    public void testIsSelected() throws TransformException {
        Model geometryModel = new JTSGeometryModel(FACTORY.createPolygon(new Coordinate[] {
            new Coordinate(0, 0),
            new Coordinate(0, 1),
            new Coordinate(1, 1),
            new Coordinate(1, 0),
            new Coordinate(0, 0)
        }), IDENTITY_CONVERTER);
        Model emptyGeometryModel = new JTSGeometryModel(FACTORY.createEmpty(2), IDENTITY_CONVERTER);
        Model other = new TestingModel();

        Predicate<Model> filter = ModelFilterEmptyGeometry.INSTANCE;
        assertFalse(filter.test(geometryModel));
        assertTrue(filter.test(emptyGeometryModel));
        assertFalse(filter.test(other));
    }
}
