package com.ignfab.minalac.generator.parameters;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.generation.Heightmap;
import com.ignfab.minalac.generator.outputs.minetest.MTVoxelWorld;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;
import org.geotools.api.referencing.FactoryException;
import org.geotools.referencing.CRS;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HeightmapParamsTest {

    @Test
    public void testConstructor() {
        HeightmapParams heightmapIntegerValue = assertDoesNotThrow(() -> new HeightmapParams("3"));
        assertEquals(3, heightmapIntegerValue.defaultValue);

        HeightmapParams heightmapMinimal = assertDoesNotThrow(() -> new HeightmapParams("minimal"));
        assertEquals(Integer.MIN_VALUE, heightmapMinimal.defaultValue);

        HeightmapParams heightmapMin = assertDoesNotThrow(() -> new HeightmapParams("min"));
        assertEquals(Integer.MIN_VALUE, heightmapMin.defaultValue);

        HeightmapParams heightmapMaximal = assertDoesNotThrow(() -> new HeightmapParams("maximal"));
        assertEquals(Integer.MAX_VALUE, heightmapMaximal.defaultValue);

        HeightmapParams heightmapMax = assertDoesNotThrow(() -> new HeightmapParams("max"));
        assertEquals(Integer.MAX_VALUE, heightmapMax.defaultValue);

        assertThrows(ParseException.class, () -> new HeightmapParams("4foo"));
    }

    @Test
    public void testCreate() throws FactoryException, ParseException {
        Generation generation = new Generation(
            new MTVoxelWorld(),
            CRS.decode("EPSG:2154"),
            657_781,
            6_860_729,
            501,
            501,
            2.0,
            3.0);
        HeightmapParams params = new HeightmapParams("3");

        Heightmap heightmap = params.create(generation);
        assertEquals(3, heightmap.get(0, 0));
        assertEquals(3, heightmap.get(0, 1));

        WorldBBox2d box = heightmap.bbox();
        assertEquals(-250, box.minX());
        assertEquals(-250, box.minY());
        assertEquals(250, box.maxX());
        assertEquals(250, box.maxY());
    }
}
