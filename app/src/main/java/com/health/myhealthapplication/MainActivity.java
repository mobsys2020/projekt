package com.health.myhealthapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.Random;

/**
 * The MainActivity identifies the user as a registered patient by using a randomly generated
 * user access token (uat). From here you can start and switch to the MedplanActivity
 *
 * @author Ole Hannemann
 * @author Sam Wolter
 */
public class MainActivity extends AppCompatActivity {

    //all digits possible to generate the uat
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref = getSharedPreferences("user_key", MODE_PRIVATE);
        SharedPreferences.Editor ed = pref.edit();
        //if uat doesnt exist create one
        if (pref.getString("user_key", null) == null) {
            StringBuilder sb = new StringBuilder();
            //small algorithm
            Random rd = new Random();
            for (int it = 0; it < 20; it++) {
                sb.append(ALPHA_NUMERIC_STRING.charAt((int) (rd.nextFloat() * ALPHA_NUMERIC_STRING.length())));
            }
            ed.putString("user_key", sb.toString());

            ed.commit();
        }
        Log.i("UAT", "onCreate: " + pref.getString("user_key", ""));

        EditText uat = findViewById(R.id.uat);
        uat.setText(pref.getString("user_key", ""));


    }

    //called by the onClickListener of the button (see layout)
    public void to_medplan(View v) {
        Intent medplanintent = new Intent(this, MedplanActivity.class);
        //medplanintent.putExtra("new","true");
        startActivity(medplanintent);
    }
}
