package com.ignfab.minalac.generator.placeables.resized.builders;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.placeables.resized.IndexMapper;
import com.ignfab.minalac.generator.placeables.resized.IndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.resized.UnresizableStructureException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PriorityRepartitionIndexMapperBuilderTest {

    @Test
    public void testMaxSizeUnderBuild() {
        IndexMapperBuilder minSizeError = new PriorityRepartitionIndexMapperBuilder(
            new IndexMapperBuilder[] {new TestingIndexMapperBuilder(2, 5), new TestingIndexMapperBuilder(0), new TestingIndexMapperBuilder(6, 7)},
            new int[] {0, 0, 0}
        );
        assertEquals(-1, minSizeError.maxSizeUnder(7));
        assertThrows(UnresizableStructureException.class, () -> minSizeError.build(7));

        IndexMapperBuilder minZero = new PriorityRepartitionIndexMapperBuilder(
            new IndexMapperBuilder[] {new TestingIndexMapperBuilder(0, 4), new TestingIndexMapperBuilder(0, 4)},
            new int[] {0, 0}
        );

        assertEquals(5, minZero.maxSizeUnder(5));
        IndexMapper withMinZero = assertDoesNotThrow(() -> minZero.build(5));
        // Tests on sizes as there is no control cn which one will have the most
        assertBrowsesAllOnce(
            List.of(3, 2),
            withMinZero.structures().stream().map(IndexMapper.StructureIndex::size).iterator()
        );

        // Same priority
        IndexMapperBuilder samePrio = new PriorityRepartitionIndexMapperBuilder(
            new IndexMapperBuilder[] {new TestingIndexMapperBuilder(2), new TestingIndexMapperBuilder(3)},
            new int[] {0, 0}
        );
        assertEquals(5, samePrio.maxSizeUnder(6));
        IndexMapper withSamePrio = assertDoesNotThrow(() -> samePrio.build(5));
        assertBrowsesAllOnce(
            List.of(new IndexMapper.StructureIndex(0, 2), new IndexMapper.StructureIndex(1, 3)),
            withSamePrio.structures()
        );

        // Priority should take all possible after distribution of min
        IndexMapperBuilder builderWithOnePrio = new PriorityRepartitionIndexMapperBuilder(
            new IndexMapperBuilder[] {new TestingIndexMapperBuilder(0, 8), new TestingIndexMapperBuilder(2, 4)},
            new int[] {1, 0}
        );

        // No remainder for first one
        assertEquals(2, builderWithOnePrio.maxSizeUnder(2));
        IndexMapper withSize2 = assertDoesNotThrow(() -> builderWithOnePrio.build(2));
        assertBrowsesAllOnce(
            List.of(new IndexMapper.StructureIndex(0, 0), new IndexMapper.StructureIndex(1, 2)),
            withSize2.structures()
        );

        // First takes all remainder
        assertEquals(8, builderWithOnePrio.maxSizeUnder(8));
        IndexMapper withSize8 = assertDoesNotThrow(() -> builderWithOnePrio.build(8));
        assertBrowsesAllOnce(
            List.of(new IndexMapper.StructureIndex(0, 6), new IndexMapper.StructureIndex(1, 2)),
            withSize8.structures()
        );

        assertEquals(10, builderWithOnePrio.maxSizeUnder(10));
        IndexMapper withSize10 = assertDoesNotThrow(() -> builderWithOnePrio.build(10));
        assertBrowsesAllOnce(
            List.of(new IndexMapper.StructureIndex(0, 8), new IndexMapper.StructureIndex(1, 2)),
            withSize10.structures()
        );

        // First has taken all available remainder the rest goes for the second one
        assertEquals(11, builderWithOnePrio.maxSizeUnder(11));
        IndexMapper withSize11 = assertDoesNotThrow(() -> builderWithOnePrio.build(11));
        assertBrowsesAllOnce(
            List.of(new IndexMapper.StructureIndex(0, 8), new IndexMapper.StructureIndex(1, 3)),
            withSize11.structures()
        );

        throw new RuntimeException("Not finished");
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
