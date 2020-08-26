package com.minetestdadeh.mapneshan.model.search;

import com.minetestdadeh.mapneshan.model.search.Item;

import java.util.List;

public class NeshanSearch {

    private Integer count;
    private List<Item> items ;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
