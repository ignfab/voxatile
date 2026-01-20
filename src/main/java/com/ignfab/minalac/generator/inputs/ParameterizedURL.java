package com.ignfab.minalac.generator.inputs;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * A URL with query parameters.
 *
 * This URL is immutable, use {@link #builder()} method to add or update parameters.
 */
public final class ParameterizedURL {
    private final String url;
    private final Map<String, String> params;

    /**
     * Creates a new ParameterizedURL.
     *
     * This constructor is private, use {@link #base()} method to start building a URL.
     *
     * @param baseURL base URL
     * @param params query parameters
     */
    private ParameterizedURL(String baseURL, Map<String, String> params) {
        url = baseURL;
        this.params = Map.copyOf(params);
    }

    /**
     * Starts building a URL (returns a Builder object) out of a base URL.
     *
     * @param baseURL base URL including protocol and path but excluding query parameters
     *
     * @return a new builder, use {@code build()} method when URL is ready.
     */
    public static Builder base(String baseURL) {
        return new Builder(baseURL);
    }

    /**
     * Creates a new builder out of this URL, for parameters.
     *
     * @return a {@code Builder} for this URL
     */
    public Builder builder() {
        return new Builder(this);
    }

    /**
     * Converts URL into a {@code java.net.URL} object.
     *
     * @return URL object
     *
     * @throws MalformedURLException if URL is invalid (most likely due to incorrect base URL)
     */
    public URL toURL() throws MalformedURLException {
        StringBuilder suffix = new StringBuilder();
        params.forEach((key, value) -> suffix.append(suffix.isEmpty() ? '?' : '&')
            .append(URLEncoder.encode(key, StandardCharsets.UTF_8))
            .append('=')
            .append(URLEncoder.encode(value, StandardCharsets.UTF_8)));
        return new URL(url + suffix);
    }

    /**
     * @return string representation of the URL
     */
    public String toString() {
        try {
            return this.toURL().toString();
        } catch (MalformedURLException e) {
            return "Malformed";
        }
    }

    /**
     * Builder for ParameterizedURL.
     */
    public static final class Builder {
        private final String url;
        private final Map<String, String> params = new HashMap<>();

        private Builder(String baseUrl) {
            this.url = baseUrl;
        }

        /**
         * Creates a new ParameterizedURL.Builder.
         *
         * @param url Starting URL to create a Builder from.
         */
        private Builder(ParameterizedURL url) {
            this.url = url.url;
            this.params.putAll(url.params);
        }

        /**
         * Adds an extra string typed parameter to the {@code Builder}.
         *
         * @param name Name of the query parameter
         * @param value Value of that parameter
         *
         * @return the {@code Builder} with extra parameter
         */
        public Builder parameter(String name, String value) {
            params.put(name, value);
            return this;
        }

        /**
         * Adds an extra integer typed parameter to the {@code Builder}.
         *
         * @param name Name of the query parameter
         * @param value Value of that parameter
         *
         * @return the {@code Builder} with extra parameter
         */
        public Builder parameter(String name, int value) {
            return parameter(name, String.valueOf(value));
        }

        /**
         * Creates a new {@code ParameterizedURL} from this {@code Builder}.
         *
         * @return the created {@code ParameterizedURL}
         */
        public ParameterizedURL build() {
            return new ParameterizedURL(url, params);
        }

        /**
         * Creates a new {@code URL} from this {@code Builder}.
         *
         * This is a shortcut for {@code .build().toURL()}.
         *
         * @return resulting {@code URL} object
         *
         * @throws MalformedURLException if URL is invalid (most likely due to incorrect base URL)
         */
        public URL buildURL() throws MalformedURLException {
            return build().toURL();
        }
    }
}
