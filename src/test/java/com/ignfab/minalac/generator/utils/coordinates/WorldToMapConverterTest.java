package com.ignfab.minalac.generator.utils.coordinates;


import java.awt.geom.AffineTransform;

import org.geotools.referencing.operation.transform.AffineTransform2D;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WorldToMapConverterTest {

    @Test
    public void testConvert() throws TransformException {
        // Simple since this converter is made via the constructor.
        // Testing of the proper inversion of MapToWorldConverter into WorldToMapConverter is done in MapToWorldConverterTest
        WorldToMapConverter converter = new WorldToMapConverter(new AffineTransform2D(AffineTransform.getRotateInstance(Math.PI / 2)));
        assertEquals(new MapCoordinates2d(2, 1), converter.convert(new WorldCoords2d(1, -2)));
        assertEquals(new Coordinate(1.5, 1), converter.convert(new Coordinate(1, -1.5)));
    }
}
