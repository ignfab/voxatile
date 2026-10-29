package fr.ign.voxatile.core.parameters.tasks;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.generation.Generation;
import fr.ign.voxatile.core.parameters.models.values.ModelValueParams;
import fr.ign.voxatile.core.parameters.placeables.PlaceableParams;
import fr.ign.voxatile.core.tasks.RenderRoofTask;
import fr.ign.voxatile.core.tasks.RenderRoofTask.RoofType;
import fr.ign.voxatile.core.utils.execution.Task;


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
     * Roof base altitude
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public ModelValueParams altitude;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param place what to place on roof
     * @param roofType type of roof to render
     * @param altitude building altitude metadata name
     * @param height building height metadata name.
     */
    @ConstructorProperties({"place", "roofType", "altitude"})
    public RenderRoofTaskParams(PlaceableParams place, RoofTypeParams roofType, ModelValueParams altitude) {
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
    public Task create(Generation generation) {
        return new RenderRoofTask(
            models.create(generation),
            roofType.create(),
            altitude.create(generation),
            place.create(generation.seed())
        );
    }
}
