package com.ignfab.minalac.generator.parameters.placeables.layouts;

import java.util.List;

import com.ignfab.minalac.generator.parameters.placeables.structures.PlaceableStructureParams;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.placeables.layouts.DefaultLayoutBuilder;
import com.ignfab.minalac.generator.placeables.layouts.LayoutBuilder;
import com.ignfab.minalac.generator.utils.axis.Axis;
import com.ignfab.minalac.generator.utils.random.Seed;

public class StretchedStructureBuilderParams extends LayoutBuilderParams {
    public PlaceableStructureParams stretched;
    public ElasticAtParams elasticAt;

    public void validate() {
        stretched.validate();
        if (elasticAt.x != null)
            validateElastic(elasticAt.x);
        if (elasticAt.y != null)
            validateElastic(elasticAt.y);
        if (elasticAt.z != null)
            validateElastic(elasticAt.z);
    }

    private void validateElastic(List<Integer> axe) {
        if (axe.isEmpty())
            throw new IllegalArgumentException("Bad request : elastic axe can not be null or empty");
        if (axe.size() > 2 )
            throw new IllegalArgumentException("Bad request : elastic axe too much arg");
        if (axe.get(1) < 0)
            throw new IllegalArgumentException("Bad request : min repeti must be postive");
    }

    public LayoutBuilder create(Seed seed) {
        PlaceableStructure structure = stretched.create(seed);
        LayoutBuilder builder = structure.toLayoutBuilder();
        if (elasticAt.x !=  null) {
            int elasticAtValue = elasticAt.x.get(0);
            int minRepetition = elasticAt.x.get(1);
            if (elasticAtValue > structure.limits().maxX() || elasticAtValue < structure.limits().minX())
                throw new UnsupportedOperationException("Not inside");
            if (minRepetition == 0 && structure.limits().sizeX() <= 1)
                throw new UnsupportedOperationException("TODO-12");
            builder = DefaultLayoutBuilder.stretch(builder, Axis.X, elasticAtValue, minRepetition, Integer.MAX_VALUE);
        }

        if (elasticAt.y !=  null) {
            int elasticAtValue = elasticAt.y.get(0);
            int minRepetition = elasticAt.y.get(1);
            if (elasticAtValue > structure.limits().maxY() || elasticAtValue < structure.limits().minY())
                throw new UnsupportedOperationException("Not inside");
            if (minRepetition == 0 && structure.limits().sizeY() <= 1)
                throw new UnsupportedOperationException("TODO-12");
            builder = DefaultLayoutBuilder.stretch(builder, Axis.Y, elasticAtValue, minRepetition, Integer.MAX_VALUE);
        }

        if (elasticAt.z !=  null) {
            int elasticAtValue = elasticAt.z.get(0);
            int minRepetition = elasticAt.z.get(1);
            if (elasticAtValue > structure.limits().maxZ() || elasticAtValue < structure.limits().minZ())
                throw new UnsupportedOperationException("Not inside");
            if (minRepetition == 0 && structure.limits().sizeZ() <= 1)
                throw new UnsupportedOperationException("TODO-12");
            builder = DefaultLayoutBuilder.stretch(builder, Axis.Z, elasticAtValue, minRepetition, Integer.MAX_VALUE);
        }
        return builder;
    }

    public static class ElasticAtParams {
        public List<Integer> x;
        public List<Integer> y;
        public List<Integer> z;
    }
}
