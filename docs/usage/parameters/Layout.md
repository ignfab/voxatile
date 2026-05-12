
# Layout

Placeholder/

## Table of contents

* [Voxels](#voxels)
  * [Minecraft voxel description](#minecraft-voxel-description)
  * [Minetest voxel description](#minetest-voxel-description)
  * [Disambiguation](#disambiguation)
* [Nothing](#nothing)
* [Structures](#structures)
  * [Boxes structures](#boxes-structures)
  * [Blueprint structures](#blueprint-structures)
* [Patterns](#patterns)
  * [Random patterns](#random-patterns)
  * [Repeat patterns](#repeat-patterns)

## Stretchable Layout

Placeholder.
```yaml

structure:
  axes: x
  blueprint: "rvb"
  with:
    "r": wool:red
    "v": wool:green
    "b": wool:blue
stretchableAlongX: # Optionnel
  alongX: # Optionnel
  at: 1 # Obligatoire
  atLeast: 2 # Défaut : 1
  atMost: 5 # Défaut: Infini
stretchableAlongY: # Optionnel
  at: 1 # Obligatoire
  atLeast: 2 # Défaut : 1
  atMost: 5 # Défaut: Infini
stretchableAlongZ: # Optionnel
  at: 1 # Obligatoire
  atLeast: 2 # Défaut : 1
  atMost: 5 # Défaut: Infini
```

## Repeat Layout

Placeholder.

```yaml
repeat: otherBuilder
along: x # Obligatoire
atLeast: 1 # Défaut: 1
atMost: 2 # Défaut: Infini
```

## Concatenate Layout

Placeholder.

```yaml
concatenate:
  - priority: 1 # Défaut: 0
    otherBuilder
  - priority: 2 # Défaut: 0
    anotherBuilder
along: y
xPolicy: INHERIT
yPolicy: INHERIT
zPolicy: INHERIT
```

## Combine Layout

Placeholder.
