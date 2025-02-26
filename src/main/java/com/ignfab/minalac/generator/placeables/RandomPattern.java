package com.ignfab.minalac.generator.placeables;

import java.util.Random;

/**
 * x pattern placing something or not according to x simple "dice roll".
 */
public class RandomPattern implements Pattern {
    private final Placeable placeable;
    private final double chance;

    private Random random = new Random(0);

    private static int mix(int x, int y, int z)
    {
      x=x-y;  x=x-z;  x=x^(z >>> 13);
      y=y-z;  y=y-x;  y=y^(x << 8);
      z=z-x;  z=z-y;  z=z^(y >>> 13);
      x=x-y;  x=x-z;  x=x^(z >>> 12);
      y=y-z;  y=y-x;  y=y^(x << 16);
      z=z-x;  z=z-y;  z=z^(y >>> 5);
      x=x-y;  x=x-z;  x=x^(z >>> 3);
      y=y-z;  y=y-x;  y=y^(x << 10);
      z=z-x;  z=z-y;  z=z^(y >>> 15);
      return z;
    }

    /**
     * Creates a new {@code RandomPattern}.
     *
     * @param placeable Placeable to place
     * @param chance Chances to have it placed (from 0.0 = never placed, to 1.0 = always placed)
     */
    public RandomPattern(Placeable placeable, double chance) {
        this.placeable = placeable;
        this.chance = chance;
    }

    @Override
    public Placeable get(int x, int y, int z) {
        random.setSeed(mix(x, y, z));
        if (Math.abs(random.nextDouble()) < chance)
            return placeable;
        return NoVoxel.INSTANCE;
    }
}
