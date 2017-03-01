package com.jackis.elasticsearch.response.search;

import java.math.BigDecimal;

public class SearchHit<T> {

    private Class<T> clazz;
    private String index;
    private String type;
    private String id;
    private BigDecimal score;
    private T source;

    public SearchHit(final Class<T> clazz) {
        this.clazz = clazz;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(final String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(final BigDecimal score) {
        this.score = score;
    }

    public T getSource() {
        return source;
    }

    public void setSource(final T source) {
        this.source = source;
    }

}
