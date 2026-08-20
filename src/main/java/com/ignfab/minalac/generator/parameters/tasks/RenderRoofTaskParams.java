package com.ignfab.minalac.generator.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.generation.Generation;
import com.ignfab.minalac.generator.parameters.models.values.ModelValueParams;
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
     * Base roof altitude
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelValueParams altitude;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param place what to place on roof
     * @param roofType type of roof to render
     * @param altitude building altitude metadata name
     */
    @ConstructorProperties({"place", "roofType", "altitude"})
    public RenderRoofTaskParams(
        PlaceableParams place,
        RoofTypeParams roofType,
        ModelValueParams altitude
    ) {
        this.place = place;
        this.roofType = roofType;
        this.altitude = altitude;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        super.validate();
        altitude.validate();
        place.validate();
    }

    @Override
    public TileTask create(Generation generation) {
        return new RenderRoofTask(
            models.create(generation),
            roofType.create(),
            altitude.create(generation),
            place.create(generation.seed())
        );
    }
}
