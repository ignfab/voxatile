# Fetch data tile task

Task of type `fetchData` fetches data from geographic data source, processes it, and stores it as *models* usable by other tasks.

## Table of contents

* [Example](#example)
* [Extra parameters](#extra-parameters)
* [Providers](#providers)
  * [`wfs` (Web Feature Service)](#wfs-web-feature-service)
  * [`gpkg` (GeoPackage)](#gpkg-geopackage)
  * [`shapefile` (Shapefile)](#shapefile-shapefile)
  * [`wmsFloat` (Web Map Service with floating point values)](#wmsfloat-web-map-service-with-floating-point-values)
* [Processors](#processors)
  * [`geoToolsVector` (GeoTools vector processor)](#geotoolsvector-geotools-vector-processor)
  * [`floatMatrix` (Float matrix processor)](#floatmatrix-float-matrix-processor)

See also [post processing](PostProcessing.md) documentation.

## Example

```yaml
fetchBuildings:
  type: fetchData
  modelType: building
  provider:
    type: wfs
    url: https://data.geopf.fr/wfs/wfs
    features: BDTOPO_V3:batiment
  processor:
    type: geoToolsVector
  postProcessing:
    - type: copy
      metadata: hauteur
      to: height
```

## Extra parameters

- `modelType` (required): the type of models to create.
- `provider` (required): Definition of the [data provider](#providers) to use.
    - `type` (required): Type of data provider.
    - Additional parameters specific to the given [type](#providers)
- `processor` (required): Definition of the [data processor](#processors) to use.
    - `type` (required): Type of data processor.
    - Additional parameters specific to the given [type](#processors)
- `postProcessing` (optional): Additional post-processing steps.
    - [Post-processing](PostProcessing.md) definition

Not all processors are compatible with all provider. See [providers](#provider-parameters) and [processors](#processor-parameters) documentation for compatibility.

## Providers

A provider fetches data from a source and provides it as-is to a processor. Provider type is identified by `type` field.

### `wfs` (Web Feature Service)
Provider of type `wfs` fetches vector data from a [Web Feature Service](https://en.wikipedia.org/wiki/Web_Feature_Service) source. Source must support WFS 1.1 and GML 3.1 versions.

**Extra parameters**:
- `url` (required): Base URL, including protocol and path, excluding request arguments (example: `https://data.geopf.fr/wfs/wfs`)
- `features` (required): Name of WFS feature type to fetch
- `crs` (optional): Wanted CRS for these features (defaults to target CRS)
- `maxFeaturesPerQuery` (optional): Maximum number of features fetched per query (default 1000)

**Suitable processors**: `geoToolsVector`

### `gpkg` (GeoPackage)
Provider of type `gpkg` reads vector data from a [GeoPackage](https://en.wikipedia.org/wiki/GeoPackage) file.

**Extra parameters**:
- `filePath` (required): Path of the GPKG file (absolute, or relative to execution context)
- `typeName` (required): Name of feature type to read
- `crsOverride` (optional, default none): CRS to use when reading data. By default, the CRS is read from the GeoPackage itself. You should only use this parameter if the CRS is invalid or missing from the file. This **DOES NOT** reproject data!

**Suitable processors**: `geoToolsVector`

### `shapefile` (Shapefile)
Provider of type `shapefile` reads vector data from a [Shapefile](https://en.wikipedia.org/wiki/Shapefile).

**Extra parameters**:
- `filePath` (required): Path of the Shapefile (absolute, or relative to execution context)
- `crsOverride` (optional, default none): CRS to use when reading data. By default, the CRS is read from the Shapefile itself. You should only use this parameter if the CRS is invalid or missing from the file. This **DOES NOT** reproject data!

**Suitable processors**: `geoToolsVector`

### `wmsFloat` (Web Map Service with floating point values)
Provider of type `wmsFloat` fetches **float** data from a [Web Map Service](https://en.wikipedia.org/wiki/Web_Map_Service) source. Source must be able to provide `x-bil` format.

**Extra parameters**:
- `url` (required): Base URL, including protocol and path, excluding request arguments (example: `https://data.geopf.fr/wms-r/wms`)
- `layer` (required): Name of layer to fetch
- `crs` (optional): Wanted CRS for this layer (defaults to target CRS)

**Suitable processors**: `floatMatrix`

## Processors

A processor converts data from a provider into models. Processor type is identified by `type` field.

### `geoToolsVector` (GeoTools vector processor)
Processor of type `geoToolsVector` takes vector features and turns them into models, using GeoTools library.

**Extra parameters**: None

**Suitable providers**: `wfs`, `gpkg`, `shapefile`

### `floatMatrix` (Float matrix processor)
Processor of type `floatMatrix` translates a float data matrix to a model.

**Extra parameters**: None

**Suitable providers**: `wmsFloat`
