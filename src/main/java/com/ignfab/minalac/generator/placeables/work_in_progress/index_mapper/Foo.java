package com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper;

import com.ignfab.minalac.generator.placeables.work_in_progress.DefaultResizedStructureBuilder;
import com.ignfab.minalac.generator.placeables.work_in_progress.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.IndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.work_in_progress.ResizedStructureBuilder;
import com.ignfab.minalac.generator.placeables.work_in_progress.Structure;

public class Foo implements ResizedStructureBuilder {
    ResizedStructureBuilder[] builders;
    private IndexMapperBuilder axisXBuilder;

    @Override
    public Structure build(int sizeX, int sizeY, int sizeZ) {
        IndexMapper aX = axisXBuilder.build(sizeX);
        IndexMapper[] aY = new IndexMapper[aX.structure().length()];
        IndexMapper[] aZ = new IndexMapper[aX.structure().length()];
        for (IndexMapper.StructureIndex iX : aX.structure()) {
            aY[iX.index()] = builders[iX.index()].axisY().build(sizeY);
            aZ[iX.index()] = builders[iX.index()].axisZ().build(sizeZ);
        }
        ResizedStructureBuilder[] s = new ResizedStructureBuilder[aX.structure().length()];
        for (int i = 0; i < s.length; i++) {
            s[i] = new DefaultResizedStructureBuilder(builders[i], builders[i].axisX(), builders[i].axisY(), builders[i].axisZ());
        }
        //throw new UnsupportedOperationException("Not implemented yet");
        for (ResizedStructureBuilder b : builders) {
            Structure sa = b.build(sizeX, sizeY, sizeZ);
        }
        return null;
    }

    //public void foo(IndexMapperBuilder[] tab, )

    @Override
    public IndexMapperBuilder axisX() {
       // return x.foo(0);
        throw new UnsupportedOperationException("Not implemented yet");
    }

    ResizedStructureBuilder test(ResizedStructureBuilder[] builders, int i) {
        //
    }

    @Override
    public IndexMapperBuilder axisY() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public IndexMapperBuilder axisZ() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void checkResizability(int sizeX, int sizeY, int sizeZ) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @FunctionalInterface
    public interface Test {
        IndexMapperBuilder foo(int i);
    }
}
