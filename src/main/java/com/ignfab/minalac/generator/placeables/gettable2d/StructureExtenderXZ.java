package com.ignfab.minalac.generator.placeables.gettable2d;

import com.ignfab.minalac.generator.placeables.Nothing;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.utils.Segmenter;

// TODO-PR: Should probably be generalized in 3d ?
/**
 * This class extends and or repeats a {@link PlaceableStructure} along x-axis and z-axis.
 * There are two parameters that controls how this extension/repetition operates: {@code preferredLength} and {@code totalLength}.
 * {@code preferredLength} and {@code totalLength} determines together how many times the structure is repeated along a certain axis.
 * <ul>
 *     <li>{@code preferredLength} defines the ideal length, along a given axis, of each repeated structure. It therefore also controls how a structure is extended.</li>
 *     <li>{@code totalLength} is the total length along a given axis.</li>
 * </ul>
 * For example, along the x-axis, assuming we have a structure A that is extendable and is 2 units long. If the {@code totalLength} is 9 and the
 * {@code preferredLength} is 4, the structure will be repeated twice, one structure will be extended to 4 units and the other to 5 units.
 * @see Segmenter
 * @see PlaceableStructure
 */
public class StructureExtenderXZ extends DimensionedGettable2d {
    private final Segmenter segmenterX;
    private final Segmenter segmenterZ;
    private final PlaceableStructure structure;

    /**
     * Creates a new {@link StructureExtenderXZ}.
     * It does not check whether the provided arguments are compatible with extendability.
     * @param totalLengthX total length along x-axis
     * @param totalLengthZ total length along z-axis
     * @param preferredLengthX the length the structure should take within the total length along x-axis
     * @param preferredLengthZ the length the structure should take within the total length along z-axis
     * @param structure the structure to extend and or repeat
     */
    public StructureExtenderXZ(int totalLengthX, int totalLengthZ, int preferredLengthX, int preferredLengthZ, PlaceableStructure structure) {
        super(totalLengthX, totalLengthZ);
        segmenterX = create(totalLengthX, preferredLengthX);
        segmenterZ = create(totalLengthZ, preferredLengthZ);
        this.structure = structure;
    }

    private Segmenter create(int totalLength, int preferredLength) {
        // TODO-PR: Might not be appropriate to handle it that way (But should not be the Segmenter nor the Structure) [See comment on RenderFacadesTask run() method]
        if (totalLength < preferredLength)
            return new Segmenter(preferredLength, preferredLength);
        return new Segmenter(totalLength, preferredLength);
    }

    @Override
    public Placeable get(int u, int v) {
        Segmenter.Segment segmentX = segmenterX.getSegment(u);
        Segmenter.Segment segmentZ = segmenterZ.getSegment(v);

        if (segmentX == null || segmentZ == null)
            return Nothing.INSTANCE;

        int x = structure.axisX().atIndex(segmentX.index(), segmentX.length());
        int z = structure.axisZ().atIndex(segmentZ.index(), segmentZ.length());

        return structure.get(x, 0, z);
    }
}
