package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;

import static org.junit.jupiter.api.Assertions.*;

public class StretcherIndexMapperBuilderTest {
    @Test
    public void testBuild() {
        // Not enough space
        assertThrows(UnbuildableException.class, () -> new StretcherAxisMapperBuilder(new TestingIndexMapperBuilder(3), 0, 1, 2).build(2));
        // requested size too much
        assertThrows(UnbuildableException.class, () -> new StretcherAxisMapperBuilder(new TestingIndexMapperBuilder(3), 0, 1, 2).build(5));

        // TODO: not finished
    }

    @Test
    public void testMaxSizeUnder() {
        assertEquals(-1, new StretcherAxisMapperBuilder(new TestingIndexMapperBuilder(3), 0, 1, Integer.MAX_VALUE).maxSizeUnder(2));
        assertEquals(3, new StretcherAxisMapperBuilder(new TestingIndexMapperBuilder(3), 0, 1, Integer.MAX_VALUE).maxSizeUnder(3));
        assertEquals(7, new StretcherAxisMapperBuilder(new TestingIndexMapperBuilder(3), 0, 1, Integer.MAX_VALUE).maxSizeUnder(7));

        assertEquals(-1, new StretcherAxisMapperBuilder(new TestingIndexMapperBuilder(3), 0, 0, Integer.MAX_VALUE).maxSizeUnder(1));
        assertEquals(2, new StretcherAxisMapperBuilder(new TestingIndexMapperBuilder(3), 0, 0, Integer.MAX_VALUE).maxSizeUnder(2));
        assertEquals(3, new StretcherAxisMapperBuilder(new TestingIndexMapperBuilder(3), 0, 0, Integer.MAX_VALUE).maxSizeUnder(3));

        assertEquals(0, new StretcherAxisMapperBuilder(new TestingIndexMapperBuilder(0), 0, 0, Integer.MAX_VALUE).maxSizeUnder(3));

        assertEquals(4, new StretcherAxisMapperBuilder(new TestingIndexMapperBuilder(3), 0, 1, 2).maxSizeUnder(6));
    }

    @Test
    public void testMinimumSize() {
        assertEquals(2, new StretcherAxisMapperBuilder(new TestingIndexMapperBuilder(3), 0, 0, Integer.MAX_VALUE).minimumSize());
        assertEquals(3, new StretcherAxisMapperBuilder(new TestingIndexMapperBuilder(3), 0, 1, Integer.MAX_VALUE).minimumSize());
        assertEquals(7, new StretcherAxisMapperBuilder(new TestingIndexMapperBuilder(3), 0, 5, Integer.MAX_VALUE).minimumSize());

        // Resizable to 0 only
        assertEquals(0, new StretcherAxisMapperBuilder(new TestingIndexMapperBuilder(0), 0, 0, Integer.MAX_VALUE).minimumSize());
        assertEquals(0, new StretcherAxisMapperBuilder(new TestingIndexMapperBuilder(0), 0, 3, Integer.MAX_VALUE).minimumSize());

        // Shrink to 0
        assertEquals(0, new StretcherAxisMapperBuilder(new TestingIndexMapperBuilder(1), 0, 0, Integer.MAX_VALUE).minimumSize());
    }
}
