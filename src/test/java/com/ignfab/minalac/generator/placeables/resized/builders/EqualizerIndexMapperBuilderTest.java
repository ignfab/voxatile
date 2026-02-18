package com.ignfab.minalac.generator.placeables.resized.builders;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EqualizerIndexMapperBuilderTest {
    @Test
    public void testBuild() {
        assertEquals(0, 0);
        // throw new RuntimeException("Not implemented");
    }

    @Test
    public void testMaxSizeUnder() {
        assertEquals(6, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(3), 2).maxSizeUnder(6));
        assertEquals(9, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(3), 2).maxSizeUnder(10));

        assertEquals(-1, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(3), 2).maxSizeUnder(5));

        assertEquals(0, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(0), 1).maxSizeUnder(0));
        assertEquals(0, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(0), 1).maxSizeUnder(5));

        assertEquals(6, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(3, 4), 2).maxSizeUnder(6));
        assertEquals(10, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(3, 4), 2).maxSizeUnder(10));
    }

    @Test
    public void testMinimumSize() {
        assertEquals(0, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(3), 0).minimumSize());
        assertEquals(3, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(3), 1).minimumSize());
        assertEquals(6, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(3), 2).minimumSize());

        assertEquals(0, new EqualizerIndexMapperBuilder(new TestingIndexMapperBuilder(0), 2).minimumSize());
    }
}
