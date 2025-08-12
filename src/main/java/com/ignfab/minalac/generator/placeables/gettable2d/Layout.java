package com.ignfab.minalac.generator.placeables.gettable2d;

import com.ignfab.minalac.generator.placeables.Nothing;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.utils.world2d.WorldBBox2d;

public abstract class Layout implements Gettable2d {
    private final WorldBBox2d bbox;
    private final PlaceableStructure structure;
    private final Segmenter segmenterX;
    private final Segmenter segmenterZ;

    protected Layout(WorldBBox2d bbox, PlaceableStructure structure, Segmenter segmenterX, Segmenter segmenterZ) {
        this.bbox = bbox;
        this.structure = structure;
        this.segmenterX = segmenterX;
        this.segmenterZ = segmenterZ;
    }

    @Override
    public Placeable get(int u, int v) {
        Integer x = segmenterX.get(u - bbox.minX());
        Integer z = segmenterZ.get(v - bbox.minY());

        if (x == null || z == null)
            return Nothing.INSTANCE;

        return structure.get(x, 0, z);
    }

    public WorldBBox2d bbox() {
        return bbox;
    }

    public static class RepeatX extends Layout {

        public RepeatX(WorldBBox2d bbox, PlaceableStructure structure) {
            super(
                bbox,
                structure,
                Segmenter.repeat(bbox.sizeX(), structure.axisX()),
                // NewSegmenter.extend(bbox.sizeY(), structure.axisZ())
                (structure.axisZ().isExpendable() ? Segmenter.extend(bbox.sizeY(), structure.axisZ()) : Segmenter.same(structure.axisZ()))
            );
            // TODO-PR: Unsure it should check
            //  Can be repeated even if not extendable
            //  Can not be extended if not extendable
        }
    }

    public static class RepeatZ extends Layout {

        public RepeatZ(WorldBBox2d bbox, PlaceableStructure structure) {
            super(
                bbox,
                structure,
                // NewSegmenter.extend(bbox.sizeX(), structure.axisX()),
                (structure.axisX().isExpendable() ? Segmenter.extend(bbox.sizeX(), structure.axisX()) : Segmenter.same(structure.axisX())),
                Segmenter.repeat(bbox.sizeY(), structure.axisZ())
            );
        }
    }

    public static class RepeatXZ extends Layout {

        public RepeatXZ(WorldBBox2d bbox, PlaceableStructure structure) {
            super(
                bbox,
                structure,
                Segmenter.repeat(bbox.sizeX(), structure.axisX()),
                Segmenter.repeat(bbox.sizeY(), structure.axisZ())
            );
        }
    }
}
