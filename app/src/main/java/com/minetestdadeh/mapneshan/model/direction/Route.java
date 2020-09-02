package com.minetestdadeh.mapneshan.model.direction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Route {
    @SerializedName("overview_polyline")
    @Expose
    public OverviewPolyline overviewPolyline;
    @SerializedName("legs")
    @Expose
    public List<Leg> legs = null;
}
