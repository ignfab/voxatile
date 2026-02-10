package com.ignfab.minalac.generator.placeables.work_in_progress;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.outputs.testing.TestingVoxel;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;

public class PlaygroundTest {

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
            .toStretchedXBuilder(0);

        // TODO : Il y a un problème dans la manière dont on chaine
        // CF  DefaultResizedStructureBuilder.STRETCHED
        // ResizedStructureBuilder rb = DefaultResizedStructureBuilder.STRETCHED(base, 0, null, null);
        // Structure bug = rb.build(4, 2, 1); // 1111

        Structure resized = base.build(4, 2, 1); // 1123
        Structure display = resized;

        for (int y = display.limits().maxY(); y >= display.limits().minY(); y--) {
            for (int x = display.limits().minX(); x <= display.limits().maxX(); x++) {
                System.out.print(display.get(x, y, 0));
            }
            System.out.println();
        }
    }

    @Test
    public void foo3() {
//        ResizedStructureBuilder baseBottom = PlaceableStructure.builder()
//            .set(0, 0, 0, new TestingVoxel("1"))
//            .set(1, 0, 0, new TestingVoxel("2"))
//            .set(2, 0, 0, new TestingVoxel("3"))
//            .set(0, 1, 0, new TestingVoxel("4"))
//            .set(1, 1, 0, new TestingVoxel("5"))
//            .set(2, 1, 0, new TestingVoxel("6"))
//            .build()
//            .toStretchedXYBuilder(0, 0);
//
//
//        ResizedStructureBuilder baseTop = PlaceableStructure.builder()
//            .set(0, 0, 0, new TestingVoxel("a"))
//            .set(1, 0, 0, new TestingVoxel("b"))
//            .set(2, 0, 0, new TestingVoxel("c"))
//            .set(0, 1, 0, new TestingVoxel("d"))
//            .set(1, 1, 0, new TestingVoxel("e"))
//            .set(2, 1, 0, new TestingVoxel("f"))
//            .build()
//            .toStretchedXYBuilder(0, 1);
//
//        ResizedStructureBuilder bottom = baseBottom;
//        ResizedStructureBuilder top = DefaultResizedStructureBuilder.REPEAT_XY(baseTop);
//
//        ResizedStructureBuilder concat = new Concat(top, bottom);
//
//        Structure resized = concat.build(11, 7, 1);
//
//        Structure display = resized;
//
//        for (int y = display.limits().maxY(); y >= display.limits().minY(); y--) {
//            for (int x = display.limits().minX(); x <= display.limits().maxX(); x++) {
//                System.out.print(display.get(x, y, 0));
//            }
//            System.out.println();
//        }
    }

    @Test
    public void foo4() {
        ResizedStructureBuilder AC = PlaceableStructure.builder()
            .set(0, 0, 0, new TestingVoxel("1"))
            .set(1, 0, 0, new TestingVoxel("2"))
            .set(2, 0, 0, new TestingVoxel("3"))
            .set(0, 1, 0, new TestingVoxel("4"))
            .set(1, 1, 0, new TestingVoxel("5"))
            .set(2, 1, 0, new TestingVoxel("6"))
            .build()
            .toStretchedXYBuilder(1, 1);

        ResizedStructureBuilder B = PlaceableStructure.builder()
            .set(0, 0, 0, new TestingVoxel("a"))
            .set(1, 0, 0, new TestingVoxel("b"))
            .set(2, 0, 0, new TestingVoxel("c"))
            .set(0, 1, 0, new TestingVoxel("d"))
            .set(1, 1, 0, new TestingVoxel("e"))
            .set(2, 1, 0, new TestingVoxel("f"))
            .build()
            .toStretchedXBuilder(0);

        ResizedStructureBuilder builderAC = AC;
        ResizedStructureBuilder builderB = DefaultResizedStructureBuilder.REPEAT_XY(B);
        List<ResizedStructureBuilder> builders = new ArrayList<>();
        builders.add(builderAC);
        builders.add(builderB);
        System.out.println(builders.get(1).axisX());

        ResizedStructureBuilder concat = DefaultResizedStructureBuilder.testFacade(builderAC, builderB);//new NewConcatPOC(builders);

        Structure resized = concat.build(21, 8, 1);
        // Structure resized = AC.build(2, 8, 1);

        Structure display = resized;

        for (int y = display.limits().maxY(); y >= display.limits().minY(); y--) {
            for (int x = display.limits().minX(); x <= display.limits().maxX(); x++) {
                System.out.print(display.get(x, y, 0));
            }
            System.out.println();
        }
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

