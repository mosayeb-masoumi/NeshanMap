package com.minetestdadeh.mapneshan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.minetestdadeh.mapneshan.model.NeshanAddress;
import com.minetestdadeh.mapneshan.network.RetrofitClientInstance;
import com.minetestdadeh.mapneshan.network.ReverseService;
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
import org.neshan.ui.MapView;
import org.neshan.utils.BitmapUtils;
import org.neshan.vectorelements.Marker;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class NameOfAreaActivity extends AppCompatActivity {


    // used to track request permissions
    final int REQUEST_CODE = 123;

    final int BASE_MAP_INDEX = 0;

    // User's current location
    Location userLocation;
    FusedLocationProviderClient fusedLocationClient;

    NeshanMapStyle mapStyle;

    VectorElementLayer userMarkerLayer;
    private MapView map;

    //    double currentLAT=0.0;
//    double currentLNG=0.0;
    public static final ReverseService GET_DATA_SERVICE = RetrofitClientInstance.getRetrofitInstance().create(ReverseService.class);
    private final PublishSubject<LngLat> locationPublishSubject = PublishSubject.create();
    private Disposable disposable;
    private LngLat currentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_name_of_area);

    }

    @Override
    protected void onStart() {
        super.onStart();
        initView();
        initMap();
    }

    private void initView() {
        map = findViewById(R.id.map);
    }


    private void initMap() {

        userMarkerLayer = NeshanServices.createVectorElementLayer();
        map.getLayers().add(userMarkerLayer);

        // initLocation
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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

        getCurrentLocation();
    }

    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        onLocationChange(task.getResult());
                    } else {
                        Toast.makeText(NameOfAreaActivity.this, "موقعیت یافت نشد.", Toast.LENGTH_SHORT).show();
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

        getAreaName(loc);
    }

    private void getAreaName(LngLat currentLocation) {
        initReverseObserver(currentLocation);
    }

    private void initReverseObserver(LngLat currentLocation) {

        disposable = locationPublishSubject
                .debounce(100, TimeUnit.MILLISECONDS)
                .flatMap(this::getReverseObserver)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    Toast.makeText(this, result.getState() + "", Toast.LENGTH_SHORT).show();
                }, t -> {
                    Toast.makeText(this, "شهر بی‌نام", Toast.LENGTH_SHORT).show();
                });

        locationPublishSubject.onNext(currentLocation);
    }

    private Observable<NeshanAddress> getReverseObserver(LngLat it) {
        return GET_DATA_SERVICE
                .getReverse("https://api.neshan.org/v1/reverse?lat=" + it.getY() + "&lng=" + it.getX())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}