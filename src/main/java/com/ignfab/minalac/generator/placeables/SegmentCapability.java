package com.ignfab.minalac.generator.placeables;

// TODO-PR: Change class name?
/**
 * Represents the capabilities of a segment. A segment has a minimal length and may be expendable.
 */
public abstract class SegmentCapability {
    /**
     *  The coordinate value at which this segment begins.
     */
    protected int startCoordinate;
    /**
     * The minimal length of this segment.
     */
    protected int minimalLength;

    protected SegmentCapability(int startCoordinate, int minimalLength) {
        if (minimalLength < 0)
            throw new IllegalArgumentException("Minimal length must be positive");
        this.startCoordinate = startCoordinate;
        this.minimalLength = minimalLength;
    }

    /**
     * Returns the coordinate corresponding to the given index within this segment.
     * The segment may expand according to the specified length.
     *
     * @param index the relative position within the segment (e.g. 0, 1, 2, ...)
     * @param wantedLength the total length that this segment should occupy
     * @return the coordinate corresponding to the provided arguments.
     */
    public int atIndex(int index, int wantedLength) {
        if (index < 0 || wantedLength < 0)
            throw new IllegalArgumentException("index and wantedSize must be positive");
        if (index >= wantedLength)
            throw new IllegalArgumentException("index must be smaller than wantedLength");
        return compute(index + startCoordinate, wantedLength);
    }

    /**
     * Returns the minimal length of this segment.
     *
     * @return the {@code minimalLength}
     */
    public int minimalLength() {
        return minimalLength;
    }

    /**
     * Tells if this segment is expendable.
     *
     * @return {@code true} if this segment is expendable.
     */
    public abstract boolean isExpendable();

    protected abstract int compute(int iCoordinate, int wantedLength);

    /**
     * An {@link SegmentCapability} that is unexpandable.
     */
    public static class NonExpendable extends SegmentCapability {

        /**
         * Constructs a new {@code AxisSegmentCapability} that is unexpandable.
         *
         * @param startCoordinate the coordinate at which this segment begins
         * @param length the length of this segment
         */
        public NonExpendable(int startCoordinate, int length) {
            super(startCoordinate, length);
        }

        @Override
        public boolean isExpendable() {
            return false;
        }

        @Override
        protected int compute(int iCoordinate, int wantedLength) {
            // TODO-PR: What is wantedLength is superior to minimalLength?
            return iCoordinate;
        }
    }

    /**
     * An {@link SegmentCapability} that is expandable. The extension is done by repeating a certain element of this segment.
     */
    public static class Expendable extends SegmentCapability {
        private final int extendableCoordinateValue;

        /**
         * Constructs a new {@code Expendable} {@code AxisSegmentCapability}.
         *
         * @param startCoordinate the coordinate at which this segment begins
         * @param extendableCoordinateValue the coordinate value of the element of this segment that will be repeated when expended.
         * @param minimalLength the length when there is not any repetition
         */
        public Expendable(int startCoordinate, int extendableCoordinateValue, int minimalLength) {
            super(startCoordinate, minimalLength);
            if (extendableCoordinateValue < startCoordinate || extendableCoordinateValue > startCoordinate + minimalLength)
                throw new IllegalArgumentException("extendableIndexCoordinate is incompatible with provided arguments.");
            this.extendableCoordinateValue = extendableCoordinateValue;
        }

        @Override
        public boolean isExpendable() {
            return true;
        }

        @Override
        protected int compute(int coordinateValue, int wantedLength) {
            // Goes from -1 to (n - 1) where n is the number of repetition
            // -1 since extendableCoordinateValue is the coordinate value when there is 1 repetition
            int r = Math.max(wantedLength - (minimalLength + 1), -1);
            if (coordinateValue < extendableCoordinateValue) {
                return coordinateValue;
            } else if (coordinateValue <= extendableCoordinateValue + r) {
                return extendableCoordinateValue;
            } else {
                return coordinateValue - r;
            }
        }
    }
}
