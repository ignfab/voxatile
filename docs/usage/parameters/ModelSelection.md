# Model selections

A model selection describes characteristics that models must match to be processed.

Models are always selected on their type and that selection may be narrowed down by an extra filter. This filter can be a combination of other filters.

## Table of contents

* [Example](#example)
* [Extra parameters](#extra-parameters)
* [Filters](#filters)
  * [Boolean operations](#boolean-operations)
     * [Not](#not)
     * [And](#and)
     * [Or](#or)
     * [Combination](#combination)
  * [Filtering on metadata](#filtering-on-metadata)
     * [Has](#has)
     * [Equals](#equals)
     * [In](#in)
     * [Lower Than](#lower-than)
     * [Greater Than](#greater-than)
  * [Filtering on geometry](#filtering-on-geometry)
    * [Empty](#empty)

## Example

This model selection will match `buildings` models with `height` metadata defined and `classification` metadata value other than `Monument`, `Castle`, `Chapel` or `Church`.

```yaml
models:
  type: buildings
  filter:
    and:
      - hasMetadata: height
      - not:
          metadata: classification
          in: [ Monument, Castle, Chapel, Church ]
```

## Extra parameters

  - `type` (required): Type of models to select
  - `filter` (optional, default none): Extra filter

## Filters

Model filtering by type is mandatory but could be narrowed down using an extra optional filter. This filter could consist in a single criterion but may also combine multiple criteria with boolean operations.

### Boolean operations

Usual boolean operations can be applied on filters to alter them or combine them.

#### Not

A filter can be negated using `not` filter.

Example:
```yaml
  not:
    ...filter...
```

Fields:
  - `not`: The filter to negate (required)

#### And

Several filters can be combined using `and` filter. Result is true only if all of combined filters are true.

Example:
```yaml
  and:
    - ...filter1...
    - ...filter2...
    - ...filter3...
```

Fields:
  - `and` (required): A list of filters

#### Or

Several filters can be combined using `or` filter. Result is true if at least one of combined filters is true.

Example:
```yaml
  or:
    - ...filter1...
    - ...filter2...
    - ...filter3...
```

Fields:
  - `or` (required): A list of filters

#### Combination

Of course, `and`, `or` and `not` can be combined in complex expressions, for example:
```Yaml
and:
  - ...filter1...
  - not:
      ...filter2...
  - or:
    - ...filter3...
    - ...filter4...
```
This is equivalent to *filter1 AND (NOT filter2) AND (filter3 OR filter4)*.

### Filtering on metadata

These filters rely on models metadata.

#### Has

Returns true if given metadata exists for the model.

Examples:
```yaml
   hasMetadata: classification
```

```yaml
   hasMetadata: [ classification, height ]
```

Fields:
  - `hasMetadata` (required): Metadata name or list of metadata names to check

If a list of metadata name is given, model is selected only if it has all given metadata.

#### Equals

Returns true if given metadata has the given value.

Example:
```yaml
   metadata: height
   equals: 2
   as: integer
```

Fields:
  - `metadata` (required): Name of metadata to check
  - `equals` (required): Value to compare with
  - `as` (optional, default `text`): Type of value to compare with (`integer`, `decimal`, `text` or `boolean`)

**Be careful with typing!** Value comparison includes type. Values with different type won't be equal. Metadata with text value "5" won't equal to 5 as integer.

#### In

Returns true if given metadata value is among given values.

Example:
```yaml
   metadata: classification
   in: [ Monument, Castle, Chapel, Church ]
   as: text
```

Fields:
  - `metadata` (required): Name of metadata to check
  - `in` (required): List of values to compare with
  - `as` (optional, default `text`): Type of values to compare with (`integer`, `decimal`, `text` or `boolean`)

#### Lower Than

Selects model with given metadata value is strictly less than the specified threshold.

Example:
```yaml
   metadata: height
   lowerThan: 20
```

Fields:
  - `metadata` (required): Name of the metadata to check
  - `lowerThan` (required): Threshold value that the metadata must be lower than. (Only possible to compare numbers with this filter)

#### Greater Than

Selects the model with the given metadata value that is strictly greater than the specified threshold.

Example:
```yaml
   metadata: height
   greaterThan: 20
```

Fields:
  - `metadata` (required): Name of the metadata to check
  - `greaterThan` (required): Threshold value that the metadata must be greater than. (Only possible to compare numbers with this filter)

### Filtering on geometry

These filters rely on models geometry. If the model does not come from the `geoToolsVector` processor, these filters won't match.

#### Empty

Returns true if model geometry is empty.

Examples:
```yaml
   emptyGeometry:
```

Fields:
- `emptyGeometry` (required): This field has no value
