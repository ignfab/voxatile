package com.ignfab.minalac.generator.placeables.gettable2d;

import org.junit.jupiter.api.Test;

public class Container2dTest {

    // TODO-PR: Update the tests (Container2d class became Container)
    @Test
    public void toBeUpdated() {
        throw new RuntimeException("Update the tests (Container2d class became Container)");
    }
    /*
    @Test
    public void testAddGet() {
        Container2d container = new Container2d(7, 10);

        TestingGettable2d a = new TestingGettable2d(4, 6, new TestingVoxel("a"));
        TestingGettable2d b = new TestingGettable2d(4, 4, new TestingVoxel("b"));
        TestingGettable2d c = new TestingGettable2d(3, 10, new TestingVoxel("c"));

        // Not enough place
        container.add(c, 1, 1);
        assertEquals(Nothing.INSTANCE, container.get(1, 1));
        // OutOfBounds
        container.add(b, 7, 1);
        assertEquals(Nothing.INSTANCE, container.get(1, 1));

        container.add(a, 0, 0);
        container.add(b, 0, 6);
        container.add(c, 4, 0);

        // OutOfBounds
        assertEquals(Nothing.INSTANCE, container.get(7, 9));

        assertEquals(new TestingVoxel("a"), container.get(0, 0));
        assertEquals(new TestingVoxel("a"), container.get(3, 0));
        assertEquals(new TestingVoxel("a"), container.get(3, 5));
        assertEquals(new TestingVoxel("a"), container.get(2, 3));

        assertEquals(new TestingVoxel("b"), container.get(0, 6));
        assertEquals(new TestingVoxel("b"), container.get(3, 6));
        assertEquals(new TestingVoxel("b"), container.get(0, 9));
        assertEquals(new TestingVoxel("b"), container.get(2, 7));

        assertEquals(new TestingVoxel("c"), container.get(4, 0));
        assertEquals(new TestingVoxel("c"), container.get(4, 5));
        assertEquals(new TestingVoxel("c"), container.get(4, 6));
        assertEquals(new TestingVoxel("c"), container.get(6, 9));
        assertEquals(new TestingVoxel("c"), container.get(5, 5));
    }

    public static class TestingGettable2d extends DimensionedGettable2d {
        private final Placeable placeable;

        protected TestingGettable2d(int sizeFirstAxis, int sizeSecondAxis, Placeable placeable) {
            super(sizeFirstAxis, sizeSecondAxis);
            this.placeable = placeable;
        }

        @Override
        public Placeable get(int u, int v) {
            return placeable;
        }
    }*/
}
