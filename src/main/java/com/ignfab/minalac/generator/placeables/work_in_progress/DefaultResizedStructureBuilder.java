package com.ignfab.minalac.generator.placeables.work_in_progress;

import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapperBuilder;

// TODO: Might be merged with ResizedStructureBuilder
public class DefaultResizedStructureBuilder implements ResizedStructureBuilder {
    private final ResizedStructureBuilder builder;
    private final IndexMapperBuilder axisXBuilder;
    private final IndexMapperBuilder axisYBuilder;
    private final IndexMapperBuilder axisZBuilder;

    protected DefaultResizedStructureBuilder(ResizedStructureBuilder builder, IndexMapperBuilder axisXBuilder, IndexMapperBuilder axisYBuilder, IndexMapperBuilder axisZBuilder) {
        this.builder = builder;
        this.axisXBuilder = axisXBuilder;
        this.axisYBuilder = axisYBuilder;
        this.axisZBuilder = axisZBuilder;
    }

    @Override
    public Structure build(int sizeX, int sizeY, int sizeZ) {
        checkResizability(sizeX, sizeY, sizeZ);
        IndexMapper ax, aY, aZ;
        ax = axisXBuilder.build(sizeX);
        aY = axisYBuilder.build(sizeY);
        aZ = axisZBuilder.build(sizeZ);
        Structure[][][] tab = new Structure
            [ax.structure().size()]
            [aY.structure().size()]
            [aZ.structure().size()];
        for (IndexMapper.StructureIndex iX : ax.structure()) {
            for (IndexMapper.StructureIndex iY : aY.structure()) {
                for (IndexMapper.StructureIndex iZ : aZ.structure()) {
                    tab[iX.index()][iY.index()][iZ.index()] = builder.build(iX.length(), iY.length(), iZ.length());
                }
            }
        }

        return new VirtualStructure(tab, ax, aY, aZ);
    }

    @Override
    public IndexMapperBuilder axisX() {
        return axisXBuilder;
    }

    @Override
    public IndexMapperBuilder axisY() {
        return axisYBuilder;
    }

    @Override
    public IndexMapperBuilder axisZ() {
        return axisZBuilder;
    }

    public static ResizedStructureBuilder REPEAT_X(ResizedStructureBuilder builder) {
        return new DefaultResizedStructureBuilder(
            builder,
            new IndexMapperBuilder.Equalizer(builder.axisX().minimalLength()),
            new IndexMapperBuilder.Identity(builder.axisY().minimalLength()),
            new IndexMapperBuilder.Identity(builder.axisZ().minimalLength())
        );
    }

    public static ResizedStructureBuilder REPEAT_Y(ResizedStructureBuilder builder) {
        return new DefaultResizedStructureBuilder(
            builder,
            new IndexMapperBuilder.Identity(builder.axisX().minimalLength()),
            new IndexMapperBuilder.Equalizer(builder.axisY().minimalLength()),
            new IndexMapperBuilder.Identity(builder.axisZ().minimalLength())
        );
    }

    public static ResizedStructureBuilder REPEAT_Z(ResizedStructureBuilder builder) {
        return new DefaultResizedStructureBuilder(
            builder,
            new IndexMapperBuilder.Identity(builder.axisX().minimalLength()),
            new IndexMapperBuilder.Identity(builder.axisY().minimalLength()),
            new IndexMapperBuilder.Equalizer(builder.axisZ().minimalLength())
        );
    }

    public static ResizedStructureBuilder REPEAT_XY(ResizedStructureBuilder builder) {
        return new DefaultResizedStructureBuilder(
            builder,
            new IndexMapperBuilder.Equalizer(builder.axisX().minimalLength()),
            new IndexMapperBuilder.Equalizer(builder.axisY().minimalLength()),
            new IndexMapperBuilder.Identity(builder.axisZ().minimalLength())

        );
    }

    public static ResizedStructureBuilder STRETCHED(ResizedStructureBuilder builder, Integer elasticAtX, Integer elasticAtY, Integer elasticAtZ) {
        IndexMapperBuilder axisXBuilder = (elasticAtX == null) ? new IndexMapperBuilder.Identity(builder.axisX().minimalLength()) : new IndexMapperBuilder.Stretcher(elasticAtX, builder.axisX().minimalLength() - 1);
        IndexMapperBuilder axisYBuilder = (elasticAtY == null) ? new IndexMapperBuilder.Identity(builder.axisY().minimalLength()) : new IndexMapperBuilder.Stretcher(elasticAtY, builder.axisY().minimalLength() - 1);
        IndexMapperBuilder axisZBuilder = (elasticAtZ == null) ? new IndexMapperBuilder.Identity(builder.axisZ().minimalLength()) : new IndexMapperBuilder.Stretcher(elasticAtZ, builder.axisZ().minimalLength() - 1);
        return new DefaultResizedStructureBuilder(builder, axisXBuilder, axisYBuilder, axisZBuilder);
    }
}
