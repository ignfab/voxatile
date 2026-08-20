package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.tasks.RenderRoofTask;
import com.ignfab.minalac.generator.tasks.RenderRoofTask.RoofType;
import com.ignfab.minalac.generator.tasks.TileTask;

/**
 * Parameters for creating a {@link RenderRoofsTask} (POC).
 */
public class RenderRoofTaskParams extends ModelTaskParams {

    /**
     * Available type of roofs.
     */
    public enum RoofTypeParams {

        /**
         * Flat roof.
         */
        @JsonProperty("flat")
        FLAT(RenderRoofTask.RoofType.FLAT),

        /**
         * Hipped roof. Each walls have a corresponding roof section.
         */
        @JsonProperty("hipped")
        HIPPED(RenderRoofTask.RoofType.HIPPED);

        private final RoofType roofType;

        /**
         * Creates a new {@code RoofTypeParams}.
         * @param roofType corresponding {@link RoofType}.
         */
        RoofTypeParams(RoofType roofType) {
            this.roofType = roofType;
        }

        /**
         * Creates corresponding {@link RoofType}.
         * @return corresponfing roof type
         */
        public RoofType create() {
            return roofType;
        }
    }

    /**
     * What to place on roof.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableParams place;

    /**
     * Type of roof to render.
     */
    @JsonSetter(nulls = Nulls.FAIL)
    RoofTypeParams roofType;

    /**
     * Building altitude metadata name.
     *
     * TODO: We actually only need roof altitude (altitude + height)
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String altitude;

    /**
     * Building height metadata name.
     *
     * TODO: We actually only need roof altitude (altitude + height)
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public String height;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param place what to place on roof
     * @param roofType type of roof to render
     * @param altitude building altitude metadata name
     * @param height building height metadata name.
     */
    @ConstructorProperties({"place", "roofType", "altitude", "height"})
    public RenderRoofTaskParams(PlaceableParams place, RoofTypeParams roofType, String altitude, String height) {
        this.place = place;
        this.roofType = roofType;
        this.altitude = altitude;
        this.height = height;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();
        if (altitude.isBlank())
            throw new IllegalArgumentException("Altitude metadata cannot be blank");
        if (height.isBlank())
            throw new IllegalArgumentException("Height metadata cannot be blank");
        place.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        return new RenderRoofTask(
            models.create(generation),
            roofType.create(),
            altitude,
            height,
            place.create(generation.seed())
        );
    }
}
