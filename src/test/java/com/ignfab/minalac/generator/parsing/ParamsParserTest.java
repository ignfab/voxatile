package com.ignfab.minalac.generator.parsing;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.world.VoxelWorld;

import static org.junit.jupiter.api.Assertions.*;

public class ParamsParserTest {
    private static final String WORKING_JSON = """
            {
                "verticalScale": 3,
                "horizontalScale": 4,
                "area": {
                    "center": {
                        "latitude": 5.8,
                        "longitude": 2.4
                    },
                    "extendX": 500,
                    "extendY": 2500
                },
                "crs": "EPSG:5643",
                "format": "minetest"
            }
            """;

    @Test
    public void testSuccessfulJsonInstantiation() {
        assertDoesNotThrow(() -> new ParamsParser(WORKING_JSON));
    }

    @Test
    public void testSuccessfulYamlInstantiation() {
        assertDoesNotThrow(() -> new ParamsParser("""
                verticalScale: 3
                horizontalScale: 4
                area:
                    center:
                        latitude: 5.8
                        longitude: 2.4
                    extendX: 500
                    extendY: 2500
                crs: EPSG:5643
                format: minetest
                """));
    }

    @Test
    public void testMissingRequiredFieldArea() {
        assertThrows(ParseException.class, () -> new ParamsParser("""
            {
              "verticalScale": 3,
              "horizontalScale": 4,
              "crs": "EPSG:5643",
              "format": "minetest"
            }
            """));
    }

    @Test
    public void testMissingRequiredFieldLatitude() {
        assertThrows(ParseException.class, () -> new ParamsParser("""
            {
                "verticalScale": 3,
                "horizontalScale": 4,
                "area": {
                    "center": {
                        "longitude": 2.4
                    },
                    "extendX": 500,
                    "extendY": 2500
                },
                "crs": "EPSG:5643",
                "format": "minetest"
            }
            """));
    }

    @Test
    public void testMissingRequiredFieldLongitude() {
        assertThrows(ParseException.class, () -> new ParamsParser("""
            {
                "verticalScale": 3,
                "horizontalScale": 4,
                "area": {
                    "center": {
                        "latitude": 5.8
                    },
                    "extendX": 500,
                    "extendY": 2500
                },
                "crs": "EPSG:5643",
                "format": "minetest"
            }
            """));
    }

    @Test
    public void testMissingRequiredFieldExtendX() {
        assertThrows(ParseException.class, () -> new ParamsParser("""
            {
                "verticalScale": 3,
                "horizontalScale": 4,
                "area": {
                    "center": {
                        "latitude": 5.8,
                        "longitude": 2.4
                    },
                    "extendY": 2500
                },
                "crs": "EPSG:5643",
                "format": "minetest"
            }
            """));
    }

    @Test
    public void testMissingRequiredFieldExtendY() {
        assertThrows(ParseException.class, () -> new ParamsParser("""
            {
                "verticalScale": 3,
                "horizontalScale": 4,
                "area": {
                    "center": {
                        "latitude": 5.8,
                        "longitude": 2.4
                    },
                    "extendX": 500
                },
                "crs": "EPSG:5643",
                "format": "minetest"
            }
            """));
    }

    @Test
    public void testMissingRequiredFieldFormat() {
        assertThrows(ParseException.class, () -> new ParamsParser("""
            {
                "verticalScale": 3,
                "horizontalScale": 4,
                "area": {
                    "center": {
                        "latitude": 5.8,
                        "longitude": 2.4
                    },
                    "extendX": 500,
                    "extendY": 2500
                },
                "crs": "EPSG:5643"
            }
            """));
    }

    @Test
    public void testCreateGeneration() {
        Generation generation = assertDoesNotThrow(() -> new ParamsParser(WORKING_JSON).createGeneration());

        assertNotNull(generation);
        assertEquals(500, generation.getWorldBBox2d().getSize().x());
        assertEquals(2500, generation.getWorldBBox2d().getSize().y());
        assertEquals(3.0, generation.getVerticalScale(), 0.001);
    }

    @Test
    public void testCreateVoxelWorld() {
        VoxelWorld world = assertDoesNotThrow(() -> new ParamsParser(WORKING_JSON).createVoxelWorld());
        assertNotNull(world);
        assertEquals(500, world.getMetadata().getBbox().getSizeX());
        assertEquals(2500, world.getMetadata().getBbox().getSizeY());
    }
}
