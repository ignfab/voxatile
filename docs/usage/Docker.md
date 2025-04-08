# Build and run with Docker

## Build Docker image

Build a `minalac-generator` image using the following command from project root directory:

```shell
docker build --no-cache \
    --progress=plain \
    -t minalac-generator .
```

If corporate proxy is required:

```shell
docker build --no-cache \
    --progress=plain \
    --build-arg http_proxy_protocol=http \
    --build-arg http_proxy_host=your.proxy.com \
    --build-arg http_proxy_port=1234 \
    -t minalac-generator .
```

## Run image locally

Here, the generation parameters are computed using the utility script `generate.sh` (with the `-y` option) and passed to the container using the `MINALAC_PARAMS` environment variable. A local `output/` directory must have been created beforehand.

```shell
mkdir output
MINALAC_PARAMS=$(./generate.sh -y minetest full ign) docker run \
    -u $(id -u ${USER}):$(id -g ${USER}) \
    -v ./output:/output \
    -e MINALAC_PARAMS \
    minalac-generator
```

If corporate proxy is needed, use the `JAVA_TOOL_OPTIONS` environment variable to declare it:

```shell
mkdir output
MINALAC_PARAMS=$(./generate.sh -y minetest full ign) docker run \
    -u $(id -u ${USER}):$(id -g ${USER}) \
    -v ./output:/output \
    -e MINALAC_PARAMS \
    -e JAVA_TOOL_OPTIONS="-Dhttp.proxyHost=your.proxy.com -Dhttp.proxyPort=1234 -Dhttps.proxyHost=your.proxy.com -Dhttps.proxyPort=1234" \
    minalac-generator
```
