package com.ignfab.minalac.generator.placeables.resized;

import java.util.Arrays;

import com.ignfab.minalac.generator.placeables.resized.builders.DelegateIndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.resized.builders.EqualizerIndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.resized.builders.PriorityRepartitionIndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.resized.builders.StretcherIndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.resized.builders.SuperDelegateIndexMapperBuilder;
import com.ignfab.minalac.generator.placeables.Structure;
import com.ignfab.minalac.generator.placeables.VirtualStructure;

// TODO: Might be merged with ResizedStructureBuilder
public class DefaultResizedStructureBuilder implements ResizedStructureBuilder {
    private final ResizedStructureBuilderProvider provider;
    private final IndexMapperBuilder axisXBuilder;
    private final IndexMapperBuilder axisYBuilder;
    private final IndexMapperBuilder axisZBuilder;

    public DefaultResizedStructureBuilder(ResizedStructureBuilder builder, IndexMapperBuilder axisXBuilder, IndexMapperBuilder axisYBuilder, IndexMapperBuilder axisZBuilder) {
        this((i, j, k) -> builder, axisXBuilder, axisYBuilder, axisZBuilder);
    }

    private DefaultResizedStructureBuilder(ResizedStructureBuilderProvider provider, IndexMapperBuilder axisXBuilder, IndexMapperBuilder axisYBuilder, IndexMapperBuilder axisZBuilder) {
        this.provider = provider;
        this.axisXBuilder = axisXBuilder;
        this.axisYBuilder = axisYBuilder;
        this.axisZBuilder = axisZBuilder;
    }

    @Override
    public Structure build(int sizeX, int sizeY, int sizeZ) throws UnresizableStructureException {
        // checkResizability(sizeX, sizeY, sizeZ);

        IndexMapper aX, aY, aZ;
        aX = axisXBuilder.build(sizeX);
        aY = axisYBuilder.build(sizeY);
        aZ = axisZBuilder.build(sizeZ);
        Structure[][][] tab = new Structure
            [aX.structures().size()]
            [aY.structures().size()]
            [aZ.structures().size()];
        for (IndexMapper.StructureIndex iX : aX.structures()) { // 3
            for (IndexMapper.StructureIndex iY : aY.structures()) { // 1
                for (IndexMapper.StructureIndex iZ : aZ.structures()) { // 1
                    // TODO il faudra revoir ça (Techniquement on passe une taille pas possible a FixedSB)
                    ResizedStructureBuilder b = provider.get(iX.index(), iY.index(), iZ.index()); // Si 0 -> A, 1 -> B, 2 -> C
                    tab[iX.index()][iY.index()][iZ.index()] = b.build(iX.size(), iY.size(), iZ.size());
                }
            }
        }

        return new VirtualStructure(tab, aX, aY, aZ);
    }

    @Override
    public void checkResizability(int sizeX, int sizeY, int sizeZ) {
        if (sizeX <= 0)
            throw new RuntimeException(String.format("sizeX must be strictly positive (Asked : %d)", sizeX));
        if (sizeY <= 0) {
            throw new RuntimeException(String.format("sizeY must be strictly positive (Asked : %d)", sizeY));
        }if (sizeZ <= 0)
            throw new RuntimeException(String.format("sizeZ must be strictly positive (Asked : %d)", sizeZ));
        if (axisX().maxSizeUnder(sizeX) != sizeX)
            throw new RuntimeException(String.format("Asked sizeX (%d) do not match the allowed (%d)", sizeX, axisX().maxSizeUnder(sizeX)));
        if (axisY().maxSizeUnder(sizeY) != sizeY) {
            throw new RuntimeException(String.format("Asked sizeY (%d) do not match the allowed (%d)", sizeY, axisY().maxSizeUnder(sizeY)));
        }if (axisZ().maxSizeUnder(sizeZ) != sizeZ)
            throw new RuntimeException(String.format("Asked sizeZ (%d) do not match the allowed (%d)", sizeZ, axisZ().maxSizeUnder(sizeZ)));
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

    public static ResizedStructureBuilder stretchX(ResizedStructureBuilder builder, int x, int minRepetition) {
        return new DefaultResizedStructureBuilder(
            builder,
            new StretcherIndexMapperBuilder(builder.axisX(), x, minRepetition),
            new DelegateIndexMapperBuilder(builder.axisY()),
            new DelegateIndexMapperBuilder(builder.axisZ())
        );
    }

    public static ResizedStructureBuilder stretchX(ResizedStructureBuilder builder, int x, int minRepetition, int maxRepetition) {
        return new DefaultResizedStructureBuilder(
            builder,
            new StretcherIndexMapperBuilder(builder.axisX(), x, minRepetition),
            new DelegateIndexMapperBuilder(builder.axisY()),
            new DelegateIndexMapperBuilder(builder.axisZ())
        );
    }

    public static ResizedStructureBuilder stretchY(ResizedStructureBuilder builder, int y, int minRepetition) {
        return new DefaultResizedStructureBuilder(
            builder,
            new DelegateIndexMapperBuilder(builder.axisX()),
            new StretcherIndexMapperBuilder(builder.axisY(), y, minRepetition),
            new DelegateIndexMapperBuilder(builder.axisZ())
        );
    }

    public static ResizedStructureBuilder stretchY(ResizedStructureBuilder builder, int y, int minRepetition, int maxRepetition) {
        return new DefaultResizedStructureBuilder(
            builder,
            new DelegateIndexMapperBuilder(builder.axisX()),
            new StretcherIndexMapperBuilder(builder.axisY(), y, minRepetition),
            new DelegateIndexMapperBuilder(builder.axisZ())
        );
    }

    public static ResizedStructureBuilder stretchZ(ResizedStructureBuilder builder, int z, int minRepetition) {
        return new DefaultResizedStructureBuilder(
            builder,
            new DelegateIndexMapperBuilder(builder.axisX()),
            new DelegateIndexMapperBuilder(builder.axisY()),
            new StretcherIndexMapperBuilder(builder.axisZ(), z, minRepetition)
        );
    }

    public static ResizedStructureBuilder stretchZ(ResizedStructureBuilder builder, int z, int minRepetition, int maxRepetition) {
        return new DefaultResizedStructureBuilder(
            builder,
            new DelegateIndexMapperBuilder(builder.axisX()),
            new DelegateIndexMapperBuilder(builder.axisY()),
            new StretcherIndexMapperBuilder(builder.axisZ(), z, minRepetition)
        );
    }

    public static ResizedStructureBuilder repeatX(ResizedStructureBuilder builder, int minOccur) {
        return new DefaultResizedStructureBuilder(
            builder,
            new EqualizerIndexMapperBuilder(builder.axisX(), minOccur),
            new DelegateIndexMapperBuilder(builder.axisY()),
            new DelegateIndexMapperBuilder(builder.axisZ())
        );
    }

    public static ResizedStructureBuilder repeatY(ResizedStructureBuilder builder, int minOccur) {
        return new DefaultResizedStructureBuilder(
            builder,
            new DelegateIndexMapperBuilder(builder.axisX()),
            new EqualizerIndexMapperBuilder(builder.axisY(), minOccur),
            new DelegateIndexMapperBuilder(builder.axisZ())
        );
    }

    public static ResizedStructureBuilder repeatZ(ResizedStructureBuilder builder, int minOccur) {
        return new DefaultResizedStructureBuilder(
            builder,
            new DelegateIndexMapperBuilder(builder.axisX()),
            new DelegateIndexMapperBuilder(builder.axisY()),
            new EqualizerIndexMapperBuilder(builder.axisZ(), minOccur)
        );
    }

    public static ResizedStructureBuilder priorityX(ResizedStructureBuilder[] builders, int[] priority){
        if (builders.length == 0 || builders.length != priority.length)
            throw new RuntimeException("tab length do not match");
        ResizedStructureBuilderProvider provider = (i, j, k) -> {return builders[i];};
        IndexMapperBuilder[] tabX = Arrays.stream(builders).map(ResizedStructureBuilder::axisX).toArray(IndexMapperBuilder[]::new);
        IndexMapperBuilder[] tabY = Arrays.stream(builders).map(ResizedStructureBuilder::axisY).toArray(IndexMapperBuilder[]::new);
        IndexMapperBuilder[] tabZ = Arrays.stream(builders).map(ResizedStructureBuilder::axisZ).toArray(IndexMapperBuilder[]::new);
        return new DefaultResizedStructureBuilder(
            provider,
            new PriorityRepartitionIndexMapperBuilder(tabX, priority),
            new SuperDelegateIndexMapperBuilder(tabY),
            new SuperDelegateIndexMapperBuilder(tabZ)
        );
    }

    @FunctionalInterface
    private interface ResizedStructureBuilderProvider {
        ResizedStructureBuilder get(int i, int j, int k);
    }
}
