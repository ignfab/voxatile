# Post-processing

During [fetch data task](FetchDataTask.md), post-processing can be applied to models to alter them so they can comply with tasks requirements. This can be particularly useful to mix two heterogeneous data sources to later use them indifferently.
Each post-processor has a field `type` which is used to identify it.

## Table of contents
* [Accepted and processed model types](#accepted-and-processed-model-types)
* [Multi-processing steps](#multi-processing-steps)
* [Conditional post-processing](#conditional-post-processing)
* [Generic post-processors](#generic-post-processors)
  * [Identity](#identity)
  * [Discard](#discard)
  * [Metadata copy](#metadata-copy)
  * [Metadata default](#metadata-default)
  * [Metadata parse](#metadata-parse)
  * [Metadata remap](#metadata-remap)
  * [Metadata truncate](#metadata-truncate)
  * [Metadata explode](#metadata-explode)
* [Geometry post-processors](#geometry-post-processors)
  * [Geometry buffer](#geometry-buffer)

## Accepted and processed model types

Post-processors take a model as input, and return a processed version of that model as output. Because of this, not all post-processors can be applied to all types of model. Furthermore, a post-processor can return either the (modified) input model, or a complete new one. It can even not return anything, discarding the model.

Most of them are generic, meaning they can work with any type of model, and return the input model. However, some are more specific and only accept a specific model type. In the following sections, these capabilities are expressed as "accepted model type" and "processed model type".

## Multi-processing steps

Post-processors can be chained together sequentially. This is done by giving a list of post-processors instead of a single post-processor. They will be applied in the same order they are defined, and their accepted and processed model type must match with their previous and following one.

For example, to parse a metadata value, removing it on failure, then apply a default one:
```yaml
postProcessing:
  - type: parse
    metadata: height
    as: decimal
    ifMissing: ignore
    ifNotParsable: removeMetadata
  - type: default
    metadata: height
    value: 5
    as: integer
```

In this situation, the accepted model type of the chain is the one of the first post-processor of the chain, and the processed model type is the one from the last post-processor of the chain.

Note: An empty chain (`postProcessing: []`) is equivalent to the [identity](#identity) post-processor, and a chain of only one post-processor is equivalent to that single post-processor.

## Conditional post-processing

Sometimes, only a subset of models need specific post-processing. This can be handled using a conditional post-processor, with a [model filter](ModelSelection.md#filters) as condition, and post-processing to apply to matching models.

**Type**: `conditional`

**Extra parameters**:
- `if` (required): [Models filter](ModelSelection.md#filters) to decide which models should be post-processed.
- `then` (required): Post-processing to apply to matching models.
- `else` (optional, default [identity](#identity)): Post-processing to apply to other models.

For example, to apply a buffer around the geometry of models with a given metadata value, and discard others:
```yaml
postProcessing:
  type: conditional
  if:
    metadata: classification
    equals: forest
  then:
    type: geometryBuffer
    buffer: 3
  else:
    type: discard
```

Of course, `then` can contain multiple post-processing steps, as explained in [the previous section](#multi-processing-steps).

Warning: Models that do not match the condition will be left untouched. Thus, the processed model type of this post-processor is defined as "Same as input", and the accepted model type is the one of the underlying post-processing operation. This means that operation must comply with the "Same as input" processed model type requirement.

## Generic post-processors

These post-processors can be applied to any model, and they only modifies the model, without changing its type.

**Accepted model type**: All

**Processed model type**: Same as input

### Identity

A post-processor doing nothing, returning the input model untouched. It can be convenient if one is required, but you don't need one.

**Type**: `identity`

### Discard

A post-processor discarding everything. It can be paired with [conditional post-processing](#conditional-post-processing) to drop models based on a condition.

**Type**: discard

### Metadata copy

A post-processor copying a metadata into another.

**Type**: `copy`

**Extra parameters**:
- `metadata` (required, `text`): Name of the metadata to copy.
- `to` (required, `text`): Name of destination metadata.
- `abortIfMetadataIsAbsent` (optional, default `no`): `yes` to stop the copy if the metadata is missing, `no` to allow the copy to proceed even if the metadata is missing (in this case, the metadata value will be empty).
- `keepExisting` (optional, default `no`): `no` to overwrite existing data, `yes` to keep existing metadata.

### Metadata default

Post-processor that applies a default value for a specified metadata.

**Type**: `default`

**Extra parameters**:
- `metadata` (required): the name of the metadata.
- `value` (required): the default value to use if the metadata is not present.
- `as` (required): the type to which the value should be converted:  `integer`, `decimal`, `boolean`, `text`

### Metadata parse

Post-processor parsing a metadata value in-place.

**Type**: `parse`

**Extra parameters**:
- `metadata` (required): the name of the metadata to parse.
- `as` (required): the type of parsed value: `integer`, `decimal`, `boolean`, `text`.
- `ifMissing` (optional, default `error`): What to do when metadata is absent. See failure policies below.
- `ifNotParsable` (optional, default `error`): What to do if data parsing fails. See failure policies below.

| Failure policy   | Explanation                                |
|:-----------------|:-------------------------------------------|
| `discardModel`   | The model is discarded.                    |
| `removeMetadata` | The metadata is removed.                   |
| `ignore`         | Failure is ignored, nothing is done.       |
| `error`          | An error occurs, and the generation stops. |

### Metadata remap

Post-processor that remaps metadata values.

The metadata value is always considered as a String before comparaison.
The matched value is parsed according to the `as` attribute.

**Type**: `remap`

**Extra parameters**
- `metadata` (required): Name of the metadata to remap.
- `ifMissing` (optional, default `error`): Policy to apply if metadata is missing:

| Policy           | Explanation                                |
|:-----------------|:-------------------------------------------|
| `discardModel`   | The model is discarded.                    |
| `ignore`         | Nothing is done.                           |
| `error`          | An error occurs, and the generation stops. |

- `fromTo` (optional, required if `toFrom` is not set): Mapping table telling, for each found value, what to put instead.
- `toFrom` (optional, required if `fromTo` is not set): Reverse mapping table telling, for each desired output value, what input values are replaced. Several input values (as list) can be given for an output value.
- `default` (optional): Default value if no match found.
- `as` (optional, default `text`): Wanted type of remapped values: `integer`, `decimal`, `boolean`, `text`.
- `ifNoMatchFound` (optional, only when `default` is not set, default `error`): Policy to apply when no match found:

| Policy           | Explanation                                |
|:-----------------|:-------------------------------------------|
| `discardModel`   | The model is discarded.                    |
| `removeMetadata` | The metadata is removed.                   |
| `ignore`         | Metadata is not modified.                  |
| `error`          | An error occurs, and the generation stops. |

**Examples**:
Here's basic exemple of one to one mapping:
```yaml
type: remap
metadata: nature
fromTo:
  Haie: hedge
  Mangrove: mangrove
default: grass
```

Here many different metadata values are mapped to same output values:
```yaml
type: remap
metadata: nature
toFrom:
  forest:
     - Forêt fermée de conifères
     - Forêt fermée de feuillus
     - Peupleraie
     - Forêt ouverte
     - Zone arborée
     - Bois
  heathland: [ Lande herbacée, Lande ligneuse ]
  vine: Vigne
default: grass
```

### Metadata truncate

Post-processor that truncates metadata values to integers.

**Type**: `truncate`

**Extra parameters**
- `metadata` (required): Name of the metadata whose value will be truncated.
- `method` (required): How value is truncated.

| Method           | Explanation                                                      |
|:-----------------|:-----------------------------------------------------------------|
| `round`          | Round to nearest integer.                                        |
| `ceil`           | Round up to the smallest integer greater than or equal to value. |
| `floor`          | Round down to the largest integer less than or equal to value.   |

- `ifMissing` (optional, default `error`): Policy to apply if metadata is missing (see below).
- `ifTruncationFail` (optional, default `error`): Policy to apply if truncation fails (see below).
Avaliable policies:

| Policy           | Explanation                                          |
|:-----------------|:-----------------------------------------------------|
| `discardModel`   | The model is discarded.                              |
| `removeMetadata` | The metadata is removed (no effect for `ifMissing`). |
| `ignore`         | Metadata is not modified.                            |
| `error`          | An error occurs, and the generation stops.           |

### Metadata explode

A post-processor exploding a nested metadata map into flat values.

**Type**: `explode`

**Extra parameters**:
- `metadata` (required, `text`): Name of the metadata to explode. The metadata must be a map (can be the result of parsing an `hstore`).
- `prefix` (optional, `text`, default none): Optional prefix to prepend to exploded metadata names.

## Geometry post-processors

These post-processors can be applied to models resulting from the [`geoToolsVector` processor](FetchDataTask.md#geotoolsvector-geotools-vector-processor), and they only modifies the model, without changing its type.

**Accepted model type**: Result of `geoToolsVector` processor

**Processed model type**: Same as input

### Geometry buffer

Post-processor that applies a buffer around the geometry of the model.

**Type**: `geometryBuffer`

**Extra parameters**:
- `buffer` (required): the signed distance of the buffer to apply, can be negative.
