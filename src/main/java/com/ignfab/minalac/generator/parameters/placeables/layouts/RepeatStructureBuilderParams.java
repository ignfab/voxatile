package com.ignfab.minalac.generator.parameters.placeables.layouts;

import java.util.List;

import com.ignfab.minalac.generator.parameters.utils.AxisParams;
import com.ignfab.minalac.generator.placeables.layouts.DefaultLayoutBuilder;
import com.ignfab.minalac.generator.placeables.layouts.LayoutBuilder;
import com.ignfab.minalac.generator.utils.random.Seed;

public class RepeatStructureBuilderParams extends LayoutBuilderParams{
    public LayoutBuilderParams repeated;
    public List<AxisParams> axes;
    public List<Integer> minRepetition;

    @Override
    public void validate() {
        if (axes.size() != minRepetition.size() || axes.isEmpty() || axes.size() > 3)
            throw new IllegalArgumentException("Bad request");
        for (Integer o : minRepetition)
            if (o <= 0)
                throw new IllegalArgumentException("Bad minOccur");
    }
    @Override
    public LayoutBuilder create(Seed seed) {
        LayoutBuilder result = repeated.create(seed);
        for (int i = 0; i < axes.size(); i++)
            result = DefaultLayoutBuilder.repeat(result, axes.get(i).create(), minRepetition.get(i));

        return result;
    }
}
