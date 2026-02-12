package com.ignfab.minalac.generator.placeables.work_in_progress;

import com.ignfab.minalac.generator.placeables.u_turn_wip.DelegateIndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.u_turn_wip.W_StretchIndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapperBuilder;

// TODO: Might be merged with ResizedStructureBuilder
public class DefaultResizedStructureBuilder implements ResizedStructureBuilder {
    // private final ResizedStructureBuilder builder;
    protected IndexesToResizedStructureBuilder provider;
    private final IndexMapperBuilder axisXBuilder;
    private final IndexMapperBuilder axisYBuilder;
    private final IndexMapperBuilder axisZBuilder;

    private DefaultResizedStructureBuilder(ResizedStructureBuilder provider, IndexMapperBuilder axisXBuilder, IndexMapperBuilder axisYBuilder, IndexMapperBuilder axisZBuilder) {
        this((i, j, k) -> provider, axisXBuilder, axisYBuilder, axisZBuilder);
    }

    public DefaultResizedStructureBuilder(IndexesToResizedStructureBuilder provider, IndexMapperBuilder axisXBuilder, IndexMapperBuilder axisYBuilder, IndexMapperBuilder axisZBuilder) {
        this.provider = provider;
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
                    tab[iX.index()][iY.index()][iZ.index()] = provider.whichOne(iX.index(), iY.index(), iZ.index()).build(iX.length(), iY.length(), iZ.length());
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
            new IndexMapperBuilder.Equalizer(builder.axisX().minimalSize()),
            new IndexMapperBuilder.Identity(builder.axisY().minimalSize()),
            new IndexMapperBuilder.Identity(builder.axisZ().minimalSize())
        );
    }

    public static ResizedStructureBuilder REPEAT_Y(ResizedStructureBuilder builder) {
        return new DefaultResizedStructureBuilder(
            builder,
            new IndexMapperBuilder.Identity(builder.axisX().minimalSize()),
            new IndexMapperBuilder.Equalizer(builder.axisY().minimalSize()),
            new IndexMapperBuilder.Identity(builder.axisZ().minimalSize())
        );
    }

    public static ResizedStructureBuilder REPEAT_Z(ResizedStructureBuilder builder) {
        return new DefaultResizedStructureBuilder(
            builder,
            new IndexMapperBuilder.Identity(builder.axisX().minimalSize()),
            new IndexMapperBuilder.Identity(builder.axisY().minimalSize()),
            new IndexMapperBuilder.Equalizer(builder.axisZ().minimalSize())
        );
    }

    public static ResizedStructureBuilder REPEAT_XY(ResizedStructureBuilder builder) {
        builder.axisX().ask(8);
        /*
        if ( axisXBuilder.minimalLength() <= sizeX) {
            // On est bon, mais ça depend du builder enfant
            IndexMapperBuilder childX = provider.whichOne(0, 0, 0).axisX();
            int r = sizeX % childX.minimalLength();
            if (r == 0) {
                // On est bon aussi, autres cas c'est celui du streched
                // Autrement dit si il peut faire childX.ask(taille-sous-segment) == taille-sous-segment
            }

        }
        */
        return new DefaultResizedStructureBuilder(
            builder,
            new IndexMapperBuilder.Equalizer(builder.axisX().minimalSize()),
            new IndexMapperBuilder.Equalizer(builder.axisY().minimalSize()),
            new IndexMapperBuilder.Identity(builder.axisZ().minimalSize())

        );
    }

    public static ResizedStructureBuilder stretchoX(ResizedStructureBuilder builder, int x) {
        return new DefaultResizedStructureBuilder(
            builder,
            new W_StretchIndexMapperBuilder(builder.axisX(), x, 1),
            new DelegateIndexMapperBuilder(builder.axisY()),
            new DelegateIndexMapperBuilder(builder.axisZ()
            )
        );
    }

    // TODO: A garder pour repro "bug"
    private static ResizedStructureBuilder STRETCHED(ResizedStructureBuilder builder, Integer elasticAtX, Integer elasticAtY, Integer elasticAtZ) {
        IndexMapperBuilder axisXBuilder = (elasticAtX == null) ? new IndexMapperBuilder.Identity(builder.axisX().minimalSize()) : new IndexMapperBuilder.AdaptativeStretcher(elasticAtX, builder.axisX().minimalSize());
        IndexMapperBuilder axisYBuilder = (elasticAtY == null) ? new IndexMapperBuilder.Identity(builder.axisY().minimalSize()) : new IndexMapperBuilder.AdaptativeStretcher(elasticAtY, builder.axisY().minimalSize());
        IndexMapperBuilder axisZBuilder = (elasticAtZ == null) ? new IndexMapperBuilder.Identity(builder.axisZ().minimalSize()) : new IndexMapperBuilder.AdaptativeStretcher(elasticAtZ, builder.axisZ().minimalSize());
        return new DefaultResizedStructureBuilder(builder, axisXBuilder, axisYBuilder, axisZBuilder);
    }

    public static DefaultResizedStructureBuilder testFacade(ResizedStructureBuilder edges, ResizedStructureBuilder middle) {
        int minY = Math.max(edges.axisY().minimalSize(), middle.axisY().minimalSize());
        int minZ = Math.max(edges.axisZ().minimalSize(), middle.axisZ().minimalSize());

        return new DefaultResizedStructureBuilder(
            (i, j, k) -> (i == 1) ? middle : edges,
            new IndexMapperBuilder.MiddleTakesAll(edges.axisX().minimalSize()),
            new IndexMapperBuilder.Delegater(edges.axisY()),
            new IndexMapperBuilder.Identity(minZ)
        );
    }
}
