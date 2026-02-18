package com.ignfab.minalac.generator.placeables.resized.builders;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.placeables.resized.IndexMapperBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PriorityRepartitionIndexMapperBuilderTest {
    @Test
    public void testBuild() {
        assertEquals(0, 0);
        throw new RuntimeException("Not implemented");
    }

    @Test
    public void testMaxSizeUnder() {
        assertEquals(
            -1,
            new PriorityRepartitionIndexMapperBuilder(
                new IndexMapperBuilder[] {new TestingIndexMapperBuilder(2, 5), new TestingIndexMapperBuilder(0), new TestingIndexMapperBuilder(6, 7)},
                new int[] {0, 1, 0}
            ).maxSizeUnder(7),
            "Requested size is under minimal size"
        );

        assertEquals(
            0,
            new PriorityRepartitionIndexMapperBuilder(
                new IndexMapperBuilder[] {new TestingIndexMapperBuilder(0, 5), new TestingIndexMapperBuilder(0)},
                new int[] {0, 1, 0}
            ).maxSizeUnder(2),
            "maximum size of something empty is always 0"
        );


        throw new RuntimeException("Not implemented");
    }

    @Test
    public void testMinimumSize() {
        assertEquals(
            8,
            new PriorityRepartitionIndexMapperBuilder(
                new IndexMapperBuilder[] {new TestingIndexMapperBuilder(2, 5), new TestingIndexMapperBuilder(0), new TestingIndexMapperBuilder(6, 7)},
                new int[] {0, 1, 0}
            ).minimumSize()
        );

        assertEquals(
            0,
            new PriorityRepartitionIndexMapperBuilder(
                new IndexMapperBuilder[] {new TestingIndexMapperBuilder(0, 5), new TestingIndexMapperBuilder(0), new TestingIndexMapperBuilder(0, 4)},
                new int[] {1, 0, 0}
            ).minimumSize()
        );
    }
}
