package com.ignfab.minalac.generator.inputs;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.IgnorableException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.utils.coordinates.EnvelopeProvider;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/**
 * Data provider harvesting vector features through WMS {@code GetFeatureInfo}.
 * <p>
 * This is a workaround for servers that publish a layer through WMS but keep WFS
 * disabled, which is the case of the Albanian ASIG geoportal
 * ({@code geoportal.asig.gov.al}, GeoServer answering
 * {@code ServiceException: Service WFS is disabled}). Prefer
 * {@code WFS1_1_GML3_1_DataProvider} whenever WFS is available: it is one bbox
 * query per tile instead of the grid of point queries performed here.
 * <p>
 * <b>How it works.</b> {@code GetFeatureInfo} answers a <em>point</em> query,
 * widened by the {@code BUFFER} parameter. Servers cap that buffer, so a single
 * call only returns the features around one point. Covering an area therefore
 * requires a grid of queries spaced by {@code spacing}, with de-duplication by
 * feature identifier. Expect a few hundred HTTP requests for a 500 m tile, and no
 * guarantee of exhaustiveness: this samples a rendering endpoint, it does not query
 * a feature store.
 * <p>
 * <b>Scale matters.</b> A WMS layer may declare a
 * {@code MinScaleDenominator} / {@code MaxScaleDenominator} window, outside of
 * which it is simply not rendered — and an unrendered layer answers
 * {@code GetFeatureInfo} with an empty collection, not an error. {@code resolution}
 * therefore has to place {@link #scaleDenominator()} inside that window, or this
 * provider silently returns nothing. On ASIG, the windows are 0.14 to 1.40 map
 * units per pixel for {@code adresar:adr_ndertese}, 0.06 to 0.70 for
 * {@code zrpp:ndertesa_qkd_042025} and the INSTAT census layers, and 0.06 to 14 for
 * the ASHK cadastre.
 * <p>
 * <b>Harvest radius.</b> The buffer cap is expressed in pixels, measured at roughly
 * 60 to 75 px on ASIG, so the radius actually harvested scales with
 * {@code resolution}: about 76 m at 1.33 units per pixel, about 30 m at 0.5. Keep
 * {@code spacing} below that radius or features will be missed.
 * <p>
 * GeoJSON is requested rather than GML, and parsed here rather than through
 * {@code gt-geojson-core}: that module is absent from the project and would pull a
 * second Jackson implementation. GML would be the natural choice given
 * {@code gt-xsd-wfs}, but the {@code schemaLocation} of a GetFeatureInfo response
 * points at {@code DescribeFeatureType} on the very WFS that is disabled, so the
 * application namespace cannot be resolved.
 */
public class WMSFeatureInfoDataProvider implements Provider<SimpleFeature> {
    private static final String SERVICE = "WMS";

    /**
     * WMS 1.1.1 is used on purpose. In 1.3.0 the {@code BBOX} follows the axis order
     * of the CRS, and ASIG declares EPSG:6870 north-first — its advertised bounding
     * box starts with a northing. Sticking to 1.1.1 keeps {@code BBOX} easting-first
     * and gives the {@code X} / {@code Y} pixel parameters.
     */
    private static final String VERSION = "1.1.1";

    /** OGC standardized rendering pixel size, in metres. Used to derive scale denominators. */
    private static final double OGC_PIXEL_SIZE = 0.00028;

    /** Guard against a huge query image when resolution is tiny against queryRadius. */
    private static final int MAX_QUERY_IMAGE_SIZE = 2001;

    private static final Logger LOGGER = LoggerFactory.getLogger(WMSFeatureInfoDataProvider.class);

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    private final ParameterizedURL baseURL;
    private final CoordinateReferenceSystem crs;
    private final EnvelopeProvider envelopeProvider;
    private final double queryRadius;
    private final double resolution;
    private final double spacing;
    private final String layer;
    private final boolean northEastAxisOrder;

    /**
     * Built from the first feature returned, then reused: a WMS layer has a fixed
     * schema. Volatile and published under a lock, because a single provider instance
     * is shared by the tiles, which are generated concurrently.
     */
    private volatile SimpleFeatureType featureType;

    /**
     * Creates a new {@code WMSFeatureInfoDataProvider}.
     *
     * @param baseURL base URL of the service, excluding query parameters
     * @param layer name of the WMS layer to query
     * @param crs coordinate reference system to use, which must be advertised by the
     *            service for this layer
     * @param resolution size of a pixel, in {@code crs} units; must place
     *                   {@link #scaleDenominator()} inside the scale window of the layer
     * @param queryRadius half-width of the bounding box of a single query, in
     *                    {@code crs} units
     * @param spacing distance between two query points of the grid, in {@code crs}
     *                units; should not exceed the radius actually harvested by the
     *                server, or features will be missed
     * @param buffer value of the {@code BUFFER} parameter, in pixels
     * @param maxFeaturesPerQuery value of the {@code FEATURE_COUNT} parameter
     * @param envelopeProvider function to use to compute envelopes from bounding boxes
     */
    public WMSFeatureInfoDataProvider(
            String baseURL,
            String layer,
            CoordinateReferenceSystem crs,
            double resolution,
            double queryRadius,
            double spacing,
            int buffer,
            int maxFeaturesPerQuery,
            EnvelopeProvider envelopeProvider
    ) {
        this.crs = crs;
        this.layer = layer;
        this.resolution = resolution;
        this.queryRadius = queryRadius;
        this.spacing = spacing;
        this.envelopeProvider = envelopeProvider;

        String srsName = CRS.toSRS(crs);
        if (srsName == null)
            throw new IllegalArgumentException("Could not retrieve SRS name for layer");

        // A ReferencedEnvelope follows the axis order of its CRS, and several national
        // systems are northing first: EPSG:6870 (Albania TM 2010) and EPSG:4326 among
        // them, unlike EPSG:2154 or EPSG:3857. WMS 1.1.1 and the GeoJSON output of the
        // service are both easting first, so coordinates are swapped at both boundaries.
        northEastAxisOrder = CRS.getAxisOrder(crs) == CRS.AxisOrder.NORTH_EAST;

        // The query image size follows from the radius and the pixel size, so that
        // the scale of the request can be tuned independently of the area covered.
        // Kept odd, so that the queried pixel sits exactly at the centre.
        int queryImageSize = Math.min(
            MAX_QUERY_IMAGE_SIZE,
            Math.max(3, 1 | (int) Math.round(2 * queryRadius / resolution)));

        this.baseURL = ParameterizedURL.base(baseURL)
            .parameter("SERVICE", SERVICE)
            .parameter("VERSION", VERSION)
            .parameter("REQUEST", "GetFeatureInfo")
            .parameter("LAYERS", layer)
            .parameter("QUERY_LAYERS", layer)
            .parameter("STYLES", "")
            .parameter("SRS", srsName)
            .parameter("INFO_FORMAT", "application/json")
            .parameter("FEATURE_COUNT", maxFeaturesPerQuery)
            .parameter("BUFFER", buffer)
            .parameter("WIDTH", queryImageSize)
            .parameter("HEIGHT", queryImageSize)
            .parameter("X", queryImageSize / 2)
            .parameter("Y", queryImageSize / 2)
            .build();
    }

    /**
     * Returns the scale denominator of each query, assuming {@code resolution} is
     * expressed in metres.
     * <p>
     * Must fall inside the {@code MinScaleDenominator} /
     * {@code MaxScaleDenominator} window declared by the layer, otherwise the server
     * answers an empty feature collection without any error.
     *
     * @return the scale denominator
     */
    public double scaleDenominator() {
        return resolution / OGC_PIXEL_SIZE;
    }

    @Override
    public Class<SimpleFeature> providedType() {
        return SimpleFeature.class;
    }

    @Override
    public Result<SimpleFeature> provide(WorldBBox3d bbox)
            throws GenerationFailedException, RetryableException {

        ReferencedEnvelope envelope;
        try {
            envelope = envelopeProvider.computeForCRS(crs, bbox);
        } catch (FactoryException | TransformException e) {
            throw new GenerationFailedException(e);
        }

        return new GridResult(envelope);
    }

    /**
     * Walks the grid of query points covering the requested envelope, issuing one
     * request per point and buffering the features it returns.
     * <p>
     * De-duplication is local to this result: a feature straddling the boundary
     * between two tiles is returned once per tile, as a bbox query would.
     */
    private final class GridResult implements Result<SimpleFeature> {
        private final ReferencedEnvelope envelope;
        private final int columns;
        private final int rows;
        private final Set<String> seen = new HashSet<>();
        private final Deque<SimpleFeature> pending = new ArrayDeque<>();
        private int index;

        private GridResult(ReferencedEnvelope envelope) {
            this.envelope = envelope;
            this.columns = pointCount(envelope.getWidth());
            this.rows = pointCount(envelope.getHeight());
        }

        private int pointCount(double span) {
            return Math.max(1, (int) Math.ceil(span / spacing));
        }

        @Override
        public CoordinateReferenceSystem crs() {
            return crs;
        }

        @Override
        public boolean hasNext() throws GenerationFailedException, RetryableException {
            // Many query points return nothing at all, so keep walking the grid
            // until a feature shows up or the grid is exhausted.
            while (pending.isEmpty() && index < columns * rows)
                fetchNextPoint();
            return !pending.isEmpty();
        }

        @Override
        public SimpleFeature next() throws GenerationFailedException, RetryableException {
            if (!hasNext())
                throw new NoSuchElementException();
            return pending.removeFirst();
        }

        private void fetchNextPoint() throws GenerationFailedException, RetryableException {
            int column = index % columns;
            int row = index / columns;
            index++;

            // Query points are spread over the actual envelope rather than stepped by
            // spacing, so that they stay inside it even when the tile is smaller than
            // spacing: stepping would then place the single point beyond maxX.
            double x = envelope.getMinX() + envelope.getWidth() * (column + 0.5) / columns;
            double y = envelope.getMinY() + envelope.getHeight() * (row + 0.5) / rows;

            for (SimpleFeature feature : getFeatureInfo(x, y))
                if (seen.add(feature.getID()))
                    pending.addLast(feature);
        }

        @Override
        public void close() throws IgnorableException {
            // Nothing to release: every connection is closed as soon as it is parsed.
            pending.clear();
            seen.clear();
        }
    }

    private List<SimpleFeature> getFeatureInfo(double x, double y)
            throws GenerationFailedException, RetryableException {

        // WMS 1.1.1 always expects the easting first, whatever the axis order of the CRS.
        double east = northEastAxisOrder ? y : x;
        double north = northEastAxisOrder ? x : y;

        ParameterizedURL url = baseURL.builder()
            .parameter("BBOX", (east - queryRadius)
                + "," + (north - queryRadius)
                + "," + (east + queryRadius)
                + "," + (north + queryRadius))
            .build();

        URLConnection connection;
        String requestURL;
        try {
            URL resolved = url.toURL();
            // Kept as a string: ParameterizedURL has no toString(), and the resolved
            // URL is what has to appear in the logs to be reproducible in a browser.
            requestURL = resolved.toString();
            connection = resolved.openConnection();
            connection.setConnectTimeout(10_000);
            connection.setReadTimeout(60_000);
            connection.connect();
        } catch (MalformedURLException e) {
            throw new GenerationFailedException("Invalid URL for layer", e);
        } catch (IOException e) {
            throw new RetryableException("Error opening connection", e);
        }

        if (connection instanceof HttpURLConnection http) {
            int status;
            try {
                status = http.getResponseCode();
            } catch (IOException e) {
                throw new RetryableException("Error reading response status", e);
            }

            if (status == 429 || status >= HttpURLConnection.HTTP_INTERNAL_ERROR)
                throw new RetryableException("Service returned HTTP " + status);

            if (status != HttpURLConnection.HTTP_OK)
                throw new GenerationFailedException(
                    "Service returned HTTP " + status + ": " + excerpt(http.getErrorStream()));
        }

        // GeoServer answers a ServiceExceptionReport in XML when the layer is not
        // queryable or the info format is unsupported, with a 200 status.
        String contentType = connection.getContentType();
        if (contentType == null || !contentType.contains("json"))
            throw new GenerationFailedException("Service exception (" + contentType + "): "
                + excerpt(streamOrNull(connection)));

        JsonNode root;
        try (InputStream inputStream = connection.getInputStream()) {
            root = readTree(inputStream);
        } catch (IOException e) {
            throw new RetryableException("Error fetching features", e);
        } catch (JacksonException e) {
            // Most likely a truncated response rather than a malformed service.
            throw new RetryableException("Unparsable GeoJSON response", e);
        }

        List<SimpleFeature> features = new ArrayList<>();
        for (JsonNode node : root.path("features")) {
            SimpleFeature feature = readFeature(node);
            if (feature != null)
                features.add(feature);
        }

        // An empty result is the normal answer for a point with nothing under it, but
        // it is also what a layer outside its scale window returns. Logging the query
        // is the only way to tell the two apart.
        LOGGER.debug("{} features from {}", features.size(), requestURL);

        return features;
    }

    /**
     * Single entry point to Jackson, so that adapting to another mapper
     * configuration only touches one place.
     *
     * @param inputStream the response body
     * @return the parsed tree
     */
    private JsonNode readTree(InputStream inputStream) {
        return JsonMapper.builder().build().readTree(inputStream);
    }

    private SimpleFeature readFeature(JsonNode node) {
        Geometry geometry = readGeometry(node.path("geometry"));
        if (geometry == null)
            return null;

        JsonNode properties = node.path("properties");

        SimpleFeatureType type = featureType;
        if (type == null)
            synchronized (this) {
                type = featureType;
                if (type == null) {
                    type = buildFeatureType(geometry, properties);
                    featureType = type;
                }
            }

        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);
        builder.set(type.getGeometryDescriptor().getLocalName(), geometry);

        for (Map.Entry<String, JsonNode> property : properties.properties())
            if (type.getDescriptor(property.getKey()) != null)
                builder.set(property.getKey(), readValue(property.getValue()));

        return builder.buildFeature(node.path("id").asString(null));
    }

    /**
     * Builds the feature type from the first feature encountered. A WMS layer has a
     * fixed schema, so the attributes of the first feature describe them all;
     * attributes absent from a later feature are simply left null.
     *
     * @param geometry the geometry of the first feature, used for its binding
     * @param properties the properties of the first feature
     * @return the resulting feature type
     */
    private SimpleFeatureType buildFeatureType(Geometry geometry, JsonNode properties) {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName(layer);
        builder.setCRS(crs);
        builder.add("the_geom", geometry.getClass());
        builder.setDefaultGeometry("the_geom");

        for (Map.Entry<String, JsonNode> property : properties.properties())
            builder.add(property.getKey(), bindingOf(property.getValue()));

        return builder.buildFeatureType();
    }

    private static Class<?> bindingOf(JsonNode value) {
        if (value.isIntegralNumber())
            return Long.class;
        if (value.isNumber())
            return Double.class;
        if (value.isBoolean())
            return Boolean.class;
        return String.class;
    }

    private static Object readValue(JsonNode value) {
        if (value.isNull())
            return null;
        if (value.isIntegralNumber())
            return value.asLong();
        if (value.isNumber())
            return value.asDouble();
        if (value.isBoolean())
            return value.asBoolean();
        return value.asString();
    }

    private Geometry readGeometry(JsonNode geometry) {
        String type = geometry.path("type").asString("");
        JsonNode coordinates = geometry.path("coordinates");

        return switch (type) {
            case "Point" -> point(coordinates);
            case "MultiPoint" -> GEOMETRY_FACTORY.createMultiPoint(
                map(coordinates, this::point, Point[]::new));
            case "LineString" -> lineString(coordinates);
            case "MultiLineString" -> GEOMETRY_FACTORY.createMultiLineString(
                map(coordinates, this::lineString, LineString[]::new));
            case "Polygon" -> polygon(coordinates);
            case "MultiPolygon" -> GEOMETRY_FACTORY.createMultiPolygon(
                map(coordinates, this::polygon, Polygon[]::new));
            default -> null;
        };
    }

    private static <T> T[] map(JsonNode array, Function<JsonNode, T> mapper, IntFunction<T[]> factory) {
        List<T> values = new ArrayList<>();
        for (JsonNode element : array)
            values.add(mapper.apply(element));
        return values.toArray(factory.apply(values.size()));
    }

    private Point point(JsonNode position) {
        return GEOMETRY_FACTORY.createPoint(coordinate(position));
    }

    private LineString lineString(JsonNode positions) {
        return GEOMETRY_FACTORY.createLineString(coordinates(positions));
    }

    private Polygon polygon(JsonNode rings) {
        LinearRing shell = GEOMETRY_FACTORY.createLinearRing(coordinates(rings.path(0)));

        LinearRing[] holes = new LinearRing[Math.max(0, rings.size() - 1)];
        for (int i = 1; i < rings.size(); i++)
            holes[i - 1] = GEOMETRY_FACTORY.createLinearRing(coordinates(rings.path(i)));

        return GEOMETRY_FACTORY.createPolygon(shell, holes);
    }

    private Coordinate[] coordinates(JsonNode positions) {
        Coordinate[] result = new Coordinate[positions.size()];
        for (int i = 0; i < positions.size(); i++)
            result[i] = coordinate(positions.path(i));
        return result;
    }

    private Coordinate coordinate(JsonNode position) {
        // GeoJSON positions are [x, y] or [x, y, z], and the service writes them
        // easting first; restore the axis order of the CRS so that the geometries
        // match the CRS advertised by this result.
        double first = position.path(0).asDouble();
        double second = position.path(1).asDouble();
        double x = northEastAxisOrder ? second : first;
        double y = northEastAxisOrder ? first : second;

        return position.size() > 2
            ? new Coordinate(x, y, position.path(2).asDouble())
            : new Coordinate(x, y);
    }

    private static InputStream streamOrNull(URLConnection connection) {
        try {
            return connection.getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    private static String excerpt(InputStream inputStream) {
        if (inputStream == null)
            return "<no response body>";
        try (InputStream stream = inputStream) {
            String text = new String(stream.readNBytes(2048), StandardCharsets.UTF_8);
            return text.isBlank() ? "<empty response body>" : text.strip();
        } catch (IOException e) {
            return "<unreadable response body>";
        }
    }
}
