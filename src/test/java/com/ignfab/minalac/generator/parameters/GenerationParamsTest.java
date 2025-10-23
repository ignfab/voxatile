package com.ignfab.minalac.generator.parameters;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.heightmaps.HeightmapDeclaration;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxelWorld;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.voxels.TestingVoxelParams;
import com.ignfab.minalac.generator.parameters.processors.TestingProcessorParams;
import com.ignfab.minalac.generator.parameters.providers.TestingProviderParams;
import com.ignfab.minalac.generator.parameters.tasks.FetchDataTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.RenderBuildingsTaskParams;
import com.ignfab.minalac.generator.parameters.tasks.TestingTaskParams;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

import static org.junit.jupiter.api.Assertions.*;

public class GenerationParamsTest {
    private GenerationParams params;

    @BeforeEach
    void setUp() {
        GenerationParams.Area.LatitudeLongitude center = new GenerationParams.Area.LatitudeLongitude(5.8, 2.4);
        GenerationParams.Area area = new GenerationParams.Area(center, 50, 75);
        OutputFormat format = new OutputFormat(TestingVoxelWorld::new, TestingVoxelParams.class, TestingVoxelParams::new);
        params = new GenerationParams(area, format);
        params.heightmaps = new HashMap<>();
        params.forEachTile = new TileScheduleParams();
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
    public void testValidateWorldName() {
        params.worldName = " ";
        assertThrows(IllegalArgumentException.class, params::validate);
    }

    @Test
    public void testCreate() throws ParseException {
        params.worldName = "test";
        params.verticalScale = 3.0;
        params.horizontalScale = 4.0;
        params.crs = "EPSG:5643";
        params.heightmaps.put("ground", new HeightmapDeclarationParams("3"));
        params.heightmaps.put("altitude", new HeightmapDeclarationParams("minimal"));
        params.forEachTile.put("renderer1", new TestingTaskParams("value1"));
        params.forEachTile.put("renderer2", new TestingTaskParams("value2"));
        params.forEachTile.put("source1", new FetchDataTaskParams("models1", new TestingProviderParams("value3"), new TestingProcessorParams("value4")));
        params.forEachTile.put("source2", new FetchDataTaskParams("models2", new TestingProviderParams("value5"), new TestingProcessorParams("value6")));

        TestingVoxelParams placeable = new TestingVoxelParams("voxel");
        RenderBuildingsTaskParams task = new RenderBuildingsTaskParams(
            placeable,
            placeable,
            placeable
        );
        task.models = new ModelSelectionParams();
        task.models.type = "building";

        params.forEachTile.put("building", task);
        Generation generation = params.create(100);

        assertNotNull(generation);
        assertEquals("test", generation.world().getMetadata().getWorldName());
        assertEquals(50, generation.world().limits().sizeX());
        assertEquals(75, generation.world().limits().sizeY());
        assertEquals(3.0, generation.getVerticalScale(), 0.001);

        HeightmapDeclaration ground = assertDoesNotThrow(() -> generation.heightmaps().get("ground"));
        assertEquals(3, ground.create(WorldBBox2d.ORIGIN).get(0, 0));

        HeightmapDeclaration altitude = assertDoesNotThrow(() -> generation.heightmaps().get("altitude"));
        assertEquals(Integer.MIN_VALUE, altitude.create(WorldBBox2d.ORIGIN).get(0, 0));
    }
}
