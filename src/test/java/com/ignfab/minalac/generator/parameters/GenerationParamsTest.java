package com.ignfab.minalac.generator.parameters;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.outputs.minetest.MTVoxelWorld;
import com.ignfab.minalac.generator.parameters.placeables.minetest.MTVoxelTypeParams;
import com.ignfab.minalac.generator.parameters.processors.TestingProcessorParams;
import com.ignfab.minalac.generator.parameters.providers.TestingProviderParams;
import com.ignfab.minalac.generator.parameters.renderers.TestingRendererParams;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class GenerationParamsTest {
    private GenerationParams params;

    @BeforeEach
    void setUp() {
        GenerationParams.Area.LatitudeLongitude center = new GenerationParams.Area.LatitudeLongitude(5.8, 2.4);
        GenerationParams.Area area = new GenerationParams.Area(center, 500, 2500);
        OutputFormat format = new OutputFormat(MTVoxelWorld::new, MTVoxelTypeParams.class, MTVoxelTypeParams::new);
        params = new GenerationParams(area, format);
        params.heightmaps = new HashMap<>();
        params.renderers = new HashMap<>();
    }

    @Test
    public void testValidateValidParams() {
        assertDoesNotThrow(params::validate);
    }

    @Test
    public void testValidateVerticalScale() {
        params.verticalScale = 0.0;
        assertThrows(IllegalArgumentException.class, params::validate);

        params.verticalScale = -5.0;
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testValidateHorizontalScale() {
        params.horizontalScale = 0.0;
        assertThrows(IllegalArgumentException.class, params::validate);

        params.horizontalScale = -1.0;
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testValidateLatitude() {
        params.area.center.latitude = 90.0;
        assertDoesNotThrow(params::validate);

        params.area.center.latitude = 91.0;
        assertThrows(IllegalArgumentException.class, params::validate);

        params.area.center.latitude = -90.0;
        assertDoesNotThrow(params::validate);

        params.area.center.latitude = -91.0;
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testValidateLongitude() {
        params.area.center.longitude = 180.0;
        assertDoesNotThrow(params::validate);

        params.area.center.longitude = 181.0;
        assertThrows(IllegalArgumentException.class, params::validate);

        params.area.center.longitude = -180.0;
        assertDoesNotThrow(params::validate);

        params.area.center.longitude = -181.0;
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testValidateExtentX() {
        params.area.extentX = 0;
        assertThrows(IllegalArgumentException.class, params::validate);

        params.area.extentX = -500;
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testValidateExtentY() {
        params.area.extentY = 0;
        assertThrows(IllegalArgumentException.class, params::validate);

        params.area.extentY = -10;
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testCreate() throws ParseException {
        params.verticalScale = 3.0;
        params.horizontalScale = 4.0;
        params.crs = "EPSG:5643";
        params.heightmaps.put("ground", new HeightmapParams("0"));
        params.heightmaps.put("altitude", new HeightmapParams("minimal"));
        params.renderers.put("renderer1", new TestingRendererParams("value1"));
        params.renderers.put("renderer2", new TestingRendererParams("value2"));
        params.sources.put("source1", new DataSourceParams("models1", new TestingProviderParams("value3"), new TestingProcessorParams("value4")));
        params.sources.put("source2", new DataSourceParams("models2", new TestingProviderParams("value5"), new TestingProcessorParams("value6")));

        Generation generation = params.create();

        assertNotNull(generation);
        assertEquals(500, generation.world().limits().sizeX());
        assertEquals(2500, generation.world().limits().sizeY());
        assertEquals(3.0, generation.getVerticalScale(), 0.001);

        Heightmap ground = assertDoesNotThrow(() -> generation.heightmaps().get("ground"));
        assertEquals(0, ground.get(0, 0));

        Heightmap altitude = assertDoesNotThrow(() -> generation.heightmaps().get("altitude"));
        assertEquals(Integer.MIN_VALUE, altitude.get(0, 0));
    }
}
