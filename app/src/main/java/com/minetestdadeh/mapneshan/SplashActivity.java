package com.minetestdadeh.mapneshan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SplashActivity extends AppCompatActivity {

    Button btn_main,btn_label ,btn_add_marker,btn_direction ,btn_myDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        btn_main = findViewById(R.id.btn_main);
        btn_label = findViewById(R.id.btn_lable);
        btn_add_marker = findViewById(R.id.btn_add_marker);
        btn_direction = findViewById(R.id.btn_direction);
        btn_myDirection = findViewById(R.id.btn_myDirection);

        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashActivity.this,MainActivity.class));
            }
        });

        btn_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashActivity.this, LabelAddActivity.class));
            }
        });
        btn_add_marker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashActivity.this, AddMarkerActivity.class));
            }
        });

        btn_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashActivity.this, DirectionActivity.class));
            }
        });

        btn_myDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashActivity.this, DirectionMineTest.class));
            }
        });

    }
}