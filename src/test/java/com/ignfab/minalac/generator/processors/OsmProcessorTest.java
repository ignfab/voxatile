package com.ignfab.minalac.generator.processors;

import java.util.List;

import de.topobyte.osm4j.core.dataset.InMemoryListDataSet;
import de.topobyte.osm4j.core.model.impl.Node;
import de.topobyte.osm4j.core.model.impl.Tag;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.feature.SchemaException;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.inputs.OsmData;
import com.ignfab.minalac.generator.models.JTSGeometryModel;
import com.ignfab.minalac.generator.utils.coordinates.TestingConverter;
import com.ignfab.minalac.generator.voxelization.shape2d.Point2d;
import com.ignfab.minalac.generator.voxelization.shape2d.Shape2d;
import com.ignfab.minalac.generator.voxelization.shape3d.Point3d;
import com.ignfab.minalac.generator.voxelization.shape3d.Shape3d;

import static org.junit.jupiter.api.Assertions.*;

public class OsmProcessorTest {

    @Test
    public void test() throws SchemaException, FactoryException {
        CoordinateReferenceSystem crs2154 = CRS.decode("EPSG:2154");
        CoordinateReferenceSystem crs4326 = CRS.decode("EPSG:4326");
        OsmProcessor processor = new OsmProcessor(TestingConverter.IDENTITY);

        assertThrows(GenerationFailedException.class, () -> processor.initialize(crs2154));
        assertDoesNotThrow(() -> processor.initialize(crs4326));

        // Validate capabilities of processor
        assertTrue(processor.acceptedType().isAssignableFrom(OsmData.class), "processor accepts OsmData");
        assertTrue(JTSGeometryModel.class.isAssignableFrom(processor.modelType()), "processor produces JTSGeometryModel");

        InMemoryListDataSet resolver = new InMemoryListDataSet();

        // Construct dummy node
        OsmData data = new OsmData(resolver, new Node(1, 2.3, 4.5, List.of(
            new Tag("name", "toto"),
            new Tag("job", "yes")
        )));

        // Validate processing
        JTSGeometryModel model = assertDoesNotThrow(() -> processor.process(data));

        // Validate metadata
        assertEquals("toto", model.getMetadata("name"));
        assertEquals("yes", model.getMetadata("job"));

        // Test 2d conversion (Could be improved checking shape contains only given point)
        Shape2d shape2d = assertDoesNotThrow(() -> model.toShape2d());
        assertInstanceOf(Point2d.class, shape2d);

        // Test 3d conversion (Could be improved checking shape contains only given point)
        Shape3d shape3d = assertDoesNotThrow(() -> model.toShape3d());
        assertInstanceOf(Point3d.class, shape3d);
    }
}
