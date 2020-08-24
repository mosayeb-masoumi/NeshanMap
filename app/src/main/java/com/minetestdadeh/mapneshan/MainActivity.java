package com.minetestdadeh.mapneshan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.minetestdadeh.mapneshan.util.RecordKeeper;

import org.neshan.core.Bounds;
import org.neshan.core.LngLat;
import org.neshan.core.Range;
import org.neshan.layers.VectorElementLayer;
import org.neshan.services.NeshanMapStyle;
import org.neshan.services.NeshanServices;
import org.neshan.styles.AnimationStyle;
import org.neshan.styles.AnimationStyleBuilder;
import org.neshan.styles.AnimationType;
import org.neshan.styles.MarkerStyle;
import org.neshan.styles.MarkerStyleCreator;
import org.neshan.ui.ClickData;
import org.neshan.ui.ClickType;
import org.neshan.ui.MapEventListener;
import org.neshan.ui.MapView;
import org.neshan.utils.BitmapUtils;
import org.neshan.vectorelements.Marker;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    // used to track request permissions
    final int REQUEST_CODE = 123;

    final int BASE_MAP_INDEX = 0;

    // User's current location
    Location userLocation;
    FusedLocationProviderClient fusedLocationClient;

    LngLat clickedLocation;
    NeshanMapStyle mapStyle;
    DrawerLayout drawer;
    //    NavigationView nav;
//    Toolbar toolbar;
    Button focusOnUserLocationBtn;

    VectorElementLayer userMarkerLayer;
    VectorElementLayer markerLayer;
    VectorElementLayer lineLayer;
    VectorElementLayer polygonLayer;
    private MapView map;
    Button btn_currentlocation;
    Button btn_night_mode;
    Button btn_day_mode;
    Button btn_btn_reset_camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        initView();
        initMap();
        initLocation();


        btn_currentlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
            }
        });


        // when clicked on map, a marker is added in click location
        map.setMapEventListener(new MapEventListener() {
            @Override
            public void onMapClicked(ClickData mapClickInfo) {
                if (mapClickInfo.getClickType() == ClickType.CLICK_TYPE_LONG) {
                    clickedLocation = mapClickInfo.getClickPos();
                    LngLat lngLat = new LngLat(clickedLocation.getX(), clickedLocation.getY());
                    addMarker(lngLat);
                }
            }
        });

        btn_night_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                drawer.closeDrawer(GravityCompat.START);
                map.getLayers().remove(map.getLayers().get(0));
                map.getLayers().insert(0, NeshanServices.createBaseMap(NeshanMapStyle.STANDARD_NIGHT));
                RecordKeeper.instance(MainActivity.this).setMapTheme(NeshanMapStyle.STANDARD_NIGHT);
                mapStyle = NeshanMapStyle.STANDARD_NIGHT;
//                initSideNavigation();
            }
        });

        btn_day_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                drawer.closeDrawer(GravityCompat.START);
                map.getLayers().remove(map.getLayers().get(0));
                map.getLayers().insert(0, NeshanServices.createBaseMap(NeshanMapStyle.STANDARD_DAY));
                RecordKeeper.instance(MainActivity.this).setMapTheme(NeshanMapStyle.STANDARD_DAY);
                mapStyle = NeshanMapStyle.STANDARD_DAY;
//                initSideNavigation();
            }
        });

        btn_btn_reset_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();
        getLocationPermission();
    }

    private void initLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }

    private void initMap() {

        userMarkerLayer = NeshanServices.createVectorElementLayer();
        markerLayer = NeshanServices.createVectorElementLayer();
        lineLayer = NeshanServices.createVectorElementLayer();
        polygonLayer = NeshanServices.createVectorElementLayer();
        map.getLayers().add(userMarkerLayer);
        map.getLayers().add(markerLayer);
        map.getLayers().add(lineLayer);
        map.getLayers().add(polygonLayer);

        // add Standard_day map to layers
        mapStyle = RecordKeeper.instance(this).getMapTheme();
        map.getOptions().setZoomRange(new Range(4.5f, 18f));
        map.getLayers().insert(BASE_MAP_INDEX, NeshanServices.createBaseMap(mapStyle));

        //set map focus position
        map.setFocalPointPosition(new LngLat(53.529929, 35.164676), 0f);
        map.setZoom(4.5f, 2f);

        //Iran Bound
        map.getOptions().setPanBounds(new Bounds(
                new LngLat(43.505859, 24.647017),
                new LngLat(63.984375, 40.178873))
        );
    }


    private void initView() {
        map = findViewById(R.id.map);
        btn_currentlocation = findViewById(R.id.btn_currentlocation);
        btn_night_mode = findViewById(R.id.btn_night_mode);
        btn_day_mode = findViewById(R.id.btn_day_mode);
    }

//    @SuppressLint("MissingPermission")
    private void getLastLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            onLocationChange(task.getResult());
//                            Log.i(TAG, "lat "+task.getResult().getLatitude()+" lng "+task.getResult().getLongitude());
                        } else {
                            Toast.makeText(MainActivity.this, "موقعیت یافت نشد.", Toast.LENGTH_SHORT).show();
                        }
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
        markStCr.setSize(20f);
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker)));
        markStCr.setAnimationStyle(animSt);
        MarkerStyle markSt = markStCr.buildStyle();

        Marker marker = new Marker(loc, markSt);

        userMarkerLayer.add(marker);
    }

    private boolean getLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, REQUEST_CODE);
                return false;
            }
        }
        return true;
    }

    private void addMarker(LngLat loc) {
        markerLayer.clear();  // to clear the former marker

        AnimationStyleBuilder animStBl = new AnimationStyleBuilder();
        animStBl.setFadeAnimationType(AnimationType.ANIMATION_TYPE_SMOOTHSTEP);
        animStBl.setSizeAnimationType(AnimationType.ANIMATION_TYPE_SPRING);
        animStBl.setPhaseInDuration(0.5f);
        animStBl.setPhaseOutDuration(0.5f);
        AnimationStyle animSt = animStBl.buildStyle();

        MarkerStyleCreator markStCr = new MarkerStyleCreator();
        markStCr.setSize(20f);
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker)));
        markStCr.setAnimationStyle(animSt);
        MarkerStyle markSt = markStCr.buildStyle();

        Marker marker = new Marker(loc, markSt);
        markerLayer.add(marker);
    }


    private void initSideNavigation() {
    }

}