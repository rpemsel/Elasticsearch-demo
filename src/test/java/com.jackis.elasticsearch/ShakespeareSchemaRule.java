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
public class ShakespeareSchemaRule implements TestRule {

    private int httpPort;

    public ShakespeareSchemaRule(final int httpPort) {
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
                ShakespeareSchemaRule.class.getResource("/shakespeare/shakespeare-index.json").toURI()).toFile();

        final File dataFile = Paths.get(
                ShakespeareSchemaRule.class.getResource("/shakespeare/shakespeare-data.json").toURI()).toFile();

        client.performRequest("PUT", "/shakespeare", Collections.emptyMap(),
                EntityBuilder.create().setFile(indexFile).build());

        client.performRequest("POST", "/shakespeare/_bulk", Collections.emptyMap(),
                EntityBuilder.create().setFile(dataFile).build());

        client.close();
    }

    private void deleteSchema() throws Exception {
        RestClient client = ClientFactory.getClient(httpPort);
        client.performRequest("DELETE", "/shakespeare", Collections.emptyMap(),
                EntityBuilder.create().setText("").build());
        client.close();
    }
}
