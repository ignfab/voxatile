# Fixed
```
structure:
  ... Structure description ...
```

# Concatenate

```
along: x | y | z
place:
  [... Layout builders descriptions ...]
```

# Repeat

```
along: x | y | z
atLeast: (minimum repetition, default 1)
atMost: (maximum repetition, default infinite)
repeat:
  ... Layout builders descriptions ...
```

# Stretch
```
alongX: 0
alongY:
  at: 2
  atLeast: 0
alongZ:
stretch:
  ... Structure definition ...
```




