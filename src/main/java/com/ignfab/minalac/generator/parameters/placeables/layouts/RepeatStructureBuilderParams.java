package com.ignfab.minalac.generator.parameters.placeables.layouts;

import java.util.List;

import com.ignfab.minalac.generator.placeables.resized.DefaultResizedStructureBuilder;
import com.ignfab.minalac.generator.placeables.resized.ResizedStructureBuilder;
import com.ignfab.minalac.generator.utils.random.Seed;

public class RepeatStructureBuilderParams extends AxisStructureBuilderParams{
    public AxisStructureBuilderParams repeated;
    public List<String> axis;
    public List<Integer> minRepetition;

    @Override
    public void validate() {
        if (axis.size() != minRepetition.size() || axis.isEmpty() || axis.size() > 3)
            throw new IllegalArgumentException("Bad request");
        for (String s: axis)
            if (!(s.equals("x") || s.equals("y")|| s.equals("z")))
                throw new IllegalArgumentException("Bad axes");
        for (Integer o : minRepetition)
            if (o <= 0)
                throw new IllegalArgumentException("Bad minOccur");
    }
    @Override
    public ResizedStructureBuilder create(Seed seed) {
        ResizedStructureBuilder toReturn = repeated.create(seed);
        for (int i = 0; i < axis.size(); i++) {
            if (axis.get(i).equals("x"))
                toReturn = DefaultResizedStructureBuilder.repeatX(toReturn, minRepetition.get(i));
            if (axis.get(i).equals("y"))
                toReturn = DefaultResizedStructureBuilder.repeatY(toReturn, minRepetition.get(i));
            if (axis.get(i).equals("z"))
                toReturn = DefaultResizedStructureBuilder.repeatZ(toReturn, minRepetition.get(i));
        }

        return toReturn;
    }
}
