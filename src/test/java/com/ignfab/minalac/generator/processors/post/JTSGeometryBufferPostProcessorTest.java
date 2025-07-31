package com.ignfab.minalac.generator.processors.post;

import org.geotools.referencing.operation.transform.IdentityTransform;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.util.AffineTransformation;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.models.JTSGeometryModel;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;

import static org.junit.jupiter.api.Assertions.*;

public class JTSGeometryBufferPostProcessorTest {
    private JTSGeometryModel model;

    private static final MapToWorldConverter IDENTITY_CONVERTER = new MapToWorldConverter(IdentityTransform.create(2), new AffineTransformation());
    private static final WKTReader WKT_READER = new WKTReader();

    @BeforeEach
    public void setUp() throws ParseException, TransformException {
        model = new JTSGeometryModel(WKT_READER.read("POLYGON ((0 0, 5 0, 5 5, 0 5, 0 0))"), IDENTITY_CONVERTER);
    }

    private JTSGeometryModel process(double buffer) {
        JTSGeometryModel processed = assertDoesNotThrow(() -> new JTSGeometryBufferPostProcessor(buffer).process(model));
        if (processed != null)
            assertSame(model, processed);
        return processed;
    }

    @Test
    @DisplayName("JTS geometry buffer post-processor with positive buffer distance")
    public void testPositive() {
        JTSGeometryModel processed = process(2);
        assertEquals(new Envelope(-2, 7, -2, 7), processed.getGeometry().getEnvelopeInternal());
    }

    @Test
    @DisplayName("JTS geometry buffer post-processor with negative buffer distance")
    public void testNegative() {
        JTSGeometryModel processed = process(-1);
        assertEquals(new Envelope(1, 4, 1, 4), processed.getGeometry().getEnvelopeInternal());
    }

    @Test
    @DisplayName("JTS geometry buffer post-processor resulting in empty geometry model")
    public void testKeep() {
        JTSGeometryModel processed = process(-3);
        assertNotNull(processed);
        assertTrue(processed.getGeometry().isEmpty());
    }
}
