package com.minetestdadeh.mapneshan.model.direction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DirectionRoute {
    @SerializedName("routes")
    @Expose
    public List<Route> routes = null;
}
