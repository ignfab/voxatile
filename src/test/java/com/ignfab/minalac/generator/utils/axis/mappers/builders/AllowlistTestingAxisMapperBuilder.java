package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import java.util.Arrays;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;

public class AllowlistTestingAxisMapperBuilder implements AxisMapperBuilder {
    private final int[] allowedLength;
    private final int origin;

    public AllowlistTestingAxisMapperBuilder(int[] allowedLength, int origin) {
        this.allowedLength = allowedLength;
        this.origin = origin;
        Arrays.sort(this.allowedLength);
        if (this.allowedLength[0] < 0)
            throw new RuntimeException("Can not contain negative length");
    }

    public AllowlistTestingAxisMapperBuilder(int... allowedLength) {
        this(allowedLength, 0);
    }

    @Override
    public AxisMapper build(int size) throws UnbuildableException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int maxSizeUnder(int size) {
        if (size < allowedLength[0])
            return -1;
        for (int i = allowedLength.length - 1; i >= 0; i--)
            if (allowedLength[i] <= size)
                return allowedLength[i];
        throw new RuntimeException("It should not happen");
    }

    @Override
    public int minimumSize() {
        return allowedLength[0];
    }

    @Override
    public int origin() {
        return origin;
    }
}
