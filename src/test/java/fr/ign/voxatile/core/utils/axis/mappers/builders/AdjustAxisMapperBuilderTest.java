package fr.ign.voxatile.core.utils.axis.mappers.builders;

import org.junit.jupiter.api.Test;

import fr.ign.voxatile.core.placeables.layouts.UnbuildableLayoutException;

import static org.junit.jupiter.api.Assertions.*;

public class AdjustAxisMapperBuilderTest {
    @Test
    public void testBuild() {
        assertThrows(
            UnbuildableLayoutException.class,
            () -> new AdjustAxisMapperBuilder(
                new RangeTestingAxisMapperBuilder(1, 5, 0),
                new RangeTestingAxisMapperBuilder(1, 5, 1)
            ),
            "Different origin"
        );

        assertThrows(
            UnbuildableLayoutException.class,
            () -> new AdjustAxisMapperBuilder(
                new RangeTestingAxisMapperBuilder(1, 5),
                new RangeTestingAxisMapperBuilder(6, 9)
            ),
            "No min sizes"
        );

        assertDoesNotThrow(
            () -> new AdjustAxisMapperBuilder(
                new RangeTestingAxisMapperBuilder(0),
                new RangeTestingAxisMapperBuilder(0, 9)
            )
        );

        assertDoesNotThrow(
            () -> new AdjustAxisMapperBuilder(
                new RangeTestingAxisMapperBuilder(1, 6),
                new RangeTestingAxisMapperBuilder(4, 9)
            )
        );
    }

    @Test
    public void testMaxSizeFittingUnder() {
        assertThrows(
            UnbuildableLayoutException.class,
            () -> new AdjustAxisMapperBuilder(
                new RangeTestingAxisMapperBuilder(1, 5),
                new RangeTestingAxisMapperBuilder(7, 8))
        );

        AxisMapperBuilder builder = assertDoesNotThrow(
            () -> new AdjustAxisMapperBuilder(
                new RangeTestingAxisMapperBuilder(1, 7),
                new RangeTestingAxisMapperBuilder(5, 10))
        );
        assertEquals(7, builder.maxSizeFittingUnder(10));
        assertEquals(7, builder.maxSizeFittingUnder(7));
        assertEquals(6, builder.maxSizeFittingUnder(6));
        assertEquals(5, builder.maxSizeFittingUnder(5));
        assertEquals(-1, builder.maxSizeFittingUnder(4));

        AxisMapperBuilder canBeZero = assertDoesNotThrow(
            () -> new AdjustAxisMapperBuilder(
                new RangeTestingAxisMapperBuilder(0, 7),
                new RangeTestingAxisMapperBuilder(0, 10))
        );
        assertEquals(7, canBeZero.maxSizeFittingUnder(10));
        assertEquals(0, canBeZero.maxSizeFittingUnder(0));
    }

    @Test
    public void testMinimumSize() {
        AxisMapperBuilder nonZeroMinSize = assertDoesNotThrow(
            () -> new AdjustAxisMapperBuilder(
                new RangeTestingAxisMapperBuilder(1, 7),
                new RangeTestingAxisMapperBuilder(5, 10),
                new RangeTestingAxisMapperBuilder(4, 6))
        );
        assertEquals(5, nonZeroMinSize.minimumSize());

        AxisMapperBuilder zeroMinSize = assertDoesNotThrow(
            () -> new AdjustAxisMapperBuilder(
                new RangeTestingAxisMapperBuilder(0, 7),
                new RangeTestingAxisMapperBuilder(0, 10)
            ));
        assertEquals(0, zeroMinSize.minimumSize());
    }

    @Test
    public void testOrigin() {
        AxisMapperBuilder builder = assertDoesNotThrow(
            () -> new AdjustAxisMapperBuilder(
                new RangeTestingAxisMapperBuilder(1, 7, -3),
                new RangeTestingAxisMapperBuilder(5, 10, -3))
        );
        assertEquals(-3, builder.origin());
    }
}
