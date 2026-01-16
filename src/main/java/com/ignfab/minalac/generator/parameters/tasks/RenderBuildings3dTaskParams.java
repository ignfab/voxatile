package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.models.BuildingSurfaceType;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.tasks.RenderBuildings3dTask;
import com.ignfab.minalac.generator.tasks.TileTask;

public class RenderBuildings3dTaskParams extends ModelTaskParams {
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams place;

    @JsonSetter(nulls = Nulls.FAIL)
    public Type surfaceType;

    @ConstructorProperties({"place", "surfaceType"})
    public RenderBuildings3dTaskParams(PlaceableParams place, Type surfaceType) {
        this.place = place;
        this.surfaceType = surfaceType;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();
        place.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        return new RenderBuildings3dTask(
            models.create(),
            place.create(generation.seed()),
            surfaceType.create()
        );
    }

    public enum Type {
        @JsonProperty("ground")
        GROUND(BuildingSurfaceType.GROUND),

        @JsonProperty("wall")
        WALL(BuildingSurfaceType.WALL),

        @JsonProperty("roof")
        ROOF(BuildingSurfaceType.ROOF);

        private final BuildingSurfaceType type;

        Type(BuildingSurfaceType type) {
            this.type = type;
        }
        public BuildingSurfaceType create() {
            return type;
        }
    }
}
