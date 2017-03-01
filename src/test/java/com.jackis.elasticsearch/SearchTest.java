package com.jackis.elasticsearch;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collections;

import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import com.jackis.elasticsearch.model.Play;
import com.jackis.elasticsearch.response.search.SearchResult;
import com.jackis.elasticsearch.response.search.SearchResultReader;

/**
 * Tests to check Elasticsearch search features.
 */
public class SearchTest {

    private static ElasticsearchNodeRule NODE_RULE = new ElasticsearchNodeRule("testcluster");
    private static ElasticSearchSchemaRule SCHEMA_RULE = new ElasticSearchSchemaRule(NODE_RULE.getHttpPort());

    @ClassRule
    public static RuleChain RULE_CHAIN = RuleChain.outerRule(NODE_RULE).around(SCHEMA_RULE);

    private RestClient client;

    @Before
    public void setup() throws IOException {
        client = ClientFactory.getClient(NODE_RULE.getHttpPort());

        client.performRequest("POST", "/shakespeare/_refresh", Collections.emptyMap(),
                EntityBuilder.create().setText("").build());
    }

    @After
    public void teardown() throws IOException {
        client.close();
    }

    @Test
    public void testMatchQuery() throws IOException {

        final Response response = client.performRequest("GET", "/shakespeare/line/_search", Collections.emptyMap(),
                new NStringEntity(
                        "{"
                            + "\"query\": "
                                + "{"
                                    + "\"match\":"
                                        + "{"
                                            + "\"text_entry\": \"yourselves\""
                                        + "}"
                                + "}"
                        + "}"));

        final SearchResult<Play> searchResult = new SearchResultReader<>(Play.class).readRawResult(
                response.getEntity().getContent());

        assertTrue("There is no document with word \"yourself\" in the search index",
                searchResult.getSearchHits().getSearchHit().stream()
                        .anyMatch(line -> line.getSource().getTextEntry().contains("yourselves")));

    }
}
