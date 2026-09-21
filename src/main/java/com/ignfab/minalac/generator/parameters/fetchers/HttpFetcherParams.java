package com.ignfab.minalac.generator.parameters.fetchers;

import java.beans.ConstructorProperties;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.ignfab.minalac.generator.fetchers.HttpFetcher;
import com.ignfab.minalac.generator.utils.network.HttpTrustAllSSL;

public abstract class HttpFetcherParams extends FetcherParams {
    @JsonSetter(nulls = Nulls.SKIP)
    public int timeout = -1;

    @JsonSetter(nulls = Nulls.SKIP)
    public boolean followRedirects = false;

    @JsonSetter(nulls = Nulls.SKIP)
    public boolean disableSsl = false;

    @JsonSetter(nulls = Nulls.SKIP)
    public ProxyParams proxy;

    @JsonSetter(nulls = Nulls.SKIP)
    public Map<String, String> headers = new HashMap<>();

    @JsonSetter(nulls = Nulls.SKIP)
    public Map<String, String> queryParams = new HashMap<>();

    @Override
    public void validate() throws IllegalArgumentException {
        if (timeout <= 0 && timeout != -1)
            throw new IllegalArgumentException("Invalid timeout value: " + timeout);
        if (proxy != null)
            proxy.validate();
        headers.forEach((k, v) -> {
            if (k.isBlank())
                throw new IllegalStateException("Header key cannot be empty or blank");
            if (v.isBlank())
                throw new IllegalStateException("Header value cannot be empty or blank");
        });
        queryParams.forEach((k, v) -> {
            if (k.isBlank())
                throw new IllegalStateException("Query param key cannot be empty or blank");
            if (v.isBlank())
                throw new IllegalStateException("Query param value cannot be empty or blank");
        });
    }

    protected final HttpFetcher.HttpInit createHttpInit() {
        HttpClient.Builder builder = HttpClient.newBuilder();
        if (timeout > 0)
            builder.connectTimeout(Duration.ofSeconds(timeout));
        if (followRedirects)
            builder.followRedirects(HttpClient.Redirect.ALWAYS);
        if (disableSsl)
            builder.sslContext(HttpTrustAllSSL.getContext());
        if (proxy != null)
            builder.proxy(ProxySelector.of(proxy.create()));
        return new HttpFetcher.HttpInit(builder.build(), headers, queryParams);
    }

    public static class ProxyParams {
        @JsonSetter(nulls = Nulls.FAIL)
        public String host;

        @JsonSetter(nulls = Nulls.FAIL)
        public int port;

        @ConstructorProperties({ "host", "port" })
        public ProxyParams(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public void validate() {
            if (host.isBlank())
                throw new IllegalArgumentException("Invalid proxy host (empty or blank)");
            if (port <= 0)
                throw new IllegalArgumentException("Invalid proxy port: " + port);
        }

        public InetSocketAddress create() {
            return new InetSocketAddress(host, port);
        }
    }
}
