package com.jackis.elasticsearch.model.products;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Product {

    private String name;
    private Set<String> categories;
    @JsonProperty("dynamic_attributes")
    private Set<DynamicAttribute> dynamicAttributes;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getCategories() {
        return categories;
    }

    public void setCategories(Set<String> categories) {
        this.categories = categories;
    }

    public Set<DynamicAttribute> getDynamicAttributes() {
        return dynamicAttributes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDynamicAttributes(Set<DynamicAttribute> dynamicAttributes) {
        this.dynamicAttributes = dynamicAttributes;

    }
}
