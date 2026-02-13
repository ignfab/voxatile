package com.ignfab.minalac.generator.placeables.work_in_progress;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.outputs.testing.TestingVoxel;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;

public class PlaygroundTest {

    @Test
    public void jesaisaps() {
        ResizedStructureBuilder A = PlaceableStructure.builder()
            .set(0, 0, 0, new TestingVoxel("1"))
            .set(1, 0, 0, new TestingVoxel("2"))
            .set(2, 0, 0, new TestingVoxel("3"))
            .set(0, 1, 0, new TestingVoxel("4"))
            .set(1, 1, 0, new TestingVoxel("5"))
            .set(2, 1, 0, new TestingVoxel("6"))
            .build()
            .toFixedResizedBuilder();

        ResizedStructureBuilder B = PlaceableStructure.builder()
            .set(0, 0, 0, new TestingVoxel("a"))
            .set(1, 0, 0, new TestingVoxel("b"))
            .set(2, 0, 0, new TestingVoxel("c"))
            .set(0, 1, 0, new TestingVoxel("d"))
            .set(1, 1, 0, new TestingVoxel("e"))
            .set(2, 1, 0, new TestingVoxel("f"))
            .build()
            .toFixedResizedBuilder();

        ResizedStructureBuilder C = PlaceableStructure.builder()
            .set(0, 0, 0, new TestingVoxel("7"))
            .set(1, 0, 0, new TestingVoxel("8"))
            .set(2, 0, 0, new TestingVoxel("9"))
            .set(0, 1, 0, new TestingVoxel("i"))
            .set(1, 1, 0, new TestingVoxel("j"))
            .set(2, 1, 0, new TestingVoxel("k"))
            .build()
            .toFixedResizedBuilder();

        /*
        ResizedStructureBuilder strX = DefaultResizedStructureBuilder.stretchX(AC, 1, 1);
        ResizedStructureBuilder strXY = DefaultResizedStructureBuilder.stretchY(strX, 0, 1);
        strXY.build(4, 3, 1);*/
        /*
        ResizedStructureBuilder builder = DefaultResizedStructureBuilder.stretchX(AC, 1, 1);
        builder = DefaultResizedStructureBuilder.repeatX(builder, 1);*/

        A = DefaultResizedStructureBuilder.stretchX(A, 1, 0);
        A = DefaultResizedStructureBuilder.stretchY(A, 1, 1);
        B = DefaultResizedStructureBuilder.repeatX(B, 2);
       // B = DefaultResizedStructureBuilder.stretchY(B, 1, 1);
        C = DefaultResizedStructureBuilder.stretchX(C, 1, 0);
        C = DefaultResizedStructureBuilder.stretchY(C, 1, 1);

        ResizedStructureBuilder[] builders = {A, B, C};
        int[] priorityX = {0, 1, 0};

        ResizedStructureBuilder builder = DefaultResizedStructureBuilder.priorityX(builders, priorityX);

        Structure result = builder.build(11, 3, 1);
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

