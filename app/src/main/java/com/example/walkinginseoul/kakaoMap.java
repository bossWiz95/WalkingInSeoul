package com.example.walkinginseoul;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;


public class kakaoMap extends AppCompatActivity {
    private String latitude;
    private String longitude;
    private String name;

    private MapView mapView;

    private Button btnCurrent;

    private double lat;
    private double lon;

    // 위경도 관련
    private GpsTracker gpsTracker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakaomap);

        Intent intent = getIntent();

        latitude = intent.getExtras().getString("latitude");
        longitude = intent.getExtras().getString("longitude");
        name = intent.getExtras().getString("name");

        currentGPS();

        btnCurrent = findViewById(R.id.btnCurrent);
        btnCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToCurrent();
            }
        });

        mapView = new MapView(this);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);

        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(Double.parseDouble(latitude), Double.parseDouble(longitude));
        mapView.setMapCenterPoint(mapPoint, true);
        mapViewContainer.addView(mapView);


        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(name);
        marker.setTag(0);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker);
    }

    private void moveToCurrent() {
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(lat, lon);
        mapView.setMapCenterPoint(mapPoint, true);

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("내 위치");
        marker.setTag(1);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker);
    }

    private void currentGPS() {


        gpsTracker = new GpsTracker(kakaoMap.this);

        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();


        this.lat = latitude;
        this.lon = longitude;
    }

}
