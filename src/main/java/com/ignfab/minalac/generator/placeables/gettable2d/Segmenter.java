package com.ignfab.minalac.generator.placeables.gettable2d;

import java.util.Arrays;

import com.ignfab.minalac.generator.placeables.SegmentCapability;

public final class Segmenter {
    private final int[] segmentsLength;
    private final int totalLength;
    private final SegmentCapability capability;

    private Segmenter(int totalLength, int segmentPreferredLength, SegmentCapability capability) {
        if (totalLength < segmentPreferredLength) {
            // throw new IllegalArgumentException("totalLength can not be less that segmentPreferredLength");
            // Temporary
            totalLength = segmentPreferredLength;
        }
        if (totalLength <= 0 || segmentPreferredLength <= 0)
            throw new IllegalArgumentException("Arguments must be positive.");

        this.totalLength = totalLength;
        this.capability = capability;

        segmentsLength = new int[totalLength / segmentPreferredLength];
        Arrays.fill(segmentsLength, segmentPreferredLength);
        distributeRemainder(totalLength % segmentPreferredLength);
    }

    public static Segmenter repeat(int totalLength, SegmentCapability capability) {
        return new Segmenter(totalLength, capability.minimalLength(), capability);
    }

    public static Segmenter extend(int totalLength, SegmentCapability capability) {
        return new Segmenter(totalLength, totalLength, capability);
    }

    public static Segmenter same(SegmentCapability capability) {
        return new Segmenter(capability.minimalLength(), capability.minimalLength(), capability);
    }

    public Integer get(int i) {
        Segment s = this.getSegment(i);
        if (s == null)
            return null;
        return capability.atIndex(s.index, s.length);
    }

    private Segment getSegment(int originSegmentIndex) {
        if (originSegmentIndex < 0 || originSegmentIndex > totalLength)
            // TODO-PR: Maybe should return null instead
            throw new IndexOutOfBoundsException(String.format("Index should be between 0 and %d, provided: %d", totalLength, originSegmentIndex));
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

    private record Segment(Integer index, Integer length) {
    }
}
