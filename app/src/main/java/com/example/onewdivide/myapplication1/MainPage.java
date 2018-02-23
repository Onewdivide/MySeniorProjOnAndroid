package com.example.onewdivide.myapplication1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Locale;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;

public class MainPage extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private GestureDetector gestureDetector;
    private TextToSpeech tts;
    private LinearLayout test123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        tts = new TextToSpeech(this, this);
        tts.setLanguage(new Locale("th"));

        Button toMapPage = (Button)findViewById(R.id.toMapPage);
        Button toNavigationPage = (Button) findViewById(R.id.toNavigationPage);
        toMapPage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent it = new Intent(getApplicationContext(),ExplorerMap.class);
                startActivity(it);
                finish();
            }
        });

        toNavigationPage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent it = new Intent(getApplicationContext(),selectStartLocationActivity.class);
                startActivity(it);
                finish();
            }
        });

        Button testLocation = (Button) findViewById(R.id.testLocation);
        testLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                TextView RequestTimeStamp = (TextView) findViewById(R.id.RewuestTimeStamp);
                RequestTimeStamp.setText(String.valueOf(timestamp.getTime()));
                new FeedJSONTask().execute("");
            }
        });

//        };
//        test123 = (LinearLayout)findViewById(R.id.testtest);
//        test123.setOnTouchListener(new OnSwipe(this){
//            public void onSwipeTop() {
//                tts.speak("ปัดขวาเพื่อเข้าสู่หน้าสำรวจแผนที่อาคาร หรือ ปัดซ้ายเพื่อเข้าสู่หน้านำทาง",TextToSpeech.QUEUE_FLUSH,null);
//            }
//            public void onSwipeRight() {
//                Intent it = new Intent(getApplicationContext(),ExplorerMap.class);
//                startActivity(it);
//                finish();
//            }
//            public void onSwipeLeft() {
//                Intent it = new Intent(getApplicationContext(),NavigationActivity.class);
//                startActivity(it);
//                finish();
//            }
//            public void onSwipeBottom() {
//                tts.speak("หน้านี้คือหน้าหลัก", TextToSpeech.QUEUE_FLUSH,null);
//                Log.d("debug","-> MainPage test SwipeBottom");
//            }
//        });
    }

    public class FeedJSONTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... strings) {
            String result = FeedJSON();
            Gson gson = new Gson();
//            Type collectionType = new TypeToken<Collection<CMXResponse>>() {}.getType();
//            Collection<CMXResponse> enums = gson.fromJson(result, collectionType);
//            CMXResponse[] CMXresult = enums.toArray(new CMXResponse[enums.size()]);

            CurrentLocationResponse[] currentLocationResponse = gson.fromJson(result, CurrentLocationResponse[].class);
//            String[] currentResponseLast = {currentLocationResponse.getMapCoordinate().getX()};
//            String test = result.get()
            double locateX = 0;
            double locateY = 0;
            String currentServerTime = "";
            String firstLocateTime = "";
            String lastLocateTime = "";
            locateX = currentLocationResponse[0].getMapCoordinate().getX();
            locateY = currentLocationResponse[0].getMapCoordinate().getY();
            currentServerTime = currentLocationResponse[0].getStatistics().getCurrentServerTime();
            firstLocateTime = currentLocationResponse[0].getStatistics().getFirstLocatedTime();
            lastLocateTime = currentLocationResponse[0].getStatistics().getLastLocatedTime();
//            double CoXX = ((372*locateX)/345)+4;
//            double CoYY= ((268*locateY)/243)+124;
            int CoX = (int)locateX;
            int CoY = (int)locateY;
            String[] res = {Integer.toString(CoX),Integer.toString(CoY),currentServerTime,firstLocateTime,lastLocateTime};


            return res;
        }

        @Override
        protected void onPostExecute(String[] s) {

            super.onPostExecute(s);
            TextView CurrentX = (TextView) findViewById(R.id.CurrentX);
            TextView CurrentY = (TextView) findViewById(R.id.CurrentY);
            TextView ServerTime = (TextView) findViewById(R.id.ServerTime);
            TextView FirstLocatedTime = (TextView) findViewById(R.id.FirstLocateTime);
            TextView LastLocatedTime = (TextView) findViewById(R.id.LastLocateTime);
            TextView SuccessTimeStamp = (TextView) findViewById(R.id.SuccessTimeStamp);

            CurrentX.setText(s[0]);
            CurrentY.setText(s[1]);
            ServerTime.setText(s[2]);
            FirstLocatedTime.setText(s[3]);
            LastLocatedTime.setText(s[4]);
            Timestamp timestamplast = new Timestamp(System.currentTimeMillis());
            SuccessTimeStamp.setText(String.valueOf(timestamplast.getTime()));

        }
    }

    private String FeedJSON(){

        try {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

            OkHttpClient client = SelfSigningClientBuilder.createClient()
                    .authenticator(new Authenticator() {
                        @Nullable
                        @Override
                        public Request authenticate(Route route, Response response) throws IOException {
                            String credential = Credentials.basic("dev", "dev12345");
                            return response.request().newBuilder().header("Authorization", credential).build();
                        }
                    }).addInterceptor(logging).build();
//            https://10.34.250.12/api/location/v1/history/clients/78%3A4f%3A43%3A8a%3Adb%3Aab?date=2017%2F09%2F19
//            https://10.34.250.12/api/location/v2/clients?macAddress=0a:4f:83:17:19:7b
            Request request = new Request.Builder().url("https://10.34.250.12/api/location/v2/clients?macAddress=60:83:34:6D:11:8D")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }catch (Exception e){

        }
        return null;
    }

    @Override
    public void onInit(int status) {

    }
}
