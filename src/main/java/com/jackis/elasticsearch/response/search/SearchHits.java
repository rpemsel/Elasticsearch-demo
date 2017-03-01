package com.jackis.elasticsearch.response.search;

import java.util.List;

public class SearchHits<T> {

    private List<SearchHit<T>> searchHit;
    private int count;

    public SearchHits(final List<SearchHit<T>> searchHit, final int count) {
        this.searchHit = searchHit;
        this.count = count;
    }

    public List<SearchHit<T>> getSearchHit() {
        return searchHit;
    }

    public int getCount() {
        return count;
    }
}
