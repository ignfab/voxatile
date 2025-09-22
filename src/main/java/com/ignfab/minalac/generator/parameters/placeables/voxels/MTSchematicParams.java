package com.ignfab.minalac.generator.parameters.placeables.voxels;

import java.beans.ConstructorProperties;
import java.io.File;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.outputs.minetest.MTSchematic;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.FileHelpers;
import com.ignfab.minalac.generator.utils.random.Seed;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

public class MTSchematicParams extends PlaceableParams {
    @JsonSetter(nulls = Nulls.FAIL)
    public String file;

    public int xOffset = 0;

    public int yOffset = 0;

    public int zOffset = 0;

    @JsonSetter(nulls = Nulls.SKIP)
    public String seed = "";

    @ConstructorProperties("file")
    public MTSchematicParams(String file) {
        this.file = file;
    }

    @Override
    public Placeable create(Seed seed) {
        File schematic = new File(file);
        if (!FileHelpers.isReadableRegularFile(schematic))
            throw new IllegalArgumentException("Schematic file \"%s\" does not exist".formatted(schematic.getAbsolutePath()));
        return new MTSchematic(schematic, new WorldCoords3d(xOffset, yOffset, zOffset), seed.salt(this.seed));
    }
}
