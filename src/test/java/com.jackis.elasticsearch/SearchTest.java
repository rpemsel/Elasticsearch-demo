package com.jackis.elasticsearch;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import com.jackis.elasticsearch.model.products.Product;
import com.jackis.elasticsearch.model.shakespeare.Play;
import com.jackis.elasticsearch.response.search.SearchHit;
import com.jackis.elasticsearch.response.search.SearchResult;
import com.jackis.elasticsearch.response.search.SearchResultReader;

/**
 * Tests to check Elasticsearch search features.
 */
public class SearchTest {

    private static ElasticsearchNodeRule NODE_RULE = new ElasticsearchNodeRule("testcluster");
    private static ShakespeareSchemaRule SHAKESPEARE_SCHEMA_RULE = new ShakespeareSchemaRule(NODE_RULE.getHttpPort());
    private static ProductsSchemaRule PRODUCT_SCHEMA_RULE = new ProductsSchemaRule(NODE_RULE.getHttpPort());

    @ClassRule
    public static RuleChain RULE_CHAIN = RuleChain.outerRule(NODE_RULE).around(SHAKESPEARE_SCHEMA_RULE).around(
            PRODUCT_SCHEMA_RULE);

    private RestClient client;

    @Before
    public void setup() throws IOException {
        client = ClientFactory.getClient(NODE_RULE.getHttpPort());

        client.performRequest("POST", "/shakespeare/_refresh", Collections.emptyMap(),
                EntityBuilder.create().setText("").build());

        client.performRequest("POST", "/products/_refresh", Collections.emptyMap(),
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

    @Test
    public void testNestedQuerySearch() throws URISyntaxException, IOException {
        final Response response = client.performRequest("POST", "/products/product/_search", Collections.emptyMap(),
                EntityBuilder.create().setContentType(ContentType.APPLICATION_JSON).setText(new String(
                        Files.readAllBytes(Paths.get(
                                SearchTest.class.getResource("/query/nestedSearch.json").toURI()))))
                        .build());

        final SearchResult<Product> searchResult = new SearchResultReader<>(Product.class).readRawResult(
                response.getEntity().getContent());

        assertTrue("Searched product was not found", searchResult.getSearchHits().getSearchHit().stream().anyMatch(
                productSearchHit -> "Scarf".equals(productSearchHit.getSource().getName())));

    }

    @Test
    public void testRegExpQuery() throws IOException, URISyntaxException {
        final Response response = client.performRequest("POST", "/products/product/_search", Collections.emptyMap(),
                EntityBuilder.create().setContentType(ContentType.APPLICATION_JSON).setText(new String(
                        Files.readAllBytes(
                                Paths.get(SearchTest.class.getResource("/query/regexpSearch.json").toURI())))).build());

        final SearchResult<Product> searchResult = new SearchResultReader<>(Product.class).readRawResult(
                response.getEntity().getContent());

        searchResult.getSearchHits().getSearchHit().stream().map(SearchHit::getSource).forEach(
                product -> assertTrue("Found line does not include the term \"awesome\".",
                        product.getDescription().contains("awesome")));
    }

    @Test
    public void testWildcardQuery() throws IOException, URISyntaxException {
        final Response response = client.performRequest("POST", "/products/product/_search", Collections.emptyMap(),
                EntityBuilder.create().setContentType(ContentType.APPLICATION_JSON).setText(new String(
                        Files.readAllBytes(
                                Paths.get(SearchTest.class.getResource("/query/wildcardSearch.json").toURI())))).build());

        final SearchResult<Product> searchResult = new SearchResultReader<>(Product.class).readRawResult(
                response.getEntity().getContent());

        searchResult.getSearchHits().getSearchHit().stream().map(SearchHit::getSource).forEach(
                product -> assertTrue("Found line does not include the term \"awesome\".",
                        product.getDescription().contains("awesome")));
    }

    @Test
    public void ngramSearchTest () throws IOException, URISyntaxException {
        final Response response = client.performRequest("POST", "/products/product/_search", Collections.emptyMap(),
                EntityBuilder.create().setContentType(ContentType.APPLICATION_JSON).setText(new String(
                        Files.readAllBytes(Paths.get(SearchTest.class.getResource("/query/ngramSearch.json").toURI()))))
                        .build());

        final SearchResult<Product> searchResult = new SearchResultReader<>(Product.class).readRawResult(
                response.getEntity().getContent());

        searchResult.getSearchHits().getSearchHit().stream().map(SearchHit::getSource).forEach(
                product -> assertTrue("Found line does not include the term \"awesome\".",
                        product.getDescription().contains("awesome")));
    }
}
