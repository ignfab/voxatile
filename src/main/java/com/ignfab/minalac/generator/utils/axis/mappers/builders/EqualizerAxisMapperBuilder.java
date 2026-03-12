package com.ignfab.minalac.generator.utils.axis.mappers.builders;

import java.util.Arrays;

import com.ignfab.minalac.generator.exceptions.UnbuildableException;
import com.ignfab.minalac.generator.utils.axis.mappers.AxisMapper;
import com.ignfab.minalac.generator.utils.axis.mappers.SizesAxisMapper;
/**
 * An {@link AxisMapperBuilder} that repeats underlying {@link AxisMapperBuilder} as many times as possible.
 */
public class EqualizerAxisMapperBuilder implements AxisMapperBuilder {
    private final AxisMapperBuilder underlying;
    private final int minSize;

    /**
     * Creates a new {@code EqualizerAxisMapperBuilder}.
     *
     * @param underlying Undelying {@link AxisMapperBuilder} to repeat
     * @param minOccur Minimum number of occurences
     */
    public EqualizerAxisMapperBuilder(AxisMapperBuilder underlying, int minOccur) {
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

    // underlyingMin should be strictly positive
    // TODO-12 : Revoir cette partie : elle est testée à la louche
    private DistributionResult compute(int size, int underlyingMin) {
        int n = size / underlyingMin;
        int remainder = size % underlyingMin;

        int[] lengths = new int[n];
        Arrays.fill(lengths, underlyingMin);

        for (int i = 0; i < n; i++) {
            int subRemainder = (remainder % n == 0) ? remainder / n : (remainder / n) + 1;
            int maxPossibleSize = underlying.maxSizeUnder(underlyingMin + subRemainder);
            if (lengths[i] < maxPossibleSize) {
                int added = maxPossibleSize - lengths[i];
                lengths[i] = maxPossibleSize;
                remainder = remainder - added;
            }
            n--;
        }

        return new DistributionResult(lengths, remainder);
    }

    private record DistributionResult(int[] lengths, int remainder) {
    }
}
