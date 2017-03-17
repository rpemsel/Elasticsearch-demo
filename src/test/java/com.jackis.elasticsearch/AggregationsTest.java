package com.jackis.elasticsearch;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.entity.ContentType;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AggregationsTest {

    private static ElasticsearchNodeRule NODE_RULE = new ElasticsearchNodeRule("testcluster");
    private static ProductsSchemaRule SCHEMA_RULE = new ProductsSchemaRule(NODE_RULE.getHttpPort());

    @ClassRule
    public static RuleChain RULE_CHAIN = RuleChain.outerRule(NODE_RULE).around(SCHEMA_RULE);

    private RestClient client;
    private ObjectMapper objectMapper;

    @Before
    public void setup() throws IOException {
        client = ClientFactory.getClient(NODE_RULE.getHttpPort());
        objectMapper = ObjectMapperFactory.getObjectMapper();

        client.performRequest("POST", "/products/_refresh", Collections.emptyMap(),
                EntityBuilder.create().setText("").build());
    }

    @After
    public void teardown() throws IOException {
        client.close();
    }

    @Test
    public void fashionAggregation() throws IOException, URISyntaxException {
        final Response response = client.performRequest("POST", "/products/product/_search", Collections.emptyMap(),
                EntityBuilder.create().setContentType(ContentType.APPLICATION_JSON).setText(new String(
                        Files.readAllBytes(Paths.get(
                                AggregationsTest.class.getResource("/query/basicAttributeAggregation.json").toURI()))))
                        .build());

        final Map<String, Set<String>> dynamicAttributes = getDynamicAttributes(response);
        final List<String> controlList = Arrays.asList("S", "M", "L", "XL", "30", "32", "34", "36");

        assertTrue("No attributes were found", !dynamicAttributes.isEmpty());
        assertTrue(controlList.containsAll(dynamicAttributes.get("sizes")));
        assertTrue(dynamicAttributes.get("sizes").containsAll(controlList));
    }

    @Test
    public void menFashionAggregation() throws URISyntaxException, IOException {
        final Response response = client.performRequest("POST", "/products/product/_search", Collections.emptyMap(),
                EntityBuilder.create().setContentType(ContentType.APPLICATION_JSON).setText(new String(
                        Files.readAllBytes(Paths.get(
                                AggregationsTest.class.getResource("/query/productAggregationWithFilter.json").toURI()))))
                        .build());

        final Map<String, Set<String>> dynamicAttributes = getDynamicAttributes(response);
        final List<String> controlList = Arrays.asList("white", "black");

        assertTrue("No attributes were found", !dynamicAttributes.isEmpty());
        assertTrue(controlList.containsAll(dynamicAttributes.get("color")));
        assertTrue(dynamicAttributes.get("color").containsAll(controlList));
    }

    private Map<String, Set<String>> getDynamicAttributes (final Response response) throws IOException {
        final Map<String, Set<String>> dynamicAttributes = new HashMap<>();
        final JsonNode tree = objectMapper.readTree(response.getEntity().getContent());
        final JsonNode keys = tree.get("aggregations").get("dynamic_attributes").get("keys").get("buckets");

        if (keys.isArray()) {
            keys.elements().forEachRemaining(key-> {
                final Set<String> valuesSet = new TreeSet<>();
                final JsonNode values = key.get("values").get("buckets");

                if (values.isArray()) {
                    values.elements().forEachRemaining(value -> valuesSet.add(value.get("key").asText()));
                }

                dynamicAttributes.put(key.get("key").asText(), valuesSet);
            });
        }

        return dynamicAttributes;
    }
}
