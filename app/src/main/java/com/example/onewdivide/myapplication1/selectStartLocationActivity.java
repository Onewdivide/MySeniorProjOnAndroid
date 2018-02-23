package com.example.onewdivide.myapplication1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;

import static java.lang.Integer.parseInt;

public class selectStartLocationActivity extends AppCompatActivity {

    public int loopcount ;
    public TextView waitPlease;
    public int getLocationX,getLocaitonY;
    public List<Integer> allGetLocaitonX = new ArrayList<>();
    public List<Integer> allGetLocationY = new ArrayList<>();
    public int mostRepeatlyIntegerX,mostRepeatlyIntegerY;
    public int repeatlyMostCount = 0;
    public int repeatlyCheckCount = 0;
    final public Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_start_location);

        waitPlease = (TextView) findViewById(R.id.pleaseWait);

        MyTTS.getInstance(selectStartLocationActivity.this).speak(waitPlease.getText().toString());

        runLoopWithDelay();

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

                getLocationX = parseInt(s[0]);
                getLocaitonY = parseInt(s[1]);

                allGetLocaitonX.add(getLocationX);
                allGetLocationY.add(getLocaitonY);


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

    public final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (loopcount<6){
                    new FeedJSONTask().execute("");
                    loopcount+=1;
                    handler.postDelayed(runnable,5000);
                }
                else{
                    for (int i = 0 ; i<allGetLocaitonX.size(); i++){
                        for (int j = 0 ; j<allGetLocaitonX.size(); j++){
                            if (allGetLocaitonX.get(i) == allGetLocaitonX.get(j)
                                    && allGetLocationY.get(i) == allGetLocationY.get(j)){
                                repeatlyCheckCount+=1;
                            }
                        }
                        if (repeatlyCheckCount >= repeatlyMostCount){
                            repeatlyMostCount = repeatlyCheckCount;
                            repeatlyCheckCount = 0;
                            mostRepeatlyIntegerX = allGetLocaitonX.get(i);
                            mostRepeatlyIntegerY = allGetLocationY.get(i);
                        }
                    }

                    Intent it = new Intent(getApplicationContext(),NavigationActivity.class);
                    it.putExtra("startX",mostRepeatlyIntegerX);
                    it.putExtra("startY",mostRepeatlyIntegerY);
                    startActivity(it);
                    finish();

                }

            }

    };

    private void runLoopWithDelay(){
        loopcount = 0;
        runnable.run();
    }

}