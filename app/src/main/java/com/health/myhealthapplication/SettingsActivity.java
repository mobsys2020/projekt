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

/**
 * SettingsActivity is for defining the times of the day the user wants to get reminded.
 * Also you can see the user access token.
 *
 * @author Ole Hannemann
 * @author Sam Wolter
 */
public class SettingsActivity extends AppCompatActivity {
    //delaration Views
    Button btn1, btn2, btn3, btn4;
    TextView tv1, tv2, tv3, tv4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        //initialize views
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);

        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);

        //onClickListener of the "change"-buttons
        //in the morning
        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                //TimePicerDialog
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        //build the date and print on the TextView
                        String h = "0" + selectedHour;
                        String m = "0" + selectedMinute;
                        tv1.setText(h.substring(h.length() - 2) + ":" +
                                (m.substring(m.length() - 2)));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
        //at noon
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
                        String m = "0" + selectedMinute;
                        tv2.setText(h.substring(h.length() - 2) + ":" +
                                (m.substring(m.length() - 2)));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker2.setTitle("Select Time");
                mTimePicker2.show();

            }
        });
        //in the evening
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
                        String m = "0" + selectedMinute;
                        tv3.setText(h.substring(h.length() - 2) + ":" +
                                (m.substring(m.length() - 2)));
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker3.setTitle("Select Time");
                mTimePicker3.show();

            }
        });
        //before going to sleep
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
                        String m = "0" + selectedMinute;
                        tv4.setText(h.substring(h.length() - 2) + ":" +
                                (m.substring(m.length() - 2)));
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
        //safe current values in SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences("time", MODE_PRIVATE).edit();
        editor.putString("time_morgens", String.valueOf(tv1.getText()));
        editor.putString("time_mittags", String.valueOf(tv2.getText()));
        editor.putString("time_abends", String.valueOf(tv3.getText()));
        editor.putString("time_zur_nacht", String.valueOf(tv4.getText()));
        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //load and print SharedPreferences
        SharedPreferences prefs = getSharedPreferences("time", MODE_PRIVATE);
        String time_morgens = prefs.getString("time_morens", getResources().getString(R.string.default_morgens));
        tv1.setText(time_morgens);
        String time_mittags = prefs.getString("time_mittags", getResources().getString(R.string.default_mittags));
        tv2.setText(time_mittags);
        String time_abends = prefs.getString("time_abends", getResources().getString(R.string.default_abends));
        tv3.setText(time_abends);
        String time_zur_nacht = prefs.getString("time_zur_nacht", getResources().getString(R.string.default_zur_nacht));
        tv4.setText(time_zur_nacht);
    }

}
