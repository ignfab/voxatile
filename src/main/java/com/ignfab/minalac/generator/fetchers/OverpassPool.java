package com.ignfab.minalac.generator.fetchers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.geotools.api.referencing.FactoryException;
import org.geotools.geometry.jts.ReferencedEnvelope;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.exceptions.TransformException;
import com.ignfab.minalac.generator.geodata.OsmData;
import com.ignfab.minalac.generator.utils.world3d.WorldBBox3d;

/*
TODOS:
- Trouver comment gérer la bbox: pour une tuile, il faut fusionner les demandes (mais on ne les a pas toutes, ça va poser pb)
- Trouver comment gérer le passage d'une tuile à l'autre
*/

public class OverpassPool {
    private final OverpassPoolSpec specs;
    private final List<String> results = new LinkedList<>();
    private WorldBBox3d bbox; // Will include margins
    private boolean fetched = false;

    /**
     *
     */
    public OverpassPool(
        OverpassPoolSpec specs
    ) {
        this.specs = specs;
    }

    synchronized private void fetchData() throws GenerationFailedException, RetryableException {
        if (fetched)
            return;

        ReferencedEnvelope envelope;
        try {
            envelope = specs.envelopeProvider().computeForCRS(OsmData.CRS, bbox);
        } catch (FactoryException | TransformException e) {
            throw new GenerationFailedException(e);
        }

        String query = String.format(Locale.US, "[bbox:%f,%f,%f,%f];",
            envelope.getMinX(), envelope.getMinY(), envelope.getMaxX(), envelope.getMaxY());;

        // This could be moved to Specs but:
        // - We may need to perform some extra stuff here later
        // - Anyway, it's good to check result and query counts match at the end, so we would have to store count in spec
        for (String queryPart: specs.queries())
            query += queryPart + ";out;>;out skel;make separator;out;";

        HttpResponse<InputStream> response;
        try {
            response = specs.fetcher().executePost(specs.fetcher().baseURL.toURI(), "data=" + URLEncoder.encode(query, StandardCharsets.UTF_8));
        } catch (URISyntaxException e) {
            throw new GenerationFailedException("Invalid URL", e);
        }

        byte[] bytes;
        try {
            bytes = response.body().readAllBytes();
            response.body().close();
        } catch (IOException e) {
            // need to close stream here ?
            throw new RetryableException("Error reading response body", e);
        }

        Pattern pattern = Pattern.compile("(.*?)(<(node|way|relation).*)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(new String(bytes, StandardCharsets.UTF_8));

        if (!matcher.find())
            throw new IllegalArgumentException("Bad format");

        String prefix = matcher.group(1);
        String remains = matcher.group(2);
        String suffix = "</osm>";

        pattern = Pattern.compile("(.*?)<separator .*?/>", Pattern.DOTALL);
        matcher = pattern.matcher(remains);

        while(matcher.find())
            results.add(prefix + matcher.group(1) + suffix);

        if (results.size() != specs.queries().size())
            throw new IllegalStateException("Tell me what to do");

        fetched = true;
    }

    /**
     *
     */
    public InputStream getResults(int id, WorldBBox3d bbox) throws GenerationFailedException, RetryableException {
        if (id < 0 || id >= specs.queries().size())
            throw new IndexOutOfBoundsException();

        //TODO: manage bbox and completude (we need to have all bboxes BEFORE perfoming query)
        //TODO: maybe see with margins
        this.bbox = bbox; // Quick and dirty, without margins, bbox is always the same.

        fetchData();
        // TODO: Check non existing ?
        return new ByteArrayInputStream(results.get(id).getBytes());
    }

    /*
     * As we need to know all bbox (to merge them in final query) before fetching data,
     * we need a way to tell that we eventually wont claim the results. This can happen now,
     * all tasks run, but it could be solved that way:
     *
     * public void giveUp(String name) {
     */
}
