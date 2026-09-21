package com.ignfab.minalac.generator.fetchers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import com.ignfab.minalac.generator.exceptions.GenerationFailedException;
import com.ignfab.minalac.generator.exceptions.RetryableException;
import com.ignfab.minalac.generator.utils.network.ParameterizedURL;

public abstract class HttpFetcher implements Fetcher {
    protected final HttpClient client;
    protected final ParameterizedURL baseURL;
    protected final Map<String, String> headers;

    protected HttpFetcher(HttpInit init, ParameterizedURL baseURL) {
        this.client = init.client();
        this.baseURL = appendQueryParams(baseURL, init.queryParams());
        this.headers = Map.copyOf(init.headers());
    }

    private static ParameterizedURL appendQueryParams(ParameterizedURL baseURL, Map<String, String> queryParams) {
        if (queryParams.isEmpty())
            return baseURL;
        ParameterizedURL.Builder builder = baseURL.builder();
        queryParams.forEach(builder::parameter);
        return builder.build();
    }

    protected final HttpResponse<InputStream> execute(HttpRequest.Builder requestBuilder) throws GenerationFailedException, RetryableException {
        HttpRequest request = addHeaders(requestBuilder).build();
        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            handleStatusCodeErrors(response);
            return response;
        } catch (IOException | InterruptedException e) {
            throw new RetryableException("Error sending HTTP " + request.method() + " request to: " + request.uri(), e);
        }
    }

    protected final HttpResponse<InputStream> executeGet(URI uri) throws GenerationFailedException, RetryableException {
        return execute(HttpRequest.newBuilder().GET().uri(uri));
    }

    protected final HttpRequest.Builder addHeaders(HttpRequest.Builder request) {
        headers.forEach(request::header);
        return request;
    }

    protected final void handleStatusCodeErrors(HttpResponse<?> response) throws GenerationFailedException, RetryableException {
        int code = response.statusCode();
        switch (code) {
            // Continue normally
            case 200, 201, 202, 203, 204, 302, 304 -> {}
            // Redirections should be followed by the HttpClient if enabled
            case 300, 301, 303, 307, 308 -> throw new GenerationFailedException("Cannot follow redirection (HTTP status code: " + code + ")");
            // Retryable errors (either client- or server-side)
            case 408, 409, 429, 500, 502, 503, 504, 507 -> throw new RetryableException("Retryable error (HTTP status code: " + code + ")");
            // Fatal client-side errors
            case 400, 401, 402, 403, 404, 405, 406, 407, 410, 411, 412, 413, 414, 415, 416, 417, 418, 421, 422, 423, 424, 425, 426, 428, 431, 451 -> throw new GenerationFailedException("Fatal client-side error (HTTP status code: " + code + ")");
            // Fatal server-side errors
            case 501, 505, 506, 508, 510, 511 -> throw new GenerationFailedException("Fatal server-side error (HTTP status code: " + code + ")");
            // Unimplemented HTTP stats codes
            case 100, 101, 102, 103, 205, 206, 207, 208, 226 -> throw new GenerationFailedException("HTTP status code " + code + " is not implemented");
            default -> throw new GenerationFailedException("Unknown HTTP status code: " + code);
        }
    }

    public record HttpInit(HttpClient client, Map<String, String> headers, Map<String, String> queryParams) {}
}
