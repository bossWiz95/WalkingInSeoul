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
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

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
        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {
            checkRunTimePermission();
        }

        gpsTracker = new GpsTracker(kakaoMap.this);

        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();


        this.lat = latitude;
        this.lon = longitude;
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // 퍼미션 처리하기위한 dialog 알림
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(kakaoMap.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n 위치 서비스를 켜시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.create().show();

    }

    // 퍼미션 가지고있는지 체크
    void checkRunTimePermission(){

        // 런타임 퍼미션 처리
        // 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(kakaoMap.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(kakaoMap.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 모두 허용되어있으므로 PASS
        } else {
            // 0 > LOCATION check
            if (ActivityCompat.shouldShowRequestPermissionRationale(kakaoMap.this, REQUIRED_PERMISSIONS[0])) {

                Toast.makeText(kakaoMap.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(kakaoMap.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);

                // else문은 COALSE check
            } else {
                ActivityCompat.requestPermissions(kakaoMap.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    // 퍼미션 요청 결과
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            boolean check_result = true;

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if ( check_result ) {
                // 동작 O
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(kakaoMap.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();

                }else {
                    Toast.makeText(kakaoMap.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }

        }
    }
}
