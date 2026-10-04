package fr.ign.voxatile.core.parameters.placeables.patterns;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import fr.ign.voxatile.core.parameters.placeables.structures.PlaceableStructureParams;
import fr.ign.voxatile.core.placeables.Nothing;
import fr.ign.voxatile.core.placeables.Pattern;
import fr.ign.voxatile.core.placeables.PlaceableStructure;
import fr.ign.voxatile.core.placeables.patterns.RepeatPattern;
import fr.ign.voxatile.core.utils.random.Seed;
import fr.ign.voxatile.core.utils.world3d.Positioned3d;
import fr.ign.voxatile.core.utils.world3d.WorldCoords3d;
import fr.ign.voxatile.core.utils.world3d.WorldSize3d;

/**
 * Parameters for {@link RepeatPattern} placeable.
 */
public class RepeatPatternParams extends PatternParams {
    /**
     * What to repeat (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableStructureParams repeatStructure;

    /**
     * Represents shifts along the 3 axes.
     */
    public static class CoordShifts implements Positioned3d {
        /**
         * Number of voxels to shift on the X-axis (optional).
         */
        @JsonSetter(nulls = Nulls.SKIP)
        public int shiftX = 0;

        /**
         * Number of voxels to shift on the Y-axis (optional).
         */
        @JsonSetter(nulls = Nulls.SKIP)
        public int shiftY = 0;

        /**
         * Number of voxels to shift on the Z-axis (optional).
         */
        @JsonSetter(nulls = Nulls.SKIP)
        public int shiftZ = 0;

        @Override
        public WorldCoords3d coords() {
            return new WorldCoords3d(shiftX, shiftY, shiftZ);
        }
    }

    /**
     * X, Y or/and Z axes shift for each X-axis change (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public CoordShifts eachX = new CoordShifts();

    /**
     * X, Y or/and Z axes shift for each Y-axis change (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public CoordShifts eachY = new CoordShifts();

    /**
     * X, Y or/and Z axes shift for each Z-axis change (optional).
     */
    @JsonSetter(nulls = Nulls.SKIP)
    public CoordShifts eachZ = new CoordShifts();

    /**
     * Creates a new {@code RepeatPatternParams}.
     *
     * @param repeatStructure what to repeat
     */
    @ConstructorProperties({ "repeatStructure" })
    public RepeatPatternParams(PlaceableStructureParams repeatStructure) {
        this.repeatStructure = repeatStructure;
    }

    @Override
    public void validate() throws IllegalArgumentException {
        repeatStructure.validate();
    }

    @Override
    public Pattern create(Seed seed) {
        PlaceableStructure struct = repeatStructure.create(seed);
        WorldSize3d size = struct.limits().size();

        if (struct.limits().isEmpty()
            || size.x() + eachX.shiftX <= 0
            || size.y() + eachY.shiftY <= 0
            || size.z() + eachZ.shiftZ <= 0)
            return Nothing.INSTANCE;
        return new RepeatPattern(
            struct,
            eachX.coords(),
            eachY.coords(),
            eachZ.coords()
        );
    }
}
