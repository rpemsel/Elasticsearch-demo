package com.jackis.elasticsearch.response.search;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jackis.elasticsearch.ObjectMapperFactory;

public class SearchResultReader<R> {

    private Class<R> resultType;

    public SearchResultReader(final Class<R> resultType) {
        this.resultType = resultType;
    }

    public SearchResult<R> readRawResult(InputStream text) throws IOException {
        final SearchResult<R> searchResult = new SearchResult<>();
        final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        JsonNode node = objectMapper.readTree(text);

        final List<SearchHit<R>> results = new ArrayList<>();

        if (node.get("hits") != null && node.get("hits").get("hits") != null && node.get("hits").get("hits")
                .isArray()) {
            for (JsonNode arrayElement : node.get("hits").get("hits")) {
                final SearchHit<R> searchHit = new SearchHit<>(this.resultType);

                searchHit.setIndex(arrayElement.get("_index").asText());
                searchHit.setType(arrayElement.get("_type").asText());
                searchHit.setId(arrayElement.get("_id").asText());
                searchHit.setScore(arrayElement.get("_score").decimalValue());

                if (arrayElement.get("_source") != null) {
                    searchHit.setSource(
                            objectMapper.readValue(arrayElement.get("_source").traverse(), this.resultType));
                }

                results.add(searchHit);
            }
        }

        searchResult.setSearchHits(new SearchHits<>(results, node.get("hits").get("total").asInt()));
        return searchResult;
    }
}
