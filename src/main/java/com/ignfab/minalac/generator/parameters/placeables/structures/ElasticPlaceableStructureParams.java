package com.ignfab.minalac.generator.parameters.placeables.structures;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.parameters.placeables.PlaceableParams;
import com.ignfab.minalac.generator.placeables.PlaceableStructure;
import com.ignfab.minalac.generator.utils.random.Seed;

// TODO-PR: Probably a temporary class.
//  Discuss whether or not PlaceableStructure should hold the information about elasticity.
//  (It is something really specific, only used for making facade building)
//  If we decide that PlaceableStructure should hold that info, PlaceableStructureParams should be revamped a little.
//  Currently structure have 2 variant a list (BoxPlaceableStructureParams) and a map (BlueprintPlaceableStructureParams), which is not convenient to add optional field.
//  There will be 3 possibles way of writing it.
//  # Optional fields for Blueprint
//  structure:
//    with:
//    axes:
//    ...
//    elasticAtX:
//  # Non expendable Box
//  structure:
//    - put:
//      at:
//  # Expendable Box with optional fields
//  structure:
//    box:
//      - put:
//        at:
//    elasticAtX:
/**
 * Parameter for {@link PlaceableStructure} that is extendable at certain axis coordinates.
 */
public class ElasticPlaceableStructureParams extends PlaceableParams {
    /**
     * The placeable structure (required).
     */
    @JsonSetter(nulls = Nulls.FAIL)
    public PlaceableStructureParams structure;
    /**
     * x-axis coordinate for the elasticity (optional).
     */
    public Integer elasticAtX;
    /**
     * y-axis coordinate for the elasticity (optional).
     */
    public Integer elasticAtY;
    /**
     * z-axis coordinate for the elasticity (optional).
     */
    public Integer elasticAtZ;

    /**
     * Constructor used to ensure that the required fields are present during deserialization.
     *
     * @param structure the {@link PlaceableStructureParams}.
     */
    @ConstructorProperties("structure")
    public ElasticPlaceableStructureParams(PlaceableStructureParams structure) {
        this.structure = structure;
    }

    @Override
    public void validate() {
        structure.validate();
    }

    @Override
    public PlaceableStructure create(Seed seed) {
        return new PlaceableStructure(structure.createPlaceables(seed), elasticAtX, elasticAtY, elasticAtZ);
    }
}
