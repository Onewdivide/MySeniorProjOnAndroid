package com.example.onewdivide.myapplication1;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.util.Locale;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;

public class NavigationActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private GestureDetector gestureDetector;
    private TextToSpeech tts;
    private LinearLayout test123;
    private String URL = "https://10.34.250.12/api/config/v1/maps/imagesource/domain_0_1500368087062.jpg";
    private ImageView imgView;
    private int CoX,CoY,wantX,wantY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Bitmap bmp = null;

        imgView =(ImageView)findViewById(R.id.imageView);
        String imageUrl = "10.34.250.12/api/config/v1/maps/imagesource/domain_0_1500368087062.jpg";
//        URL url = new URL(imageUrl);
//        HttpURLConnection connection = (HttpURLConnection)
//        imgView.setImageBitmap(helper.getHTTPData(imageUrl));
//        imgView.setImageBitmap(GetBitmapfromUrl("https://httpbin.org/image/png"));

//        imgView.setTag("10.34.250.12/api/config/v1/maps/imagesource/domain_0_1500368087062.jpg");
//        imgView.setTag("https://httpbin.org/image/png");
//        imgView.setWebViewClient(new CustomWebViewClient());
//        imgView.loadUrl(imageUrl);
//        new DownloadImagesTask().execute(imgView);
        NetworkTask network = new NetworkTask();
        network.execute("");


        Bitmap bitMap = Bitmap.createBitmap(380 , 516, Bitmap.Config.ARGB_8888);  //creates bmp
        bitMap = bitMap.copy(bitMap.getConfig(), true);     //lets bmp to be mutable
        Canvas canvas = new Canvas(bitMap);                 //draw a canvas in defined bmp

        Paint paint = new Paint();                          //define paint and paint color
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        //paint.setStrokeWidth(0.5f);
        paint.setAntiAlias(true);                           //smooth edges


        ImageView imgView2 = (ImageView) findViewById(R.id.imageView2);
        imgView2.bringToFront();
        imgView2.setImageBitmap(bitMap);
        TextView text1 = (TextView) findViewById(R.id.textView6);
        text1.setText(" MAC 60:83:34:6D:11:8D ");
        //changed set image resource to set image background resource
//        imViewAndroid.setBackgroundResource(R.drawable.map);

        //       true start(223,83) +20,+130 OK!!
//       next (204,168) => (224,298) NOT OK!!
//        at top left in mobile screen (4,124)
//        at bottom right in mobile screen (376,392)
//        mobile use (376-4,392-124) => (372,268)
//        at top left in real map (0,0)
//        at bottom right in real map (345,243)
//        so we want (95,70) => calculate : CoX/372 = 95/345 so CoX = (372*95)/345 = 102.43
//        CoY/268 = 70/243 so CoY = (268*70)/243 = 77.2
//        and!! plus (4,124) in to CoX and CoY
//        So real CoX = 102+4 = 106, CoY = 77+124 = 201
        wantX = 188;
        wantY = 125;
        CoX = ((372*wantX)/345)+4;
        CoY = ((268*wantY)/243)+124;
        canvas.drawCircle(CoX, CoY, 2, paint);
        //invalidate to update bitmap in imageview
        imgView2.invalidate();


        tts = new TextToSpeech(this, this);
        tts.setLanguage(new Locale("th"));
        test123 = (LinearLayout)findViewById(R.id.testtest);
        test123.setOnTouchListener(new OnSwipe(this){
            public void onSwipeTop() {
                tts.speak("ปัดขวาเพื่อเข้าสู่หน้าหลัก",TextToSpeech.QUEUE_FLUSH,null);
            }
            public void onSwipeRight() {
                Intent it = new Intent(getApplicationContext(),MainPage.class);
                startActivity(it);
                finish();
            }
            public void onSwipeLeft() {

            }
            public void onSwipeBottom() {
                tts.speak("หน้านี้คือหน้านำทาง", TextToSpeech.QUEUE_FLUSH,null);
            }
        });
    }

    @Override
    public void onInit(int status) {

    }

    class NetworkTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... voids) {
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

            Request request = new Request.Builder().url(URL)
                    .build();

            Bitmap bitmap = null;
            try {
                Response response = client.newCall(request).execute();
                InputStream inputStream = response.body().byteStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                imgView.setImageBitmap(bitmap);
            }
        }
    }


}
