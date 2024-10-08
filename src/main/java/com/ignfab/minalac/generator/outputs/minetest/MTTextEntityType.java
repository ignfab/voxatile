package com.ignfab.minalac.generator.outputs.minetest;

import com.ignfab.minalac.generator.outputs.minetest.mod.FloatingTextsLuaMod;
import com.ignfab.minalac.generator.world.EntityType;

public class MTTextEntityType implements EntityType {
    private final FloatingTextsLuaMod mod;
    private final String text;

    public MTTextEntityType(FloatingTextsLuaMod mod, String text) {
        this.mod = mod;
        this.text = text;
    }

    @Override
    public void place(double x, double y, double z) {
        mod.addText(x, y, z, text);
    }
}
