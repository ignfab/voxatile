package com.ignfab.minalac.generator.parameters.placeables.patterns;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.parameters.ParamsTester;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class RepeatPatternParamsTest {
    @Test
    public void testDeserialize() {
        // Minimal test
        assertDoesNotThrow(() -> ParamsTester.deserialize(
            RepeatPatternParams.class,
            """
                repeatStructure:
                  with:
                    'B': default:stonebrick
                    'S': default:stone
                  axes: [ x, y ]
                  blueprint:
                    - 'SS'
                    - 'SB'
            """
        ));

        // Full test
        assertDoesNotThrow(() -> ParamsTester.deserialize(
            RepeatPatternParams.class,
            """
                repeatStructure:
                  with:
                    'B': default:stonebrick
                    'S': default:stone
                  axes: [ x, y ]
                  blueprint:
                    - 'SS'
                    - 'SB'
                forEachXRepetition:
                  shiftYBy: 1
                  shiftZBy: 1
                forEachYRepetition:
                  shiftXBy: 1
                  shiftZBy: 1
                forEachZRepetition:
                  shiftXBy: 1
                  shiftYBy: 1
            """
        ));
    }
}
