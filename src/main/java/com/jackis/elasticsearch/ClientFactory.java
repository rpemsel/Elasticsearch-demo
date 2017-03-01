package com.jackis.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

/**
 * Factory for creating JEST Elasticsearch clients
 */
public class ClientFactory {

    public static RestClient getClient(final int httpPort) {
        return RestClient.builder(new HttpHost("localhost", httpPort)).build();
    }


}
