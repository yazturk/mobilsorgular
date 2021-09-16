package com.ayazturk.mobilsorgular;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class MainActivity extends AppCompatActivity {
    DatePicker ilktarih, sontarih;
    TimePicker ilksaat, sonsaat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ilktarih = findViewById(R.id.ilktarih);
        sontarih = findViewById(R.id.sontarih);
        ilksaat = findViewById(R.id.ilksaat);
        sonsaat = findViewById(R.id.sonsaat);

        ilksaat.setIs24HourView(true);
        sonsaat.setIs24HourView(true);
    }
    public void sorgu1_gonder(View view) {
        Intent intent = new Intent(this, Sorgu1Sonuc.class);
        intent.putExtra("sorgu", "1");
        startActivity(intent);
    }
    public void sorgu2_gonder(View view) {
        Intent intent = new Intent(this, Sorgu1Sonuc.class);

        String baslangic = dateToString(ilktarih) + " " + timeToString(ilksaat);
        String bitis     = dateToString(sontarih) + " " + timeToString(sonsaat);

        intent.putExtra("sorgu", "2");
        intent.putExtra("baslangic",baslangic);
        intent.putExtra("bitis", bitis);
        startActivity(intent);
    }
    public void sorgu3_gonder(View view) {
        Intent intent = new Intent(this, Sorgu3Sonuc.class);
        startActivity(intent);
    }
    public String dateToString(DatePicker date) {
        int year = date.getYear();
        int month = date.getMonth();
        int day = date.getDayOfMonth();
        String monthString, dayString;
        
        month++;
        if (month < 10) monthString = "-0" + month;
        else monthString = "-" + month;
        if (day < 10) dayString = "-0" + day;
        else dayString = "-" + day;
        
        return year + monthString + dayString;
    }
    public String timeToString(TimePicker time) {
        int hour = time.getHour();
        int minute = time.getMinute();
        String hourString, minuteString;

        if (hour < 10) hourString = "0" + hour;
        else hourString = "" + hour;
        if (minute < 10) minuteString = ":0" + minute;
        else minuteString = ":" + minute;

        return hourString + minuteString + ":00";
    }
}