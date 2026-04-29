package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import java.util.Arrays;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;
import com.ignfab.minalac.generator.utils.axis.mappers.SizesAxisMapper;

/**
 * An {@link AxisMapperBuilder} that repeats underlying {@link AxisMapperBuilder} as many times as possible.
 */
public class RepeatAxisMapperBuilder implements AxisMapperBuilder {
    private final AxisMapperBuilder underlying;
    private final int minSize;
    private final int maxOccur;

    /**
     * Creates a new {@code EqualizerAxisMapperBuilder}.
     *
     * @param underlying Undelying {@link AxisMapperBuilder} to repeat
     * @param minOccur Minimum number of occurences
     * @param maxOccur Maximum number of occurences
     * @throws UnbuildableException
     */
    public RepeatAxisMapperBuilder(AxisMapperBuilder underlying, int minOccur, int maxOccur) throws UnbuildableException {
        this.maxOccur = maxOccur;
        this.underlying = underlying;
        minSize = underlying.minimumSize() * minOccur;
    }

    @Override
    public AxisMapper build(int size) throws UnbuildableException {
        if (size < minSize)
            throw new UnbuildableException("Requested size is not enough");

        int underlyingMin = underlying.minimumSize();
        // TODO-12 : Initialement j'avais mis que si la talle minimale de underlying était de 0 alors le build était de 0.
        // C'est pas juste, enfin ça depend du choix. Admettons qu'on a un IMB de 0..3, on demande 5
        // Possibilité 1: -> 0 (Mais c'est bizarre de dire ça l'IMB sous jacent peut prendre une taille
        // Possibilité 2: -> 3 / 2 (Mais encore une fois bizarre car peut y avoir IMB avec taille infinie
        // Possibilité 3: -> 1 / 1 / 1 / 1 / 1 ou variante, mais encore une fois c'est bizarre
        // Possibilité 4: Cette classe refuse un IMB de minSize de 0
        // Possibilité 5: Revoir algo de repartition et fare un truc similaire à Priority
        /*
        if (underlyingMin == 0) {
            underlyingMin = computeNonZeroMinimalSize(size);
            if (underlyingMin == 0) return new LengthIndexMapper(0);
        }*/
        // For now let us assume has min size different of zero (Need minSizeOver(0)
        if (underlyingMin == 0) return new SizesAxisMapper(0);

        DistributionResult result = compute(size, underlyingMin);
        int remainder = result.remainder;

        if (remainder != 0)
            throw new UnbuildableException("Requested size iis either not enough or too large. Failed to distribute remainder");

        return new SizesAxisMapper(result.lengths);
    }

    @Override
    public int maxSizeUnder(int size) {
        if (size < minSize)
            return -1;

        int underlyingMin = underlying.minimumSize();
        // Voir TODO-12
        /*
        if (underlyingMin == 0) {
            underlyingMin = computeNonZeroMinimalSize(size);
            if (underlyingMin == 0) return 0;
        }*/
        // For now let us assume has min size different of zero (Need minSizeOver(0)
        if (underlyingMin == 0) return 0;

        DistributionResult result = compute(size, underlyingMin);
        return size - result.remainder;
    }


    @Override
    public int minimumSize() {
        return minSize;
    }

    @Override
    public int origin() {
        return 0;
    }

    // TODO-Z: Clarifier comment ça marche pour des cas tordus, par exemple tailles non continues 2, 12
    private DistributionResult compute(int size, int underlyingMin) {
        // TODO-Z: Prendre en compte les tailles mins de 0
        if (underlyingMin <= 0)
            throw new UnsupportedOperationException("Can not repeat a layout that has a minimal size equal or bellow zero");
        int count = Math.min(size / underlyingMin, maxOccur);
        // Modulo not used since maxOccur can be bellow (size / underlyingMin)
        int remaining = size - count * underlyingMin;

        int[] lengths = new int[count];
        Arrays.fill(lengths, underlyingMin);

        for (int index = 0; index < lengths.length; index++) {
            // Math.ceilDiv not available in Java 17
            int possible = underlying.maxSizeUnder(underlyingMin + (remaining + count - 1) / count);
            // (lengths[index] - possible) = distributedRemaining
            remaining =  remaining + lengths[index] - possible;
            lengths[index] = possible;
            count--;
        }

        return new DistributionResult(lengths, remaining);
    }

    private record DistributionResult(int[] lengths, int remainder) {
    }

}
