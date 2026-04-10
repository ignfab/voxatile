package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.models.ModelSelectionParams;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.tasks.RenderRoofTask;
import com.ignfab.minalac.generator.tasks.RenderRoofTask.RoofType;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for creating a {@link RenderRoofsTask}.
 */
public class RenderRoofTaskParams extends TileTaskParams {

    public enum RoofTypeParams {

        @JsonProperty("flat")
        FLAT(RenderRoofTask.RoofType.FLAT),

        @JsonProperty("hipped")
        HIPPED(RenderRoofTask.RoofType.HIPPED);

        private final RoofType roofType;

        RoofTypeParams(RoofType roofType) {
            this.roofType = roofType;
        }

        public RoofType create() {
            return roofType;
        }
    }
    /**
     * The type of models to render (required).
     */
    public ModelSelectionParams models;
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

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param models models selection to render.
     * @param heightmap the name of the ground heightmap to use.
     */
    @ConstructorProperties({"models", "place", "roofType", "altitude", "height"})
    public RenderRoofTaskParams(ModelSelectionParams models, PlaceableParams place, RoofTypeParams roofType, String altitude, String height) {
        this.models = models;
        this.place = place;
        this.roofType = roofType;
        this.altitude = altitude;
        this.height = height;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        models.validate();
        if (altitude.isBlank())
            throw new IllegalArgumentException("Altitude metadata cannot be blank");
        if (height.isBlank())
            throw new IllegalArgumentException("Height metadata cannot be blank");
        place.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        return new RenderRoofTask(
            models.create(),
            roofType.create(),
            altitude,
            height,
            place.create(generation.seed())
        );
    }
}
