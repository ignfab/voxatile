package com.ignfab.minalac.generator.placeables.work_in_progress;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.outputs.testing.TestingVoxel;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;

public class PlaygroundTest {
    @Test
    public void foo() {
        PlaceableStructure base = PlaceableStructure.builder()
            .set(0, 0, 0, new TestingVoxel("1"))
            .set(1, 0, 0, new TestingVoxel("2"))
            .set(2, 0, 0, new TestingVoxel("3"))
            .set(0, 1, 0, new TestingVoxel("4"))
            .set(1, 1, 0, new TestingVoxel("5"))
            .set(2, 1, 0, new TestingVoxel("6"))
            //.set(2, 2, 0, new TestingVoxel("?"))
            .build();

        ResizedStructureBuilder rb = RepeatStructureBuilder.XY(new IdentityStructureBuilder(base));
        // rb = new StretchedStructureBuilder(new IdentityStructureBuilder(base), 1);

        Structure resized = rb.build(9, 4, 1);
       // System.out.println(rb.axisY().ask(0));

        Structure display = resized;

        for (int y = 3 - 1; y >= 0; y--) {
            for (int x = 0; x < 9; x++) {
                System.out.print(display.get(x, y, 0));
            }
            System.out.println();
        }
    }
}
