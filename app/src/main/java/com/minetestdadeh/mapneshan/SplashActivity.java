package com.minetestdadeh.mapneshan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class SplashActivity extends AppCompatActivity {

    Button btn_main,btn_label ,btn_add_marker,btn_direction
            ,btn_myDirection,btn_api_retrofit,btn_name_of_area ,btn_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        btn_main = findViewById(R.id.btn_main);
        btn_label = findViewById(R.id.btn_lable);
        btn_add_marker = findViewById(R.id.btn_add_marker);
        btn_direction = findViewById(R.id.btn_direction);
        btn_myDirection = findViewById(R.id.btn_myDirection);
        btn_api_retrofit = findViewById(R.id.btn_api_retrofit);
        btn_name_of_area = findViewById(R.id.btn_name_of_area);
        btn_search = findViewById(R.id.btn_search);

        btn_main.setOnClickListener(view -> startActivity(new Intent(SplashActivity.this,MainActivity.class)));
        btn_label.setOnClickListener(view -> startActivity(new Intent(SplashActivity.this, LabelAddActivity.class)));
        btn_add_marker.setOnClickListener(view -> startActivity(new Intent(SplashActivity.this, AddMarkerActivity.class)));
        btn_direction.setOnClickListener(view -> startActivity(new Intent(SplashActivity.this, DirectionActivity.class)));
        btn_myDirection.setOnClickListener(view -> startActivity(new Intent(SplashActivity.this, DirectionMineActivity.class)));
        btn_api_retrofit.setOnClickListener(view -> startActivity(new Intent(SplashActivity.this, AddressAPIretrofit.class)));
        btn_name_of_area.setOnClickListener(view -> startActivity(new Intent(SplashActivity.this, NameOfAreaActivity.class)));
        btn_search.setOnClickListener(view -> startActivity(new Intent(SplashActivity.this, SearchActivity.class)));

    }
}