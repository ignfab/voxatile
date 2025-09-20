# Model values

A model value describes a way to compute a numeric value from a model.
It can be seen as an operator producing a number with or without a model.

A model value might be absent for some models, and all operators have well-defined behaviors regarding absent values.

## Table of contents

* [Example](#example)
* [Root values](#root-values)
  * [Absent](#absent)
  * [Fixed](#fixed)
  * [Metadata](#metadata)
* [Operators on a single model value](#operators-on-a-single-model-value)
  * [Round, floor, ceil](#round-floor-ceil)
  * [Inverse](#inverse)
* [Operators on multiple model values](#operators-on-multiple-model-values)
  * [Sum, product](#sum-product)
  * [Lowest, highest](#lowest-highest)
  * [First of](#first-of)
* [Conditional model value](#conditional-model-value)
* [Random model values](#random-model-values)
  * [Uniform](#uniform)

## Example

This model value will compute the sum of three values: the constant value `1`, the value of the metadata `ground_elevation`, and a more complex value.
This more complex value will be the first value that is not absent from that list:
- The `height` metadata value (rounded to the nearest integer)
- The highest value between:
  - `3`
  - The `number_of_floors` metadata value multiplied by `3`
- `3` if the metadata `type` equals `House`, `5` otherwise

```yaml
value:
  sum:
    - 1
    - metadata: ground_elevation
    - firstOf:
      - round: { metadata: height }
      - highest:
        - 3
        - product: [ { metadata: number_of_floors }, 3 ]
      - if:
          metadata: type
          equals: "House"
        then: 3
        else: 5
```

## Root values

Model values not based on another value are called "root values". They are the base of all computations that could be done.

### Absent

Whenever a model value is required, you can provide an absent model value that will always be absent.

Usage:
```yaml
value: null
```

Note: Unless explicitly specified, omitting the whole model value parameter is not the same as using an absent value parameter.

### Fixed

Whenever a model value is required, you can provide a constant model value that returns the same value for every model. It is never absent.
There are two ways to do it: one is by simply providing the value and the other is by using the field `fixed`.

Example:
```yaml
value: 7
otherValue:
  fixed: -3
```

Field:
- `fixed` (required): The constant value

### Metadata

The most natural way to provide a value from a model is by reading a metadata from that model.
It is absent if the model does not have this metadata, or if its value is not a number.

Example:
```yaml
value:
  metadata: height
```

Field:
- `metadata` (required): The name of the metadata

## Operators on a single model value

A model value can be affected by simple operations, to modify it.

### Round, floor, ceil

A model value is represented by a decimal number. You can round it to an integer using either `round` (nearest integer), `floor` (largest integer smaller than value) or `ceil` (smallest integer larger than value).
The result will be absent if the value to round is absent.

Example:
```yaml
value1:
  round: { metadata: height }
value2:
  floor: { metadata: height }
value3:
  ceil: { metadata: height }
```

Field:
- `round`/`floor`/`ceil` (required): Model value to round, floor or ceil.

### Inverse

You can compute the mathematical inverse (`1/x`) of a model value. You can pair it with the [`product`](#sum-product) operator to make a division.
The result will be absent if the value to compute inverse from is either absent or zero.

Example:
```yaml
value:
  inverse: { metadata: leaves_density }
otherValue:
  product:
    - metadata: height
    - inverse: { metadata: number_of_floors }
```

Field:
- `inverse` (required): Model value to compute inverse from.

Note: This should not be confused with the "opposite" mathematical operation, which negates the value. The latter can be achieved using the [`product`](#sum-product) operator with a [fixed](#fixed) value of `-1`.

## Operators on multiple model values

Some operations can work with multiple model values to combine or select them.

### Sum, product

You can compute the sum (addition) or the product (multiplication) of multiple model values.
The result will be absent if any of the value is absent.

Example:
```yaml
value:
  sum:
    - metadata: ground_elevation
    - metadata: height
    - 1
otherValue:
  product:
    - metadata: number_of_lanes
    - metadata: lane_width
```

Field:
- `sum`/`product` (required, non-empty): List of model values to sum or multiply.

Note: To divide by a value, instead multiply by its [inverse](#inverse).

### Lowest, highest

You can find the lowest (minimum) or highest (maximum) between multiple model values.
The result will be absent if any of the value is absent.

Example:
```yaml
value:
  lowest:
    - metadata: height
    - product:
        - metadata: number_of_floors
        - metadata: floor_height
otherValue:
  highest:
    - metadata: ground
    - metadata: elevation
    - 0
```

Field:
- `lowest`/`highest` (required, non-empty): List of model values to use.

Note: This can be used to clamp a value `x` into an interval `[a, b]` using:
```yaml
lowest:
  - highest: [ a, x ]
  - b
```

### First of

To deal with absent values, you can use the `firstOf` operator to use another model value when the initial one is absent. It will use the first non-absent model value of the list.
Each model value will be evaluated in order, until one is not absent. Remaining values won't be evaluated.
The result will be absent if all the values are absent.

Example:
```yaml
value:
  firstOf:
    - metadata: height
    - product:
        - metadata: number_of_floors
        - metadata: floor_height
    - if:
        metadata: type
        equals: "House"
      then: 3
    - if:
        metadata: type
        equals: "Shed"
      then: 0
    - 5
```

Field:
- `firstOf` (required, non-empty): List of model values to use.

Note: Because [conditional](#conditional-model-value)'s `else` model value defaults to [absent](#absent), you can imitate an if/else-if/else-if/.../else sequence using a `firstOf` model value with multiple conditional ones.

## Conditional model value

Sometimes, the model value to use depend on some characteristics of the model itself. This can be handled using a conditional model value, with a [model filter](ModelSelection.md#filters) as condition, and model values to use for matching models.
The result will be absent if the corresponding value is absent.

Example:
```yaml
value:
  if:
    metadata: road_type
    equals: highway
  then:
    product: [ { metadata: number_of_lanes }, 3 ]
  else: 5
```

Fields:
- `if` (required): [Models filter](ModelSelection.md#filters) to decide which model value to use.
- `then` (required): Model value to use for matching models.
- `else` (optional, default [absent](#absent)): Model value to use for other models.

## Random model values

To add variety to the generation, a model value might include randomness.

However, to guarantee reproducibility between runs (which is needed for example for tiling), random numbers are deterministic, and tied to models.
This means that two different model values with the same parameters will always produce the same pseudo-random numbers for a given model. If it is not the desired behavior, you can specify a seed that will change the value.

### Uniform

Computes a random number following a uniform law (every number has the same probability of appearance) in a given interval.
The result will be absent if either bound value is absent, or if the lower bound is greater than or equals to the upper bound.

Example:
```yaml
value:
  min: 5
  max: 10
```

Fields:
- `min` (required): Model value for the lower bound of the interval (inclusive).
- `max` (required): Model value for the upper bound of the interval (exclusive).
- `seed` (optional, default none): Random seed

Note: Model values are decimal numbers. To compute a random integer, use the [`floor`](#round-floor-ceil) operator on the resulting value.
