package com.ignfab.minalac.generator.processors;

import com.ignfab.minalac.generator.models.JTSGeometryModel;
import com.ignfab.minalac.generator.utils.coordinates.MapToWorldConverter;
import com.ignfab.minalac.generator.utils.iterator.Iterables;
import com.ignfab.minalac.generator.utils.world2d.Positioned2d;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import com.ignfab.minalac.generator.utils.world2d.WorldCoords2d;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.transform.IdentityTransform;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.util.AffineTransformation;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.assertBrowsesAllOnce;
import static org.junit.jupiter.api.Assertions.*;

public class GeoToolsVectorProcessorTest {
    private static final MapToWorldConverter IDENTITY_CONVERTER = new MapToWorldConverter(IdentityTransform.create(2), new AffineTransformation());

    @Test
    public void test() throws SchemaException, FactoryException {
        CoordinateReferenceSystem crs2154 = CRS.decode("EPSG:2154");
        AtomicBoolean initialized = new AtomicBoolean(false);
        GeoToolsVectorProcessor processor = new GeoToolsVectorProcessor(crs -> {
            initialized.set(crs == crs2154);
            return IDENTITY_CONVERTER;
        });
        assertDoesNotThrow(() -> processor.initialize(crs2154)); // layerCrs is only validated but not actually used by the converter provider we gave above
        assertTrue(initialized.get(), "processor initialized with given CRS");

        // Validate capabilities of processor
        assertTrue(processor.acceptedType().isAssignableFrom(SimpleFeature.class), "processor accepts SimpleFeature");
        assertTrue(JTSGeometryModel.class.isAssignableFrom(processor.modelType()), "processor produces JTSGeometryModel");

        // Construct dummy feature
        SimpleFeatureType featureType = DataUtilities.createType("FLAG", "id:Integer,name:String,*geom:Geometry:4326");
        SimpleFeature feature = DataUtilities.createFeature(featureType, "1|Dummy feature|POINT (1 2)");

        // Validate processing
        JTSGeometryModel model = assertDoesNotThrow(() -> processor.process(feature));

        // Validate metadata
        assertEquals(1, (int) model.getMetadata("id"));
        assertEquals("Dummy feature", model.getMetadata("name"));
        assertTrue(model.hasMetadata("geom"));

        // Validate geometry
        assertBrowsesAllOnce(
            List.of(new WorldCoords2d(1, 2)),
            Iterables.remap(
                model.voxelize2d(new WorldBBox2d(0, 0, 5, 5)),
                Positioned2d::coords
            )
        );
    }
}
