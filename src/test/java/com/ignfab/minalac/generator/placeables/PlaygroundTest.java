package com.ignfab.minalac.generator.placeables;

import org.junit.jupiter.api.Test;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.outputs.testing.TestingVoxel;
import com.ignfab.minalac.generator.placeables.layouts.LayoutBuilder;
import com.ignfab.minalac.generator.placeables.layouts.DefaultLayoutBuilder;
import com.ignfab.minalac.generator.utils.axis.Axis;

public class PlaygroundTest {

    @Test
    public void jesaisaps() throws UnbuildableException {
        LayoutBuilder A = PlaceableStructure.builder()
            .set(0, 0, 0, new TestingVoxel("1"))
            .set(1, 0, 0, new TestingVoxel("2"))
            .set(2, 0, 0, new TestingVoxel("3"))
            .set(0, 1, 0, new TestingVoxel("4"))
            .set(1, 1, 0, new TestingVoxel("5"))
            .set(2, 1, 0, new TestingVoxel("6"))
            .build()
            .toLayoutBuilder();

        LayoutBuilder B = PlaceableStructure.builder()
            .set(0, 0, 0, new TestingVoxel("a"))
            .set(1, 0, 0, new TestingVoxel("b"))
            .set(2, 0, 0, new TestingVoxel("c"))
            .set(0, 1, 0, new TestingVoxel("d"))
            .set(1, 1, 0, new TestingVoxel("e"))
            .set(2, 1, 0, new TestingVoxel("f"))
            .build()
            .toLayoutBuilder();

        LayoutBuilder C = PlaceableStructure.builder()
            .set(0, 0, 0, new TestingVoxel("7"))
            .set(1, 0, 0, new TestingVoxel("8"))
            .set(2, 0, 0, new TestingVoxel("9"))
            .set(0, 1, 0, new TestingVoxel("i"))
            .set(1, 1, 0, new TestingVoxel("j"))
            .set(2, 1, 0, new TestingVoxel("k"))
            .build()
            .toLayoutBuilder();

        /*
        ResizedStructureBuilder strX = DefaultResizedStructureBuilder.stretchX(AC, 1, 1);
        ResizedStructureBuilder strXY = DefaultResizedStructureBuilder.stretchY(strX, 0, 1);
        strXY.build(4, 3, 1);*/
        /*
        ResizedStructureBuilder builder = DefaultResizedStructureBuilder.stretchX(AC, 1, 1);
        builder = DefaultResizedStructureBuilder.repeatX(builder, 1);*/

        A = DefaultLayoutBuilder.stretch(A, Axis.X, 1, 0, Integer.MAX_VALUE);
        A = DefaultLayoutBuilder.stretch(A, Axis.Y, 1, 1, Integer.MAX_VALUE);
        B = DefaultLayoutBuilder.repeat(B, Axis.X, 2);
        B = DefaultLayoutBuilder.repeat(B, Axis.Y, 2);
        C = DefaultLayoutBuilder.stretch(C, Axis.X, 1, 0, Integer.MAX_VALUE);
        C = DefaultLayoutBuilder.stretch(C, Axis.Y, 0, 1, Integer.MAX_VALUE);

        LayoutBuilder[] builders = {A, B, C};
        int[] priorityX = {0, 1, 0};

        LayoutBuilder builder = DefaultLayoutBuilder.priority(builders, Axis.X, priorityX);
        // builder = DefaultResizedStructureBuilder.repeatX(builder, 2);

        Structure result = builder.build(12, 4, 1);
        printo(result);
    }
    @Test
    public void machin() throws UnbuildableException {
        LayoutBuilder A = PlaceableStructure.builder()
            .set(0, 0, 0, new TestingVoxel("1"))
            .set(1, 0, 0, new TestingVoxel("2"))
            .set(2, 0, 0, new TestingVoxel("3"))
            .set(0, 1, 0, new TestingVoxel("4"))
            .set(1, 1, 0, new TestingVoxel("5"))
            .set(2, 1, 0, new TestingVoxel("6"))
            .build()
            .toLayoutBuilder();

        LayoutBuilder n = DefaultLayoutBuilder.stretch(A, Axis.X, 0, 1, Integer.MAX_VALUE);
        n = DefaultLayoutBuilder.stretch(n, Axis.Y, 0, 1, Integer.MAX_VALUE);


        LayoutStructure r = (LayoutStructure) n.build(10, 10, 1);
        System.out.println(r.axisX);
        System.out.println(r.axisY);
        printo(r);
    }

    @Test
    public void illegal() throws UnbuildableException {
        LayoutBuilder base = PlaceableStructure.builder()
            .set(0, 0, 0, new TestingVoxel("1"))
            .set(1, 0, 0, new TestingVoxel("2"))
            .set(2, 0, 0, new TestingVoxel("3"))
            .set(0, 1, 0, new TestingVoxel("4"))
            .set(1, 1, 0, new TestingVoxel("5"))
            .set(2, 1, 0, new TestingVoxel("6"))
            .set(0, 2, 0, new TestingVoxel("7"))
            .set(1, 2, 0, new TestingVoxel("8"))
            .set(2, 2, 0, new TestingVoxel("9"))
            .build()
            .toLayoutBuilder();

        LayoutBuilder n = DefaultLayoutBuilder.stretch(base, Axis.X, 1, 1, Integer.MAX_VALUE);
        System.out.println(n.axisZ().maxSizeUnder(1));
        int sizeZ = n.axisZ().maxSizeUnder(1);
        System.out.println(sizeZ);
        LayoutStructure r = (LayoutStructure) n.build(10, 3, sizeZ);

        printo(r);
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

