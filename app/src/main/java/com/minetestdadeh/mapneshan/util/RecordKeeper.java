package com.minetestdadeh.mapneshan.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.neshan.services.NeshanMapStyle;

public class RecordKeeper {

    private static final String SP_KEY_MAP_THEME = "SP_KEY_MAP_THEME";
    private static final String SP_NAME = "org.neshan.sample1";
    private static RecordKeeper sInstance = null;
    private Context appContext = null;

    public static RecordKeeper instance(Context context) {
        if (sInstance == null)
            sInstance = new RecordKeeper(context);
        return sInstance;
    }

    private RecordKeeper(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public NeshanMapStyle getMapTheme(){
        int themePos = this.appContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getInt(SP_KEY_MAP_THEME, 0);
        return NeshanMapStyle.values()[themePos];
    }

    public void setMapTheme(NeshanMapStyle theme) {
        SharedPreferences.Editor editor = this.appContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(SP_KEY_MAP_THEME, theme.ordinal());
        editor.apply();
    }
}
