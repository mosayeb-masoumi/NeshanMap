package com.minetestdadeh.mapneshan.model.direction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Duration {

    @SerializedName("value")
    @Expose
    public Integer value;
    @SerializedName("text")
    @Expose
    public String text;
}
