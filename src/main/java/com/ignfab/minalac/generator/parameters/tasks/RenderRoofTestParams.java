package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.tasks.RenderRoofTask.RoofType;
import com.ignfab.minalac.generator.tasks.RenderRoofTest;
import com.ignfab.minalac.generator.tasks.TileTask;

public class RenderRoofTestParams extends TileTaskParams {

    public enum RoofTypeParams {

        @JsonProperty("flat")
        FLAT(RoofType.FLAT),

        @JsonProperty("hipped")
        HIPPED(RoofType.HIPPED);

        private final RoofType roofType;

        RoofTypeParams(RoofType roofType) {
            this.roofType = roofType;
        }

        public RoofType create() {
            return roofType;
        }
    }
    /**
     * Building altitude and height metadata names
     *
     * TODO: Compute that upstream
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String altitude;
    @JsonSetter(nulls = Nulls.FAIL)
    public String height;
    /**
     * What to place on roof.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams place;

    @JsonSetter(nulls = Nulls.FAIL)
    RoofTypeParams roofType;

    public boolean applyF = false;

    @ConstructorProperties({"place", "roofType", "altitude", "height"})
    public RenderRoofTestParams(PlaceableParams place, RoofTypeParams roofType, String altitude, String height) {
        this.place = place;
        this.roofType = roofType;
        this.altitude = altitude;
        this.height = height;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        if (altitude.isBlank())
            throw new IllegalArgumentException("Altitude metadata cannot be blank");
        if (height.isBlank())
            throw new IllegalArgumentException("Height metadata cannot be blank");
        place.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        return new RenderRoofTest(
            roofType.create(),
            altitude,
            height,
            place.create(generation.seed()),
            applyF
        );
    }
}
