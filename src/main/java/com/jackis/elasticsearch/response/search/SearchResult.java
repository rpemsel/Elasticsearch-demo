package com.jackis.elasticsearch.response.search;

public class SearchResult<T> {

    private SearchHits<T> searchHits;

    public SearchHits<T> getSearchHits() {
        return searchHits;
    }

    public void setSearchHits(final SearchHits<T> searchHits) {
        this.searchHits = searchHits;
    }

}
