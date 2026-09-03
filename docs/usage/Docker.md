# Build and run with Docker

## Build Docker image

Build a `voxatile` image using the following command from project root directory:

```shell
docker build --no-cache \
    --progress=plain \
    -t voxatile .
```

If corporate proxy is required:

```shell
docker build --no-cache \
    --progress=plain \
    --build-arg http_proxy_protocol=http \
    --build-arg http_proxy_host=your.proxy.com \
    --build-arg http_proxy_port=1234 \
    -t voxatile .
```

## Run image locally

Here, the generation parameters are computed using the utility script `generate.sh` (with the `-y` option) and passed to the container using the `VOXATILE_PARAMS` environment variable. A local `output/` directory must have been created beforehand.

```shell
mkdir output
VOXATILE_PARAMS=$(./generate.sh -y minetest full ign) docker run \
    -u $(id -u ${USER}):$(id -g ${USER}) \
    -v ./output:/output \
    -e VOXATILE_PARAMS \
    voxatile
```

If corporate proxy is needed, use the `JAVA_TOOL_OPTIONS` environment variable to declare it:

```shell
mkdir output
VOXATILE_PARAMS=$(./generate.sh -y minetest full ign) docker run \
    -u $(id -u ${USER}):$(id -g ${USER}) \
    -v ./output:/output \
    -e VOXATILE_PARAMS \
    -e JAVA_TOOL_OPTIONS="-Dhttp.proxyHost=your.proxy.com -Dhttp.proxyPort=1234 -Dhttps.proxyHost=your.proxy.com -Dhttps.proxyPort=1234" \
    voxatile
```
