package com.ignfab.minalac.generator.placeables.work_in_progress;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.outputs.testing.TestingVoxel;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;

public class PlaygroundTest {
    @Test
    public void foo() {
        /*
        PlaceableStructure base = PlaceableStructure.builder()
            .set(0, 0, 0, new TestingVoxel("1"))
            .set(1, 0, 0, new TestingVoxel("2"))
            .set(2, 0, 0, new TestingVoxel("3"))
            .set(0, 1, 0, new TestingVoxel("4"))
            .set(1, 1, 0, new TestingVoxel("5"))
            .set(2, 1, 0, new TestingVoxel("6"))
            //.set(2, 2, 0, new TestingVoxel("?"))
            .build();

        ResizedStructureBuilder rb = DefaultResizedStructureBuilder.REPEAT_XY(new IdentityOldStructureBuilder(base));
        // rb = new StretchedStructureBuilder(new IdentityStructureBuilder(base), 1);
        rb = DefaultResizedStructureBuilder.STRETCHED(new IdentityOldStructureBuilder(base), 1, null, null);
        rb = DefaultResizedStructureBuilder.REPEAT_X(rb);
        Structure resized = rb.build(7, 2, 1);
        rb = DefaultResizedStructureBuilder.STRETCHED(new IdentityOldStructureBuilder(resized), 3, null, null);
        resized = rb.build(14, 2, 1);

        // Structure resized = rb.build(7, 2, 1);
        // System.out.println(rb.axisY().ask(0));

        Structure display = resized;

        System.out.println(display.limits());
        for (int y = display.limits().maxY(); y >= display.limits().minY(); y--) {
            for (int x = display.limits().minX(); x <= display.limits().maxX(); x++) {
                System.out.print(display.get(x, y, 0));
            }
            System.out.println();
        }*/
    }

    @Test
    public void foo2() {
        ResizedStructureBuilder base = PlaceableStructure.builder()
            .set(0, 0, 0, new TestingVoxel("1"))
            .set(1, 0, 0, new TestingVoxel("2"))
            .set(2, 0, 0, new TestingVoxel("3"))
            .set(0, 1, 0, new TestingVoxel("4"))
            .set(1, 1, 0, new TestingVoxel("5"))
            .set(2, 1, 0, new TestingVoxel("6"))
            .build()
            .toIdentityResizedBuilder();

        ResizedStructureBuilder rb = DefaultResizedStructureBuilder.STRETCHED(base, 0, 0, null);

        Structure resized = rb.build(8, 2, 1);

        Structure display = resized;

        // System.out.println(display.limits());
        for (int y = display.limits().maxY(); y >= display.limits().minY(); y--) {
            for (int x = display.limits().minX(); x <= display.limits().maxX(); x++) {
                System.out.print(display.get(x, y, 0));
            }
            System.out.println();
        }
    }
}
