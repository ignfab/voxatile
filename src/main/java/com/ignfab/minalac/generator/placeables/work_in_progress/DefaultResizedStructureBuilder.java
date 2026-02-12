package com.ignfab.minalac.generator.placeables.work_in_progress;

import com.ignfab.minalac.generator.placeables.u_turn_wip.DelegateIndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.u_turn_wip.W_StretchIndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapper;
import com.ignfab.minalac.generator.placeables.work_in_progress.index_mapper.IndexMapperBuilder;

// TODO: Might be merged with ResizedStructureBuilder
public class DefaultResizedStructureBuilder implements ResizedStructureBuilder {
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
            [ax.structure().length()]
            [aY.structure().length()]
            [aZ.structure().length()];
        for (IndexMapper.StructureIndex iX : ax.structure()) {
            for (IndexMapper.StructureIndex iY : aY.structure()) {
                for (IndexMapper.StructureIndex iZ : aZ.structure()) {
                    // TODO il faudra revoir ça (Techniquement on passe une taille pas possible a FixedSB)
                    tab[iX.index()][iY.index()][iZ.index()] = provider.whichOne(iX.index(), iY.index(), iZ.index()).build(iX.size(), iY.size(), iZ.size());
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

    public static ResizedStructureBuilder stretchoX(ResizedStructureBuilder builder, int x) {
        return new DefaultResizedStructureBuilder(
            builder,
            new W_StretchIndexMapperBuilder(builder.axisX(), x, 0),
            new DelegateIndexMapperBuilder(builder.axisY()),
            new DelegateIndexMapperBuilder(builder.axisZ()
            )
        );
    }
}
