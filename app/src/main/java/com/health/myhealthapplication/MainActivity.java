package com.health.myhealthapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref = getSharedPreferences("user_key", MODE_PRIVATE);
        SharedPreferences.Editor ed = pref.edit();
        if (pref.getString("user_key",null) == null){
            StringBuilder sb = new StringBuilder();
            Random rd = new Random();
            for(int it = 0; it<20; it++){
                sb.append(ALPHA_NUMERIC_STRING.charAt((int)(rd.nextFloat()*ALPHA_NUMERIC_STRING.length())));
            }
            ed.putString("user_key",sb.toString());

            ed.commit();
        }
        Log.i("UAT", "onCreate: " + pref.getString("user_key",""));

        /*EditText uat = findViewById(R.id.uat);
        uat.setText(pref.getString("user_key",""));*/


    }

    public void to_medplan(View v){
        Intent medplanintent = new Intent(this, MedplanActivity.class);
        //medplanintent.putExtra("new","true");
        startActivity(medplanintent);
    }
}
