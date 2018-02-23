package com.example.onewdivide.myapplication1;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.WheelPicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;

import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;

import static java.lang.Integer.parseInt;
import static java.lang.Math.sqrt;
import static java.lang.Thread.sleep;

public class NavigationActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener {

    private GestureDetector gestureDetector;
    private TextToSpeech tts;
    private LinearLayout test123;
    private String URL = "https://10.34.250.12/api/config/v1/maps/imagesource/domain_0_1500368087062.jpg";
    private String LocationHistoryURL = "https://10.34.250.12/api/location/v1/history/clients/78%3A4f%3A43%3A8a%3Adb%3Aab?date=2017%2F09%2F19";
    private ImageView imgView;
    private ImageView imgView2;
    private int CoX,CoY,wantX,wantY;
    private int[] vrCurrent = {1,2};
    private List<Integer> VirtualCurrentLocationOnX = new ArrayList<>();
    private List<Integer> VirtualCurrentLocationOnY = new ArrayList<>();
    public List<String> resultPath;
    public int checkWord;
    final public Handler handler = new Handler();
    public String distance;
    public TextView currentLocation;
    public TextView currentPath;
    public TextView xyLocation,OnTheWayTest;
    public List<Vertex> path;
    public int loopcount = 1;
    public int tempcheck = 0;
    public double distanceToThisNode;
    public int checkArriveThisNodeYet;
    public ArrayList<String> WordInPath = new ArrayList<>();
    public int APIcallCurrentlocationX, APIcallCurrentLocationY;
    public int checkOnDestinationYet = 0;
    public Vertex startLocation;
    public boolean inLoopFirstTime = true;

    private final Runnable runnable  = new Runnable() {
        @Override
        public void run() {
            if (checkOnDestinationYet == 0 ){


                new FeedJSONTaskCurrentLocation().execute("");
                int[] currentRecall;
                if (inLoopFirstTime){
                    currentRecall = new int[]{parseInt(getIntent().getStringExtra("startX")),
                            parseInt(getIntent().getStringExtra("startY"))};
                    inLoopFirstTime = false;
                }
                else{
                    currentRecall = new int[]{APIcallCurrentlocationX, APIcallCurrentLocationY};
                }
                xyLocation.setText(APIcallCurrentlocationX+","+APIcallCurrentLocationY);
//                for (int test = 0; test<WordInPath.size();test++){
//                    Log.e("wordInPath",WordInPath.get(test));
//                }
//                int[] currentRecall = {VirtualCurrentLocationOnX.get(loopcount),VirtualCurrentLocationOnY.get(loopcount)};
//                Log.e("Show Case","Show VirtualX : "+VirtualCurrentLocationOnX.get(loopcount).toString()
//                        + "Show VirtualY : "+VirtualCurrentLocationOnY.get(loopcount).toString());
//                Log.e("Current Recall : ", String.valueOf(currentRecall[0]+" , "+ currentRecall[1]));
//                Log.e("checkArrive : ", String.valueOf(checkArriveThisNodeYet));
//                Log.e("Current Arrive Node : ",String.valueOf(path.get(checkArriveThisNodeYet).location[0]
//                        +" , "+ path.get(checkArriveThisNodeYet).location[1]));


//                Log.e("test",String.valueOf(checkNextLocation));
//                Log.e("virtualPathTestX", String.valueOf(currentRecall[0]));
//                Log.e("virtualPathTestY",String.valueOf(currentRecall[1]));
//                if (currentRecall[0] == path.get(checkArriveThisNodeYet).location[0]
//                        && currentRecall[1] == path.get(checkArriveThisNodeYet).location[1]){
                if (path.get(checkArriveThisNodeYet).imInThisAreaRight(currentRecall[0],currentRecall[1])){
                    // on this condition fix it to < xMax, < yMax and > xMin, > yMin
                    //if possible fix this condition to use imInThisAreaRight function
                    loopcount +=1 ;
                    currentLocation.setText("On point!");
//                    currentPath.setText(WordInPath.get(checkArriveThisNodeYet));
                    Log.e("OnPoint!", "");

                    Log.e("Debug >>" ,"This is checkArriveThisNodeYet : "+checkArriveThisNodeYet
                            +" and this is WordInpath.size : "+WordInPath.size() + "This is loopcount : "+loopcount);

                    if (checkArriveThisNodeYet < WordInPath.size()){
                        currentPath.setText(WordInPath.get(checkArriveThisNodeYet));

                            checkArriveThisNodeYet+=1;

                    }
                    else{
                        checkOnDestinationYet = 1;
                        currentPath.setText("ถึงจุดหมายเรียบร้อยแล้ว");
                    }
//                    checkArriveThisNodeYet+=1;

                    MyTTS.getInstance(NavigationActivity.this).speak(currentPath.getText().toString());

                    handler.postDelayed(runnable,4000L);
                }
                else{
                    OnTheWayTest.setText(path.get(checkArriveThisNodeYet).toString());
                    int x = currentRecall[0] - path.get(checkArriveThisNodeYet).location[0];
                    int y = currentRecall[1] - path.get(checkArriveThisNodeYet).location[1];
                    loopcount +=1  ;
//                    Log.e("Rx : " , String.valueOf(currentRecall[0]));
//                    Log.e("Ry : " , String.valueOf(currentRecall[1]));
//                    Log.e("Px : " , String.valueOf(path.get(checkArriveThisNodeYet-1).location[0]));
//                    Log.e("Py : " , String.valueOf(path.get(checkArriveThisNodeYet-1).location[1]));
//                    Log.e("X : " , String.valueOf(x));
//                    Log.e("Y : " , String.valueOf(y));
                    distanceToThisNode = (sqrt(x*x+y*y));
//                    Log.e("distance2thisBF : " , String.valueOf(distanceToThisNode));
                    distanceToThisNode = distanceToThisNode*(0.18);
                    if (distanceToThisNode < 1){
                        distanceToThisNode = 1;
                    }
//                    Log.e("distance2thisAT : " , String.valueOf(distanceToThisNode));
                    distance = String.format("%.2f",distanceToThisNode);
                    //dont forget to change to integer
//                                int tt = parseInt(distance);
//                    Log.e("this is distance : ",String.valueOf(distance));
                    String wordDistance = "เหลืออีก"+ distance + "เมตร ก่อนจะถึงจุดต่อไป";

//                    if(loopcount == VirtualCurrentLocationOnX.size()-1){
//                        checkOnDestinationYet = 1;
//                        wordDistance = "ถึงจุดหมายเรียบร้อยแล้ว";
//                    }

                    currentLocation.setText("Continue...");
                    currentPath.setText(wordDistance);
//                    Log.e("Continue...", wordDistance);
                    Log.e("Debug >>" ,"This is checkArriveThisNodeYet : "+checkArriveThisNodeYet
                            +" and this is WordInpath.size : "+WordInPath.size() + "This is loopcount : "+loopcount);
                    MyTTS.getInstance(NavigationActivity.this).speak(currentPath.getText().toString());

                    handler.postDelayed(runnable,4000L);
                }
            }
        }
    };

    private void runLoopWithDelay(){
        loopcount = 0;
        checkArriveThisNodeYet = 0;
        runnable.run();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Button btnSubmit = (Button) findViewById(R.id.btn_submit);
//        final TextView currentLocation = (TextView) findViewById(R.id.textView5);
//        final TextView currentPath = (TextView) findViewById(R.id.textView7);
        currentLocation = (TextView) findViewById(R.id.textView5);
        currentPath = (TextView) findViewById(R.id.textView7);
        OnTheWayTest = (TextView) findViewById(R.id.OnTheWayTest);
        xyLocation = (TextView) findViewById(R.id.xyLocation);
        btnSubmit.setOnClickListener(this);

        //Add VirtualCurrentLocation
//        VirtualCurrentLocationOnX.add(230);
//        VirtualCurrentLocationOnY.add(83);
//
//        VirtualCurrentLocationOnX.add(232);
//        VirtualCurrentLocationOnY.add(82);
//
//        VirtualCurrentLocationOnX.add(234);
//        VirtualCurrentLocationOnY.add(82);
//
//        VirtualCurrentLocationOnX.add(234);
//        VirtualCurrentLocationOnY.add(78);
//
//        VirtualCurrentLocationOnX.add(234);
//        VirtualCurrentLocationOnY.add(75);
//
//        VirtualCurrentLocationOnX.add(234);
//        VirtualCurrentLocationOnY.add(72);
//
//        VirtualCurrentLocationOnX.add(234);
//        VirtualCurrentLocationOnY.add(70);
//
//        VirtualCurrentLocationOnX.add(234);
//        VirtualCurrentLocationOnY.add(67);
//
//        VirtualCurrentLocationOnX.add(234);
//        VirtualCurrentLocationOnY.add(63);
//
//        VirtualCurrentLocationOnX.add(234);
//        VirtualCurrentLocationOnY.add(59);
//
//        VirtualCurrentLocationOnX.add(234);
//        VirtualCurrentLocationOnY.add(57);
//
//        VirtualCurrentLocationOnX.add(234);
//        VirtualCurrentLocationOnY.add(53);

//

//        VirtualCurrentLocationOnX.add(230);
//        VirtualCurrentLocationOnY.add(83);
//
//        VirtualCurrentLocationOnX.add(232);
//        VirtualCurrentLocationOnY.add(82);
//
//        VirtualCurrentLocationOnX.add(234);
//        VirtualCurrentLocationOnY.add(82);
//
//        VirtualCurrentLocationOnX.add(234);
//        VirtualCurrentLocationOnY.add(78);
//
//        VirtualCurrentLocationOnX.add(234);
//        VirtualCurrentLocationOnY.add(75);
//
//        VirtualCurrentLocationOnX.add(250);
//        VirtualCurrentLocationOnY.add(75);
//
//        VirtualCurrentLocationOnX.add(262);
//        VirtualCurrentLocationOnY.add(75);
//
//        VirtualCurrentLocationOnX.add(270);
//        VirtualCurrentLocationOnY.add(75);
//
//        VirtualCurrentLocationOnX.add(270);
//        VirtualCurrentLocationOnY.add(76);

        //Vertex at each place
        Vertex Entrance1 = new Vertex("Entrance1");
        Vertex Ladder1 = new Vertex("Ladder1");
        Vertex Toilet1Man = new Vertex("Toilet1Man");
        Vertex Toilet1Woman = new Vertex("Toilet1Woman");
        Vertex Library = new Vertex("Library");
        Vertex DSSRoom = new Vertex("DDSRoom");
        Vertex ATRoom = new Vertex("ATRoom");
        Vertex Entrance2 = new Vertex("Entrance2");
        Vertex PublicRelation = new Vertex("PublicRelation");
        Vertex Room102 = new Vertex("Room102");
        Vertex Ladder2 = new Vertex("Ladder2");
        Vertex Lift = new Vertex("Lift");
        Vertex Room104 = new Vertex("Room104");
        Vertex Room105 = new Vertex("Room105");
        Vertex KKRoom = new Vertex("KKRoom");
        Vertex Room107 = new Vertex("Room107");
        Vertex Room108 = new Vertex("Room108");
        Vertex Room110 = new Vertex("Room110");
        Vertex Toilet2Man = new Vertex("Toilet2Man");
        Vertex Toilet2Woman = new Vertex("Toilet2Woman");
        Vertex Ladder3 = new Vertex("Ladder3");
        Vertex CopyStore = new Vertex("CopyStore");
        Vertex Room115 = new Vertex("Room115");
        Vertex Room116 = new Vertex("Room116");
        Vertex Room118 = new Vertex("Room118");





        //Vertex at each node
        Vertex Node2 = new Vertex("Node2");
        Vertex Node3 = new Vertex("Node3");
        Vertex Node4 = new Vertex("Node4");
        Vertex Node5 = new Vertex("Node5");
        Vertex Node6 = new Vertex("Node6");
        Vertex Node8 = new Vertex("Node8");
        Vertex Node9 = new Vertex("Node9");
        Vertex Node10 = new Vertex("Node10");
        Vertex Node11 = new Vertex("Node11");
        Vertex Node12 = new Vertex("Node12");
        Vertex Node13 = new Vertex("Node13");
        Vertex Node14 = new Vertex("Node14");
        Vertex Node15 = new Vertex("Node15");
        Vertex Node155 = new Vertex("Node155");
        Vertex Node16 = new Vertex("Node16");
//        Vertex Node17 = new Vertex("Node17");
        Vertex Node18 = new Vertex("Node18");
        Vertex Node19 = new Vertex("Node19");
        Vertex Node20 = new Vertex("Node20");
        Vertex Node205 = new Vertex("Node205");
        Vertex Node21 = new Vertex("Node21");
        Vertex Node22 = new Vertex("Node22");
        Vertex Node23 = new Vertex("Node23");
        Vertex Node24 = new Vertex("Node24");
        Vertex Node25 = new Vertex("Node25");

        //input location
        Entrance1.inputLocation(new int[]{234, 53});
        Ladder1.inputLocation(new int[]{239,70});
        Toilet1Man.inputLocation(new int[]{278,77});
        Toilet1Woman.inputLocation(new int[]{272,71});
        Library.inputLocation(new int[]{230,83});
        DSSRoom.inputLocation(new int[]{240,103});
        ATRoom.inputLocation(new int[]{262,158});
        Entrance2.inputLocation(new int[]{281,171});
        PublicRelation.inputLocation(new int[]{273,183});
        Room102.inputLocation(new int[]{251,184});
        Ladder2.inputLocation(new int[]{227,184});
        Lift.inputLocation(new int[]{220,175});
        Room104.inputLocation(new int[]{203,175});
        Room105.inputLocation(new int[]{188,168});
        KKRoom.inputLocation(new int[]{180,194});
        Room107.inputLocation(new int[]{153,168});
        Room108.inputLocation(new int[]{127,168});
        Room110.inputLocation(new int[]{122,168});
        Toilet2Man.inputLocation(new int[]{95,202});
        Toilet2Woman.inputLocation(new int[]{89,200});
        Ladder3.inputLocation(new int[]{88,168});
        CopyStore.inputLocation(new int[]{78,163});
        Room115.inputLocation(new int[]{114,134});
        Room116.inputLocation(new int[]{114,129});
        Room118.inputLocation(new int[]{120,106});
        Node2.inputLocation(new int[]{234,70});
        Node3.inputLocation(new int[]{234,75});
        Node4.inputLocation(new int[]{262,75});
        Node5.inputLocation(new int[]{234,82});
        Node6.inputLocation(new int[]{234,103});
        Node8.inputLocation(new int[]{234,163});
        Node9.inputLocation(new int[]{234,170});
        Node10.inputLocation(new int[]{234,176});
        Node11.inputLocation(new int[]{252,170});
        Node12.inputLocation(new int[]{262,170});
        Node13.inputLocation(new int[]{273,170});
        Node14.inputLocation(new int[]{219,176});
        Node15.inputLocation(new int[]{188,163});
        Node155.inputLocation(new int[]{205,163});
        Node16.inputLocation(new int[]{180,163});
//        int[] Node17test = {180,181};
        Node18.inputLocation(new int[]{153,163});
        Node19.inputLocation(new int[]{127,163});
        Node20.inputLocation(new int[]{121,163});
        Node205.inputLocation(new int[]{118,163});
        Node21.inputLocation(new int[]{118,134});
        Node22.inputLocation(new int[]{118,128});
        Node23.inputLocation(new int[]{93,163});
        Node24.inputLocation(new int[]{88,163});
        Node25.inputLocation(new int[]{93,190});


        //this code will port point location system to area location system
        Entrance1.inputAreaLocation(229,52,238,66);
        Ladder1.inputAreaLocation(234,67,238,70);
        Toilet1Man.inputAreaLocation(238,75,273,78);
        Toilet1Woman.inputAreaLocation(260,70,263,74);
        Library.inputAreaLocation(229,80,234,85);
        DSSRoom.inputAreaLocation(234,100,239,105);
        ATRoom.inputAreaLocation(259,158,264,165);
        Entrance2.inputAreaLocation(275,165,282,176);
        PublicRelation.inputAreaLocation(271,176,275,183);
        Room102.inputAreaLocation(249,176,255,183);
        Ladder2.inputAreaLocation(225,180,229,183);
        Lift.inputAreaLocation(219,174,223,177);
        Room104.inputAreaLocation(202,167,205,174);
        Room105.inputAreaLocation(184,162,190,167);
        KKRoom.inputAreaLocation(177,180,182,194);
        Room107.inputAreaLocation(150,162,156,167);
        Room108.inputAreaLocation(124,162,129,167);
        Room110.inputAreaLocation(118,162,124,167);
        Toilet2Man.inputAreaLocation(89,197,97,201);
        Toilet2Woman.inputAreaLocation(89,189,93,192);
        Ladder3.inputAreaLocation(86,162,89,167);
        CopyStore.inputAreaLocation(71,157,86,167);
        Room115.inputAreaLocation(114,132,119,137);
        Room116.inputAreaLocation(114,126,119,131);
        Room118.inputAreaLocation(114,106,124,111);

        Node2.inputAreaLocation(229,67,238,70);
        Node3.inputAreaLocation(229,70,260,78);
        Node4.inputAreaLocation(260,70,273,78);
        Node5.inputAreaLocation(229,80,238,85);
        Node6.inputAreaLocation(229,100,238,105);
        Node8.inputAreaLocation(229,158,238,165);
        Node9.inputAreaLocation(229,165,249,176);
        Node10.inputAreaLocation(226,176,238,183);
        Node11.inputAreaLocation(249,165,259,176);
        Node12.inputAreaLocation(259,165,271,176);
        Node13.inputAreaLocation(271,165,275,176);
        Node14.inputAreaLocation(219,176,229,183);
        Node15.inputAreaLocation(184,157,202,167);
        Node155.inputAreaLocation(202,157,229,165);
        Node16.inputAreaLocation(177,157,182,180);
        Node18.inputAreaLocation(150,157,177,167);
        Node19.inputAreaLocation(124,157,150,167);
        Node20.inputAreaLocation(118,157,123,167);
        Node205.inputAreaLocation(114,137,123,167);
        Node21.inputAreaLocation(114,132,124,137);
        Node22.inputAreaLocation(114,111,124,132);
        Node23.inputAreaLocation(89,157,114,167);
        Node24.inputAreaLocation(86,157,89,167);
        Node25.inputAreaLocation(89,167,98,198);



        //this is for CurrentLocation [0] is X, [1] is Y
        int[] Current = {234,52};
        //put x and y of each point in this list
        int[] Entrance1test = {234,53};
        int[] Ladder1test = {239,70};
        int[] Toilet1Mantest = {278,77};
        int[] Toilet1Womantest = {272,71};
        int[] Librarytest = {230,83};
        int[] DSSRoomtest = {240,103};
        int[] ATRoomtest = {262,158};
        int[] Entrance2test = {281,171};
        int[] PublicRelationtest = {273,183};
        int[] Room102test = {251,184};
        int[] Ladder2test = {227,184};
        int[] Lifttest = {220,175};
        int[] Room104test = {203,175};
        int[] Room105test = {188,168};
        int[] KKRoomtest = {180,194};
        int[] Room107test = {153,168};
        int[] Room108test = {127,168};
        int[] Room110test = {122,168};
        int[] Toilet2Mantest = {95,202};
        int[] Toilet2Womantest = {89,200};
        int[] Ladder3test = {88,168};
        int[] CopyStoretest = {78,163};
        int[] Room115test = {114,134};
        int[] Room116test = {114,129};
        int[] Room118test = {120,106};
        int[] Node2test = {234,70};
        int[] Node3test = {234,75};
        int[] Node4test = {262,75};
        int[] Node5test = {234,82};
        int[] Node6test = {234,103};
        int[] Node8test = {234,163};
        int[] Node9test = {234,170};
        int[] Node10test = {234,176};
        int[] Node11test = {252,170};
        int[] Node12test = {262,170};
        int[] Node13test = {273,170};
        int[] Node14test = {219,176};
        int[] Node15test = {188,163};
        int[] Node155test = {205,163};
        int[] Node16test = {180,163};
        int[] Node17test = {180,181};
        int[] Node18test = {153,163};
        int[] Node19test = {127,163};
        int[] Node20test = {121,163};
        int[] Node205test = {118,163};
        int[] Node21test = {118,134};
        int[] Node22test = {118,128};
        int[] Node23test = {93,163};
        int[] Node24test = {88,163};
        int[] Node25test = {93,190};

        List<int[]> EachXandY = new ArrayList<>();
        EachXandY.add(Entrance1test);
        EachXandY.add(Ladder1test);
        EachXandY.add(Toilet1Mantest);
        EachXandY.add(Toilet1Womantest);
        EachXandY.add(Librarytest);
        EachXandY.add(DSSRoomtest);
        EachXandY.add(ATRoomtest);
        EachXandY.add(Entrance2test);
        EachXandY.add(PublicRelationtest);
        EachXandY.add(Room102test);
        EachXandY.add(Ladder2test);
        EachXandY.add(Lifttest);
        EachXandY.add(Room104test);
        EachXandY.add(Room105test);
        EachXandY.add(KKRoomtest);
        EachXandY.add(Room107test);
        EachXandY.add(Room108test);
        EachXandY.add(Room110test);
        EachXandY.add(Toilet2Mantest);
        EachXandY.add(Toilet2Womantest);
        EachXandY.add(Ladder3test);
        EachXandY.add(CopyStoretest);
        EachXandY.add(Room115test);
        EachXandY.add(Room116test);
        EachXandY.add(Room118test);
        EachXandY.add(Node2test);
        EachXandY.add(Node3test);
        EachXandY.add(Node4test);
        EachXandY.add(Node5test);
        EachXandY.add(Node6test);
        EachXandY.add(Node8test);
        EachXandY.add(Node9test);
        EachXandY.add(Node10test);
        EachXandY.add(Node11test);
        EachXandY.add(Node12test);
        EachXandY.add(Node13test);
        EachXandY.add(Node14test);
        EachXandY.add(Node15test);
        EachXandY.add(Node155test);
        EachXandY.add(Node16test);
        EachXandY.add(Node17test);
        EachXandY.add(Node18test);
        EachXandY.add(Node19test);
        EachXandY.add(Node20test);
        EachXandY.add(Node205test);
        EachXandY.add(Node21test);
        EachXandY.add(Node22test);
        EachXandY.add(Node23test);
        EachXandY.add(Node24test);
        EachXandY.add(Node25test);

        int checkInEachXandY = 0;
        double checkLastPithagorus = 100000;
//        new FeedJSONTaskCurrentLocation().execute("");

//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
////                new FeedJSONTaskCurrentLocation().execute("");
//            }
//        },3000);

        int x ;
        int y ;
        Log.e("callcurrentAtStart:", getIntent().getStringExtra("startX"));
        Log.e("callcurrentAtStart:", getIntent().getStringExtra("startX"));
        xyLocation.setText(getIntent().getStringExtra("startX")+","+getIntent().getStringExtra("startY"));
        double Pithagorus;
        for(int i = 0 ; i<EachXandY.size(); i++){
            x =  parseInt(getIntent().getStringExtra("startX")) - EachXandY.get(i)[0];
            y =  parseInt(getIntent().getStringExtra("startY")) - EachXandY.get(i)[1];
            Pithagorus = sqrt(x*x+y*y);
            if(Pithagorus<checkLastPithagorus){
                checkLastPithagorus = Pithagorus;
                checkInEachXandY = i+1;
            }
        }

        HashMap NumWithPlace = new HashMap();
        NumWithPlace.put(1, Entrance1);
        NumWithPlace.put(2, Ladder1);
        NumWithPlace.put(3, Toilet1Man);
        NumWithPlace.put(4, Toilet1Woman);
        NumWithPlace.put(5, Library);
        NumWithPlace.put(6, DSSRoom);
        NumWithPlace.put(7, ATRoom);
        NumWithPlace.put(8, Entrance2);
        NumWithPlace.put(9, PublicRelation);
        NumWithPlace.put(10, Room102);
        NumWithPlace.put(11, Ladder2);
        NumWithPlace.put(12, Lift);
        NumWithPlace.put(13, Room104);
        NumWithPlace.put(14, Room105);
        NumWithPlace.put(15, KKRoom);
        NumWithPlace.put(16, Room107);
        NumWithPlace.put(17, Room108);
        NumWithPlace.put(18, Room110);
        NumWithPlace.put(19, Toilet2Man);
        NumWithPlace.put(20, Toilet2Woman);
        NumWithPlace.put(21, Ladder3);
        NumWithPlace.put(22, CopyStore);
        NumWithPlace.put(23, Room115);
        NumWithPlace.put(24, Room116);
        NumWithPlace.put(25, Room118);
        NumWithPlace.put(26, Node2);
        NumWithPlace.put(27, Node3);
        NumWithPlace.put(28, Node4);
        NumWithPlace.put(29, Node5);
        NumWithPlace.put(30, Node6);
        NumWithPlace.put(31, Node8);
        NumWithPlace.put(32, Node9);
        NumWithPlace.put(33, Node10);
        NumWithPlace.put(34, Node11);
        NumWithPlace.put(35, Node12);
        NumWithPlace.put(36, Node13);
        NumWithPlace.put(37, Node14);
        NumWithPlace.put(38, Node15);
        NumWithPlace.put(39, Node155);
        NumWithPlace.put(40, Node16);
//        NumWithPlace.put(41, "Node17");
        NumWithPlace.put(42, Node18);
        NumWithPlace.put(43, Node19);
        NumWithPlace.put(44, Node20);
        NumWithPlace.put(45, Node205);
        NumWithPlace.put(46, Node21);
        NumWithPlace.put(47, Node22);
        NumWithPlace.put(48, Node23);
        NumWithPlace.put(49, Node24);
        NumWithPlace.put(50, Node25);

        startLocation = (Vertex) NumWithPlace.get(checkInEachXandY);
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                currentLocation.setText(startLocation.toString());
//            }
//        },1500);
        currentLocation.setText(startLocation.toString());
        MyTTS.getInstance(NavigationActivity.this).speak("ตอนนี้คุณอยู่ที่"+currentLocation.getText().toString());
//        System.out.println("Your Current Location is : "+ NumWithPlace.get(checkInEachXandY));

        //Put in all path
        Entrance1.adjacencies = new Edge[]{ new Edge(Node2,5)};
        Node2.adjacencies = new Edge[]{ new Edge(Ladder1,1.4),new Edge(Node3,1.625),new Edge(Entrance1,5) };
        Ladder1.adjacencies = new Edge[]{ new Edge(Node2,1) };
        Node3.adjacencies = new Edge[]{ new Edge(Node2,1),new Edge(Node4,10.7),new Edge(Node5,2.25) };
        Node4.adjacencies = new Edge[]{ new Edge(Node3,10.7),new Edge(Toilet1Man,1.1),new Edge(Toilet1Woman,1) };
        Toilet1Man.adjacencies = new Edge[]{ new Edge(Node4,1.1) };
        Toilet1Woman.adjacencies = new Edge[]{ new Edge(Node4,1) };
        Node5.adjacencies = new Edge[]{ new Edge(Library,1.4),new Edge(Node3,10.7),new Edge(Node6,7) };
        Library.adjacencies = new Edge[]{ new Edge(Node5,1.4) };
        Node6.adjacencies = new Edge[]{ new Edge(Node5,7),new Edge(DSSRoom,1.4),new Edge(Node8,18) };
        DSSRoom.adjacencies = new Edge[]{ new Edge(Node6,1.4) };
        Node8.adjacencies = new Edge[]{ new Edge(Node6,18),new Edge(Node9,2.75),new Edge(Node155,9.1) };
        Node9.adjacencies = new Edge[]{ new Edge(Node8,2.75),new Edge(Node10,1.25),new Edge(Node11,5.5) };
        Node10.adjacencies = new Edge[]{ new Edge(Node9,1.25),new Edge(Node14,2.4) };
        Node14.adjacencies = new Edge[]{ new Edge(Node10,2.4),new Edge(Ladder2,2.4),new Edge(Lift,4) };
        Ladder2.adjacencies = new Edge[]{ new Edge(Node14,2.4) };
        Lift.adjacencies = new Edge[]{ new Edge(Node14,4) };
        Node11.adjacencies = new Edge[]{ new Edge(Node9,5.5),new Edge(Room102,3.75),new Edge(Node12,3.2) };
        Room102.adjacencies = new Edge[]{ new Edge(Node11,3.75) };
        Node12.adjacencies = new Edge[]{ new Edge(Node11,3.2),new Edge(ATRoom,3.75),new Edge(Node13,3.5) };
        ATRoom.adjacencies = new Edge[]{ new Edge(Node12,3.75) };
        Node13.adjacencies = new Edge[]{ new Edge(Node12,3.5),new Edge(PublicRelation,3.75),new Edge(Entrance2,2.6) };
        PublicRelation.adjacencies = new Edge[]{ new Edge(Node13,3.75) };
        Entrance2.adjacencies = new Edge[]{ new Edge(Node13,2.6) };
        Node155.adjacencies = new Edge[]{ new Edge(Node8,9.1),new Edge(Room104,6),new Edge(Node15,5.4) };
        Room104.adjacencies = new Edge[]{ new Edge(Node155,6) };
        Node15.adjacencies = new Edge[]{ new Edge(Node155,5.4),new Edge(Room105,2.7),new Edge(Node16,2) };
        Room105.adjacencies = new Edge[]{ new Edge(Node15,2.7) };
        Node16.adjacencies = new Edge[]{ new Edge(Node15,2),new Edge(KKRoom,4.15),new Edge(Node18,5.8) };
//     Node17.adjacencies = new Edge[]{ new Edge(Node16,4.15),new Edge(KKRoom,10.7) };
        KKRoom.adjacencies = new Edge[]{ new Edge(Node16,8.3) };
        Node18.adjacencies = new Edge[]{ new Edge(Node16,5.8),new Edge(Room107,2.7),new Edge(Node19,7.8) };
        Room107.adjacencies = new Edge[]{ new Edge(Node18,2.7) };
        Node19.adjacencies = new Edge[]{ new Edge(Node18,7.8),new Edge(Room108,2.7),new Edge(Node20,1.7) };
        Room108.adjacencies = new Edge[]{ new Edge(Node19,2.7) };
        Node20.adjacencies = new Edge[]{ new Edge(Node19,1.7),new Edge(Room110,2.7),new Edge(Node205,0.8) };
        Room110.adjacencies = new Edge[]{ new Edge(Node20,2.7) };
        Node205.adjacencies = new Edge[]{ new Edge(Node20,0.8),new Edge(Node21,8.2),new Edge(Node23,7.5) };
        Node21.adjacencies = new Edge[]{ new Edge(Node205,8.2),new Edge(Room115,1.4),new Edge(Node22,2) };
        Room115.adjacencies = new Edge[]{ new Edge(Node21,1.4) };
        Node22.adjacencies = new Edge[]{ new Edge(Node21,2),new Edge(Room116,1.4),new Edge(Room118,6.7) };
        Room116.adjacencies = new Edge[]{ new Edge(Node22,1.4) };
        Room118.adjacencies = new Edge[]{ new Edge(Node22,6.7) };
        Node23.adjacencies = new Edge[]{ new Edge(Node205,7.5),new Edge(Node25,7),new Edge(Node24,1.75) };
        Node25.adjacencies = new Edge[]{ new Edge(Node23,7),new Edge(Toilet2Man,3),new Edge(Toilet2Woman,1) };
        Toilet2Man.adjacencies = new Edge[]{ new Edge(Node25,3) };
        Toilet2Woman.adjacencies = new Edge[]{ new Edge(Node25,1) };
        Node24.adjacencies = new Edge[]{ new Edge(Node23,1.75),new Edge(Ladder3,1.7),new Edge(CopyStore,0.5) };
        Ladder3.adjacencies = new Edge[]{ new Edge(Node24,1.7) };
        CopyStore.adjacencies = new Edge[]{ new Edge(Node24,0.5) };

        //we're setting that first location is Library don't forget to change this location to dynamic
        final Vertex current = startLocation;
        Vertex destination;
        String destinationz = getIntent().getStringExtra("Destination");
        Log.e("pass from last activity", destinationz);
        if (destinationz.equals("Entrance1")){
            destination = Entrance1;
        }
        else if (destinationz.equals("Ladder1")){
            destination = Ladder1;
        }
        else if (destinationz.equals("Toilet1Man")){
            destination = Toilet1Man;
        }
        else if (destinationz.equals("Toilet1Woman")){
            destination = Toilet1Woman;
        }
        else if (destinationz.equals("Library")){
            destination = Library;
        }
        else if (destinationz.equals("DSSRoom")){
            destination = DSSRoom;
        }
        else if (destinationz.equals("ATRoom")){
            destination = ATRoom;
        }
        else if (destinationz.equals("Entrance2")){
            destination = Entrance2;
        }
        else if (destinationz.equals("PublicRelation")){
            destination = PublicRelation;
        }
        else if (destinationz.equals("Room102")){
            destination = Room102;
        }
        else if (destinationz.equals("Ladder2")){
            destination = Ladder2;
        }
        else if (destinationz.equals("Lift")){
            destination = Lift;
        }
        else if (destinationz.equals("Room104")){
            destination = Room104;
        }
        else if (destinationz.equals("Room105")){
            destination = Room105;
        }
        else if (destinationz.equals("KKRoom")){
            destination = KKRoom;
        }
        else if (destinationz.equals("Room107")){
            destination = Room107;
        }
        else if (destinationz.equals("Room108")){
            destination = Room108;
        }
        else if (destinationz.equals("Room110")){
            destination = Room110;
        }
        else if (destinationz.equals("Toilet2Man")){
            destination = Toilet2Man;
        }
        else if (destinationz.equals("Toilet2Woman")){
            destination = Toilet2Woman;
        }
        else if (destinationz.equals("Ladder3")){
            destination = Ladder3;
        }
        else if (destinationz.equals("CopyStore")){
            destination = CopyStore;
        }
        else if (destinationz.equals("Room115")){
            destination = Room115;
        }
        else if (destinationz.equals("Room116")){
            destination = Room116;
        }
        else{
            destination = Room118;
        }


        //Put Source
        computePaths(current); // run Dijkstra
        //Put Destination and calculate long
        System.out.println("Distance to " + destination + ": " + destination.minDistance);
        //Calculate path
//        final List<Vertex> path = getShortestPathTo(destination);
        path = getShortestPathTo(destination);
        System.out.println("Path: " + path);

//        int SizeOfPath = path.size();
//        int checkArriveThisNodeYet = 0;
//        double distanceToThisNode;
//        for (int i = 0 ; i<VirtualCurrentLocationOnX.size(); i++){
//            int[] currentRecall = {VirtualCurrentLocationOnX.get(i),VirtualCurrentLocationOnY.get(i)};
//            if (currentRecall == path.get(checkArriveThisNodeYet).location){
//                checkArriveThisNodeYet+=1;
//            }else {
//                x = currentRecall[0] - path.get(checkArriveThisNodeYet).location[0];
//                y = currentRecall[1] - path.get(checkArriveThisNodeYet).location[1];
//                distanceToThisNode = (sqrt(x*x+y*y))*0.28;
//
//            }
//        }

//        final ArrayList<String> WordInPath = new ArrayList<>();
        for(int i = 0 ; i < path.size(); i++){
            if(i == 0){
                if(path.get(i) == Entrance1)
                    WordInPath.add("เดินตรงไป 5 เมตร");
                else if(path.get(i) == Ladder1)
                    WordInPath.add("เดินตรงไป 1 เมตร");
                else if(path.get(i) == Toilet1Man)
                    WordInPath.add("เดินตรงไป 1 เมตร");
                else if(path.get(i) == Toilet1Woman)
                    WordInPath.add("เดินตรงไป 2 เมตร");
                else if(path.get(i) == Library)
                    WordInPath.add("เดินตรงไป 1 เมตร");
                else if(path.get(i) == DSSRoom)
                    WordInPath.add("เดินตรงไป 1 เมตร");
                else if(path.get(i) == ATRoom)
                    WordInPath.add("เดินตรงไป 4 เมตร");
                else if (path.get(i) == Room102)
                    WordInPath.add("เดินตรงไป 4 เมตร");
                else if(path.get(i) == Entrance2)
                    WordInPath.add("เดินตรงไป 3 เมตร");
                else if(path.get(i) == PublicRelation)
                    WordInPath.add("เดินตรงไป 4 เมตร");
                else if(path.get(i) == Ladder2)
                    WordInPath.add("เดินตรงไป 2 เมตร");
                else if(path.get(i) == Lift)
                    WordInPath.add("เดินตรงไป 5 เมตร");
                else if(path.get(i) == Room104)
                    WordInPath.add("เดินตรงไป 4 เมตร");
                else if(path.get(i) == Room105)
                    WordInPath.add("เดินตรงไป 3 เมตร");
                else if(path.get(i) == KKRoom)
                    WordInPath.add("เดินตรงไป 4 เมตร");
                else if(path.get(i) == Room107)
                    WordInPath.add("เดินตรงไป 3 เมตร");
                else if(path.get(i) == Room108)
                    WordInPath.add("เดินตรงไป 3 เมตร");
                else if(path.get(i) == Room110)
                    WordInPath.add("เดินตรงไป 3 เมตร");
                else if(path.get(i) == CopyStore)
                    WordInPath.add("เดินตรงไป 1 เมตร");
                else if(path.get(i) == Ladder3)
                    WordInPath.add("เดินตรงไป 3 เมตร");
                else if(path.get(i) == Toilet2Man)
                    WordInPath.add("เดินตรงไป 2 เมตร");
                else if(path.get(i) == Toilet2Woman)
                    WordInPath.add("เดินตรงไป 2 เมตร");
                else if(path.get(i) == Room115)
                    WordInPath.add("เดินตรงไป 1 เมตร");
                else if(path.get(i) == Room116)
                    WordInPath.add("เดินตรงไป 1 เมตร");
                else if(path.get(i) == Room118)
                    WordInPath.add("เดินตรงไป 3 เมตร");
            }else{

                if( i!=path.size()-1){
                    if(path.get(i+1) == Ladder1){//ตัวมันเองเป็น node 2 ไปหา Ladder1
                        if(path.get(i-1) == Entrance1){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 1 เมตร");
                        }if(path.get(i-1) == Node3){
                            WordInPath.add("หมุนขวาและเดินตรงไป 1 เมตร");
                        }
                    }
                    if(path.get(i+1) == Entrance1){//ตัวมันเองเป็น node2 ไปหา Entrance1
                        if(path.get(i-1) == Ladder1){
                            WordInPath.add("หมุนขวาและเดินตรงไป 5 เมตร");
                        }if(path.get(i-1) == Node3){
                            WordInPath.add("เดินตรงไป 5 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node3){//ตัวมันเองเป็น node2 ไปหา node3
                        if(path.get(i-1) == Ladder1){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 2 เมตร");
                        }if(path.get(i-1) == Entrance1){
                            WordInPath.add("เดินตรงไป 2 เมตร");
                        }
                    }

                    if(path.get(i+1) == Node2){//node3 to node2
                        if(path.get(i-1) == Node4){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 2 เมตร");
                        }if(path.get(i-1) == Node5){
                            WordInPath.add("เดินตรงไป 2 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node4){//node3 to node4
                        if(path.get(i-1) == Node2){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 7 เมตร");
                        }if(path.get(i-1) == Node5){
                            WordInPath.add("หมุนขวาและเดินตรงไป 7 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node5){//node3 to node 5
                        if(path.get(i-1) == Node4){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 2 เมตร");
                        }
                        if(path.get(i-1) == Node2){
                            WordInPath.add("เดินตรงไป 2 เมตร");
                        }
                    }

                    if(path.get(i+1) == Node3){//node5 to node 3
                        if(path.get(i-1) == Library){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 2 เมตร");
                        }
                        if(path.get(i-1) == Node6){
                            WordInPath.add("เดินตรงไป 2 เมตร");
                        }
                    }
                    if(path.get(i+1) == Library){//node5 to library
                        if(path.get(i-1) == Node6){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 1 เมตร");
                        }if(path.get(i-1) == Node3){
                            WordInPath.add("หมุนขวาและเดินตรงไป 1 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node6){//node5 to node6
                        if(path.get(i-1) == Library){
                            WordInPath.add("หมุนขวาและเดินตรงไป 7 เมตร");
                        }
                        if(path.get(i-1) == Node3){
                            WordInPath.add("เดินตรงไป 7 เมตร");
                        }
                    }

                    if(path.get(i+1) == Node5){//node6 to node5
                        if(path.get(i-1) == DSSRoom){
                            WordInPath.add("หมุนขวาและเดินตรงไป 7 เมตร");
                        }
                        if(path.get(i-1) == Node8){
                            WordInPath.add("เดินตรงไป 7 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node3){//node6 to DSSRoom
                        if(path.get(i-1) == Node5){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 1 เมตร");
                        }if(path.get(i-1) == Node8){
                            WordInPath.add("หมุนขวาและเดินตรงไป 1 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node8){//node6 to node 8
                        if(path.get(i-1) == DSSRoom){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 18 เมตร");
                        }
                        if(path.get(i-1) == Node5){
                            WordInPath.add("เดินตรงไป 18 เมตร");
                        }
                    }

                    if(path.get(i+1) == Toilet1Man){//node4 to toilet1man
                        if(path.get(i-1) == Toilet1Woman){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 2 เมตร");
                        }
                        if(path.get(i-1) == Node3){
                            WordInPath.add("เดินตรงไป 2 เมตร");
                        }
                    }
                    if(path.get(i+1) == Toilet1Woman){//node4 to toilet1woman
                        if(path.get(i-1) == Node3){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 1 เมตร");
                        }if(path.get(i-1) == Toilet1Man){
                            WordInPath.add("หมุนขวาและเดินตรงไป 1 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node3){//node4 to node3
                        if(path.get(i-1) == Toilet1Woman){
                            WordInPath.add("หมุนขวาและเดินตรงไป 7 เมตร");
                        }
                        if(path.get(i-1) == Toilet1Man){
                            WordInPath.add("เดินตรงไป 7 เมตร");
                        }
                    }

                    if(path.get(i+1) == Node6){//node8 to node6
                        if(path.get(i-1) == Node155){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 18 เมตร");
                        }
                        if(path.get(i-1) == Node9){
                            WordInPath.add("เดินตรงไป 18 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node155){//node8 to node155
                        if(path.get(i-1) == Node9){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 9 เมตร");
                        }if(path.get(i-1) == Node6){
                            WordInPath.add("หมุนขวาและเดินตรงไป 9 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node9){//node8 to node9
                        if(path.get(i-1) == Node155){
                            WordInPath.add("หมุนขวาและเดินตรงไป 3 เมตร");
                        }
                        if(path.get(i-1) == Node6){
                            WordInPath.add("เดินตรงไป 3 เมตร");
                        }
                    }

                    if(path.get(i+1) == Node8){//node9 to node8
                        if(path.get(i-1) == Node11){
                            WordInPath.add("หมุนขวาและเดินตรงไป 3 เมตร");
                        }
                        if(path.get(i-1) == Node10){
                            WordInPath.add("เดินตรงไป 3 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node10){//node9 to node10
                        if(path.get(i-1) == Node11){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 1 เมตร");
                        }
                        if(path.get(i-1) == Node8){
                            WordInPath.add("เดินตรงไป 1 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node11){//node9 to node 11
                        if(path.get(i-1) == Node8){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 6 เมตร");
                        }if(path.get(i-1) == Node10){
                            WordInPath.add("หมุนขวาและเดินตรงไป 6 เมตร");
                        }
                    }

                    if(path.get(i+1) == Node9){//node10 to node9
                        if(path.get(i-1) == Node14){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 1 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node14){//node10 to node14
                        if(path.get(i-1) == Node9){
                            WordInPath.add("หมุนขวาและเดินตรงไป 2 เมตร");
                        }
                    }

                    if(path.get(i+1) == Node10){//node14 to node10
                        if(path.get(i-1) == Ladder2){
                            WordInPath.add("หมุนขวาและเดินตรงไป 2 เมตร");
                        }
                        if(path.get(i-1) == Lift){
                            WordInPath.add("เดินตรงไป 2 เมตร");
                        }
                    }
                    if(path.get(i+1) == Lift){//node14 to lift
                        if(path.get(i-1) == Ladder2){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 5 เมตร");
                        }
                        if(path.get(i-1) == Node10){
                            WordInPath.add("เดินตรงไป 5 เมตร");
                        }
                    }
                    if(path.get(i+1) == Ladder2){//node14 to ladder2
                        if(path.get(i-1) == Node10){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 2 เมตร");
                        }if(path.get(i-1) == Lift){
                            WordInPath.add("หมุนขวาและเดินตรงไป 2 เมตร");
                        }
                    }

                    if(path.get(i+1) == Node9){//node11 to node9
                        if(path.get(i-1) == Room102){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 6 เมตร");
                        }
                        if(path.get(i-1) == Node12){
                            WordInPath.add("เดินตรงไป 6 เมตร");
                        }
                    }
                    if(path.get(i+1) == Room102){//node11 to room102
                        if(path.get(i-1) == Node12){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 4 เมตร");
                        }if(path.get(i-1) == Node9){
                            WordInPath.add("หมุนขวาและเดินตรงไป 4 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node12){//node11 to node12
                        if(path.get(i-1) == Room102){
                            WordInPath.add("หมุนขวาและเดินตรงไป 3 เมตร");
                        }
                        if(path.get(i-1) == Node9){
                            WordInPath.add("เดินตรงไป 3 เมตร");
                        }
                    }

                    if(path.get(i+1) == Node11){//node12 to node11
                        if(path.get(i-1) == ATRoom){
                            WordInPath.add("หมุนขวาและเดินตรงไป 3 เมตร");
                        }
                        if(path.get(i-1) == Node13){
                            WordInPath.add("เดินตรงไป 3 เมตร");
                        }
                    }
                    if(path.get(i+1) == ATRoom){//node12 to ATRoom
                        if(path.get(i-1) == Node11){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 4 เมตร");
                        }if(path.get(i-1) == Node13){
                            WordInPath.add("หมุนขวาและเดินตรงไป 4 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node13){//node12 to node13
                        if(path.get(i-1) == ATRoom){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 4 เมตร");
                        }
                        if(path.get(i-1) == Node11){
                            WordInPath.add("เดินตรงไป 4 เมตร");
                        }
                    }

                    if(path.get(i+1) == Node12){//node13 to node12
                        if(path.get(i-1) == PublicRelation){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 4 เมตร");
                        }
                        if(path.get(i-1) == Entrance2){
                            WordInPath.add("เดินตรงไป 4 เมตร");
                        }
                    }
                    if(path.get(i+1) == PublicRelation){//node13 to publicrelation
                        if(path.get(i-1) == Entrance2){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 4 เมตร");
                        }if(path.get(i-1) == Node12){
                            WordInPath.add("หมุนขวาและเดินตรงไป 4 เมตร");
                        }
                    }
                    if(path.get(i+1) == Entrance2){//node13 to Entrance2
                        if(path.get(i-1) == PublicRelation){
                            WordInPath.add("หมุนขวาและเดินตรงไป 3 เมตร");
                        }
                        if(path.get(i-1) == Node12){
                            WordInPath.add("เดินตรงไป 3 เมตร");
                        }
                    }

                    if(path.get(i+1) == Node8){//node155 to node8
                        if(path.get(i-1) == Room104){
                            WordInPath.add("หมุนขวาและเดินตรงไป 9 เมตร");
                        }
                        if(path.get(i-1) == Node15){
                            WordInPath.add("เดินตรงไป 9 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node15){//node155 to node15
                        if(path.get(i-1) == Room104){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 5 เมตร");
                        }
                        if(path.get(i-1) == Node8){
                            WordInPath.add("เดินตรงไป 5 เมตร");
                        }
                    }
                    if(path.get(i+1) == Room104){//node155 to room104
                        if(path.get(i-1) == Node8){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 4 เมตร");
                        }if(path.get(i-1) == Node15){
                            WordInPath.add("หมุนขวาและเดินตรงไป 4 เมตร");
                        }
                    }

                    if(path.get(i+1) == Node155){//node15 to node155
                        if(path.get(i-1) == Room105){
                            WordInPath.add("หมุนขวาและเดินตรงไป 5 เมตร");
                        }
                        if(path.get(i-1) == Node16){
                            WordInPath.add("เดินตรงไป 5 เมตร");
                        }
                    }
                    if(path.get(i+1) == Room105){//node15 to room105
                        if(path.get(i-1) == Node155){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 3 เมตร");
                        }if(path.get(i-1) == Node16){
                            WordInPath.add("หมุนขวาและเดินตรงไป 3 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node16){//node15 to node16
                        if(path.get(i-1) == Room105){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 2 เมตร");
                        }
                        if(path.get(i-1) == Node155){
                            WordInPath.add("เดินตรงไป 2 เมตร");
                        }
                    }

                    if(path.get(i+1) == Node15){//node16 to node15
                        if(path.get(i-1) == KKRoom){
                            WordInPath.add("หมุนขวาและเดินตรงไป 2 เมตร");
                        }
                        if(path.get(i-1) == Node18){
                            WordInPath.add("เดินตรงไป 2 เมตร");
                        }
                    }
                    if(path.get(i+1) == KKRoom){//node16 to kkroom
                        if(path.get(i-1) == Node15){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 8 เมตร");
                        }if(path.get(i-1) == Node18){
                            WordInPath.add("หมุนขวาและเดินตรงไป 8 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node18){//node16 to node18
                        if(path.get(i-1) == KKRoom){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 6 เมตร");
                        }
                        if(path.get(i-1) == Node15){
                            WordInPath.add("เดินตรงไป 6 เมตร");
                        }
                    }

//                    if(path.get(i+1) == Node16){//node17 to node16
//                        if(path.get(i-1) == KKRoom){
//                            WordInPath.add("เดินตรงไป 4.15 เมตร");
//                        }
//                    }
//                    if(path.get(i+1) == Node3){//node17 to KKRoom
//                        if(path.get(i-1) == Node16){
//                            WordInPath.add("เดินตรงไป 4.15 เมตร");
//                        }
//                    }

                    if(path.get(i+1) == Node16){//node18 to node16
                        if(path.get(i-1) == Room107){
                            WordInPath.add("หมุนขวาและเดินตรงไป 6 เมตร");
                        }
                        if(path.get(i-1) == Node19){
                            WordInPath.add("เดินตรงไป 6 เมตร");
                        }
                    }
                    if(path.get(i+1) == Room107){//node18 to room107
                        if(path.get(i-1) == Node16){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 3 เมตร");
                        }if(path.get(i-1) == Node19){
                            WordInPath.add("หมุนขวาและเดินตรงไป 3 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node19){//node18 to node19
                        if(path.get(i-1) == Room107){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 8 เมตร");
                        }
                        if(path.get(i-1) == Node16){
                            WordInPath.add("เดินตรงไป 8 เมตร");
                        }
                    }

                    if(path.get(i+1) == Node18){//node19 to node18
                        if(path.get(i-1) == Room108){
                            WordInPath.add("หมุนขวาและเดินตรงไป 8 เมตร");
                        }
                        if(path.get(i-1) == Node20){
                            WordInPath.add("เดินตรงไป 8 เมตร");
                        }
                    }
                    if(path.get(i+1) == Room108){//node19 to Room108
                        if(path.get(i-1) == Node18){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 3 เมตร");
                        }if(path.get(i-1) == Node20){
                            WordInPath.add("หมุนขวาและเดินตรงไป 3 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node20){//node19 to node20
                        if(path.get(i-1) == Room108){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 2 เมตร");
                        }
                        if(path.get(i-1) == Node18){
                            WordInPath.add("เดินตรงไป 2 เมตร");
                        }
                    }

                    if(path.get(i+1) == Node20){//node20 to node19
                        if(path.get(i-1) == Room110){
                            WordInPath.add("หมุนขวาและเดินตรงไป 2 เมตร");
                        }
                        if(path.get(i-1) == Node205){
                            WordInPath.add("เดินตรงไป 2 เมตร");
                        }
                    }
                    if(path.get(i+1) == Room110){//node20 to Room110
                        if(path.get(i-1) == Node19){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 3 เมตร");
                        }if(path.get(i-1) == Node205){
                            WordInPath.add("หมุนขวาและเดินตรงไป 3 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node205){//node20 to node205
                        if(path.get(i-1) == Room110){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 1 เมตร");
                        }
                        if(path.get(i-1) == Node23){
                            WordInPath.add("เดินตรงไป 1 เมตร");
                        }
                    }

                    if(path.get(i+1) == Node20){//node205 to node20
                        if(path.get(i-1) == Node21){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 1 เมตร");
                        }
                        if(path.get(i-1) == Node23){
                            WordInPath.add("เดินตรงไป 1 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node21){//node205 to node21
                        if(path.get(i-1) == Node23){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 8 เมตร");
                        }if(path.get(i-1) == Node20){
                            WordInPath.add("หมุนขวาและเดินตรงไป 8 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node23){//node205 to node23
                        if(path.get(i-1) == Node21){
                            WordInPath.add("หมุนขวาและเดินตรงไป 8 เมตร");
                        }
                        if(path.get(i-1) == Node20){
                            WordInPath.add("เดินตรงไป 8 เมตร");
                        }
                    }

                    if(path.get(i+1) == Node205){//node21 to node205
                        if(path.get(i-1) == Room115){
                            WordInPath.add("หมุนขวาและเดินตรงไป 8 เมตร");
                        }
                        if(path.get(i-1) == Node22){
                            WordInPath.add("เดินตรงไป 8 เมตร");
                        }
                    }
                    if(path.get(i+1) == Room115){//node21 to Room115
                        if(path.get(i-1) == Node205){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 1 เมตร");
                        }if(path.get(i-1) == Node22){
                            WordInPath.add("หมุนขวาและเดินตรงไป 1 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node22){//node21 to node22
                        if(path.get(i-1) == Room115){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 2 เมตร");
                        }
                        if(path.get(i-1) == Node205){
                            WordInPath.add("เดินตรงไป 2 เมตร");
                        }
                    }

                    if(path.get(i+1) == Node21){//node22 to node21
                        if(path.get(i-1) == Room116){
                            WordInPath.add("หมุนขวาและเดินตรงไป 2 เมตร");
                        }
                        if(path.get(i-1) == Room118){
                            WordInPath.add("เดินตรงไป 2 เมตร");
                        }
                    }
                    if(path.get(i+1) == Room116){//node22 to Room116
                        if(path.get(i-1) == Node21){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 1 เมตร");
                        }if(path.get(i-1) == Room118){
                            WordInPath.add("หมุนขวาและเดินตรงไป 1 เมตร");
                        }
                    }
                    if(path.get(i+1) == Room118){//node22 to Room118
                        if(path.get(i-1) == Room116){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 7 เมตร");
                        }
                        if(path.get(i-1) == Node21){
                            WordInPath.add("เดินตรงไป 7 เมตร");
                        }
                    }

                    if(path.get(i+1) == Node205){//node23 to node205
                        if(path.get(i-1) == Node25){
                            WordInPath.add("หมุนขวาและเดินตรงไป 8 เมตร");
                        }
                        if(path.get(i-1) == Node24){
                            WordInPath.add("เดินตรงไป 8 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node25){//node23 to node25
                        if(path.get(i-1) == Node205){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 7 เมตร");
                        }if(path.get(i-1) == Node24){
                            WordInPath.add("หมุนขวาและเดินตรงไป 7 เมตร");
                        }
                    }
                    if(path.get(i+1) == Node24){//node23 to node24
                        if(path.get(i-1) == Node25){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 2 เมตร");
                        }
                        if(path.get(i-1) == Node205){
                            WordInPath.add("เดินตรงไป 2 เมตร");
                        }
                    }

                    if(path.get(i+1) == Node23){//node24 to node23
                        if(path.get(i-1) == Ladder3){
                            WordInPath.add("หมุนขวาและเดินตรงไป 2 เมตร");
                        }
                        if(path.get(i-1) == CopyStore){
                            WordInPath.add("เดินตรงไป 2 เมตร");
                        }
                    }
                    if(path.get(i+1) == CopyStore){//node24 to copystore
                        if(path.get(i-1) == Ladder3){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 1 เมตร");
                        }
                        if(path.get(i-1) == Node23){
                            WordInPath.add("เดินตรงไป 1 เมตร");
                        }
                    }
                    if(path.get(i+1) == Ladder3){//node24 to ladder3
                        if(path.get(i-1) == Node23){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 2.7 เมตร");
                        }if(path.get(i-1) == CopyStore){
                            WordInPath.add("หมุนขวาและเดินตรงไป 2.7 เมตร");
                        }
                    }

                    if(path.get(i+1) == Node23){//node25 to node23
                        if(path.get(i-1) == Toilet2Woman){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 7 เมตร");
                        }
                        if(path.get(i-1) == Toilet2Man){
                            WordInPath.add("เดินตรงไป 7 เมตร");
                        }
                    }
                    if(path.get(i+1) == Toilet2Woman){//node25 to toilet2woman
                        if(path.get(i-1) == Toilet2Man){
                            WordInPath.add("หมุนซ้ายและเดินตรงไป 1 เมตร");
                        }if(path.get(i-1) == Node23){
                            WordInPath.add("หมุนขวาและเดินตรงไป 1 เมตร");
                        }
                    }
                    if(path.get(i+1) == Toilet2Man){//node25 to toilet2man
                        if(path.get(i-1) == Toilet2Woman){
                            WordInPath.add("หมุนขวาและเดินตรงไป 1 เมตร");
                        }
                        if(path.get(i-1) == Node23){
                            WordInPath.add("เดินตรงไป 1 เมตร");
                        }
                    }
                }
            }

        }



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
//
//        Paint paint = new Paint();                          //define paint and paint color
//        paint.setColor(Color.RED);
//        paint.setStyle(Paint.Style.FILL_AND_STROKE);
//        //paint.setStrokeWidth(0.5f);
//        paint.setAntiAlias(true);                           //smooth edges

//        new FeedJSONTask().execute();

        imgView2 = (ImageView) findViewById(R.id.imageView2);
        imgView2.bringToFront();
        imgView2.setImageBitmap(bitMap);
//        TextView text1 = (TextView) findViewById(R.id.textView6);
//        text1.setText(" MAC 60:83:34:6D:11:8D ");


        Log.d("Test Debug >>","test1111");
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
//        CoX = ((372*wantX)/345)+4;
//        CoY = ((268*wantY)/243)+124;
//        canvas.drawCircle(CoX, CoY, 2, paint);
//        //invalidate to update bitmap in imageview
        imgView2.invalidate();


//        tts = new TextToSpeech(this, this);
//        tts.setLanguage(new Locale("th"));
//        test123 = (LinearLayout)findViewById(R.id.testtest);
//        test123.setOnTouchListener(new OnSwipe(this){
//            public void onSwipeTop() {
//                tts.speak("ปัดขวาเพื่อเข้าสู่หน้าหลัก",TextToSpeech.QUEUE_FLUSH,null);
//            }
//            public void onSwipeRight() {
//                Intent it = new Intent(getApplicationContext(),MainPage.class);
//                startActivity(it);
//                finish();
//            }
//            public void onSwipeLeft() {
//
//            }
//            public void onSwipeBottom() {
//                tts.speak("หน้านี้คือหน้านำทาง 1.625", TextToSpeech.QUEUE_FLUSH,null);
//            }
//        });
//


//        tts = new TextToSpeech(this, this,"com.google.android.tts");
//        tts.setLanguage(new Locale("th"));
//        btnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // fake call for init
//                MyTTS.getInstance(NavigationActivity.this)
//                        .setEngine("com.google.android.tts")
//                        .setLocale(new Locale("th")).speak("");
//
//                //
//                final Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        int SizeOfPath = path.size();
//                        int checkArriveThisNodeYet = 0;
//                        double distanceToThisNode;
//                        String distance;
//                        for (int i = 0 ; i<VirtualCurrentLocationOnX.size(); i++){
//                            int[] currentRecall = {VirtualCurrentLocationOnX.get(i),VirtualCurrentLocationOnY.get(i)};
//                            Log.e("Current Recall : ", String.valueOf(currentRecall[0]+" , "+ currentRecall[1]));
//                            Log.e("checkArrive : ", String.valueOf(checkArriveThisNodeYet));
//                            Log.e("Current Arrive Node : ",String.valueOf(path.get(checkArriveThisNodeYet).location[0]
//                            +" , "+ path.get(checkArriveThisNodeYet).location[1]));
//                            if (currentRecall[0] == path.get(checkArriveThisNodeYet).location[0]
//                                    && currentRecall[1] == path.get(checkArriveThisNodeYet).location[1]){
//
//
//                                    MyTTS.getInstance(NavigationActivity.this).speak(WordInPath.get(checkArriveThisNodeYet));
//                                    currentLocation.setText(path.get(checkArriveThisNodeYet).toString());
//                                    currentPath.setText(WordInPath.get(checkArriveThisNodeYet));
//                                if (checkArriveThisNodeYet < WordInPath.size()-1){
//                                    checkArriveThisNodeYet+=1;
//                                }else{
//
//                                }
//
//
//                            }else {
//                                int x = currentRecall[0] - path.get(checkArriveThisNodeYet).location[0];
//                                int y = currentRecall[1] - path.get(checkArriveThisNodeYet).location[1];
//                                Log.e("Rx : " , String.valueOf(currentRecall[0]));
//                                Log.e("Ry : " , String.valueOf(currentRecall[1]));
//                                Log.e("Px : " , String.valueOf(path.get(checkArriveThisNodeYet).location[0]));
//                                Log.e("Py : " , String.valueOf(path.get(checkArriveThisNodeYet).location[1]));
//                                Log.e("X : " , String.valueOf(x));
//                                Log.e("Y : " , String.valueOf(y));
//                                distanceToThisNode = (sqrt(x*x+y*y));
//                                Log.e("distance2thisBF : " , String.valueOf(distanceToThisNode));
//                                distanceToThisNode = distanceToThisNode*(0.18);
//                                Log.e("distance2thisAT : " , String.valueOf(distanceToThisNode));
//                                distance = String.format("%.2f",distanceToThisNode);
////                                int tt = parseInt(distance);
//                                Log.e("this is distance : ",String.valueOf(distance));
//                                String wordDistance = "เหลืออีก"+ distance + "เมตร ก่อนจะถึงจุดต่อไป";
//                                if(i == VirtualCurrentLocationOnX.size()-1){
//                                    wordDistance = "ถึงจุดหมายเรียบร้อยแล้ว";
//                                }
//                                MyTTS.getInstance(NavigationActivity.this).speak(wordDistance);
//
//                            }
//                        }

//                        for (int i =0 ; i<WordInPath.size();i++) {
//                            Log.e("test", "onClick: " + WordInPath.get(i));
//                            MyTTS.getInstance(NavigationActivity.this).speak(WordInPath.get(i));
//                            currentLocation.setText(path.get(i+1).toString());
//                            currentPath.setText(WordInPath.get(i));
//
//                        }

//                    }
//                }, 1000);
//            }

//            int SizeOfPath = path.size();
//            int checkArriveThisNodeYet = 0;
//            double distanceToThisNode;
//        for (int i = 0 ; i<VirtualCurrentLocationOnX.size(); i++){
//                int[] currentRecall = {VirtualCurrentLocationOnX.get(i),VirtualCurrentLocationOnY.get(i)};
//                if (currentRecall == path.get(checkArriveThisNodeYet).location){
//                    checkArriveThisNodeYet+=1;
//                }else {
//                    x = currentRecall[0] - path.get(checkArriveThisNodeYet).location[0];
//                    y = currentRecall[1] - path.get(checkArriveThisNodeYet).location[1];
//                    distanceToThisNode = (sqrt(x*x+y*y))*0.28;
//
//                }
//            }

//        });
}


    @Override
    public void onInit(int status) {

    }

    @Override
    public void onClick(View v) {
        runLoopWithDelay();
    }

//  this for redpoint on map example
//    public class FeedJSONTask extends AsyncTask<String[], Void, String[]> {
//
//        @Override
//        protected String[] doInBackground(String[]... strings) {
//            String result = FeedJSON();
//            Gson gson = new Gson();
//            Type collectionType = new TypeToken<Collection<CMXResponse>>() {}.getType();
//            Collection<CMXResponse> enums = gson.fromJson(result, collectionType);
//            CMXResponse[] CMXresult = enums.toArray(new CMXResponse[enums.size()]);
//
////            Bitmap bitMap = Bitmap.createBitmap(380 , 516, Bitmap.Config.ARGB_8888);  //creates bmp
////            bitMap = bitMap.copy(bitMap.getConfig(), true);     //lets bmp to be mutable
////            Canvas canvas = new Canvas(bitMap);                 //draw a canvas in defined bmp
//
////            Paint paint = new Paint();                          //define paint and paint color
////            paint.setColor(Color.RED);
////            paint.setStyle(Paint.Style.FILL_AND_STROKE);
////            //paint.setStrokeWidth(0.5f);
////            paint.setAntiAlias(true);                           //smooth edges
//
////            imgView2 = (ImageView) findViewById(R.id.imageView2);
////            imgView2.bringToFront();
////            imgView2.setImageBitmap(bitMap);
//
//                double locateX = CMXresult[0].getMapCoordinate().getX();
//                double locateY = CMXresult[0].getMapCoordinate().getY();
//                double CoXX = ((372*locateX)/345)+4;
//                double CoYY= ((268*locateY)/243)+124;
//                int CoX = (int)CoXX;
//                int CoY = (int)CoYY;
//                String[] res = {Integer.toString(CoX),Integer.toString(CoY)};
////                canvas.drawCircle(CoX, CoY, 2, paint);
//                //invalidate to update bitmap in imageview
////                imgView2.invalidate();
//
//
//
//            return res;
//        }
//
//        @Override
//        protected void onPostExecute(String[] s) {
//
//            super.onPostExecute(s);
//
//            int x = Integer.valueOf(s[0]);
//            int y = Integer.valueOf(s[1]);
//
//            Bitmap bitMap = Bitmap.createBitmap(380 , 516, Bitmap.Config.ARGB_8888);  //creates bmp
//            bitMap = bitMap.copy(bitMap.getConfig(), true);     //lets bmp to be mutable
//            Canvas canvas = new Canvas(bitMap);                 //draw a canvas in defined bmp
//
//            Paint paint = new Paint();                          //define paint and paint color
//            paint.setColor(Color.RED);
//            paint.setStyle(Paint.Style.FILL_AND_STROKE);
//            //paint.setStrokeWidth(0.5f);
//            paint.setAntiAlias(true);                           //smooth edges
//
//            imgView2 = (ImageView) findViewById(R.id.imageView2);
//            imgView2.bringToFront();
//            imgView2.setImageBitmap(bitMap);
//
//            canvas.drawCircle(x, y, 2, paint);
//            imgView2.invalidate();
//        }
//    }


//    this is feedjson function for redpoint on map example
//    private String FeedJSON(){
//
//        try {
//
//            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
//
//            OkHttpClient client = SelfSigningClientBuilder.createClient()
//                    .authenticator(new Authenticator() {
//                        @Nullable
//                        @Override
//                        public Request authenticate(Route route, Response response) throws IOException {
//                            String credential = Credentials.basic("dev", "dev12345");
//                            return response.request().newBuilder().header("Authorization", credential).build();
//                        }
//                    }).addInterceptor(logging).build();
//
//            Request request = new Request.Builder().url(LocationHistoryURL)
//                    .build();
//
//            try {
//                Response response = client.newCall(request).execute();
//                String result = response.body().string();
//                return result;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }catch (Exception e){
//
//        }
//        return null;
//    }



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

    public static void computePaths(Vertex source)
    {
        source.minDistance = 0.;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
        vertexQueue.add(source);

        while (!vertexQueue.isEmpty()) {
            Vertex u = vertexQueue.poll();

            // Visit each edge exiting u
            for (Edge e : u.adjacencies)
            {
                Vertex v = e.target;
                double weight = e.weight;
                double distanceThroughU = u.minDistance + weight;
                if (distanceThroughU < v.minDistance) {
                    vertexQueue.remove(v);

                    v.minDistance = distanceThroughU ;
                    v.previous = u;
                    vertexQueue.add(v);
                }
            }
        }
    }

    public static List<Vertex> getShortestPathTo(Vertex target)
    {
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
            path.add(vertex);

        Collections.reverse(path);
        return path;
    }


    public class FeedJSONTaskCurrentLocation extends AsyncTask<String, Void, String[]> {

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
            Log.e("test httprequestX :", String.valueOf(currentLocationResponse[0].getMapCoordinate().getX()));
            Log.e("test httprequestY :",String.valueOf(currentLocationResponse[0].getMapCoordinate().getY()));
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
//            TextView CurrentX = (TextView) findViewById(R.id.CurrentX);
//            TextView CurrentY = (TextView) findViewById(R.id.CurrentY);
//            TextView ServerTime = (TextView) findViewById(R.id.ServerTime);
//            TextView FirstLocatedTime = (TextView) findViewById(R.id.FirstLocateTime);
//            TextView LastLocatedTime = (TextView) findViewById(R.id.LastLocateTime);
//            TextView SuccessTimeStamp = (TextView) findViewById(R.id.SuccessTimeStamp);
//
//            CurrentX.setText(s[0]);
//            CurrentY.setText(s[1]);
//            ServerTime.setText(s[2]);
//            FirstLocatedTime.setText(s[3]);
//            LastLocatedTime.setText(s[4]);
//            Timestamp timestamplast = new Timestamp(System.currentTimeMillis());
//            SuccessTimeStamp.setText(String.valueOf(timestamplast.getTime()));

//            currentRecall = new int[]{parseInt(s[0]), parseInt(s[1])};
            Log.e("locateX",s[0]);
            Log.e("locateY",s[1]);
            APIcallCurrentlocationX = parseInt(s[0]);
            APIcallCurrentLocationY = parseInt(s[1]);
            Log.e("APIcallCurrentLocationX", String.valueOf(APIcallCurrentlocationX));
            Log.e("APIcallcurrentLocaitonY",String.valueOf(APIcallCurrentLocationY));
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

}

class Vertex implements Comparable<Vertex>
{
    public final String name;
    public Edge[] adjacencies;
    public double minDistance = Double.POSITIVE_INFINITY;
    public Vertex previous;
    public Vertex(String argName) { name = argName; }
    public String toString() { return name; }
    public int xMin,xMax,yMin,yMax;
    public void inputAreaLocation(int xMin, int yMin, int xMax, int yMax){
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
    }
    public int[] location;
    public void inputLocation (int[] location){
        this.location = location;
    }
    public boolean imInThisAreaRight (int x, int y){
        if (x>xMin && x<xMax && y>yMin && y<yMax){
            return true;
        }
        else{
            return false;
        }
    }
    public int compareTo(Vertex other)
    {
        return Double.compare(minDistance, other.minDistance);
    }

}


class Edge
{
    public final Vertex target;
    public final double weight;
    public Edge(Vertex argTarget, double argWeight)
    { target = argTarget; weight = argWeight; }
}





