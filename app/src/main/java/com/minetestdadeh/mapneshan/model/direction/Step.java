package com.minetestdadeh.mapneshan.model.direction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Step {
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("instruction")
    @Expose
    public String instruction;
    @SerializedName("distance")
    @Expose
    public Distance_ distance;
    @SerializedName("duration")
    @Expose
    public Duration_ duration;
    @SerializedName("polyline")
    @Expose
    public String polyline;
    @SerializedName("maneuver")
    @Expose
    public String maneuver;
    @SerializedName("start_location")
    @Expose
    public List<Float> startLocation = null;
}
