package com.ignfab.minalac.generator.models;

import org.geotools.referencing.operation.transform.IdentityTransform;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.util.AffineTransformation;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.inputs.FloatGeographicDataMatrix2d;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

import static org.junit.jupiter.api.Assertions.*;

public class FloatMatrixModelTest {
    @Test
    public void testBBox() throws TransformException {

        FloatGeographicDataMatrix2d data = new FloatGeographicDataMatrix2d(3, 4, 1.0, 2.0, 1.0, 1.0);

        MapToWorldConverter converter = new MapToWorldConverter(IdentityTransform.create(2), new AffineTransformation());
        FloatMatrixModel model = new FloatMatrixModel(data, converter);

        assertEquals(new WorldBBox2d(1, 2, 3, 4), model.bbox());
    }

    @Test
    public void testGet() throws TransformException {
        // Identity converter
        MapToWorldConverter converter = new MapToWorldConverter(IdentityTransform.create(2), new AffineTransformation());
        // Beware, Y is upside down in this matrix
        float[] values = {
            9.0f, 0.0f, 13.0f,
            7.0f, 0.0f, 0.0f,
            4.0f, 0.0f, 0.0f,
            1.0f, 2.0f, 3.0f
        };

        FloatGeographicDataMatrix2d data = new FloatGeographicDataMatrix2d(values, 3, 4, 1.0, 2.0, 1.0, 1.0);

        FloatMatrixModel model = new FloatMatrixModel(data, converter);
        assertNull(model.get(new WorldCoords2d(0, 2)));
        assertEquals(1, model.get(new WorldCoords2d(1, 2)));
        assertEquals(2, model.get(new WorldCoords2d(2, 2)));
        assertEquals(3, model.get(new WorldCoords2d(3, 2)));
        assertNull(model.get(new WorldCoords2d(4, 2)));
        assertNull(model.get(new WorldCoords2d(1, 1)));
        assertEquals(1, model.get(new WorldCoords2d(1, 2)));
        assertEquals(4, model.get(new WorldCoords2d(1, 3)));
        assertEquals(7, model.get(new WorldCoords2d(1, 4)));
        assertEquals(9, model.get(new WorldCoords2d(1, 5)));
        assertNull(model.get(new WorldCoords2d(1, 6)));
        assertEquals(13, model.get(new WorldCoords2d(3, 5)));
    }

    @Test
    public void testGetInterpolation() throws TransformException {
        MapToWorldConverter converter = new MapToWorldConverter(IdentityTransform.create(2), new AffineTransformation());
        // Beware, Y is upside down in this matrix
        float[] values = {
            -1.0f, 1.0f,
            -3.0f, 5.0f,
        };
        FloatGeographicDataMatrix2d data = new FloatGeographicDataMatrix2d(values, 2, 2, 0.0, 0.0, 10.0, 10.0);

        FloatMatrixModel model = new FloatMatrixModel(data, converter);
        // Borders
        assertNull(model.get(new WorldCoords2d(0, 11)));
        assertNull(model.get(new WorldCoords2d(11, 0)));
        assertEquals(-3.0f, model.get(new WorldCoords2d(0, 0)));
        assertEquals(-1.0f, model.get(new WorldCoords2d(0, 10)));
        assertEquals(5.0f, model.get(new WorldCoords2d(10, 0)));
        assertEquals(1.0f, model.get(new WorldCoords2d(10, 10)));
        // Centers
        assertEquals(1.0f, model.get(new WorldCoords2d(5, 0)));
        assertEquals(0.0f, model.get(new WorldCoords2d(5, 10)));
        assertEquals(-2.0f, model.get(new WorldCoords2d(0, 5)));
        assertEquals(3.0f, model.get(new WorldCoords2d(10, 5)));
        assertEquals(0.5f, model.get(new WorldCoords2d(5, 5)));
    }
}
