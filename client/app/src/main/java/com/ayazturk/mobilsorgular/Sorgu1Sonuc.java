package com.ayazturk.mobilsorgular;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.widget.EditText;
import android.widget.TextView;

import java.net.*;
import java.io.*;

public class Sorgu1Sonuc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorgu1_sonuc);
        Intent intent = getIntent();
        TextView baslik = findViewById(R.id.sonuc_baslik);
        TextView sonuc = findViewById(R.id.sonuc_metin);

        Socket myClient;
        PrintWriter out;
        BufferedReader in;
        String tarih, mesafe, kalkis, varis;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            myClient = new Socket("34.207.127.179", 1163);
            out = new PrintWriter(myClient.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(myClient.getInputStream()));

            if (intent.getStringExtra("sorgu").equals("1")) {
                out.println("sorgu1");
                for(int i=1; i<=5; i++){
                    mesafe = in.readLine();
                    tarih = in.readLine();
                    sonuc.append(Html.fromHtml("<b>Tarih:</b> " + tarih));
                    sonuc.append(Html.fromHtml("&nbsp;<b>Mesafe:</b> " + mesafe + "<br>"));
                }

            } else {
                String baslangic = intent.getStringExtra("baslangic");
                String bitis = intent.getStringExtra("bitis");
                out.println("sorgu2" + " " + baslangic + " " + bitis);
                baslik.setText("Sorgu 2 -Sonuçlar");
                sonuc.setText("Başlangıç zamanı: " + baslangic + "\nBitiş zamanı: " + bitis + "\n\n");
                for (int i=1; i<=5; i++) {
                    mesafe = in.readLine();
                    tarih = in.readLine();
                    tarih += " " + in.readLine();
                    kalkis = in.readLine();
                    kalkis += " - " + in.readLine();
                    varis = in.readLine();
                    varis += " - " + in.readLine();

                    sonuc.append(Html.fromHtml("<b>Tarih:</b> " + tarih));
                    sonuc.append(Html.fromHtml("&nbsp;<b>Mesafe:</b> " + mesafe + "<br>"));
                    sonuc.append(Html.fromHtml("<b>Kalkış:</b> " + kalkis + "<br>"));
                    sonuc.append(Html.fromHtml("<b>Varış:</b> " + varis + "<br><br>"));

                }
            }

            in.close();
            out.close();
            myClient.close();
        }
        catch (Exception e){
            sonuc.setText(e.toString());
        }


    }


}