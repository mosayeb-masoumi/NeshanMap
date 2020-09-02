package com.minetestdadeh.mapneshan.model.direction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Leg {

    @SerializedName("summary")
    @Expose
    public String summary;
    @SerializedName("distance")
    @Expose
    public Distance distance;
    @SerializedName("duration")
    @Expose
    public Duration duration;
    @SerializedName("steps")
    @Expose
    public List<Step> steps = null;
}
