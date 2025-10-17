package com.ignfab.minalac.generator.models.filters;

import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.models.JTSGeometryModel;
import com.ignfab.minalac.generator.models.Model;
import com.ignfab.minalac.generator.models.TestingModel;
import com.ignfab.minalac.generator.utils.coordinates.TestingConverter;

import static org.junit.jupiter.api.Assertions.*;

public class ModelFilterEmptyGeometryTest {
    private static final GeometryFactory FACTORY = new GeometryFactory();

    @Test
    public void testIsSelected() throws TransformException {
        Model geometryModel = new JTSGeometryModel(FACTORY.createPolygon(new Coordinate[] {
            new Coordinate(0, 0),
            new Coordinate(0, 1),
            new Coordinate(1, 1),
            new Coordinate(1, 0),
            new Coordinate(0, 0)
        }), TestingConverter.IDENTITY);
        Model emptyGeometryModel = new JTSGeometryModel(FACTORY.createEmpty(2), TestingConverter.IDENTITY);
        Model other = new TestingModel();

        Predicate<Model> filter = ModelFilterEmptyGeometry.INSTANCE;
        assertFalse(filter.test(geometryModel));
        assertTrue(filter.test(emptyGeometryModel));
        assertFalse(filter.test(other));
    }
}
