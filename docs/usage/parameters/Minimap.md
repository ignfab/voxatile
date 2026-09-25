# Minimap

A minimap is a scaled 2D representation of the generated 3D world.

## Stored minimap

Minimaps are created at the beginning of the generation process, remain available throughout the generation, and can be accessed by their names.

You can generate multiple minimaps at the same time by declaring them with different names.

In the example below, two distinct minimaps named overworld and onlyWater are declared:

```yaml
minimaps:
  # A minimap named 'overworld' using the default settings
  overworld: {}
  
  # A minimap named 'onlyWater' with a custom maximum size
  onlyWater:
    size: 200
```

Fields:
- `size` : Maximum size in pixel of the longest side of the minimap. By default, this field is set to `1000`.
