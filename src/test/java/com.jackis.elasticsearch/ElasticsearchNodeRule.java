package com.jackis.elasticsearch;

import java.net.ServerSocket;
import java.net.URL;
import java.util.Properties;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

/**
 * Spins up an elasticsearch in an own process.
 */
public class ElasticsearchNodeRule implements TestRule {

    private final String clusterName;
    private final int httpPort;
    private EmbeddedElastic embeddedElastic;

    public ElasticsearchNodeRule(final String clusterName) {
        this.clusterName = clusterName;
        this.httpPort = findFreePort();
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                startNode();
                try {
                    base.evaluate();
                } finally {
                    stopNode();
                }
            }
        };
    }

    private void startNode() throws Exception {

        final Properties elasticsearchInstallProperties = new Properties();
        elasticsearchInstallProperties.load(this.getClass().getResourceAsStream("/elasticsearch-test.properties"));

        embeddedElastic = EmbeddedElastic.builder().withDownloadUrl(
                new URL(elasticsearchInstallProperties.getProperty("elasticsearch.download.url")))
                .withSetting(PopularProperties.HTTP_PORT, httpPort)
                .withSetting(PopularProperties.CLUSTER_NAME, clusterName)
                .build();

        embeddedElastic.start();
    }

    private void stopNode() throws Exception {
        embeddedElastic.stop();
    }

    public String getClusterName() {
        return clusterName;
    }

    public int getHttpPort() { return httpPort; }


    private int findFreePort() {
        try(ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
