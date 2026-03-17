package com.ignfab.minalac.generator.parameters.placeables.layouts;

import java.util.List;

import com.ignfab.minalac.generator.placeables.layouts.DefaultLayoutBuilder;
import com.ignfab.minalac.generator.placeables.layouts.LayoutBuilder;
import com.ignfab.minalac.generator.parameters.utils.AxisParams;
import com.ignfab.minalac.generator.utils.random.Seed;

public class DistributedByPriorityStructureBuilderParams extends LayoutBuilderParams {
    public List<PriorityStructureBuilderParams> distributedByPriority;
    AxisParams axis;

    @Override
    public void validate() {
        if (distributedByPriority.isEmpty())
            throw new IllegalArgumentException("Cannot be empty");
    }

    @Override
    public LayoutBuilder create(Seed seed) {
        LayoutBuilder[] builders = new LayoutBuilder[distributedByPriority.size()];
        int[] priorities = new int[distributedByPriority.size()];
        for (int i = 0; i < distributedByPriority.size(); i++) {
            builders[i] = distributedByPriority.get(i).builder.create(seed);
            priorities[i] = distributedByPriority.get(i).priority;
        }
        return DefaultLayoutBuilder.priority(builders, axis.create(), priorities);
    }

    public static class PriorityStructureBuilderParams {
        public LayoutBuilderParams builder;
        public int priority;
    }
}
