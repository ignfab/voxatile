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
  * [Filtering on value](#filtering-on-value)
    * [Has value](#has-value)
    * [Value equals](#value-equals)
    * [Lower Than](#lower-than)
    * [Greater Than](#greater-than)
    * [Comparing two dynamic model values](#comparing-two-dynamic-model-values)
  * [Filtering on metadata](#filtering-on-metadata)
     * [Has metadata](#has-metadata)
     * [Metadata equals](#metadata-equals)
     * [In](#in)
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

### Filtering on value

These filters rely on [model values](ModelValues.md).

Keep in mind that model values can only represent numbers!
To test equality of a textual metadata, use [metadata filters](#filtering-on-metadata).

#### Has value

Returns true if given value is not absent for the model.

Example:
```yaml
   hasValue: height
```

Field:
- `hasValue` (required): [Model value](ModelValues.md) to check

#### Value equals

Returns true if given value is equals to a given number.

Example:
```yaml
   value: height
   equals: 2
```

Fields:
- `value` (required): [Model value](ModelValues.md) to check
- `equals` (required): Number to compare with

#### Lower Than

Selects models for which the given value is strictly lower than the specified threshold.

Example:
```yaml
   value: height
   lowerThan: 20
```

Fields:
- `value` (required): [Model value](ModelValues.md) to check
- `lowerThan` (required): Threshold value that the model value must be lower than

#### Greater Than

Selects models for which the given value is strictly greater than the specified threshold.

Example:
```yaml
   value: height
   greaterThan: 20
```

Fields:
- `value` (required): [Model value](ModelValues.md) to check
- `greaterThan` (required): Threshold value that the model value must be greater than

#### Comparing two dynamic model values

You can test for equality/inequality between two dynamic values `A` and `B` by testing for equality/inequality between `A - B` and `0`.

Example:
```yaml
value:
  sum:
    - A
    - product: [ B, -1 ]
# or lowerThan or greaterThan
equals: 0
```

### Filtering on metadata

These filters rely on models metadata.

While [model values-based filters](#filtering-on-value) are more powerful for numbers, metadata types are not limited to numbers.
When working with textual values for example, you must use metadata directly and not through the [metadata model value](ModelValues.md#metadata) that would otherwise be absent.

#### Has metadata

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

#### Metadata equals

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
