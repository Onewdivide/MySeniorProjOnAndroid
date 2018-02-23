package com.example.onewdivide.myapplication1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.aigestudio.wheelpicker.WheelPicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class selectStartLocationActivity extends AppCompatActivity implements View.OnClickListener, WheelPicker.OnItemSelectedListener {

    public List<String> Allplace = new ArrayList<String>();
    public List<Integer> allPlace2SendX = new ArrayList<Integer>();
    public List<Integer> allPlace2SendY = new ArrayList<Integer>();
    public int thatPosition = 0;
    public Button next;
    private WheelPicker mainwheel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_start_location);

        Allplace.add("ทางเข้าหนึ่ง");
        Allplace.add("บันไดหนึ่ง");
        Allplace.add("ห้องน้ำชายหนึ่ง");
        Allplace.add("ห้องน้ำหญิงหนึ่ง");
        Allplace.add("ห้องสมุด");
        Allplace.add("ห้องดีเอสเอส");
        Allplace.add("ห้องเอที");
        Allplace.add("ทางเข้าสอง");
        Allplace.add("ประชาสัมพัน");
        Allplace.add("ห้องหนึ่งศูนย์สอง");
        Allplace.add("บันไดสอง");
        Allplace.add("ลิป");
        Allplace.add("ห้องหนึ่งศูนย์สี่");
        Allplace.add("ห้องหนึ่งศูนย์ห้า");
        Allplace.add("ห้องกิดจะกาน");
        Allplace.add("ห้องหนึ่งศูนย์เจ็ด");
        Allplace.add("ห้องหนึ่งศูนย์แปด");
        Allplace.add("ห้องหนึ่งหนึ่งศูนย์");
        Allplace.add("ห้องน้ำชายสอง");
        Allplace.add("ห้องน้ำหญิงสอง");
        Allplace.add("บันไดสาม");
        Allplace.add("ร้านถ่ายเอกสาร");
        Allplace.add("ห้องหนึ่งหนึ่งห้า");
        Allplace.add("ห้องหนึ่งหนึ่งหก");
        Allplace.add("ห้องหนึ่งหนึ่งแปด");

        allPlace2SendX.add(234);
        allPlace2SendY.add(53);
        allPlace2SendX.add(239);
        allPlace2SendY.add(70);
        allPlace2SendX.add(278);
        allPlace2SendY.add(77);
        allPlace2SendX.add(272);
        allPlace2SendY.add(71);
        allPlace2SendX.add(230);
        allPlace2SendY.add(83);
        allPlace2SendX.add(240);
        allPlace2SendY.add(103);
        allPlace2SendX.add(262);
        allPlace2SendY.add(158);
        allPlace2SendX.add(281);
        allPlace2SendY.add(171);
        allPlace2SendX.add(273);
        allPlace2SendY.add(183);
        allPlace2SendX.add(251);
        allPlace2SendY.add(184);
        allPlace2SendX.add(227);
        allPlace2SendY.add(184);
        allPlace2SendX.add(220);
        allPlace2SendY.add(175);
        allPlace2SendX.add(203);
        allPlace2SendY.add(175);
        allPlace2SendX.add(188);
        allPlace2SendY.add(168);
        allPlace2SendX.add(180);
        allPlace2SendY.add(194);
        allPlace2SendX.add(153);
        allPlace2SendY.add(168);
        allPlace2SendX.add(127);
        allPlace2SendY.add(168);
        allPlace2SendX.add(122);
        allPlace2SendY.add(168);
        allPlace2SendX.add(95);
        allPlace2SendY.add(202);
        allPlace2SendX.add(89);
        allPlace2SendY.add(200);
        allPlace2SendX.add(88);
        allPlace2SendY.add(168);
        allPlace2SendX.add(78);
        allPlace2SendY.add(163);
        allPlace2SendX.add(114);
        allPlace2SendY.add(134);
        allPlace2SendX.add(114);
        allPlace2SendY.add(129);
        allPlace2SendX.add(120);
        allPlace2SendY.add(106);

        mainwheel = (WheelPicker) findViewById(R.id.mainwheel);
        mainwheel.setData(Allplace);
        mainwheel.setOnItemSelectedListener(this);

        next = (Button) findViewById(R.id.submitBtn);
        next.setOnClickListener(this);

    }

    @Override
    public void onItemSelected(WheelPicker picker, Object data, int position) {

        MyTTS.getInstance(selectStartLocationActivity.this)
                .setEngine("com.google.android.tts")
                .setLocale(new Locale("th")).speak("");

        switch (picker.getId()) {
            case R.id.mainwheel:
                thatPosition = position;
                MyTTS.getInstance(selectStartLocationActivity.this).speak(Allplace.get(position));
//                Log.e("mainwheels",Allplace.get(position));
//                Log.e("putExtra",allPlace2Send.get(position));
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Intent it = new Intent(getApplicationContext(),NavigationActivity.class);
        it.putExtra("startX",allPlace2SendX.get(thatPosition));
        it.putExtra("startY",allPlace2SendY.get(thatPosition));
        startActivity(it);
        finish();



    }

}
