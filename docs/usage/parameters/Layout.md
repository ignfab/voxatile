
# Layouts

Layouts are a kind of structure that can adapt itself to fit a requested space.

## Table of contents

* [Stretchable Layout](#stretchable-layout)
* [Repeat Layout](#repeat-layout)
* [Concatenate Layout](#concatenate-layout)

## Stretchable Layout

Makes a structure stretchable by having one band per axis (row, column or layer) that gets repeated or omitted to fit the requested size.

```yaml
structure:
  axes: x
  blueprint: "rgb"
  with:
    "r": wool:red
    "g": wool:green
    "b": wool:blue
stretchableAlongX:
  at: 2
  atLeast: 2
  atMost: 5
stretchableAlongY:
  at: 1
```

Fields:
  - `structure` (required) : The structure to make stretchable
  - `stretchableAlongX`/`Y`/`Z` (each optional): Strech parameters along x/y/z-axis. Omit to keep that fixed (non-stretchable).
    - `at` (required): coordinate of the band to stretch
    - `atLeast` (optional, default `1`): Minimum repetition of the band. `0` allows squeezing.
    - `atMost` (optional, default `infinite`): Maximum repetition of the band.

## Repeat Layout

Repeats a layout along a given axis. If the repeated layout is resizable on that axis,
it may be stretched to fill the requested size.

```yaml
repeat: otherLayout
along: x
atLeast: 1
atMost: 2
```

Fields:
  - `repeat` (required): The layout to repeat.
  - `along` (required): Axis along which the layout is repeated (`x`, `y` or `z`)
  - `atLeast` (optional, default `1`): Minimum repetitions of the layout. `0` allows the layout to be empty.
  - `atMost` (optional, default `infinite`): Maximum repetitions of the layout.

## Concatenate Layout

Places several layouts side by side on a given axis, with priorities.

It proceeds that way:
1. All layouts gets their minimal required space (minimum size).
1. Higher priority layouts (higher number) start first and get as much remaining space as possible.
1. If space still remains, continue with lower priorities until no space left.

If serveral layouts have the same priority, the available space is distributed between them as evenly as possible.

```yaml
concatenate:
  - priority: 1
    otherLayout
  - priority: 2
    anotherLayout
along: y
zPolicy: keep
```

Fields:
- `concatenate` (required): List of layouts to place side by side. Each layout definition may have an extra `priority`  field (default `0`) to set its order of priority (higher values get space first).
- `along` (required): Axis which the layouts are placed (`x`, `y` or `z`)
- `x`/`y`/`zPolicy` (optional, default `inherit`): Policy for the x/y/z-axis (only for axes other than `along` axis).

Policies control how other axes are sized:
| Policy    | Constraint on concerned axis                            |
|:----------|:--------------------------------------------------------|
| `inherit` | Use parent policy (layout or task).                     |
| `adjust`  | Force same size for all child layouts.                  |
| `keep`    | Keep child layouts sizes (different sizes are allowed). |
