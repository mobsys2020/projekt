package com.health.myhealthapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {
    TimePicker timePicker;
    Button btn1, btn2, btn3, btn4;
    TextView tv1, tv2, tv3, tv4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);

        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);

        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String h = "0" + selectedHour;
                        String m = "0" +selectedMinute;
                        tv1.setText(h.substring(h.length()-2) + ":" +
                                (m.substring(m.length()-2)));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker2;
                mTimePicker2 = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String h = "0" + selectedHour;
                        String m = "0" +selectedMinute;
                        tv2.setText(h.substring(h.length()-2) + ":" +
                                (m.substring(m.length()-2)));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker2.setTitle("Select Time");
                mTimePicker2.show();

            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker3;
                mTimePicker3 = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String h = "0" + selectedHour;
                        String m = "0" +selectedMinute;
                        tv3.setText(h.substring(h.length()-2) + ":" +
                                (m.substring(m.length()-2)));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker3.setTitle("Select Time");
                mTimePicker3.show();

            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker4;
                mTimePicker4 = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String h = "0" + selectedHour;
                        String m = "0" +selectedMinute;
                        tv4.setText(h.substring(h.length()-2) + ":" +
                                (m.substring(m.length()-2)));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker4.setTitle("Select Time");
                mTimePicker4.show();

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = getSharedPreferences("morgens", MODE_PRIVATE).edit();
        editor.putString("time", String.valueOf(tv1.getText()));
        editor.apply();
        SharedPreferences.Editor editor2 = getSharedPreferences("mittags", MODE_PRIVATE).edit();
        editor2.putString("time", String.valueOf(tv2.getText()));
        editor2.apply();
        SharedPreferences.Editor editor3 = getSharedPreferences("abends", MODE_PRIVATE).edit();
        editor3.putString("time", String.valueOf(tv3.getText()));
        editor3.apply();
        SharedPreferences.Editor editor4 = getSharedPreferences("zur_nacht", MODE_PRIVATE).edit();
        editor4.putString("time", String.valueOf(tv4.getText()));
        editor4.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("morgens", MODE_PRIVATE);
        String time_morgens = prefs.getString("time", "No time defined");//"No name defined" is the default value.
        tv1.setText(time_morgens);
        SharedPreferences prefs2 = getSharedPreferences("mittags", MODE_PRIVATE);
        String time_mittags = prefs2.getString("time", "No time defined");//"No name defined" is the default value.
        tv2.setText(time_mittags);
        SharedPreferences prefs3 = getSharedPreferences("abends", MODE_PRIVATE);
        String time_abends = prefs3.getString("time", "No time defined");//"No name defined" is the default value.
        tv3.setText(time_abends);
        SharedPreferences prefs4 = getSharedPreferences("zur_nacht", MODE_PRIVATE);
        String time_zur_nacht = prefs4.getString("time", "No time defined");//"No name defined" is the default value.
        tv4.setText(time_zur_nacht);
    }

}
