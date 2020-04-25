package com.example.walkinginseoul;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
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
    private static final String TAG = "MainActivity";
    private String API_url = "http://openapi.seoul.go.kr:8088/4e6d464a42726577383861756e7759/xml/SearchParkInfoService/1/200/";

    // 초기 Splash 화면
    private Boolean mIsSplash = true;

    // 자치구 데이터 - Spinner
    private Spinner mSpinner;
    private ArrayList<String> mSpinner_ArrList;
    private ArrayAdapter<String> mSpinner_ArrAdapter;
    private int mIsSpinner;

    // RecyclerView 관련
    private RecyclerView mRecyclerView;
    private ArrayList<ParkVO> mRecycler_ArrList;
    private ParkAdapter mRecycler_Adapter;
    private ParkVO mParkVO;

    // API Data Parsing
    private MyAsyncTask mAsyncGetData = new MyAsyncTask();
    private ArrayList<Bundle> mDataSet_ArrList;
    private int error_count = 0;


    // ScreenLoadingBar 제어
    private ControlActivity mControlActivity;

    // 데이터 갯수 및 앱 정보화면
    private TextView txtDataNum;
    private Button btnAppInfo;

    // 뒤로가기 1회 시 종료 방지
    private BackPressHandler mBackPressHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(mIsSplash) {
            mIsSplash = false;

            Intent splash = new Intent(this, SplashActivity.class);
            startActivity(splash);

            // 파싱이 안되거나 로딩이 길어질 때 15초 뒤 알림.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(error_count == 100){
                    }else{
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("데이터를 일정 시간동안 받아오지 못했습니다.\n")
                                .append("네트워크 상태를 확인해주세요");

                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("오류").setMessage(stringBuilder)
                                .setPositiveButton("확인", (dialog, which) -> {
                                    mControlActivity.hideFullscreenLoading();
                                });
                    }
                }
            }, 15000);
        }

        // Kakao Map HashKey 추출.
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.walkinginseoul", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d(TAG, "KeyHash:" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // XML 참조 정의
        setReference();

        // ScreenLoading 객체 생성 후 View 전달
        mControlActivity = new ControlActivity();
        mControlActivity.initCommonControls(new View[]{findViewById(R.id.fullscreen_load),
                findViewById(R.id.fullscreen_load_bar),findViewById(R.id.fullscreen_load_txt)});

        // Spinner Setting
        spinnerSetting();

        // ScreenLoading 창 띄워 데이터 다 받을때까지 입력 방지.
        mControlActivity.showFullscreenLoading(0, "공원 정보를 불러오는 중입니다...");

        // API Data Parsing Start
        mAsyncGetData.execute();

        // 뒤로가기 객체 정의 후 Context 넘겨줌
        mBackPressHandler = new BackPressHandler(this);

        // 앱 정보 표시
        btnAppInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder str = new StringBuilder();
                str.append("서울산책하자\n\n").append("서울시자치구공원정보를 제공해줍니다! \n")
                        .append("오류나 개선사항이 있으면 아래로 연락 부탁드립니다.\n\n")
                        .append("Author : dongbin\n\n").append("Github Address : /github.com/mdongbin\n\n")
                        .append("Email Address : rewgh0@gmail.com");

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("앱 정보").setMessage(str)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
            }
        });
    }

    // 참조 정의
    private void setReference() {
        mSpinner = findViewById(R.id.spinner);
        txtDataNum = findViewById(R.id.txtMainInfo);
        btnAppInfo = findViewById(R.id.btnAppInfo);
    }

    // RecyclerView 관련.
    private void rvProcessing(String str) {
        ArrayList<Bundle> arrayList = new ArrayList<>();
        StringBuffer stringBuffer = new StringBuffer();
        mRecycler_ArrList = new ArrayList<ParkVO>();

        try{
            if(str.equals("서울시 전체")){
                for(int i = 0; i< mDataSet_ArrList.size(); i++){
                    arrayList.add(mDataSet_ArrList.get(i));
                }
            }else{
                for(int i = 0; i< mDataSet_ArrList.size(); i++){
                    if(mDataSet_ArrList.get(i).getString("11").equals(str)){
                        arrayList.add(mDataSet_ArrList.get(i));
                    }
                }
            }
        } catch (NullPointerException e){
            e.getMessage();
        }

        // 아래 반복문은 이미지가 나타나지 않아 임의로 지정하여 넣어줌
        for(int i=0; i<arrayList.size(); i++){
            mParkVO = new ParkVO();

            if(arrayList.get(i).getString("2").equals("서울숲")){
                mParkVO.setImg("https://parks.seoul.go.kr/file/info/view.do?fIdx=13154");
            } else if(arrayList.get(i).getString("2").equals("대모산도시자연공원")){
                mParkVO.setImg("https://parks.seoul.go.kr/file/info/view.do?fIdx=17851");
            } else if(arrayList.get(i).getString("2").equals("아차산생태공원")){
                mParkVO.setImg("https://parks.seoul.go.kr/file/info/view.do?fIdx=17852");
            } else{
                mParkVO.setImg(arrayList.get(i).getString("10"));
            }

            mParkVO.setAddress(arrayList.get(i).getString("12"));
            mParkVO.setTitle(arrayList.get(i).getString("2"));
            mParkVO.setPhone(arrayList.get(i).getString("13"));
            mParkVO.setDepartment(arrayList.get(i).getString("16"));
            mParkVO.setOpenDate(arrayList.get(i).getString("5"));
            mParkVO.setArea(arrayList.get(i).getString("4"));
            mParkVO.setDetail(arrayList.get(i).getString("3"));
            mParkVO.setWay(arrayList.get(i).getString("9"));
            mParkVO.setLatitude(arrayList.get(i).getString("15"));
            mParkVO.setLongitude(arrayList.get(i).getString("14"));

            mRecycler_ArrList.add(mParkVO);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecycler_Adapter = new ParkAdapter(getApplicationContext(), mRecycler_ArrList);
        mRecycler_Adapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mRecycler_Adapter);

        mControlActivity.hideFullscreenLoading();

        stringBuffer.append(str).append("의 공원이 ").append(mRecycler_ArrList.size())
                .append("개 검색되었습니다.");
        txtDataNum.setText(stringBuffer);
    }

    @Override
    public void onItemClick(View view, ParkVO parkVO) {
        Intent intent = new Intent(this, DetailActivity.class);

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
        // Initializing
        mIsSpinner = 0;

        mSpinner_ArrList = new ArrayList<String>();

        mSpinner_ArrList.add("전체 보기");
        mSpinner_ArrList.add("강남구");
        mSpinner_ArrList.add("강동구");
        mSpinner_ArrList.add("강북구");
        mSpinner_ArrList.add("강서구");
        mSpinner_ArrList.add("과천시");
        mSpinner_ArrList.add("관악구");
        mSpinner_ArrList.add("광진구");
        mSpinner_ArrList.add("구로구");
        mSpinner_ArrList.add("금천구");
        mSpinner_ArrList.add("노원구");
        mSpinner_ArrList.add("도봉구");
        mSpinner_ArrList.add("동대문구");
        mSpinner_ArrList.add("동작구");
        mSpinner_ArrList.add("마포구");
        mSpinner_ArrList.add("서대문구");
        mSpinner_ArrList.add("서초구");
        mSpinner_ArrList.add("성동구");
        mSpinner_ArrList.add("성북구");
        mSpinner_ArrList.add("송파구");
        mSpinner_ArrList.add("양천구");
        mSpinner_ArrList.add("영등포구");
        mSpinner_ArrList.add("용산구");
        mSpinner_ArrList.add("은평구");
        mSpinner_ArrList.add("종로구");
        mSpinner_ArrList.add("기타");

        mSpinner_ArrAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, mSpinner_ArrList);
        mSpinner.setAdapter(mSpinner_ArrAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    if(mIsSpinner < 1){
                        Toast.makeText(getApplicationContext(), "반갑습니다.", Toast.LENGTH_LONG).show();
                        mIsSpinner++;
                    }else{
                        rvProcessing("서울시 전체");
                    }
                }else{
                    rvProcessing(mSpinner_ArrList.get(position));
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
            mDataSet_ArrList = new ArrayList<>();
            // bundle 객체는 하나의 공원을 읽어올 때마다 생성하고 끝날 때 ArrayList 에 담는다
            Bundle bundle = null;
            String tag;
            int count = 0;

            try {
                URL url= new URL(API_url);
                InputStream is= url.openStream();

                XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
                XmlPullParser xpp= factory.newPullParser();
                xpp.setInput( new InputStreamReader(is, "UTF-8") );

                xpp.next();
                int eventType= xpp.getEventType();

                // XML 종료 시까지 반복
                while( eventType != XmlPullParser.END_DOCUMENT ){
                    switch( eventType ){
                        case XmlPullParser.START_DOCUMENT:
                            break;

                        case XmlPullParser.START_TAG:
                            tag= xpp.getName();

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
                                mDataSet_ArrList.add(bundle);
                            }
                            break;

                        case XmlPullParser.TEXT:
                            break;

                        case XmlPullParser.END_TAG:
                            count++;
                            break;
                    }
                    Log.e(TAG, count + "번째 XML Parsing END");
                    eventType= xpp.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 파싱 끝나 while문 빠져나왔음을 표시.
            error_count = 100;

            Log.e(TAG, "XML Parsing Completed");

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            rvProcessing("서울시 전체");
        }
    }


    @Override
    public void onBackPressed() {
        mBackPressHandler.onBackPressed();
    }

    // 1.5초 이내에 다시 한 번 누르면 종료.
    public class BackPressHandler{
        private long time = 0;
        private Activity activity;

        public BackPressHandler(Activity activity) {
            this.activity = activity;
        }

        public void onBackPressed(){
            if(System.currentTimeMillis() > time + 1500) {
                time = System.currentTimeMillis();
                Toast.makeText(getApplicationContext(), "뒤로가기 버튼을 한번 더 누르시면 앱이 종료됩니다.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if(System.currentTimeMillis() <= time + 1500){
                activity.finish();
            }
        }
    }
}
