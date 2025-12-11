package com.ignfab.minalac.generator.parameters.utils;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectReader;
import tools.jackson.dataformat.yaml.YAMLMapper;

import com.ignfab.minalac.generator.utils.IntegerInterval;

import static org.junit.jupiter.api.Assertions.*;

public class IntegerIntervalParamsTest {

    private ObjectReader reader = YAMLMapper.shared().readerFor(IntegerIntervalParams.class);

    @Test
    void testFromToParams() {
        IntegerInterval interval;
        IntegerIntervalParams params;

        params = assertDoesNotThrow(() -> reader.readValue("{ from: 2, to: 1 }"));
        assertThrows(IllegalArgumentException.class, params::validate);

        params = assertDoesNotThrow(() -> reader.readValue("{ from: 1, to: 2 }"));
        assertDoesNotThrow(params::validate);
        interval = assertDoesNotThrow(params::create);
        assertEquals(1, interval.begin());
        assertEquals(2, interval.end());

        params = assertDoesNotThrow(() -> reader.readValue("{ from: 2, to: 2 }"));
        assertDoesNotThrow(params::validate);
        assertDoesNotThrow(params::create);
    }

    @Test
    void testValueParams() {
        IntegerInterval interval;
        IntegerIntervalParams params;

        params = assertDoesNotThrow(() -> reader.readValue("{ value: 1 }"));
        assertDoesNotThrow(params::validate);
        interval = assertDoesNotThrow(params::create);
        assertEquals(1, interval.begin());
        assertEquals(1, interval.end());
    }

    @Test
    void testFallbackParams() {
        IntegerInterval interval;
        IntegerIntervalParams params;

        params = assertDoesNotThrow(() -> reader.readValue("1..2"));
        assertDoesNotThrow(params::validate);
        interval = assertDoesNotThrow(params::create);
        assertEquals(1, interval.begin());
        assertEquals(2, interval.end());

        params = assertDoesNotThrow(() -> reader.readValue("1"));
        assertDoesNotThrow(params::validate);
        interval = assertDoesNotThrow(params::create);
        assertEquals(1, interval.begin());
        assertEquals(1, interval.end());

        params = assertDoesNotThrow(() -> reader.readValue("-3..-2"));
        assertDoesNotThrow(params::validate);
        interval = assertDoesNotThrow(params::create);
        assertEquals(-3, interval.begin());
        assertEquals(-2, interval.end());

        params = assertDoesNotThrow(() -> reader.readValue("1..0"));
        assertThrows(IllegalArgumentException.class, params::validate);
    }
}
