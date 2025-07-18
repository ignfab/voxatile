package com.ignfab.minalac.generator.utils;

import java.util.Arrays;

/**
 * The {@code Segmenter} divides a segment of a given length into multiple subsegment of a preferred length.
 *
 * If the segment cannot be evenly divided into subsegments of the preferred length, the remainder is distributed as evenly as possible among the subsegments.
 * This distribution starts from the centered subsegment and alternates in a zigzag pattern.
 */
public class Segmenter {
    private final int[] segmentsLength;
    private final int totalLength;

    /**
     * Creates a new {@link Segmenter}.
     *
     * @param totalLength the length of the original segment
     * @param segmentPreferredLength the preferred length of the subsegments
     */
    public Segmenter(int totalLength, int segmentPreferredLength) {
        if (totalLength < segmentPreferredLength)
            throw new IllegalArgumentException("totalLength can not be less that segmentPreferredLength");
        if (totalLength <= 0 || segmentPreferredLength <= 0)
            throw new IllegalArgumentException("Arguments must be positive.");

        this.totalLength = totalLength;
        segmentsLength = new int[totalLength / segmentPreferredLength];
        Arrays.fill(segmentsLength, segmentPreferredLength);
        distributeRemainder(totalLength % segmentPreferredLength);
    }

    /**
     * Returns the corresponding subsegment from the index of the original segment.
     * It contains the length of the subsegment and the corresponding relative index.
     *
     * @param originSegmentIndex the index within the original segment
     * @return the corresponding {@link Segment}
     */
    public Segment getSegment(int originSegmentIndex) {
        if (originSegmentIndex < 0 || originSegmentIndex > totalLength)
            throw new IndexOutOfBoundsException(String.format("Index should be between 0 and %d", totalLength));
        // TODO-PR: Should be improved (null and for-loop)
        int sum = 0;
        for (int k : segmentsLength) {
            sum = sum + k;
            if (originSegmentIndex < sum)
                return new Segment(originSegmentIndex - (sum - k), k);
        }
        return null;
    }

    private static int centeredIndex(int i, int middleIndex) {
        if (i % 2 == 0)
            return middleIndex + ((i + 1) / 2);
        else
            return middleIndex - ((i + 1) / 2);
    }

    private void distributeRemainder(int r) {
        int i = 0;
        int m = segmentsLength.length / 2;
        int toAdd;
        while (r > 0 && i < segmentsLength.length) {
            int j = centeredIndex(i, m);
            toAdd = roundIfRemainder(r, segmentsLength.length - i);
            segmentsLength[j] = segmentsLength[j] + toAdd;
            r = r - toAdd;
            i++;
        }
    }

    private int roundIfRemainder(int a, int b) {
        return (a % b) > 0 ? (a / b) + 1 : (a / b);
    }

    /**
     * A segment that is defined by its index and length.
     *
     * @param index the current index of this segment.
     * @param length the total length of this segment.
     */
    public record Segment(Integer index, Integer length) {
    }
}
