package com.ignfab.minalac.generator.inputs;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * A URL builder out of base URL and query parameters.
 */
public class URLBuilder {
    private String url;
    private String params;

    /**
     * Creates a new URLBuilder.
     *
     * @param baseURL base URL including protocol and path but excluding query parameters
     */
    public URLBuilder(String baseURL) {
        url = baseURL;
        params = "";
    }

    /**
     * Adds String typed query parameter to the URL being built.
     *
     * @param name Name of the query parameter
     * @param value Value of that parameter
     */
    public void addQueryParameter(String name, String value) {
        if (params == "")
            params = "?";
        else
            params += "&";
        params += URLEncoder.encode(name, StandardCharsets.UTF_8) + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /**
     * Adds int typed query parameter to the URL being built.
     *
     * @param name Name of the query parameter
     * @param value Value of that parameter
     */
    public void addQueryParameter(String name, int value) {
        addQueryParameter(name, String.valueOf(value));
    }

    /**
     * Actually builds URL into a {@code java.net.URL} object.
     *
     * @return built URL
     *
     * @throws MalformedURLException if build URL is invalid (most likely due to incorrect base URL)
     */
    public URL toURL() throws MalformedURLException {
        return new URL(url + params);
    }
}
