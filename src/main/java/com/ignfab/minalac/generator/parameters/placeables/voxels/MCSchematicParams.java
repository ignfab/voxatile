package com.ignfab.minalac.generator.parameters.placeables.voxels;

import java.beans.ConstructorProperties;
import java.io.File;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.outputs.minecraft.MCSchematic;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.placeables.Placeable;
import com.ignfab.minalac.generator.utils.FileHelpers;
import com.ignfab.minalac.generator.utils.random.Seed;
import com.ignfab.minalac.generator.utils.world3d.WorldCoords3d;

public class MCSchematicParams extends PlaceableParams {
    @JsonSetter(nulls = Nulls.FAIL)
    public String file;

    public boolean excludeAir = false;

    public int xOffset = 0;

    public int yOffset = 0;

    public int zOffset = 0;

    @ConstructorProperties("file")
    public MCSchematicParams(String file) {
        this.file = file;
    }

    @Override
    public Placeable create(Seed seed) {
        File schematic = new File(file);
        if (!FileHelpers.isReadableRegularFile(schematic))
            throw new IllegalArgumentException("Schematic file \"%s\" does not exist".formatted(schematic.getAbsolutePath()));
        return new MCSchematic(schematic, excludeAir, new WorldCoords3d(xOffset, yOffset, zOffset));
    }
}
