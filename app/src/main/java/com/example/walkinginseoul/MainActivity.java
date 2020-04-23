package com.example.walkinginseoul;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements ParkAdapter.OnItemClickListener{
    private TextView tv;
    private Button btnGo;
    private String data;

    private Spinner spinner;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;

    private Context mContext;
    private RecyclerView rv_park;
    private ParkAdapter mParkAdapter;

    private ArrayList<ParkVO> arrparkVO;
    private ParkVO parkVO;

    private MyAsyncTask myAsyncTask = new MyAsyncTask();
    ArrayList<Bundle> arrayList2;

    private Boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(isFirst){
            isFirst = false;

            Intent splash = new Intent(this, SplashActivity.class);
            startActivity(splash);
        }

        mContext = this;

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.walkinginseoul", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        tv = findViewById(R.id.tv);
        btnGo = findViewById(R.id.btngo);
        spinner = findViewById(R.id.spinner);
        Log.e("a", "a");
        spinnerSetting();

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAsyncTask.execute();
            }
        });

        myAsyncTask.execute();
    }

    // 5번 서울숲 1364번 아차산생태공원 59번 대모산도시자연공원
    private void rvProcessing(String gu) {
        ArrayList<Bundle> arr = new ArrayList<>();
        Log.e("a", "a2");
        try{
            if(gu.equals("SEOUL")){
                for(int i=0; i<arrayList2.size(); i++){
                    arr.add(arrayList2.get(i));
                }
            }else{
                for(int i=0; i<arrayList2.size(); i++){
                    if(arrayList2.get(i).getString("11").equals(gu)){
                        arr.add(arrayList2.get(i));
                    }
                }
            }
        } catch (NullPointerException e){
            e.getMessage();
            Log.e("eerrror", "d");
        }

        // 이미지 : 10 제목 : 2, 주소 : 12, 전화번호 : 13
        // 관리부서 : 17, 개원일 : 5, 면적 : 4, 공원 개요 : 3, 오시는길 : 9
        // 위도 : 15, 경도 : 14

        arrparkVO = new ArrayList<ParkVO>();
        Log.e("a", "a3");
        for(int i=0; i<arr.size(); i++){
            parkVO = new ParkVO();

            if(arr.get(i).getString("2").equals("서울숲")){
                parkVO.setImg("https://parks.seoul.go.kr/file/info/view.do?fIdx=13154");
                Log.e("@@@1", arr.get(i).getString("2"));
            } else if(arr.get(i).getString("2").equals("대모산도시자연공원")){
                parkVO.setImg("https://parks.seoul.go.kr/file/info/view.do?fIdx=17851");
                Log.e("@@@2", arr.get(i).getString("2"));
            } else if(arr.get(i).getString("2").equals("아차산생태공원")){
                parkVO.setImg("https://parks.seoul.go.kr/file/info/view.do?fIdx=17852");
                Log.e("@@@3", arr.get(i).getString("2"));
            } else{
                parkVO.setImg(arr.get(i).getString("10"));
                Log.e("@@@4", arr.get(i).getString("2"));
            }



            parkVO.setAddress(arr.get(i).getString("12"));
            parkVO.setTitle(arr.get(i).getString("2"));
            parkVO.setPhone(arr.get(i).getString("13"));
            parkVO.setDepartment(arr.get(i).getString("17"));
            parkVO.setOpenDate(arr.get(i).getString("5"));
            parkVO.setArea(arr.get(i).getString("4"));
            parkVO.setDetail(arr.get(i).getString("3"));
            parkVO.setWay(arr.get(i).getString("9"));
            parkVO.setLatitude(arr.get(i).getString("15"));
            parkVO.setLongitude(arr.get(i).getString("14"));

            arrparkVO.add(parkVO);
        }



        rv_park = (RecyclerView) findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        rv_park.setLayoutManager(layoutManager);
        mParkAdapter = new ParkAdapter(mContext, arrparkVO);
        mParkAdapter.setOnItemClickListener(this);
        rv_park.setAdapter(mParkAdapter);
    }

    @Override
    public void onItemClick(View view, ParkVO parkVO) {
        Intent intent = new Intent(this, DetailActivity.class);
        // 이미지 : 10 제목 : 2, 주소 : 12, 전화번호 : 13
        // 관리부서 : 17, 개원일 : 5, 면적 : 4, 공원 개요 : 3, 오시는길 : 9


        intent.putExtra("image", parkVO.getImg());
        intent.putExtra("title", parkVO.getTitle());
        intent.putExtra("address", parkVO.getAddress());
        intent.putExtra("phone", parkVO.getPhone());
        intent.putExtra("department", parkVO.getDepartment());
        intent.putExtra("opendate", parkVO.getOpenDate());
        intent.putExtra("area", parkVO.getArea());
        intent.putExtra("detail", parkVO.getDetail());
        intent.putExtra("way", parkVO.getWay());
        intent.putExtra("latitude", parkVO.getLatitude());
        intent.putExtra("longitude", parkVO.getLongitude());

        startActivity(intent);
    }

    private void spinnerSetting() {
        arrayList = new ArrayList<String>();

        arrayList.add("전체 보기");
        arrayList.add("강남구");
        arrayList.add("강동구");
        arrayList.add("강북구");
        arrayList.add("강서구");
        arrayList.add("과천시");
        arrayList.add("관악구");
        arrayList.add("광진구");
        arrayList.add("구로구");
        arrayList.add("금천구");
        arrayList.add("노원구");
        arrayList.add("도봉구");
        arrayList.add("동대문구");
        arrayList.add("동작구");
        arrayList.add("마포구");
        arrayList.add("서대문구");
        arrayList.add("서초구");
        arrayList.add("성동구");
        arrayList.add("성북구");
        arrayList.add("송파구");
        arrayList.add("양천구");
        arrayList.add("영등포구");
        arrayList.add("용산구");
        arrayList.add("은평구");
        arrayList.add("종로구");
        arrayList.add("기타");
        Log.e("a", "a4");
        arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, arrayList);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    rvProcessing("SEOUL");
                }else{
                    rvProcessing(arrayList.get(position));
                }

                Log.e("a", "a6");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    public class MyAsyncTask extends AsyncTask<Void, String, String>{

        @Override
        protected String doInBackground(Void... Void) {
            StringBuffer buffer = new StringBuffer();
            arrayList2 = new ArrayList<>();

            Bundle bundle = null;
            String queryUrl="http://openapi.seoul.go.kr:8088/4e6d464a42726577383861756e7759/xml/SearchParkInfoService/1/200/";

            try {
                URL url= new URL(queryUrl);//문자열로 된 요청 url을 URL 객체로 생성.
                InputStream is= url.openStream(); //url위치로 입력스트림 연결

                XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
                XmlPullParser xpp= factory.newPullParser();
                xpp.setInput( new InputStreamReader(is, "UTF-8") ); //inputstream 으로부터 xml 입력받기

                String tag;

                xpp.next();
                int eventType= xpp.getEventType();
                Log.e("a", "7a");

                while( eventType != XmlPullParser.END_DOCUMENT ){

                    Log.e("a", "7a2222");
                    switch( eventType ){
                        case XmlPullParser.START_DOCUMENT:

                            Log.e("a", "7a33333");
                            buffer.append("파싱 시작...\n\n");
                            break;
                        case XmlPullParser.START_TAG:
                            tag= xpp.getName();//태그 이름 얻어오기
                            if(tag.equals("row")){// 첫번째 검색결과
                                bundle = new Bundle();
                            }

                            else if(tag.equals("P_IDX")){
                                buffer.append("공원 번호 : ");
                                xpp.next();
                                buffer.append(xpp.getText());//addr 요소의 TEXT 읽어와서 문자열버퍼에 추가
                                buffer.append("\n"); //줄바꿈 문자 추가

                                bundle.putString("1", xpp.getText());
                            }
                            else if(tag.equals("P_PARK")){
                                buffer.append("공원명 : ");
                                xpp.next();
                                buffer.append(xpp.getText());
                                buffer.append("\n");
                                bundle.putString("2", xpp.getText());
                            }
                            else if(tag.equals("P_LIST_CONTENT")){
                                buffer.append("공원 설명 :");
                                xpp.next();
                                buffer.append(xpp.getText());//cpId
                                buffer.append("\n");
                                Log.e("a", "7a444444");
                                bundle.putString("3", xpp.getText());
                            }
                            else if(tag.equals("AREA")){
                                buffer.append("공원 크기 :");
                                xpp.next();
                                buffer.append(xpp.getText());//cpNm
                                buffer.append("\n");

                                bundle.putString("4", xpp.getText());
                            }
                            else if(tag.equals("OPEN_DT")){
                                buffer.append("개장일 :");
                                xpp.next();
                                buffer.append(xpp.getText());//
                                buffer.append("\n");

                                bundle.putString("5", xpp.getText());
                            }
                            else if(tag.equals("MAIN_EQUIP")){
                                buffer.append("주요 시설 :");
                                xpp.next();
                                buffer.append(xpp.getText());//
                                buffer.append("  ,  ");

                                bundle.putString("6", xpp.getText());
                            }
                            else if(tag.equals("MAIN_PLANTS")){
                                buffer.append("주요 식물 :");
                                xpp.next();
                                buffer.append(xpp.getText());//csId
                                buffer.append("\n");

                                bundle.putString("7", xpp.getText());
                            }
                            else if(tag.equals("GUIDANCE")){
                                buffer.append("공원 상세 이미지 :");
                                xpp.next();
                                buffer.append(xpp.getText());
                                buffer.append("\n");

                                bundle.putString("8", xpp.getText());
                            }
                            else if(tag.equals("VISIT_ROAD")){
                                buffer.append("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                                buffer.append("오시는 길 :");
                                xpp.next();
                                buffer.append(xpp.getText());//
                                buffer.append("\n");
                                Log.e("a", "766123213a");
                                bundle.putString("9", xpp.getText());
                            }
                            else if(tag.equals("P_IMG")){
                                buffer.append("공원 전망도 :");
                                xpp.next();
                                buffer.append(xpp.getText());//
                                buffer.append("\n");

                                bundle.putString("10", xpp.getText());
                            }
                            else if(tag.equals("P_ZONE")){
                                buffer.append("포함 자치구 :");
                                xpp.next();
                                buffer.append(xpp.getText());//
                                buffer.append("\n");

                                bundle.putString("11", xpp.getText());
                            }
                            else if(tag.equals("P_ADDR")){
                                buffer.append("공원 주소 :");
                                xpp.next();
                                buffer.append(xpp.getText());//
                                buffer.append("\n");

                                bundle.putString("12", xpp.getText());
                            }
                            else if(tag.equals("P_ADMINTEL")){
                                buffer.append("공원 전화번호 :");
                                xpp.next();
                                buffer.append(xpp.getText());//
                                buffer.append("\n");

                                bundle.putString("13", xpp.getText());
                            }
                            else if(tag.equals("LONGITUDE")){
                                buffer.append("경도 :");
                                xpp.next();
                                buffer.append(xpp.getText());//
                                buffer.append("\n");

                                bundle.putString("14", xpp.getText());
                            }
                            else if(tag.equals("LATITUDE")){
                                buffer.append("위도 :");
                                xpp.next();
                                buffer.append(xpp.getText());//
                                buffer.append("\n");

                                bundle.putString("15", xpp.getText());
                            }
                            else if(tag.equals("P_NAME")){
                                buffer.append("관리부서 :");
                                xpp.next();
                                buffer.append(xpp.getText());//
                                buffer.append("\n");

                                bundle.putString("17", xpp.getText());
                            }
                            else if(tag.equals("TEMPLATE_URL")){
                                buffer.append("홈페이지 URL :");
                                xpp.next();
                                buffer.append(xpp.getText());//
                                buffer.append("\n");
                                buffer.append("------------------------------------------------");
                                Log.e("a", "7@@@@a");
                                bundle.putString("16", xpp.getText());
                                arrayList2.add(bundle);
                            }
                            break;

                        case XmlPullParser.TEXT:
                            break;

                        case XmlPullParser.END_TAG:
                            tag= xpp.getName(); //태그 이름 얻어오기

                            if(tag.equals("row")) buffer.append("\n");// 첫번째 검색결과종료..줄바꿈

                            break;
                    }

                    eventType= xpp.next();

                    Log.e("AAA", arrayList2.size() + "");
                }

            } catch (Exception e) {
                // TODO Auto-generated catch blocke.printStackTrace();
                Log.e("asdsad", e.getMessage());
                e.printStackTrace();
            }

            buffer.append("파싱 끝\n");

            return buffer.toString();//StringBuffer 문자열 객체 반환
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e(">>", s + "\n");
            tv.setText(s);
        }
    }
}
