package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestingIndexMapperBuilderTest {
    @Test
    public void testBuild() {
        // TODO: not finished
    }

    @Test
    public void testMaxSizeUnder() {
        assertEquals(0, new TestingIndexMapperBuilder(0).maxSizeUnder(1));
        assertEquals(1, new TestingIndexMapperBuilder(0, 2).maxSizeUnder(1));

        assertEquals(-1, new TestingIndexMapperBuilder(2, 5).maxSizeUnder(1));

        assertEquals(2, new TestingIndexMapperBuilder(2, 5).maxSizeUnder(2));
        assertEquals(5, new TestingIndexMapperBuilder(2, 5).maxSizeUnder(5));
        assertEquals(3, new TestingIndexMapperBuilder(2, 5).maxSizeUnder(3));
        assertEquals(5, new TestingIndexMapperBuilder(2, 5).maxSizeUnder(6));

        assertEquals(2, new TestingIndexMapperBuilder(2).maxSizeUnder(3));
        assertEquals(2, new TestingIndexMapperBuilder(2).maxSizeUnder(2));
        assertEquals(-1, new TestingIndexMapperBuilder(2).maxSizeUnder(1));

    }

    @Test
    public void testMinimumSize() {
        assertEquals(2, new TestingIndexMapperBuilder(2, 5).minimumSize());
        assertEquals(2, new TestingIndexMapperBuilder(2).minimumSize());
    }
}
