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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MedplanActivity shows the for the patient relevant information of the currently loaded "Medikationsplan".
 * You can load a Medikationsplan by scanning it, update the Medikationsplan or navigate to the Settings
 * from here.
 *
 * @author Sam Wolter
 * @author Ole Hannemann
 */
public class MedplanActivity extends AppCompatActivity implements View.OnClickListener {

    //declarations
    ListView listView;
    Button btnNeuerPlan, btnUpdate;
    private RequestQueue requestQueue;
    MedPlan medplan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medplan);
        //initialize Views
        listView = (ListView) findViewById(R.id.listMain);
        btnNeuerPlan = (Button) findViewById(R.id.btnNeuerPlan);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnNeuerPlan.setOnClickListener(this);

        //add back button to navigation bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get persisted medplan if it exists
        Medplaninfo check = Medplaninfo.findById(Medplaninfo.class, (long) 1);
        if (check != null) {
            TextView tvarzt = findViewById(R.id.tvArzt);
            TextView tvpatient = findViewById(R.id.tvPatient);
            tvarzt.setText("Austellender Arzt: " + Medplaninfo.findById(Medplaninfo.class, (long) 1).getDoctor());
            tvpatient.setText("Ausgestellt für: " + Medplaninfo.findById(Medplaninfo.class, (long) 1).getPatient());

            List<Meds> medlist = Meds.listAll(Meds.class);
            Log.e("listsizeMEDS", "0" + medlist.size());
            MedicineListAdapter adapter = new MedicineListAdapter(getApplicationContext(), R.layout.adapter_view_layout, medlist);
            listView = findViewById(R.id.listMain);
            listView.setAdapter(adapter);

        } else {
            //create empty list
            Medplaninfo needtoinit = new Medplaninfo();
            needtoinit.save();
        }

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //checks the online service for a newer Medplan
                update_medplan(getApplicationContext());
            }
        });
    }

    /*called when user pressed back button, if the parent class of the certain activity is MedplanActivity
     * (manifest)  */
    @Override
    public void onBackPressed() {
        //intent that references this class
        Intent intent = new Intent(this, MedplanActivity.class);
        startActivity(intent);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //checks the online service for a newer Medplan und replaces the current one with the newer one
    public void update_medplan(final Context context) {
        SharedPreferences pref = getSharedPreferences("user_key", MODE_PRIVATE);
        String url = "https://mobsysbackend.herokuapp.com/request.json";
        //TODO queue verwenden oder löschen
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
                        medplan = g.fromJson(json.toString(), MedPlan.class);
                        Log.i("JSONtoString", "onResponse: " + json.toString());
                        tvarzt.setText("Austellender Arzt: " + medplan.getDoctor());
                        tvpatient.setText("Ausgestellt für: " + medplan.getPatient());

                        List<Meds> medlist = medplan.meds;
                        MedicineListAdapter adapter = new MedicineListAdapter(context, R.layout.adapter_view_layout, medlist);
                        listView.setAdapter(adapter);
                        for(int i=0; i<medlist.size(); i++)
                        {
                            //createAlarm(Meds.findById(Meds.class, (long) i));
                        }
                        //doing some databse stuff
                        //remember record indexes start with 1
                        Medplaninfo check = Medplaninfo.findById(Medplaninfo.class, (long) 1);

                        if (check == null) {
                            Log.e("SATAN", "no empty object was found somethign must have went wrong(quite badly lol)");
                        } else {
                            check.setDoctor(medplan.getDoctor());
                            check.setMedcount(medplan.getMedcount());
                            check.setPatient(medplan.getPatient());
                            //delete old meds
                            Meds.deleteAll(Meds.class);
                            //save new meds
                            for (Meds _med : medplan.meds) {
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
        //create intent for the SettingsActivity
        Intent intent = new Intent(this, SettingsActivity.class);
        /*start*/
        startActivity(intent);
    }

    //creates the alarm
    //TODO
    public void createAlarm() {
//Manager abrufen
        Log.i("Alarm", "createAlarm: ");
        AlarmManager mng = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//Action zur Ausführung festlegen
        Intent intent = new Intent(this, Alarm.class);
//PendingIntent erstellen
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //List<Meds> medlist = medplan.meds;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 1);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Log.i("Millesekunden", "createAlarm: " +cal.getTimeInMillis());
        //SharedPrefs zur Umwandlung in Uhrzeit
        SharedPreferences prefs = getSharedPreferences("time", MODE_PRIVATE);
        String time_morgens = prefs.getString("time_morgens", "No time defined");
        String time_mittags = prefs.getString("time_mittags", "No time defined");
        String time_abends = prefs.getString("time_abends", "No time defined");
        String time_zur_nacht = prefs.getString("time_zur_nacht", "No time defined");
        System.currentTimeMillis();
        long waitingInMillis = cal.getTimeInMillis() - System.currentTimeMillis();;
        mng.setRepeating(AlarmManager.RTC_WAKEUP,  waitingInMillis, 86400000, pi);
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
                    if (medplan_string.contains("<MP")) {
                        //get Patientname
                        String vorname = medplan_string.substring(medplan_string.indexOf("g=") + 3, medplan_string.indexOf("\" f=\""));
                        String nachname = medplan_string.substring(medplan_string.indexOf("f=") + 3, medplan_string.indexOf("\" b=\""));
                        //get Doctorname
                        String doctor = medplan_string.substring(medplan_string.indexOf("A n=") + 5, medplan_string.indexOf("\" s=\""));
                        Log.e("test123", vorname + " " + nachname + " - " + doctor);


                        //replace mednames since we dont have the db for them

                        medplan_string = medplan_string.replace("230272", "Metoprolol succinat");
                        medplan_string = medplan_string.replace("2223945", "Ramipril");
                        medplan_string = medplan_string.replace("558736", "Insulin aspart");
                        medplan_string = medplan_string.replace("9900751", "Simvastatim");
                        medplan_string = medplan_string.replace("2239828", "Fentanyl");
                        medplan_string = medplan_string.replace("2455874", "Johanniskrat Trockenextrakt");

                        //time to get all meds
                        StringBuffer workingcopy = new StringBuffer(medplan_string);
                        workingcopy.replace(0, medplan_string.indexOf("<S") - 1, "");
                        String medstring1 = workingcopy.substring(workingcopy.indexOf("<S>"), workingcopy.indexOf("</S>") + 4);
                        //remove first medstring
                        workingcopy.replace(workingcopy.indexOf("<S>"), workingcopy.indexOf("</S>") + 4, "");
                        //remove first medstring <S> tags
                        medstring1 = medstring1.replace("<S>", "");
                        medstring1 = medstring1.replace("</S>", "");

                        //get second medstring
                        String medstring2 = workingcopy.toString().substring(workingcopy.indexOf("<S"), workingcopy.indexOf("</S>") + 4);
                        //remove second medstring
                        workingcopy = workingcopy.replace(workingcopy.indexOf("<S"), workingcopy.indexOf("</S>") + 4, " ");
                        medstring2 = medstring2.replace("<S t=\"zu besonderen Zeiten anzuwendende Medikamente\">", " ");
                        medstring2 = medstring2.replace("</S>", " ");

                        //get third medstring
                        String medstring3 = workingcopy.toString().substring(workingcopy.indexOf("\">"), workingcopy.indexOf("</S>") + 4);
                        //remove second medstring
                        workingcopy = workingcopy.replace(workingcopy.indexOf("<S"), workingcopy.indexOf("</S>") + 4, " ");
                        medstring3 = medstring3.replace("\">", " ");
                        medstring3 = medstring3.replace("</S>", " ");

                        Log.e("SATAN", "\n" + medstring1 + "\n" + medstring2 + "\n" + medstring3);

                        String meds_bmp = medstring1 + medstring2 + medstring3;

                        Gson g2 = new Gson();


                        bmpmeds meds = g2.fromJson(XML.toJSONObject(meds_bmp).toString(), bmpmeds.class);
                        //(JsonObject) XML.toJSONObject(meds_bmp);
                        Log.e("Satan", "" + meds.M.size());

                        for (bmpmed m : meds.M) {
                            //check if dosage is part of our enum class
                            if (m.du.charAt(0) >= 'a' && m.du.charAt(0) <= 'v') {
                            } else {
                                //add z infornt so we can get the dosage out of our enum
                                m.du = "z" + m.du;
                            }
                            String quantity_mod = enum_dosierung.valueOf(m.du).get_name();
                            Log.e("Satan", quantity_mod);
                        }


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
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}

