package com.minetestdadeh.mapneshan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import org.neshan.core.LngLat;
import org.neshan.core.Range;
import org.neshan.graphics.ARGB;
import org.neshan.layers.Layer;
import org.neshan.layers.VectorElementLayer;
import org.neshan.services.NeshanMapStyle;
import org.neshan.services.NeshanServices;
import org.neshan.styles.LabelStyle;
import org.neshan.styles.LabelStyleCreator;
import org.neshan.ui.ClickData;
import org.neshan.ui.ClickType;
import org.neshan.ui.MapEventListener;
import org.neshan.ui.MapView;
import org.neshan.vectorelements.Label;

public class LabelAddActivity extends AppCompatActivity {

    final int BASE_MAP_INDEX = 0;
    MapView map;
    VectorElementLayer labelLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_label);
    }

    @Override
    protected void onStart() {
        super.onStart();

        initLayoutReferences();
    }


    private void initLayoutReferences() {

        initViews();

        initMap();


        map.setMapEventListener(new MapEventListener() {
            @Override
            public void onMapClicked(ClickData mapClickInfo) {
                super.onMapClicked(mapClickInfo);
                if (mapClickInfo.getClickType() == ClickType.CLICK_TYPE_LONG) {

                    LngLat clickedLocation = mapClickInfo.getClickPos();

                    addLabel(clickedLocation);
                }
            }
        });
    }


    private void initViews() {
        map = findViewById(R.id.map);
    }

    // Initializing map
    private void initMap() {

        labelLayer = NeshanServices.createVectorElementLayer();
        map.getLayers().add(labelLayer);


        map.getOptions().setZoomRange(new Range(4.5f, 18f));
        Layer baseMap = NeshanServices.createBaseMap(NeshanMapStyle.STANDARD_DAY, getCacheDir() + "/baseMap", 10);
        map.getLayers().insert(BASE_MAP_INDEX, baseMap);


        map.setFocalPointPosition(new LngLat(51.330743, 35.767234), 0);
        map.setZoom(14, 0);
    }

    private void addLabel(LngLat loc) {

//        labelLayer.clear();

        LabelStyleCreator labelStCr = new LabelStyleCreator();
        labelStCr.setFontSize(15f);
        labelStCr.setBackgroundColor(new ARGB((short) 255, (short) 150, (short) 150, (short) 255));
        LabelStyle labelSt = labelStCr.buildStyle();

        Label label = new Label(loc, labelSt, "مکان انتخاب شده");

        labelLayer.add(label);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}