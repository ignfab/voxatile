package com.ignfab.minalac.generator.utils.paramsparser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestParamsParser {
    @Test
    public void testSuccessfulInstantiation() {
        assertDoesNotThrow(() -> new ParamsParser("""
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
            """));
        //Todo PR : check if correct values ?
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
}
