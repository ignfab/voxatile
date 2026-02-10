package com.ignfab.minalac.generator.placeables.work_in_progress;

import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapperBuilder;

// TODO: Might be merged with ResizedStructureBuilder
public class DefaultResizedStructureBuilder implements ResizedStructureBuilder {
    // private final ResizedStructureBuilder builder;
    protected IndexesToResizedStructureBuilder builder;
    private final IndexMapperBuilder axisXBuilder;
    private final IndexMapperBuilder axisYBuilder;
    private final IndexMapperBuilder axisZBuilder;

    private DefaultResizedStructureBuilder(ResizedStructureBuilder builder, IndexMapperBuilder axisXBuilder, IndexMapperBuilder axisYBuilder, IndexMapperBuilder axisZBuilder) {
        this((i, j, k) -> builder, axisXBuilder, axisYBuilder, axisZBuilder);
    }

    public DefaultResizedStructureBuilder(IndexesToResizedStructureBuilder builder, IndexMapperBuilder axisXBuilder, IndexMapperBuilder axisYBuilder, IndexMapperBuilder axisZBuilder) {
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
                    tab[iX.index()][iY.index()][iZ.index()] = builder.whichOne(iX.index(), iY.index(), iZ.index()).build(iX.length(), iY.length(), iZ.length());
                }
            }
        }

        return new VirtualStructure(tab, ax, aY, aZ);
    }

    @Override
    public void checkResizability(int sizeX, int sizeY, int sizeZ) {
        if (sizeX <= 0)
            throw new RuntimeException(String.format("sizeX must be strictly positive (Asked : %d)", sizeX));
        if (sizeY <= 0) {
            throw new RuntimeException(String.format("sizeY must be strictly positive (Asked : %d)", sizeY));
        }if (sizeZ <= 0)
            throw new RuntimeException(String.format("sizeZ must be strictly positive (Asked : %d)", sizeZ));
        if (axisX().ask(sizeX) != sizeX)
            throw new RuntimeException(String.format("Asked sizeX (%d) do not match the allowed (%d)", sizeX, axisX().ask(sizeX)));
        if (axisY().ask(sizeY) != sizeY) {
            System.out.println(this);
            System.out.println(this.axisYBuilder);
            throw new RuntimeException(String.format("Asked sizeY (%d) do not match the allowed (%d)", sizeY, axisY().ask(sizeY)));
        }if (axisZ().ask(sizeZ) != sizeZ)
            throw new RuntimeException(String.format("Asked sizeZ (%d) do not match the allowed (%d)", sizeZ, axisZ().ask(sizeZ)));
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

    // TODO: A garder pour repro "bug"
    private static ResizedStructureBuilder STRETCHED(ResizedStructureBuilder builder, Integer elasticAtX, Integer elasticAtY, Integer elasticAtZ) {
        IndexMapperBuilder axisXBuilder = (elasticAtX == null) ? new IndexMapperBuilder.Identity(builder.axisX().minimalLength()) : new IndexMapperBuilder.AdaptativeStretcher(elasticAtX, builder.axisX().minimalLength());
        IndexMapperBuilder axisYBuilder = (elasticAtY == null) ? new IndexMapperBuilder.Identity(builder.axisY().minimalLength()) : new IndexMapperBuilder.AdaptativeStretcher(elasticAtY, builder.axisY().minimalLength());
        IndexMapperBuilder axisZBuilder = (elasticAtZ == null) ? new IndexMapperBuilder.Identity(builder.axisZ().minimalLength()) : new IndexMapperBuilder.AdaptativeStretcher(elasticAtZ, builder.axisZ().minimalLength());
        return new DefaultResizedStructureBuilder(builder, axisXBuilder, axisYBuilder, axisZBuilder);
    }

    public static DefaultResizedStructureBuilder testFacade(ResizedStructureBuilder edges, ResizedStructureBuilder middle) {
        int minY = Math.max(edges.axisY().minimalLength(), middle.axisY().minimalLength());
        int minZ = Math.max(edges.axisZ().minimalLength(), middle.axisZ().minimalLength());

        return new DefaultResizedStructureBuilder(
            (i, j, k) -> (i == 1) ? middle : edges,
            new IndexMapperBuilder.MiddleTakesAll(edges.axisX().minimalLength()),
            new IndexMapperBuilder.Identity(minY),
            new IndexMapperBuilder.Identity(minZ)
        );
    }
}
