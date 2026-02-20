package com.ignfab.minalac.generator.parameters.placeables.resized;

import java.util.List;

import com.ignfab.minalac.generator.placeables.resized.DefaultResizedStructureBuilder;
import com.ignfab.minalac.generator.placeables.resized.ResizedStructureBuilder;
import com.ignfab.minalac.generator.utils.random.Seed;

public class DistributedByPriorityStructureBuilderParams extends ResizedStructureBuilderParams {
    public List<PriorityStructureBuilderParams> distributedByPriority;
    public String axis;

    public void validate() {
        if (!(axis.equals("x") || axis.equals("y") ||axis.equals("z")))
            throw new IllegalArgumentException("Temp: must x");
        if (distributedByPriority.isEmpty())
            throw new IllegalArgumentException("Cannot be empty");
    }

    @Override
    public ResizedStructureBuilder create(Seed seed) {
        ResizedStructureBuilder[] builders = new ResizedStructureBuilder[distributedByPriority.size()];
        int[] priority = new int[distributedByPriority.size()];
        for (int i = 0; i < distributedByPriority.size(); i++) {
            builders[i] = distributedByPriority.get(i).resized.create(seed);
            priority[i] = distributedByPriority.get(i).priority;
        }
        switch (axis) {
            case "x":
                return DefaultResizedStructureBuilder.priorityX(builders, priority);
            case "y":
                return DefaultResizedStructureBuilder.priorityY(builders, priority);
            case "z":
                return DefaultResizedStructureBuilder.priorityZ(builders, priority);
        }
        throw new IllegalArgumentException("");
    }

    public static class PriorityStructureBuilderParams {
        public ResizedStructureBuilderParams resized;
        public int priority;
    }
}
