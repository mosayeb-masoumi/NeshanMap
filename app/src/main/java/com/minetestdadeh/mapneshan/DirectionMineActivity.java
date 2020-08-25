package com.minetestdadeh.mapneshan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.minetestdadeh.mapneshan.R;
import com.minetestdadeh.mapneshan.util.PolylineEncoding;

import org.json.JSONArray;
import org.json.JSONObject;
import org.neshan.core.LngLat;
import org.neshan.core.LngLatVector;
import org.neshan.core.Range;
import org.neshan.core.Variant;
import org.neshan.geometry.LineGeom;
import org.neshan.graphics.ARGB;
import org.neshan.layers.VectorElementLayer;
import org.neshan.services.NeshanMapStyle;
import org.neshan.services.NeshanServices;
import org.neshan.styles.AnimationStyle;
import org.neshan.styles.AnimationStyleBuilder;
import org.neshan.styles.AnimationType;
import org.neshan.styles.LineStyle;
import org.neshan.styles.LineStyleCreator;
import org.neshan.styles.MarkerStyle;
import org.neshan.styles.MarkerStyleCreator;
import org.neshan.ui.ClickData;
import org.neshan.ui.ClickType;
import org.neshan.ui.MapEventListener;
import org.neshan.ui.MapView;
import org.neshan.utils.BitmapUtils;
import org.neshan.vectorelements.Line;
import org.neshan.vectorelements.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectionMineActivity extends AppCompatActivity {

    Button btn_clear_map,btn_clear_route,btn_route ,btn_current_location;

    final int BASE_MAP_INDEX = 0;


    MapView map;


    FusedLocationProviderClient fusedLocationClient;
    Location userLocation;
    VectorElementLayer userMarkerLayer;


    // we save decoded Response of routing encoded string because we don't want request every time we clicked toggle buttons
    List<PolylineEncoding.LatLng> decodedOverviewPath;
    List<PolylineEncoding.LatLng> decodedStepByStepPath;

    // value for difference mapSetZoom
    boolean overview = false;

    // You can add some elements to a VectorElementLayer. We add lines and markers to this layer.
    VectorElementLayer lineLayer;
    VectorElementLayer markerLayer;
    // Marker that will be added on map
    Marker marker;
    // an id for each marker
    long markerId = 0;
    // marker animation style
    AnimationStyle animSt;

    double currentLocatonLAT=0.0;
    double currentLocatonLNG=0.0;
    double markerLAT=0.0;
    double markerLNG=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_direction_mine_test);

        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // everything related to ui is initialized here
        initMap();
    }


    private void initMap() {

        userMarkerLayer = NeshanServices.createVectorElementLayer();
        map.getLayers().add(userMarkerLayer);

        // Creating a VectorElementLayer(called lineLayer) to add line to it and adding it to map's layers
        lineLayer = NeshanServices.createVectorElementLayer();
//        markerLayer = NeshanServices.createVectorElementLayer();
        map.getLayers().add(lineLayer);
//        map.getLayers().add(markerLayer);

        // add Standard_day map to layer BASE_MAP_INDEX
        map.getOptions().setZoomRange(new Range(4.5f, 18f));
        map.getLayers().insert(BASE_MAP_INDEX, NeshanServices.createBaseMap(NeshanMapStyle.STANDARD_DAY));

        // Setting map focal position to a fixed position and setting camera zoom
        map.setFocalPointPosition(new LngLat(51.330743, 35.767234),0 );
        map.setZoom(14,0);

        
        getCurrentLocation();
        
        

        map.setMapEventListener(new MapEventListener() {
            @Override
            public void onMapClicked(ClickData mapClickInfo) {
                super.onMapClicked(mapClickInfo);

                if (mapClickInfo.getClickType() == ClickType.CLICK_TYPE_LONG && markerId < 2) {
                    LngLat clickedLocation = mapClickInfo.getClickPos();
                    addMarker(clickedLocation, markerId);
                    // increment id
                    markerId++;

//                    // check until second marker is insert draw an overview line between that 2 marker
//                    if (markerId == 1) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
////                                overviewToggleButton.setChecked(true);
////                                neshanRoutingApi();
//                            }
//                        });
//                    }
                }
            }
        });
    }

    private void addMarker(LngLat loc, long id) {

        // get lat lng marker
        markerLAT = loc.getY();
        markerLNG = loc.getX();


        AnimationStyleBuilder animStBl = new AnimationStyleBuilder();
        animStBl.setFadeAnimationType(AnimationType.ANIMATION_TYPE_SMOOTHSTEP);
        animStBl.setSizeAnimationType(AnimationType.ANIMATION_TYPE_SPRING);
        animStBl.setPhaseInDuration(0.5f);
        animStBl.setPhaseOutDuration(0.5f);
        animSt = animStBl.buildStyle();

        // Creating marker style. We should use an object of type MarkerStyleCreator, set all features on it
        // and then call buildStyle method on it. This method returns an object of type MarkerStyle
        MarkerStyleCreator markStCr = new MarkerStyleCreator();
        markStCr.setSize(30f);
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker)));
        // AnimationStyle object - that was created before - is used here
        markStCr.setAnimationStyle(animSt);
        MarkerStyle markSt = markStCr.buildStyle();

        // Creating marker
        marker = new Marker(loc, markSt);
        // Setting a metadata on marker, here we have an id for each marker
        marker.setMetaDataElement("id", new Variant(id));
        // Adding marker to markerLayer, or showing marker on map!
        userMarkerLayer.add(marker);

    }



    private void getCurrentLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        onLocationChange(task.getResult());

                        // get lat lng current location
                        currentLocatonLAT = task.getResult().getLatitude();
                        currentLocatonLNG = task.getResult().getLongitude();

                    } else {

                        // request location again after 1 sec
                        final Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            getCurrentLocation();
//                            Toast.makeText(MapActivity.this, "موقعیت یافت نشد.دوباره تلاش کنید ", Toast.LENGTH_SHORT).show();
                        }, 100);

                    }
                });
    }

    private void onLocationChange(Location location) {
        this.userLocation = location;

        addUserMarker(new LngLat(userLocation.getLongitude(), userLocation.getLatitude()));
        map.setFocalPointPosition(
                new LngLat(userLocation.getLongitude(), userLocation.getLatitude()),
                0.25f);
        map.setZoom(15, 0.25f);
    }



    private void addUserMarker(LngLat loc) {

        userMarkerLayer.clear();

        AnimationStyleBuilder animStBl = new AnimationStyleBuilder();
        animStBl.setFadeAnimationType(AnimationType.ANIMATION_TYPE_SMOOTHSTEP);
        animStBl.setSizeAnimationType(AnimationType.ANIMATION_TYPE_SPRING);
        animStBl.setPhaseInDuration(0.5f);
        animStBl.setPhaseOutDuration(0.5f);
        AnimationStyle animSt = animStBl.buildStyle();

        MarkerStyleCreator markStCr = new MarkerStyleCreator();
        markStCr.setSize(35f);

        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.map_currentloc10)));

        markStCr.setAnimationStyle(animSt);
        MarkerStyle markSt = markStCr.buildStyle();
        Marker marker = new Marker(loc, markSt);
        userMarkerLayer.add(marker);
    }


    private void initView() {
        map = findViewById(R.id.map);
        btn_clear_map = findViewById(R.id.btn_clear_map);
        btn_clear_route = findViewById(R.id.btn_clear_route);
        btn_route = findViewById(R.id.btn_route);
        btn_current_location = findViewById(R.id.btn_current_location);

        btn_route.setOnClickListener(view -> neshanRoutingApi());
        btn_clear_route.setOnClickListener(view -> lineLayer.clear());
        btn_current_location.setOnClickListener(view -> getCurrentLocation());
        btn_clear_map.setOnClickListener(view -> {
            userMarkerLayer.clear();
            lineLayer.clear();
        });


    }



    private void neshanRoutingApi() {

        String requestURL = "https://api.neshan.org/v2/direction?origin=" + currentLocatonLAT+ "," + currentLocatonLNG + "&destination=" + markerLAT + "," + markerLNG;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest reverseGeoSearchRequest = new StringRequest(
                Request.Method.GET,
                requestURL,
                new com.android.volley.Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            lineLayer.clear();
                            JSONObject obj = new JSONObject(response);
                            String encodedOverviewPath = obj.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").getString("points");
                            JSONArray stepByStepPath = obj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");

                            // two type of routing
                            decodedOverviewPath = PolylineEncoding.decode(encodedOverviewPath);
                            decodedStepByStepPath = new ArrayList<>();

                            // decoding each segment of steps and putting to an array
                            for (int i = 0; i < stepByStepPath.length(); i++) {
                                List<PolylineEncoding.LatLng> decodedEachStep = PolylineEncoding.decode(stepByStepPath.getJSONObject(i).getString("polyline"));
                                decodedStepByStepPath.addAll(decodedEachStep);
                            }

                            drawLineGeom(decodedOverviewPath);
//                          Log.e("response", String.valueOf(decodedStepByStepPath));

                        } catch (Exception e) {

                            Log.e("error", e.getMessage());
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                // TODO: replace "YOUR_API_KEY" with your api key
                params.put("Api-Key", "service.VNlPhrWb3wYRzEYmstQh3GrAXyhyaN55AqUSRR3V");
                return params;
            }
        };

        // Add the request to the queue
        requestQueue.add(reverseGeoSearchRequest);
    }



    // Drawing line on map
    public LineGeom drawLineGeom(List<PolylineEncoding.LatLng> paths) {
        // we clear every line that is currently on map
        lineLayer.clear();
        // Adding some LngLat points to a LngLatVector
        LngLatVector lngLatVector = new LngLatVector();
        for (PolylineEncoding.LatLng path : paths) {
            lngLatVector.add(new LngLat(path.lng, path.lat));
        }
        // Creating a lineGeom from LngLatVector
        LineGeom lineGeom = new LineGeom(lngLatVector);
        // Creating a line from LineGeom. here we use getLineStyle() method to define line styles
        Line line = new Line(lineGeom, getLineStyle());
        // adding the created line to lineLayer, showing it on map
        lineLayer.add(line);
        // focusing camera on first point of drawn line
        mapSetPosition(overview);
        return lineGeom;
    }

    // for overview routing we zoom out and review hole route and for stepByStep routing we just zoom to first marker position
    private void mapSetPosition(boolean overview) {
        double centerFirstMarkerX = userMarkerLayer.getAll().get(0).getGeometry().getCenterPos().getX();
        double centerFirstMarkerY = userMarkerLayer.getAll().get(0).getGeometry().getCenterPos().getY();
        if (overview) {
            double centerFocalPositionX = (centerFirstMarkerX + userMarkerLayer.getAll().get(1).getGeometry().getCenterPos().getX()) / 2;
            double centerFocalPositionY = (centerFirstMarkerY + userMarkerLayer.getAll().get(1).getGeometry().getCenterPos().getY()) / 2;
            map.setFocalPointPosition(new LngLat(centerFocalPositionX, centerFocalPositionY),0.5f );
            map.setZoom(14,0.5f);
        } else {
            map.setFocalPointPosition(new LngLat(centerFirstMarkerX, centerFirstMarkerY),0.5f );
            map.setZoom(18,0.5f);
        }

    }

    // In this method we create a LineStyleCreator, set its features and call buildStyle() method
    // on it and return the LineStyle object (the same routine as crating a marker style)
    private LineStyle getLineStyle(){
        LineStyleCreator lineStCr = new LineStyleCreator();
        lineStCr.setColor(new ARGB((short) 2, (short) 119, (short) 189, (short)190));
        lineStCr.setWidth(10f);
        lineStCr.setStretchFactor(0f);
        return lineStCr.buildStyle();
    }
}