package com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper;

import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.placeables.work_in_progress.ResizedStructureBuilder;
import com.ignfab.minalac.generator.placeables.work_in_progress.Structure;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

public class Concat implements ResizedStructureBuilder {
    ResizedStructureBuilder upBuilder;
    ResizedStructureBuilder bottomBuilder;

    public Concat(ResizedStructureBuilder upBuilder, ResizedStructureBuilder bottomBuilder) {
        this.upBuilder = upBuilder;
        this.bottomBuilder = bottomBuilder;
    }

    @Override
    public Structure build(int sizeX, int sizeY, int sizeZ) {
        int b_minX = bottomBuilder.axisX().minimalLength();
        int a_theory = sizeX - b_minX;
        if (upBuilder.axisX().ask(a_theory) != a_theory)
            throw new RuntimeException("there is a pb");
        Structure up = upBuilder.build(a_theory, sizeY, sizeZ);
        Structure bottom = bottomBuilder.build(b_minX, sizeY, sizeZ);

        PlaceableStructure.Builder b = PlaceableStructure.builder();
        for (WorldCoords3d c : bottom.limits())
            b.set(c, bottom.get(c.x(), c.y(), c.z()));

        for (WorldCoords3d c : up.limits())
            b.set(c.x() + b_minX, c.y(), c.z(), up.get(c.x(), c.y(), c.z()));


        return b.build();
    }

    private void d(Structure display) {
        for (int y = display.limits().maxY(); y >= display.limits().minY(); y--) {
            for (int x = display.limits().minX(); x <= display.limits().maxX(); x++) {
                System.out.print(display.get(x, y, 0));
            }
            System.out.println();
        }
    }

    @Override
    public IndexMapperBuilder axisX() {
        return null;
    }

    @Override
    public IndexMapperBuilder axisY() {
        return null;
    }

    @Override
    public IndexMapperBuilder axisZ() {
        return null;
    }
}
