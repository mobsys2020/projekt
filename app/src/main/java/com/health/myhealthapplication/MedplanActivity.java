package com.health.myhealthapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.xmlpull.v1.XmlSerializer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * ...
 * @author Sam Wolter
 * @author Ole Hannemann
 */
public class MedplanActivity extends AppCompatActivity implements View.OnClickListener{

ListView listView;
Button btnNeuerPlan, btnUpdate;
private ArrayAdapter<String> adapter =null;
private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medplan);
        listView = (ListView) findViewById(R.id.listMain);
        btnNeuerPlan= (Button) findViewById(R.id.btnNeuerPlan);
        btnUpdate =(Button) findViewById(R.id.btnUpdate);
        btnNeuerPlan.setOnClickListener(this);

        //addd back button to navigation bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get persisted medplan if it exists
        Medplaninfo check = Medplaninfo.findById(Medplaninfo.class,(long)1);
        if(check != null){
            TextView tvarzt = findViewById(R.id.tvArzt);
            TextView tvpatient = findViewById(R.id.tvPatient);
            tvarzt.setText("Austellender Arzt: " + Medplaninfo.findById(Medplaninfo.class,(long)1).getDoctor());
            tvpatient.setText("Ausgestellt für: " + Medplaninfo.findById(Medplaninfo.class,(long)1).getPatient());

            List<Meds> medlist = Meds.listAll(Meds.class);
            Log.e("listsizeMEDS","0"+medlist.size());
            MedicineListAdapter adapter = new MedicineListAdapter(getApplicationContext(), R.layout.adapter_view_layout, medlist);
            //listView.setAdapter(adapter);
            ListView lv = findViewById(R.id.listMain);
            lv.setAdapter(adapter);

        } else{
            //create empty list
            Medplaninfo needtoinit = new Medplaninfo();
            needtoinit.save();
        }

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        //createAlarm();
        //getItems();
        //Create the objects
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                update_medplan(getApplicationContext());
            }
        });
    }

    /*wird beim Klick auf den Pfeil in der Action Bar in der jeweiligen Activity aufgerufen
     * Andere Activities, die diese Methode nutzen, müssen im Manifest die MainActivity als Parent
     * Activity angegeben haben */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MedplanActivity.class);
        /*Starten*/
        startActivity(intent);
    }

    /*
    TextView tvarzt = findViewById(R.id.textViewArzt);
                        TextView tvpatient = findViewById(R.id.textViewPatient);
                        JSONObject json = (JSONObject) response;
                        Gson g = new Gson();
                        MedPlan medplan = g.fromJson(json.toString(),MedPlan.class);
                        tvarzt.setText("Austellender Arzt: " + medplan.doctor);
                        tvpatient.setText("Ausgestellt für: " + medplan.patient);

                        ListView lv = findViewById(R.id._lv);
                        ArrayList<Meds> medlist = medplan.meds;
                        MedsAdapter adapter = new MedsAdapter(getApplicationContext(),medlist,20);
                        lv.setAdapter(adapter);
     */
    @Override
    protected void onResume() {
        super.onResume();

        /*MedicineListAdapter adapter = new MedicineListAdapter(this, R.layout.adapter_view_layout, medsList);
        listView.setAdapter(adapter);*/

        SharedPreferences prefs = getSharedPreferences("time", MODE_PRIVATE);
        String time_morgens = prefs.getString("time_morgens", "No time defined");
        String time_mittags = prefs.getString("time_mittags", "No time defined");
        String time_abends = prefs.getString("time_abends", "No time defined");
        String time_zur_nacht = prefs.getString("time_zur_nacht", "No time defined");
    }

    /*private void createMedicinesObjects(String name, String time, String amount) {
        //TODO: wenn "morgens", "mittags" etc. nicht 0 dann
        medsList.add(new Medicine(name, time, amount));
    }*/


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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void update_medplan(final Context context) {
        SharedPreferences pref = getSharedPreferences("user_key", MODE_PRIVATE);
        String url = "https://mobsysbackend.herokuapp.com/request.json";
        //String url = "http://192.168.0.5/request/2.json";
        RequestQueue queue = Volley.newRequestQueue(this);

        Map<String, String> params = new HashMap<String, String>();
        params.put("uat", pref.getString("user_key", "missing"));
        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, parameters,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        TextView tvarzt = findViewById(R.id.tvArzt);
                        TextView tvpatient = findViewById(R.id.tvPatient);
                        JSONObject json = (JSONObject) response;
                        Gson g = new Gson();
                        MedPlan medplan = g.fromJson(json.toString(), MedPlan.class);
                        Log.i("JSONtoString", "onResponse: " + json.toString());
                        tvarzt.setText("Austellender Arzt: " + medplan.getDoctor());
                        tvpatient.setText("Ausgestellt für: " + medplan.getPatient());

                        List<Meds> medlist = medplan.meds;
                        MedicineListAdapter adapter = new MedicineListAdapter(context, R.layout.adapter_view_layout, medlist);
                        listView.setAdapter(adapter);

                        //doing some databse stuff
                        //remember record indexes start with 1
                        Medplaninfo check =Medplaninfo.findById(Medplaninfo.class,(long)1);

                        if(check == null){
                            Log.e("SATAN","no empty object was found somethign must have went wrong(quite badly lol)");
                        } else {
                            check.setDoctor(medplan.getDoctor());
                            check.setMedcount(medplan.getMedcount());
                            check.setPatient(medplan.getPatient());
                            //delete old meds
                            Meds.deleteAll(Meds.class);
                            //save new meds
                            for(Meds _med: medplan.meds){
                                _med.save();
                            }
                            check.save();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.i("TAG", "onErrorResponse: Something went wrong when updateing <.<");

                    }
                }
        );
        requestQueue.add(jsonObjReq);
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
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
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
                    TextView tvarzt = findViewById(R.id.tvArzt);
                    TextView tvpatient = findViewById(R.id.tvPatient);
                    Log.i("als String", "onActivityResult: " + result.getContents().toString());
                    //JSONObject jsonObject = XML.toJSONObject(result.getContents());
                    String medplan_string = result.getContents();

                    //check if the result contains xml stuff
                    if(medplan_string.contains("<MP")){
                        //get Patientname
                        String vorname = medplan_string.substring(medplan_string.indexOf("g=")+3,medplan_string.indexOf("\" f=\""));
                        String nachname = medplan_string.substring(medplan_string.indexOf("f=")+3,medplan_string.indexOf("\" b=\""));
                        //get Doctorname
                        String doctor = medplan_string.substring(medplan_string.indexOf("A n=")+5,medplan_string.indexOf("\" s=\""));
                        Log.e("test123", vorname + " " +nachname + " - " + doctor);


                        //replace mednames since we dont have the db for them

                        medplan_string = medplan_string.replace("230272","Metoprolol succinat");
                        medplan_string = medplan_string.replace("2223945","Ramipril");
                        medplan_string = medplan_string.replace("558736","Insulin aspart");
                        medplan_string = medplan_string.replace("9900751","Simvastatim");
                        medplan_string = medplan_string.replace("2239828","Fentanyl");
                        medplan_string = medplan_string.replace("2455874","Johanniskrat Trockenextrakt");

                        //time to get all meds
                        StringBuffer workingcopy = new StringBuffer(medplan_string);
                        workingcopy.replace(0,medplan_string.indexOf("<S")-1,"");
                        String medstring1 = workingcopy.substring(workingcopy.indexOf("<S>"),workingcopy.indexOf("</S>")+4);
                        //remove first medstring
                        workingcopy.replace(workingcopy.indexOf("<S>"),workingcopy.indexOf("</S>")+4,"");
                        //remove first medstring <S> tags
                        medstring1 = medstring1.replace("<S>","");
                        medstring1 = medstring1.replace("</S>","");

                        //get second medstring
                        String medstring2 = workingcopy.toString().substring(workingcopy.indexOf("<S"),workingcopy.indexOf("</S>")+4);
                        //remove second medstring
                        workingcopy = workingcopy.replace(workingcopy.indexOf("<S"),workingcopy.indexOf("</S>")+4," ");
                        medstring2 = medstring2.replace("<S t=\"zu besonderen Zeiten anzuwendende Medikamente\">"," ");
                        medstring2 = medstring2.replace("</S>"," ");

                        //get third medstring
                        String medstring3 = workingcopy.toString().substring(workingcopy.indexOf("\">"),workingcopy.indexOf("</S>")+4);
                        //remove second medstring
                        workingcopy = workingcopy.replace(workingcopy.indexOf("<S"),workingcopy.indexOf("</S>")+4," ");
                        medstring3 = medstring3.replace("\">"," ");
                        medstring3 = medstring3.replace("</S>"," ");

                        Log.e("SATAN","\n"+medstring1+"\n"+medstring2+"\n"+medstring3);

                        String meds_bmp = medstring1+medstring2+medstring3;

                        Gson g2 = new Gson();


                        bmpmeds meds =  g2.fromJson(XML.toJSONObject(meds_bmp).toString(),bmpmeds.class);
                                //(JsonObject) XML.toJSONObject(meds_bmp);
                        Log.e("Satan", ""+meds.M.size());



                    }


                    Gson g = new Gson();
                    MedPlan medplan = g.fromJson(medplan_string, MedPlan.class);
                    Log.i("Gson to String", "onActivityResult: " + g.toString());
                    tvarzt.setText("Austellender Arzt: " + medplan.doctor);
                    tvpatient.setText("Ausgestellt für: " + medplan.patient);
                    //Log.i("TAG", "onActivityResult: " + jsonObject.toString());
                    List<Meds> medlist = medplan.meds;
                    MedicineListAdapter adapter = new MedicineListAdapter(getApplicationContext(), R.layout.adapter_view_layout, medlist);
                    listView.setAdapter(adapter);
                    builder.setMessage("Medikationsplan erfasst");

                    //TODO persistency


                } catch (Exception e) {
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
                        //finish();
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

