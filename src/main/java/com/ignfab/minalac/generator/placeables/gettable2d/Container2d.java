package com.ignfab.minalac.generator.placeables.gettable2d;

import java.util.ArrayList;
import java.util.List;

import com.ignfab.minalac.generator.placeables.Nothing;
import com.ignfab.minalac.generator.placeables.Placeable;

/**
 * A {@link DimensionedGettable2d} that can contain other dimensioned gettables.
 */
public class Container2d extends DimensionedGettable2d {
    private final List<PositionedGettable> children = new ArrayList<>();

    /**
     * Creates a new {@code Container2d}.
     * @param sizeX length along x-axis.
     * @param sizeY length along y-axis.
     */
    public Container2d(int sizeX, int sizeY) {
        super(sizeX, sizeY);
    }

    /**
     * Adds a {@link DimensionedGettable2d} to this container.
     *
     * @param element the dimensioned gettable to add
     * @param x the relative x-position
     * @param y the relative y-position
     */
    public void add(DimensionedGettable2d element, int x, int y) {
        // TODO-PR: Prevent overlapping
        // TODO-PR: Should probably use a WorldBBOX
        if (contains(x, x + element.sizeFirstAxis - 1, y, y + element.sizeSecondAxis - 1))
            children.add(new PositionedGettable(element, x, y));
    }

    private boolean contains(int xStart, int xEnd, int yStart, int yEnd) {
        return 0 <= xStart && xStart < sizeFirstAxis
            && 0 <= xEnd && xEnd < sizeFirstAxis
            && 0 <= yStart && yStart < sizeSecondAxis
            && 0 <= yEnd && yEnd < sizeSecondAxis;

    }

    @Override
    public Placeable get(int u, int v) {
        for (PositionedGettable child : children) {
            if ((child.x <= u) && (u < child.x + child.sizeFirstAxis) && (child.y <= v) && (v < child.y + child.sizeSecondAxis))
                return child.get(u, v);
        }
        return Nothing.INSTANCE;
    }

    private static class PositionedGettable extends DimensionedGettable2d {
        private final DimensionedGettable2d element;
        private final int x;
        private final int y;

        PositionedGettable(DimensionedGettable2d element, int x, int y) {
            super(element.sizeFirstAxis, element.sizeSecondAxis);
            this.element = element;
            this.x = x;
            this.y = y;
        }

        @Override
        public Placeable get(int u, int v) {
            return element.get(u - x, v - y);
        }
    }
}
