package com.example.walkinginseoul;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLEncoder;
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

    private static final String API_KEY = "436474424972657734325a66656f76";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        tv = findViewById(R.id.tv);
        btnGo = findViewById(R.id.btngo);
        spinner = findViewById(R.id.spinner);

        spinnerSetting();

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        data = getXMLdata();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(data);
                            }
                        });
                    }
                }).start();

            }
        });

        rvProcessing();
    }

    private void rvProcessing() {
        parkVO = new ParkVO();

        parkVO.setAddress("서울시");
        parkVO.setTitle("동빈");
        parkVO.setImg(R.mipmap.kakao);

        arrparkVO = new ArrayList<ParkVO>();
        arrparkVO.add(parkVO);



        rv_park = (RecyclerView) findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(mContext, 2);
        rv_park.setLayoutManager(layoutManager);
        mParkAdapter = new ParkAdapter(mContext, arrparkVO);
        mParkAdapter.setOnItemClickListener(this);
        rv_park.setAdapter(mParkAdapter);
    }

    @Override
    public void onItemClick(View view, ParkVO parkVO) {
        Toast.makeText(this, "오홍홍",  Toast.LENGTH_SHORT).show();
    }

    private void spinnerSetting() {
        arrayList = new ArrayList<String>();

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
                Toast.makeText(MainActivity.this, position + "  번째 선택!\n" + arrayList.get(position), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private String getXMLdata() {
        StringBuffer buffer = new StringBuffer();


        String queryUrl="http://openapi.seoul.go.kr:8088/4e6d464a42726577383861756e7759/xml/SearchParkInfoService/1/10/";

        try {
            URL url= new URL(queryUrl);//문자열로 된 요청 url을 URL 객체로 생성.
            InputStream is= url.openStream(); //url위치로 입력스트림 연결

            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            XmlPullParser xpp= factory.newPullParser();
            xpp.setInput( new InputStreamReader(is, "UTF-8") ); //inputstream 으로부터 xml 입력받기

            String tag;

            xpp.next();
            int eventType= xpp.getEventType();

            while( eventType != XmlPullParser.END_DOCUMENT ){
                switch( eventType ){
                    case XmlPullParser.START_DOCUMENT:
                        buffer.append("파싱 시작...\n\n");
                        break;


                    case XmlPullParser.START_TAG:
                        tag= xpp.getName();//태그 이름 얻어오기

                        if(tag.equals("row")) ;// 첫번째 검색결과
                        else if(tag.equals("P_IDX")){
                            buffer.append("공원 번호 : ");
                            xpp.next();
                            buffer.append(xpp.getText());//addr 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("\n"); //줄바꿈 문자 추가
                        }
                        else if(tag.equals("P_PARK")){
                            buffer.append("공원명 : ");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }
                        else if(tag.equals("P_LIST_CONTENT")){
                            buffer.append("공원 설명 :");
                            xpp.next();
                            buffer.append(xpp.getText());//cpId
                            buffer.append("\n");
                        }
                        else if(tag.equals("AREA")){
                            buffer.append("공원 크기 :");
                            xpp.next();
                            buffer.append(xpp.getText());//cpNm
                            buffer.append("\n");
                        }
                        else if(tag.equals("OPEN_DT")){
                            buffer.append("개장일 :");
                            xpp.next();
                            buffer.append(xpp.getText());//
                            buffer.append("\n");
                        }
                        else if(tag.equals("MAIN_EQUIP")){
                            buffer.append("주요 시설 :");
                            xpp.next();
                            buffer.append(xpp.getText());//
                            buffer.append("  ,  ");
                        }
                        else if(tag.equals("MAIN_PLANTS")){
                            buffer.append("주요 식물 :");
                            xpp.next();
                            buffer.append(xpp.getText());//csId
                            buffer.append("\n");
                        }
                        else if(tag.equals("GUIDANCE")){
                            buffer.append("공원 상세 이미지 :");
                            xpp.next();
                            buffer.append(xpp.getText());
                            buffer.append("\n");
                        }
                        else if(tag.equals("VISIT_ROAD")){
                            buffer.append("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                            buffer.append("오시는 길 :");
                            xpp.next();
                            buffer.append(xpp.getText());//
                            buffer.append("\n");
                            buffer.append("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                        }
                        else if(tag.equals("P_IMG")){
                            buffer.append("공원 전망도 :");
                            xpp.next();
                            buffer.append(xpp.getText());//
                            buffer.append("\n");
                        }
                        else if(tag.equals("P_ZONE")){
                            buffer.append("포함 자치구 :");
                            xpp.next();
                            buffer.append(xpp.getText());//
                            buffer.append("\n");
                        }
                        else if(tag.equals("P_ADDR")){
                            buffer.append("공원 주소 :");
                            xpp.next();
                            buffer.append(xpp.getText());//
                            buffer.append("\n");
                        }
                        else if(tag.equals("P_ADMINTEL")){
                            buffer.append("공원 전화번호 :");
                            xpp.next();
                            buffer.append(xpp.getText());//
                            buffer.append("\n");
                        }
                        else if(tag.equals("LONGITUDE")){
                            buffer.append("경도 :");
                            xpp.next();
                            buffer.append(xpp.getText());//
                            buffer.append("\n");
                        }
                        else if(tag.equals("LATITUDE")){
                            buffer.append("위도 :");
                            xpp.next();
                            buffer.append(xpp.getText());//
                            buffer.append("\n");
                        }
                        else if(tag.equals("TEMPLATE_URL")){
                            buffer.append("홈페이지 URL :");
                            xpp.next();
                            buffer.append(xpp.getText());//
                            buffer.append("\n");
                            buffer.append("------------------------------------------------");
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
            }

        } catch (Exception e) {
            // TODO Auto-generated catch blocke.printStackTrace();
        }

        buffer.append("파싱 끝\n");

        return buffer.toString();//StringBuffer 문자열 객체 반환
    }




    public class MyAsyncTask extends AsyncTask<Void, String, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
