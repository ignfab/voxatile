package com.ignfab.minalac.generator.placeables.resized.builders;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StretcherIndexMapperBuilderTest {
    @Test
    public void testBuild() {
        throw new RuntimeException("Not implemented");
    }

    @Test
    public void testMaxSizeUnder() {
        assertEquals(-1, new StretcherIndexMapperBuilder(new TestingIndexMapperBuilder(3), 0, 1).maxSizeUnder(2));
        assertEquals(3, new StretcherIndexMapperBuilder(new TestingIndexMapperBuilder(3), 0, 1).maxSizeUnder(3));
        assertEquals(7, new StretcherIndexMapperBuilder(new TestingIndexMapperBuilder(3), 0, 1).maxSizeUnder(7));

        assertEquals(-1, new StretcherIndexMapperBuilder(new TestingIndexMapperBuilder(3), 0, 0).maxSizeUnder(1));
        assertEquals(2, new StretcherIndexMapperBuilder(new TestingIndexMapperBuilder(3), 0, 0).maxSizeUnder(2));
        assertEquals(3, new StretcherIndexMapperBuilder(new TestingIndexMapperBuilder(3), 0, 0).maxSizeUnder(3));

        assertEquals(0, new StretcherIndexMapperBuilder(new TestingIndexMapperBuilder(0), 0, 0).maxSizeUnder(3));

        assertEquals(4, new StretcherIndexMapperBuilder(new TestingIndexMapperBuilder(3), 0, 1, 2).maxSizeUnder(6));
    }

    @Test
    public void testMinimumSize() {
        assertEquals(2, new StretcherIndexMapperBuilder(new TestingIndexMapperBuilder(3), 0, 0).minimumSize());
        assertEquals(3, new StretcherIndexMapperBuilder(new TestingIndexMapperBuilder(3), 0, 1).minimumSize());
        assertEquals(7, new StretcherIndexMapperBuilder(new TestingIndexMapperBuilder(3), 0, 5).minimumSize());

        // Resizable to 0 only
        assertEquals(0, new StretcherIndexMapperBuilder(new TestingIndexMapperBuilder(0), 0, 0).minimumSize());
        assertEquals(0, new StretcherIndexMapperBuilder(new TestingIndexMapperBuilder(0), 0, 3).minimumSize());

        // Shrink to 0
        assertEquals(0, new StretcherIndexMapperBuilder(new TestingIndexMapperBuilder(1), 0, 0).minimumSize());
    }
}
