package com.example.walkinginseoul;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    private ArrayList<String> arrayList = new ArrayList<>();

    private ImageView mImgView;
    private TextView mTitle;
    private TextView mAddress;
    private TextView mPhone;
    private TextView mDepartment;
    private TextView mOpenDate;
    private TextView mArea;
    private TextView mDetail;
    private TextView mWay;
    private Button btnShowMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String[] values = {"image", "title", "address", "phone", "department",
                "opendate", "area", "detail", "way", "latitude", "longitude"};

        Intent intent = getIntent();
        for(int i=0; i< values.length; i++){
            arrayList.add(intent.getExtras().getString(values[i]));
        }

        setReference();

        Glide.with(this).load(arrayList.get(0)).into(mImgView);
        mTitle.setText("● 지명 : " + arrayList.get(1));
        mAddress.setText("\n● 주소 : " + arrayList.get(2));
        mPhone.setText("\n● 전화번호 : " + arrayList.get(3));
        mDepartment.setText("\n● 관리부서 : " + arrayList.get(4));
        mOpenDate.setText("\n● 개원일 : " + arrayList.get(5));
        mArea.setText("\n● 면적 : " + arrayList.get(6));
        mDetail.setText("\n● 상세 정보 :\n" + arrayList.get(7));
        mWay.setText("\n● 오시는 길 :\n" + arrayList.get(8));

        btnShowMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), kakaoMap.class);
                intent.putExtra("latitude", arrayList.get(9));
                intent.putExtra("longitude", arrayList.get(10));
                startActivity(intent);
            }
        });
    }

    private void setReference() {
        mImgView = findViewById(R.id.d_img);
        mTitle = findViewById(R.id.d_txtTitle);
        mAddress = findViewById(R.id.d_txtAddress);
        mPhone = findViewById(R.id.d_txtCallNum);
        mDepartment = findViewById(R.id.d_txtDepartment);
        mOpenDate = findViewById(R.id.d_txtDate);
        mArea = findViewById(R.id.d_txtArea);
        mDetail = findViewById(R.id.d_txtdetail);
        mWay = findViewById(R.id.d_txtway);
        btnShowMap = findViewById(R.id.btnShowMap);
    }
}
