package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;

import static com.ignfab.minalac.generator.utils.iterator.IteratorTester.*;
import static org.junit.jupiter.api.Assertions.*;

public class PriorityRepartitionAxisMapperBuilderTest {

    @Test
    public void testMaxSizeUnderDistribution() {
        // The tests here are not exact as finding the optimal distribution is not something easy.
        // The exacts results are implementation dependant.
        // However, what we want to test is that the PriorityRepartitionAxisMapperBuilder:
        //  - Try to distribute to have an roughly equals distribution
        //  - Try to leave no starved segments when possible

        // On this case, for an asked size of 48, there is a solution with no remainder which is 20-16-12.
        // What we want to test is that the segments are roughly equals (even if there remainder).
        // The result is implementation dependant, so what is tested is that the results are acceptable.
        AxisMapperBuilder fiveFourTreeBuilder = assertDoesNotThrow(
            () -> new PriorityRepartitionAxisMapperBuilder(
                new AxisMapperBuilder[] {new AllowlistTestingAxisMapperBuilder(0, 5, 10, 15, 20), new AllowlistTestingAxisMapperBuilder(0, 4, 8, 12, 16, 20), new AllowlistTestingAxisMapperBuilder(0, 3, 6, 9, 12, 15)},
                new int[] {0, 0, 0}
            )
        );
        assertTrue(fiveFourTreeBuilder.maxSizeUnder(48) >= 40);
        int resultingSize = fiveFourTreeBuilder.maxSizeUnder(48);

        int[] intervals = assertDoesNotThrow(() -> fiveFourTreeBuilder.build(resultingSize).intervals());
        assertTrue(14 <= intervals[0]);
        assertTrue(14 <= intervals[1]);
        assertTrue(14 <= intervals[2]);


        // The middle builder has a minimal size, there is to try that it doesn't get greedy
        // Basically the repartition should be fair despite minimal size
        AxisMapperBuilder noGreedTwo = assertDoesNotThrow(
            () -> new PriorityRepartitionAxisMapperBuilder(
                new AxisMapperBuilder[] {new AllowlistTestingAxisMapperBuilder(0, 3, 6, 9), new AllowlistTestingAxisMapperBuilder(2, 4, 6), new AllowlistTestingAxisMapperBuilder(0, 3, 6, 9)},
                new int[] {0, 0, 0}
            )
        );

        assertEquals(8, noGreedTwo.maxSizeUnder(8));
        AxisMapper noGreedTwoMapper = assertDoesNotThrow(() -> noGreedTwo.build(8));
        assertArrayEquals(new int[] {3, 2, 3}, noGreedTwoMapper.intervals());

        // Tree has min size
        AxisMapperBuilder noGreedTree = assertDoesNotThrow(
            () -> new PriorityRepartitionAxisMapperBuilder(
                new AxisMapperBuilder[] {new AllowlistTestingAxisMapperBuilder(3, 6, 9), new AllowlistTestingAxisMapperBuilder(0, 2, 4, 6), new AllowlistTestingAxisMapperBuilder(3, 6, 9)},
                new int[] {0, 0, 0}
            )
        );

        assertEquals(8, noGreedTree.maxSizeUnder(8));
        AxisMapper noGreedTreeMapper = assertDoesNotThrow(() -> noGreedTree.build(8));
        assertArrayEquals(new int[] {3, 2, 3}, noGreedTreeMapper.intervals());

        // The tests bellow are almost the same as noGreed but with no minimal size
        // Tested with various order
        // 3 - 2 - 3
        AxisMapperBuilder treeTwoTree = assertDoesNotThrow(
            () -> new PriorityRepartitionAxisMapperBuilder(
                new AxisMapperBuilder[] {new AllowlistTestingAxisMapperBuilder(0, 3, 6, 9), new AllowlistTestingAxisMapperBuilder(0, 2, 4, 6), new AllowlistTestingAxisMapperBuilder(0, 3, 6, 9)},
                new int[] {0, 0, 0}
            )
        );

        assertEquals(8, treeTwoTree.maxSizeUnder(8));
        AxisMapper treeTwoTreeMapper = assertDoesNotThrow(() -> treeTwoTree.build(8));
        assertArrayEquals(new int[] {3, 2, 3}, treeTwoTreeMapper.intervals());

        // 3 - 3 - 2
        AxisMapperBuilder treeTreeTwo = assertDoesNotThrow(
            () -> new PriorityRepartitionAxisMapperBuilder(
                new AxisMapperBuilder[] {new AllowlistTestingAxisMapperBuilder(0, 3, 6, 9), new AllowlistTestingAxisMapperBuilder(0, 3, 6, 9), new AllowlistTestingAxisMapperBuilder(0, 2, 4, 6)},
                new int[] {0, 0, 0}
            )
        );

        assertEquals(8, treeTreeTwo.maxSizeUnder(8));
        AxisMapper treeTreeTwoMapper = assertDoesNotThrow(() -> treeTreeTwo.build(8));
        assertArrayEquals(new int[] {3, 3, 2}, treeTreeTwoMapper.intervals());

        // 2 - 3 - 3
        AxisMapperBuilder twoTreeTree = assertDoesNotThrow(
            () -> new PriorityRepartitionAxisMapperBuilder(
                new AxisMapperBuilder[] {new AllowlistTestingAxisMapperBuilder(0, 2, 4, 6), new AllowlistTestingAxisMapperBuilder(0, 3, 6, 9), new AllowlistTestingAxisMapperBuilder(0, 3, 6, 9)},
                new int[] {0, 0, 0}
            )
        );

        assertEquals(8, twoTreeTree.maxSizeUnder(8));
        AxisMapper twoTreeTreeMapper = assertDoesNotThrow(() -> twoTreeTree.build(8));
        assertArrayEquals(new int[] {2, 3, 3}, twoTreeTreeMapper.intervals());
    }

    @Test
    public void testMaxSizeUnderBuild() {
        AxisMapperBuilder empty = assertDoesNotThrow(
            () ->  new PriorityRepartitionAxisMapperBuilder(
                new AxisMapperBuilder[] {new RangeTestingAxisMapperBuilder(0), new RangeTestingAxisMapperBuilder(0), new RangeTestingAxisMapperBuilder(0)},
                new int[] {0, 0, 0}
            )
        );
        assertEquals(0, empty.maxSizeUnder(10));
        AxisMapper emptyMapper = assertDoesNotThrow(() -> empty.build(0));
        assertBrowsesAllOnce(List.of(0, 0, 0), Arrays.stream(emptyMapper.intervals()).iterator());

        AxisMapperBuilder minSizeError = assertDoesNotThrow(
            () ->  new PriorityRepartitionAxisMapperBuilder(
                new AxisMapperBuilder[] {new RangeTestingAxisMapperBuilder(2, 5), new RangeTestingAxisMapperBuilder(0), new RangeTestingAxisMapperBuilder(6, 7)},
                new int[] {0, 0, 0}
            )
        );
        assertEquals(-1, minSizeError.maxSizeUnder(7));
        assertThrows(UnbuildableException.class, () -> minSizeError.build(7));

        AxisMapperBuilder minZero = assertDoesNotThrow(
            () ->  new PriorityRepartitionAxisMapperBuilder(
                new AxisMapperBuilder[] {new RangeTestingAxisMapperBuilder(0, 4), new RangeTestingAxisMapperBuilder(0, 4)},
                new int[] {0, 0}
            )
        );

        assertEquals(5, minZero.maxSizeUnder(5));
        AxisMapper withMinZero = assertDoesNotThrow(() -> minZero.build(5));
        // Unordered tests as there is no control on which one will have the most
        assertBrowsesAllOnce(List.of(2, 3), Arrays.stream(withMinZero.intervals()).iterator());

        // Same priority
        AxisMapperBuilder samePrio = assertDoesNotThrow(
            () ->  new PriorityRepartitionAxisMapperBuilder(
                new AxisMapperBuilder[] {new RangeTestingAxisMapperBuilder(2), new RangeTestingAxisMapperBuilder(3)},
                new int[] {0, 0}
            )
        );
        assertEquals(5, samePrio.maxSizeUnder(6));
        AxisMapper withSamePrio = assertDoesNotThrow(() -> samePrio.build(5));
        assertArrayEquals(new int[] {2, 3}, withSamePrio.intervals());

        // Priority should take all possible after distribution of min
        AxisMapperBuilder builderWithOnePrio = assertDoesNotThrow(
            () ->  new PriorityRepartitionAxisMapperBuilder(
                new AxisMapperBuilder[] {new RangeTestingAxisMapperBuilder(0, 8), new RangeTestingAxisMapperBuilder(2, 4)},
                new int[] {1, 0}
            )
        );

        // No remainder for first one
        assertEquals(2, builderWithOnePrio.maxSizeUnder(2));
        AxisMapper withSize2 = assertDoesNotThrow(() -> builderWithOnePrio.build(2));
        assertArrayEquals(new int[] {0, 2}, withSize2.intervals());

        // First takes all remainder
        assertEquals(8, builderWithOnePrio.maxSizeUnder(8));
        AxisMapper withSize8 = assertDoesNotThrow(() -> builderWithOnePrio.build(8));
        assertArrayEquals(new int[] {6, 2}, withSize8.intervals());

        assertEquals(10, builderWithOnePrio.maxSizeUnder(10));
        AxisMapper withSize10 = assertDoesNotThrow(() -> builderWithOnePrio.build(10));
        assertArrayEquals(new int[] {8, 2}, withSize10.intervals());

        // First has taken all available remainder the rest goes for the second one
        assertEquals(11, builderWithOnePrio.maxSizeUnder(11));
        AxisMapper withSize11 = assertDoesNotThrow(() -> builderWithOnePrio.build(11));
        assertArrayEquals(new int[] {8, 3}, withSize11.intervals());


    }
/*
    public static void main(String[] args) throws UnbuildableException {
        AxisMapperBuilder b = new PriorityRepartitionAxisMapperBuilder(
            new AxisMapperBuilder[] {new RangeTestingAxisMapperBuilder(0, 8), new RangeTestingAxisMapperBuilder(2, 4)},
            new int[] {1, 0});
        AxisMapper with11 =  b.build(11);
        System.out.println("hi");
    }*/

    @Test
    public void testMinimumSize() {
        assertEquals(
            8,
            assertDoesNotThrow(
                () ->  new PriorityRepartitionAxisMapperBuilder(
                    new AxisMapperBuilder[] {new RangeTestingAxisMapperBuilder(2, 5), new RangeTestingAxisMapperBuilder(0), new RangeTestingAxisMapperBuilder(6, 7)},
                    new int[] {0, 1, 0}
                )
            ).minimumSize()
        );

        assertEquals(
            0,
            assertDoesNotThrow(
                () ->  new PriorityRepartitionAxisMapperBuilder(
                    new AxisMapperBuilder[] {new RangeTestingAxisMapperBuilder(0, 5), new RangeTestingAxisMapperBuilder(0), new RangeTestingAxisMapperBuilder(0, 4)},
                    new int[] {1, 0, 0}
                )
            ).minimumSize()
        );
    }
}
