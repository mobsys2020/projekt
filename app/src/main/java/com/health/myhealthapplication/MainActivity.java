package com.health.myhealthapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * ...
 * @author Sam Wolter
 * @author Ole Hannemann
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

ListView listView;
Button btnNeuerPlan;
private ArrayAdapter<String> adapter =null;
ArrayList<Medicine> medsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listMain);
        btnNeuerPlan= (Button) findViewById(R.id.btnNeuerPlan);
        btnNeuerPlan.setOnClickListener(this);
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        createAlarm();
        //getItems();
        //Create the objects
        Medicine john = new Medicine("John","12-20-1998","1");
        Medicine steve = new Medicine("Steve","08-03-1987","2");
        Medicine stacy = new Medicine("Stacy","11-15-2000","3");
        Medicine ashley = new Medicine("Ashley","07-02-1999","4");
        Medicine matt = new Medicine("Matt","03-29-2001","5");
        Medicine matt2 = new Medicine("Matt2","03-29-2001","6");
        Medicine matt3 = new Medicine("Matt3","03-29-2001","7");
        Medicine matt4 = new Medicine("Matt4","03-29-2001","8");
        Medicine matt5 = new Medicine("Matt5","03-29-2001","9");
        Medicine matt6 = new Medicine("Matt6","03-29-2001","10");



        //Add the objects to an ArrayList
        medsList.add(john);
        medsList.add(steve);
        medsList.add(stacy);
        medsList.add(ashley);
        medsList.add(matt);
        medsList.add(matt2);
        medsList.add(matt3);
        medsList.add(matt4);
        medsList.add(matt5);
        medsList.add(matt6);

        MedicineListAdapter adapter = new MedicineListAdapter(this, R.layout.adapter_view_layout, medsList);
        listView.setAdapter(adapter);
    }

    /*wird beim Klick auf den Pfeil in der Action Bar in der jeweiligen Activity aufgerufen
     * Andere Activities, die diese Methode nutzen, müssen im Manifest die MainActivity als Parent
     * Activity angegeben haben */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        /*Starten*/
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Medicine john = new Medicine("John","12-20-1998","1");
        Medicine steve = new Medicine("Steve","08-03-1987","2");
        Medicine stacy = new Medicine("Stacy","11-15-2000","3");
        Medicine ashley = new Medicine("Ashley","07-02-1999","4");
        Medicine matt = new Medicine("Matt","03-29-2001","5");
        Medicine matt2 = new Medicine("Matt2","03-29-2001","6");
        Medicine matt3 = new Medicine("Matt3","03-29-2001","7");
        Medicine matt4 = new Medicine("Matt4","03-29-2001","8");
        Medicine matt5 = new Medicine("Matt5","03-29-2001","9");
        Medicine matt6 = new Medicine("Matt6","03-29-2001","10");

        //Add the objects to an ArrayList
        medsList.add(john);
        medsList.add(steve);
        medsList.add(stacy);
        medsList.add(ashley);
        medsList.add(matt);
        medsList.add(matt2);
        medsList.add(matt3);
        medsList.add(matt4);
        medsList.add(matt5);
        medsList.add(matt6);

        MedicineListAdapter adapter = new MedicineListAdapter(this, R.layout.adapter_view_layout, medsList);
        listView.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("morgens", MODE_PRIVATE);
        String name = prefs.getString("time", "No name defined");//"No name defined" is the default value.
        SharedPreferences prefs2 = getSharedPreferences("mittags", MODE_PRIVATE);
        String name2 = prefs.getString("time", "No name defined");//"No name defined" is the default value.
        SharedPreferences prefs3 = getSharedPreferences("abends", MODE_PRIVATE);
        String name3 = prefs.getString("time", "No name defined");//"No name defined" is the default value.
        SharedPreferences prefs4 = getSharedPreferences("zur_nacht", MODE_PRIVATE);
        String name4 = prefs.getString("time", "No name defined");//"No name defined" is the default value.
    }

    private void createMedicinesObjects(String name, String time, String amount) {
        //TODO: wenn "morgens", "mittags" etc. nicht 0 dann
        medsList.add(new Medicine(name, time, amount));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.changeSettings:
                startSettingsActivity();
                return true;
            case R.id.neuer_Medplan:
                //Über Action bar zum Scanner navigieren. Alternative
                //startScanner();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startSettingsActivity() {
        /*Erstellen eines Intents für die SettingsActivity*/
        Intent intent = new Intent(this, SettingsActivity.class);
        /*Starten*/
        startActivity(intent);
    }
    public void createAlarm() {
//Manager abrufen
        Log.i("TAG", "createAlarm: ");
        AlarmManager mng = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//Action zur Ausführung festlegen
        Intent intent = new Intent(this, Alarm.class);
//PendingIntent erstellen
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
//Alarm setzen
        Calendar cal=Calendar.getInstance();
        /*TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        cal.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
        cal.set(Calendar.MINUTE, timePicker.getCurrentMinute());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Log.i("TAG", "createAlarm: " +cal.getTimeInMillis());*/
        long currentT = cal.getTimeInMillis() +10000;
        Log.i("??", "createAlarm: " + currentT);
        //mng.setRepeating(AlarmManager.RTC_WAKEUP,  currentT, 86400000, pi);
    }

    private void startScanner() {
        scanCode();
    }

    @Override
    public void onClick(View view) {
        scanCode();
    }

    private void scanCode() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setCaptureActivity(CaptureAct.class);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.DATA_MATRIX);
        intentIntegrator.setPrompt("Scanning Code");
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Scanning result");
                try {
                    JSONObject jsonObject = XML.toJSONObject(result.getContents());
                    builder.setMessage(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                builder.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        scanCode();
                    }
                }).setNegativeButton("finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                try {
                    JSONObject jsonObject = XML.toJSONObject(result.getContents());
                    Log.i("eigener Tag", "Medikationsplan in JSON: " + jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "No Result", Toast.LENGTH_SHORT).show();
            }
        } else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Realisierung der Textgröße der List View Items
    /*public void adjustTextSize() {
        adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Item aus der List View erhalten
                View view = super.getView(position, convertView, parent);

                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                //Schriftgöße auf Wert aus den Shared Preferences (Settings Activity) setzen
                SharedPreferences prefs = getSharedPreferences(getString(R.string.txtSize_preference), MODE_PRIVATE);
                String size = prefs.getString(getString(R.string.txtSize_preference), "25");
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.parseInt(size));

                return view;
            }
        };
        listView.setAdapter(adapter);
    }*/
}
