package fr.ign.voxatile.core.parameters.placeables.voxels;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.parameters.placeables.PlaceableParams;
import fr.ign.voxatile.core.placeables.Placeable;
import fr.ign.voxatile.core.utils.random.Seed;
import fr.ign.voxatile.core.world.TestingVoxel;

/**
 * Voxel parameters for testing with name only.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE) // Prevents default deserializer from requiring type when using "default" placeable param structure
public class TestingVoxelParams extends PlaceableParams {
    /**
     * Voxel name (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String name;

    /**
     * Creates a new {@code TestingVoxelParams}.
     *
     * @param name Voxel name
     */
    @ConstructorProperties({"name"})
    public TestingVoxelParams(String name) {
        this.name = name;
    }

    @Override
    public void validate() {
        if (name.isBlank())
            throw new IllegalArgumentException();
    }

    @Override
    public Placeable create(Seed seed) {
        return new TestingVoxel(name);
    }
}
