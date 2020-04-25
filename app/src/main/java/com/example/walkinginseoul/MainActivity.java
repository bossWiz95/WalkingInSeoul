package com.example.walkinginseoul;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Spinner;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ParkAdapter.OnItemClickListener{
    private Spinner spinner;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> arrayAdapter;
    private int isSpinner;

    private RecyclerView rv_park;
    private ParkAdapter mParkAdapter;

    private ArrayList<ParkVO> arrparkVO;
    private ParkVO parkVO;

    private MyAsyncTask myAsyncTask = new MyAsyncTask();
    ArrayList<Bundle> arrParkSet;

    private Boolean isFirst = true;

    private SwipeRefreshLayout refresh_layout;

    private ControlActivity controlActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(isFirst) {
            isFirst = false;

            Intent splash = new Intent(this, SplashActivity.class);
            startActivity(splash);
        }

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

        spinner = findViewById(R.id.spinner);
        refresh_layout = findViewById(R.id.refresh_layout);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 미구현.
                refresh_layout.setRefreshing(false);
            }
        });

        controlActivity = new ControlActivity();
        controlActivity.initCommonControls(new View[]{findViewById(R.id.fullscreen_load),
                findViewById(R.id.fullscreen_load_bar),findViewById(R.id.fullscreen_load_txt)});

        spinnerSetting();
        isSpinner = 0;

        controlActivity.showFullscreenLoading(0, "공원 정보를 불러오는 중입니다...");

        myAsyncTask.execute();

        //        controlActivity.showFullscreenLoading(0, "서버 로딩중...");
    }

    // 5번 서울숲 1364번 아차산생태공원 59번 대모산도시자연공원
    private void rvProcessing(String gu) {
        ArrayList<Bundle> arr = new ArrayList<>();
        try{
            if(gu.equals("SEOUL")){
                for(int i = 0; i< arrParkSet.size(); i++){
                    arr.add(arrParkSet.get(i));
                }

//                controlActivity.hideFullscreenLoading();
            }else{
                for(int i = 0; i< arrParkSet.size(); i++){
                    if(arrParkSet.get(i).getString("11").equals(gu)){
                        arr.add(arrParkSet.get(i));
                    }
                }
            }
        } catch (NullPointerException e){
            e.getMessage();
        }

        // 이미지 : 10 제목 : 2, 주소 : 12, 전화번호 : 13
        // 관리부서 : 16, 개원일 : 5, 면적 : 4, 공원 개요 : 3, 오시는길 : 9
        // 위도 : 15, 경도 : 14

        arrparkVO = new ArrayList<ParkVO>();
        for(int i=0; i<arr.size(); i++){
            parkVO = new ParkVO();

            if(arr.get(i).getString("2").equals("서울숲")){
                parkVO.setImg("https://parks.seoul.go.kr/file/info/view.do?fIdx=13154");
            } else if(arr.get(i).getString("2").equals("대모산도시자연공원")){
                parkVO.setImg("https://parks.seoul.go.kr/file/info/view.do?fIdx=17851");
            } else if(arr.get(i).getString("2").equals("아차산생태공원")){
                parkVO.setImg("https://parks.seoul.go.kr/file/info/view.do?fIdx=17852");
            } else{
                parkVO.setImg(arr.get(i).getString("10"));
            }



            parkVO.setAddress(arr.get(i).getString("12"));
            parkVO.setTitle(arr.get(i).getString("2"));
            parkVO.setPhone(arr.get(i).getString("13"));
            parkVO.setDepartment(arr.get(i).getString("16"));
            parkVO.setOpenDate(arr.get(i).getString("5"));
            parkVO.setArea(arr.get(i).getString("4"));
            parkVO.setDetail(arr.get(i).getString("3"));
            parkVO.setWay(arr.get(i).getString("9"));
            parkVO.setLatitude(arr.get(i).getString("15"));
            parkVO.setLongitude(arr.get(i).getString("14"));

            arrparkVO.add(parkVO);
        }



        rv_park = (RecyclerView) findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        rv_park.setLayoutManager(layoutManager);
        mParkAdapter = new ParkAdapter(getApplicationContext(), arrparkVO);
        mParkAdapter.setOnItemClickListener(this);
        rv_park.setAdapter(mParkAdapter);

        controlActivity.hideFullscreenLoading();
    }

    @Override
    public void onItemClick(View view, ParkVO parkVO) {
        Intent intent = new Intent(this, DetailActivity.class);
        // 이미지 : 10 제목 : 2, 주소 : 12, 전화번호 : 13
        // 관리부서 : 16, 개원일 : 5, 면적 : 4, 공원 개요 : 3, 오시는길 : 9


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

        arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, arrayList);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    if(isSpinner < 1){
                        Toast.makeText(getApplicationContext(), "반갑습니다.", Toast.LENGTH_LONG).show();
                        isSpinner++;
                    }else{
                        rvProcessing("SEOUL");
                    }
                }else{
                    rvProcessing(arrayList.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    public class MyAsyncTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            arrParkSet = new ArrayList<>();

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

                // XML 종료 시까지 반복
                while( eventType != XmlPullParser.END_DOCUMENT ){
                    switch( eventType ){
                        case XmlPullParser.START_DOCUMENT:
                            Log.e("XML Parsing", "파싱 시작 ..");
                            break;

                        case XmlPullParser.START_TAG:
                            // 태그 이름 얻어오기
                            tag= xpp.getName();

                            // 첫번째 검색결과
                            if(tag.equals("row")){
                                bundle = new Bundle();

                                xpp.next();
                            }

                            else if(tag.equals("P_IDX")){
                                xpp.next();

                                bundle.putString("1", xpp.getText());
                            }
                            else if(tag.equals("P_PARK")){
                                xpp.next();

                                bundle.putString("2", xpp.getText());
                            }
                            else if(tag.equals("P_LIST_CONTENT")){
                                xpp.next();

                                bundle.putString("3", xpp.getText());
                            }
                            else if(tag.equals("AREA")){
                                xpp.next();

                                bundle.putString("4", xpp.getText());
                            }
                            else if(tag.equals("OPEN_DT")){
                                xpp.next();

                                bundle.putString("5", xpp.getText());
                            }
                            else if(tag.equals("MAIN_EQUIP")){
                                xpp.next();

                                bundle.putString("6", xpp.getText());
                            }
                            else if(tag.equals("MAIN_PLANTS")){
                                xpp.next();

                                bundle.putString("7", xpp.getText());
                            }
                            else if(tag.equals("GUIDANCE")){
                                xpp.next();

                                bundle.putString("8", xpp.getText());
                            }
                            else if(tag.equals("VISIT_ROAD")){
                                xpp.next();

                                bundle.putString("9", xpp.getText());
                            }
                            else if(tag.equals("P_IMG")){
                                xpp.next();

                                bundle.putString("10", xpp.getText());
                            }
                            else if(tag.equals("P_ZONE")){
                                xpp.next();

                                bundle.putString("11", xpp.getText());
                            }
                            else if(tag.equals("P_ADDR")){
                                xpp.next();

                                bundle.putString("12", xpp.getText());
                            }
                            else if(tag.equals("P_ADMINTEL")){
                                xpp.next();

                                bundle.putString("13", xpp.getText());
                            }
                            else if(tag.equals("LONGITUDE")){
                                xpp.next();

                                bundle.putString("14", xpp.getText());
                            }
                            else if(tag.equals("LATITUDE")){
                                xpp.next();

                                bundle.putString("15", xpp.getText());
                            }
                            else if(tag.equals("P_NAME")){
                                xpp.next();

                                bundle.putString("16", xpp.getText());
                            }
                            else if(tag.equals("TEMPLATE_URL")){
                                xpp.next();

                                bundle.putString("17", xpp.getText());
                                arrParkSet.add(bundle);
                            }
                            break;

                        case XmlPullParser.TEXT:
                            break;

                        case XmlPullParser.END_TAG:
                            tag= xpp.getName(); //태그 이름 얻어오기

                            Log.e("XMLXML", tag);
                            Log.e("XML Parsing", "END_TAG");

                            break;
                    }

                    eventType= xpp.next();

                    Log.e("Parsing Data Size", arrParkSet.size() + "");
                }

            } catch (Exception e) {
                // TODO Auto-generated catch blocke.printStackTrace();
                e.printStackTrace();
            }
            Log.e("XML Parsing END", "... Completed");
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Log.e("myAsyncTask END", "... Completed");
            Log.e("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@22","####");
            rvProcessing("SEOUL");
        }
    }
}
