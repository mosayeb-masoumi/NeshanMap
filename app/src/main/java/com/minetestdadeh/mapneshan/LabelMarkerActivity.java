package com.minetestdadeh.mapneshan;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.minetestdadeh.mapneshan.R;

import org.neshan.core.LngLat;
import org.neshan.core.Range;
import org.neshan.core.Variant;
import org.neshan.graphics.ARGB;
import org.neshan.layers.Layer;
import org.neshan.layers.VectorElementLayer;
import org.neshan.services.NeshanMapStyle;
import org.neshan.services.NeshanServices;
import org.neshan.styles.AnimationStyle;
import org.neshan.styles.AnimationStyleBuilder;
import org.neshan.styles.AnimationType;
import org.neshan.styles.LabelStyle;
import org.neshan.styles.LabelStyleCreator;
import org.neshan.styles.MarkerStyle;
import org.neshan.styles.MarkerStyleCreator;
import org.neshan.ui.ClickData;
import org.neshan.ui.ClickType;
import org.neshan.ui.MapEventListener;
import org.neshan.ui.MapView;
import org.neshan.utils.BitmapUtils;
import org.neshan.vectorelements.Label;
import org.neshan.vectorelements.Marker;

public class LabelMarkerActivity extends AppCompatActivity {

    final int BASE_MAP_INDEX = 0;
    MapView map;
//    VectorElementLayer labelLayer;
    VectorElementLayer markerLayer;

    AnimationStyle animSt;
    Marker marker;
    Label label;

    LabelStyleCreator labelStCr;
    AnimationStyleBuilder animStBl;
    MarkerStyleCreator markStCr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_label_marker);
    }

    @Override
    protected void onStart() {
        super.onStart();

        initLayoutReferences();
    }

    private void initLayoutReferences() {

        initViews();
        initMap();

    }


    private void initViews() {
        map = findViewById(R.id.map);
    }

    // Initializing map
    private void initMap() {

//        labelLayer = NeshanServices.createVectorElementLayer();
        markerLayer = NeshanServices.createVectorElementLayer();
//        map.getLayers().add(labelLayer);
        map.getLayers().add(markerLayer);


        map.getOptions().setZoomRange(new Range(4.5f, 18f));
        Layer baseMap = NeshanServices.createBaseMap(NeshanMapStyle.STANDARD_DAY, getCacheDir() + "/baseMap", 10);
        map.getLayers().insert(BASE_MAP_INDEX, baseMap);


        map.setFocalPointPosition(new LngLat(51.330743, 35.767234), 0);
        map.setZoom(14, 0);



        labelStCr = new LabelStyleCreator();
        markStCr = new MarkerStyleCreator();

        addMarker(new LngLat(51.330743, 35.767234));
        addLabel(new LngLat(51.330743, 35.767534));
    }


    private void addMarker(LngLat loc){

//        AnimationStyleBuilder animStBl = new AnimationStyleBuilder();
        animStBl = new AnimationStyleBuilder();
        animStBl.setFadeAnimationType(AnimationType.ANIMATION_TYPE_SMOOTHSTEP);
        animStBl.setSizeAnimationType(AnimationType.ANIMATION_TYPE_SPRING);
        animStBl.setPhaseInDuration(0.5f);
        animStBl.setPhaseOutDuration(0.5f);
        animSt = animStBl.buildStyle();


        MarkerStyleCreator markStCr = new MarkerStyleCreator();
        markStCr.setSize(30f);
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker)));
        markStCr.setAnimationStyle(animSt);
        markStCr.setHideIfOverlapped(false);
        MarkerStyle markSt = markStCr.buildStyle();

        marker = new Marker(loc, markSt);

        markerLayer.add(marker);
    }

    private void addLabel(LngLat loc) {

//        labelLayer.clear();

        labelStCr.setFontSize(15f);
        labelStCr.setBackgroundColor(new ARGB((short) 255, (short) 150, (short) 150, (short) 255));
//        labelStCr.setHideIfOverlapped(false);   // to show marker and lable together
        LabelStyle labelSt = labelStCr.buildStyle();
        label = new Label(loc, labelSt, "مکان انتخاب شده");

        markerLayer.add(label);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}