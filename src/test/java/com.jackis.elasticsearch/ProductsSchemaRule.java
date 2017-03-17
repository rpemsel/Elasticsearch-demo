package com.jackis.elasticsearch;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;

import org.apache.http.client.entity.EntityBuilder;
import org.elasticsearch.client.RestClient;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Rule to create/delete elastic search schema.
 */
public class ProductsSchemaRule implements TestRule {

    private int httpPort;

    public ProductsSchemaRule(final int httpPort) {
        this.httpPort = httpPort;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                createSchema();
                try {
                    base.evaluate();
                } finally {
                    deleteSchema();
                }
            }
        };
    }

    private void createSchema() throws Exception {
        RestClient client = ClientFactory.getClient(httpPort);

        final File indexFile = Paths.get(
                ProductsSchemaRule.class.getResource("/products/products-index.json").toURI()).toFile();

        final File dataFile = Paths.get(
                ProductsSchemaRule.class.getResource("/products/products-data.json").toURI()).toFile();

        client.performRequest("PUT", "/products", Collections.emptyMap(),
                EntityBuilder.create().setFile(indexFile).build());

        client.performRequest("POST", "/products/_bulk", Collections.emptyMap(),
                EntityBuilder.create().setFile(dataFile).build());

        client.close();
    }

    private void deleteSchema() throws Exception {
        RestClient client = ClientFactory.getClient(httpPort);
        client.performRequest("DELETE", "/products", Collections.emptyMap(),
                EntityBuilder.create().setText("").build());
        client.close();
    }
}
