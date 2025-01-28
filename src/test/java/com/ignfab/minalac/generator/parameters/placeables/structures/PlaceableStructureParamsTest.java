package com.ignfab.minalac.generator.parameters.placeables.structures;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.ignfab.minalac.generator.parameters.ParamsTester;

public class PlaceableStructureParamsTest {

    @Test
    @DisplayName("Test PlaceableStructureParams variants")
    void testVariants() {
        // Blueprint variant
        assertDoesNotThrow(() -> ParamsTester.deserialize(
            PlaceableStructureParams.class, """
                axes: x
                with:
                  'a': A
                blueprint: a
            """
        ));

        // Box variant
        assertDoesNotThrow(() -> ParamsTester.deserialize(
            PlaceableStructureParams.class, """
              - at: [0, 0, 0]
                put: A
            """
        ));

        // Illegal variant (check it doesn't always say OK)
        assertThrows(InvalidTypeIdException.class, () -> ParamsTester.deserialize(
            PlaceableStructureParams.class, """
                ok: boomer
            """
        ));
    }

    @Test
    @DisplayName("Test list or not list deserialization of PlaceableStructureParams")
    void testListDeserialization() {
        // Not list
        assertDoesNotThrow(() -> ParamsTester.deserialize(
            PlaceableStructureParams.class, """
                axes: x
                with:
                  'a': A
                blueprint: a
            """
        ));

        // One item list
        assertDoesNotThrow(() -> ParamsTester.deserialize(
            PlaceableStructureParams.class, """
              - axes: x
                with:
                  'a': A
                blueprint: a
            """
        ));

        // Several items list
        assertDoesNotThrow(() -> ParamsTester.deserialize(
            PlaceableStructureParams.class, """
              - axes: x
                with:
                  'a': A
                blueprint: a
              - at: [0, 0, 0]
                put: A
            """
        ));
    }

    @Test
    @DisplayName("Test PlaceableStructureParams propagates validation errors")
    void testValidation() throws JsonProcessingException {
        PlaceableStructureParams params;

        params = ParamsTester.deserialize(
            PlaceableStructureParams.class, """
              - at: [0, 0, 0]
                put: A
              - axes: x
                with:
                  'a': A
                blueprint: a
            """
        );

        assertDoesNotThrow(params::validate);

        params = ParamsTester.deserialize(
            PlaceableStructureParams.class, """
              - at: [0, 0, 0]
                put: A
              - axes: [ x, y ]
                with:
                  'a': A
                blueprint: a
            """
        );

        assertThrows(IllegalArgumentException.class, params::validate);


        params = ParamsTester.deserialize(
            PlaceableStructureParams.class, """
              - at: [2..1, 0, 0]
                put: A
              - axes: x
                with:
                  'a': A
                blueprint: a
            """
        );

        assertThrows(IllegalArgumentException.class, params::validate);
    }
}
