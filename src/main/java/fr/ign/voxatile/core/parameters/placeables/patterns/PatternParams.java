package fr.ign.voxatile.core.parameters.placeables.patterns;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import fr.ign.voxatile.core.parameters.placeables.PlaceableParams;
import fr.ign.voxatile.core.placeables.Pattern;
import fr.ign.voxatile.core.utils.random.Seed;

/**
 * Main parameter class for {@link Pattern}.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(RandomPatternParams.class),
    @JsonSubTypes.Type(RepeatPatternParams.class),
})
public abstract class PatternParams extends PlaceableParams {

    @Override
    public abstract Pattern create(Seed seed);
}
