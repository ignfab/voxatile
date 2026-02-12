package com.ignfab.minalac.generator.placeables.work_in_progress;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.outputs.testing.TestingVoxel;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;

public class PlaygroundTest {

    @Test
    public void jesaisaps() {
        ResizedStructureBuilder AC = PlaceableStructure.builder()
            .set(0, 0, 0, new TestingVoxel("1"))
            .set(1, 0, 0, new TestingVoxel("2"))
            .set(2, 0, 0, new TestingVoxel("3"))
            .set(0, 1, 0, new TestingVoxel("4"))
            .set(1, 1, 0, new TestingVoxel("5"))
            .set(2, 1, 0, new TestingVoxel("6"))
            .build()
            .toFixedResizedBuilder();

        ResizedStructureBuilder strX = DefaultResizedStructureBuilder.stretchX(AC, 1, 1);
        ResizedStructureBuilder strXY = DefaultResizedStructureBuilder.stretchY(strX, 1, 1);

        // strX.build(4, 3, 1);

        System.out.println(strXY.axisY().ask(6));
        Structure result = strXY.build(4, 5, 1);
        printo(result);
    }

    public static void printo(Structure display) {
        for (int y = display.limits().maxY(); y >= display.limits().minY(); y--) {
            for (int x = display.limits().minX(); x <= display.limits().maxX(); x++) {
                System.out.print(display.get(x, y, 0));
            }
            System.out.println();
        }
    }
}

