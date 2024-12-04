package com.ignfab.minalac.generator.parameters.renderers.values;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ignfab.minalac.generator.renderers.values.ModelValue;

@JsonDeserialize(using = ModelValueParamsDeserializer.class)
public abstract class ModelValueParams<T> {
    public abstract ModelValue<T> create();
}

/*

ModelValue<T> (abstract)
    FixedModelValue<T>
    MetadataModelValue<T>

ModelValueParams<T>
    MetadataModelValueParams<T>


ModelValueParamsDeserializer
  -> FixedModelValue<T> si directement désérializable
  -> Autre sinon

Complications:
    - Désérialiser un type générique
    - Quid si le type simple se confond avec un des XModelValue<T> ?

Les types souhaités sont probablement simples:
    - Integer / Float / String / Boolean

On pourrait faire un truc plus basique qui gère les deux possibilités (fixe / metadata) avec un
seul désérialiseur.

~~> Mais je ne sais toujours pas comment choisir le désérialiseur.


https://stackoverflow.com/questions/36159677/how-to-create-a-custom-deserializer-in-jackson-for-a-generic-type
https://stackoverflow.com/questions/47348029/get-the-detected-generic-type-inside-jacksons-jsondeserializer

*/