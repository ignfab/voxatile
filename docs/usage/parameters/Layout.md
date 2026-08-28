
# Layout

Layout is a kind of structure that can resize itself to fit a requested space. 

## Table of contents

* [Stretchable Layout](#stretchable-layout)
* [Repeat Layout](#repeat-layout)
* [Concatenate Layout](#concatenate-layout)

## Stretchable Layout

Makes a structure stretchable by having one band per axis (row, column or layer) that gets repeated or ommited to fit the requested size.

```yaml
structure:
  axes: x
  blueprint: "rvb"
  with:
    "r": wool:red
    "v": wool:green
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
  - `stretchableAlongX` (optional): Strech parameters along x-axis. Leaving it out keeps that axis fixed.
  - `stretchableAlongY` (optional): Strech parameters along y-axis. Leaving it out keeps that axis fixed.
  - `stretchableAlongZ` (optional): Strech parameters along z-axis. Leaving it out keeps that axis fixed.

Each `stretchableAlong` have:
  - `at` (required): coordinate of the band to stretch
  - `atLeast` (optional, default `1`): Minimum repetition of the band. `0` allows squeezing.
  - `atMost` (optional, default `infinite`): Maximum repetition of the band.

## Repeat Layout

Repeats a layout along a given axis. If the repeated layout is resizable on that axis, 
the repeated may be stretched to fill the requested size.

```yaml
repeat: otherLayout
along: x
atLeast: 1
atMost: 2
```

Fields:
  - `repeat` (required): The layout to repeat. 
  - `along` (required): Axis which the layout is repeated (`x`, `y` or `z`)
  - `atLeast` (optional, default `1`): Minimum repetition of repetition. `0` allows the layout to be empty.
  - `atMost` (optional, default `infinite`): Maximum repetition of the layout.

## Concatenate Layout

Places several layout side by side on a given axis. 
Available space is distributed by priority.
Layout having the same priority get space distributed as evenly as possibly.
If layouts of same priority can not use all the space, the rest is passed to next priority.

```yaml
concatenate:
  - priority: 1 # Défaut: 0
    otherLayout
  - priority: 2 # Défaut: 0
    anotherLayout
along: y
zPolicy: KEEP
```
Policies are used to control how non concatenate axes are sized.

Fields:
- `concatenate` (required): List of layouts to place side by side.
  - `priority` (optional, default `1`): Priority level. Layout with higher values get space first.  
- `along` (required): Axis which the layouts are placed (`x`, `y` or `z`)
- `xPolicy` (optional, default `INHERIT`): Policy for the x-axis.
- `yPolicy` (optional, default `INHERIT`): Policy for the y-axis.
- `zPolicy` (optional, default `INHERIT`): Policy for the z-axis.

Policies values:
- `INHERIT`: Uses the policy set by the parent (Parent layout or parent task).
- `KEEP`: Each layout keep its own size on that axis. Layout may have different sizes on that axis.
- `ADJUST`: All layout are forced to have the same size.
