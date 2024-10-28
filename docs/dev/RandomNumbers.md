# Random numbers in map generation
## TL;DR
If you need random numbers in a renderer:
* Add a `Seed` argument to your constructor;
* *Salt* your seed with each `Model` before usage;
* use `createRandom()` to get a random number generator;

This will ensure your random numbers to be deterministic and random enough.

 ### Nevers

* **Never reuse a seed salted by a model for another model**, always use a new
one.

* **Never use `java.util.Random` directly (or any other random source like `Math.random()`) for rendering**, always use `Seed.createRandom()`.

## What is the problem?

Map generation should be deterministic despite involved randomness. Running
same generation twice should give the same result: trees at the same place,
same random materials used for buildings, and so on.

This would allow to run a new generation with little changes in parameters
without affecting everything. This will also be imperative in order to perform
tiled generation.

At last, it should be possible to globally change the randomness (for example, if
the random rendering isn't pleasant for any reason).

## How to achieve that?

Solution is:
* Use a deterministic sequence of random numbers;
* Always fetch them the same way;

`Seed.createRandom` will provide a `java.util.Random` object to be used to get
needed sequence of random numbers. `Seed` should be *salted* with `Model` (so
each model has its own sequence) and eventually with extra salt if two
renderers working on same models need to have different randomness.


## Technically speaking

### Feature models

Every model representing a feature has to implement a `salt()` method returning
a *salt* string unique enough to ensure its random stuff to be different from
its neighbors.

Example of a `Model` `salt()` method using coordinates as salt string:
```java
    @Override
    protected String salt() {
        return String.format("%f%f", this.centerX(), this.centerY());
    }
```

### Global models

Some models, like raster models, are global to the world. There is only one
instance of which we get an extract for a given area.

The way sequences of random number could be generated for such models is not
yet decided. It will probably be something like a spatial grid with a cell
based salt.

### Renderers

Whenever a renderer needs random number, its constructor should have a `Seed`
parameter. The generation's main `Seed` will be passed to this constructor. It
must be salted with rendered model before use.

Example of a renderer using random numbers:
```java
    public MyRandomRenderer(Seed seed, ...) {
        this.seed = seed.salt("myrenderer"); // Salt seed with a custom salt (ensure we have our own randomness)
        ...
    }

    ...

    public void render(Model model) {
        // Seed MUST be salted with model we render
        Random random = seed.salt(model).createRandom();

        if (random.nextFloat() > 0.5) {
            ...
        }
    }
```

