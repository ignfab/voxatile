# Parameters

By parameters we mean input Yaml or Json data describing desired generation.

## Typed value

If you need a value with a variable type in parameters, use `ValueParser` class for that.

Yaml parameters extract:
```yaml
test:
  value: 123
  as: decimal
```

Corresponding code:
```java
class MyStuffParams {
    ...
    Object value;
    ValueParser as;
    ...
    public MyStuff create() {
        ...
        // Parse value according to `as`:
        ... = as.parse(value);
        ...
    }
}
```

Built in parsers are:

| Name      | Java class |
| --------- | ---------- |
| `integer` | `Integer`  |
| `decimal` | `Double`   |
| `text`    | `String`   |
| `boolean` | `Boolean`  |

More parsers can be registered using `ValueParser::register` method.
