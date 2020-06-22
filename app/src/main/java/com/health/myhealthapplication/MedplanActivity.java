package com.health.myhealthapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;
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
    TextView tvarzt, tvpatient;
    Button btnNeuerPlan, btnUpdate;
    private RequestQueue requestQueue;
    MedPlan medplan;
    SharedPreferences time_prefs;
    String time_morgens;
    String time_mittags;
    String time_abends;
    String time_zur_nacht;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medplan);
        //initialize Views
        listView = (ListView) findViewById(R.id.listMain);
        btnNeuerPlan = (Button) findViewById(R.id.btnNeuerPlan);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnNeuerPlan.setOnClickListener(this);
        tvarzt = findViewById(R.id.tvArzt);
        tvpatient = findViewById(R.id.tvPatient);
        time_prefs = getSharedPreferences("time", MODE_PRIVATE);
        //local class variables for the time settings are used in this activity
        time_morgens= time_prefs.getString("time_morgens", getResources().getString(R.string.default_morgens));
        time_mittags = time_prefs.getString("time_mittags", getResources().getString(R.string.default_mittags));
        time_abends =time_prefs.getString("time_abends", getResources().getString(R.string.default_abends));
        time_zur_nacht =time_prefs.getString("time_zur_nacht", getResources().getString(R.string.default_zur_nacht));

        //add back button to navigation bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get persisted medplan if it exists
        Medplaninfo check = Medplaninfo.findById(Medplaninfo.class, (long) 1);
        if (check != null) {
            tvarzt.setText("Austellender Arzt: " + check.getDoctor());
            tvpatient.setText("Ausgestellt für: " + check.getPatient());

            List<Meds> medlist = Meds.listAll(Meds.class);
            MedicineListAdapter adapter = new MedicineListAdapter(getApplicationContext(), R.layout.adapter_view_layout, medlist);
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
    public void onResume() {
        super.onResume();
        renewTimes();
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

                    String medplan_string = result.getContents();

                    //check if the result contains xml stuff
                    if (medplan_string.contains("<MP")) {
                        //OH GOD PLEASE DON'T TAKE A CLOSER LOOK AT THIS ITS JUST A BAD WORKAROUND TO GET THE BMP MEDPLAN OBJECT TO FIT OUR OBJECT
                        //get Patientname
                        String vorname = medplan_string.substring(medplan_string.indexOf("g=") + 3, medplan_string.indexOf("\" f=\""));
                        String nachname = medplan_string.substring(medplan_string.indexOf("f=") + 3, medplan_string.indexOf("\" b=\""));
                        //get Doctorname
                        String doctor = medplan_string.substring(medplan_string.indexOf("A n=") + 5, medplan_string.indexOf("\" s=\""));

                        //replace known mednames since we dont have the db for them
                        //sadly the t specification isnt coming with hte data we got as an example so we cant take that as the name <.<

                        medplan_string = medplan_string.replace("230272", "Metoprolol succinat");
                        medplan_string = medplan_string.replace("2223945", "Ramipril");
                        medplan_string = medplan_string.replace("558736", "Insulin aspart");
                        medplan_string = medplan_string.replace("9900751", "Simvastatim");
                        medplan_string = medplan_string.replace("2239828", "Fentanyl");
                        medplan_string = medplan_string.replace("2455874", "Johanniskraut Trockenextrakt");

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

                        String meds_bmp = medstring1 + medstring2 + medstring3;
                        //time to convert the xml med string to j son and to the bmpmeds object
                        Gson g2 = new Gson();
                        bmpmeds meds = g2.fromJson(XML.toJSONObject(meds_bmp).toString(), bmpmeds.class);

                        //now its time to get the bmpmeds converted to fit our datamodel
                        ArrayList<Meds> medlist = new ArrayList<>();
                        for (bmpmed m : meds.M) {
                            //check if dosage is part of our enum class
                            String quantity_mod = "";
                            if (m.du.charAt(0) >= 'a' && m.du.charAt(0) <= 'v') {
                                quantity_mod = enum_dosierung.valueOf(m.du).get_name();
                            } else {
                                if(m.du.charAt(0) == '#'){
                                    quantity_mod = "Messlöffel";
                                }else {
                                    //add z infornt so we can get the dosage out of our enum
                                    m.du = "z" + m.du;
                                    quantity_mod = enum_dosierung.valueOf(m.du).get_name();
                                }
                            }
                            //morgens
                            Meds med_buffer;
                            if(m.m.charAt(0) > '0'){
                                med_buffer = new Meds();
                                med_buffer.setName(m.p);
                                med_buffer.time = "Morgens";
                                med_buffer.quantity=m.m + quantity_mod;
                                med_buffer.days="Täglich";
                                medlist.add(med_buffer);

                            }
                            //abends
                            if(m.v.charAt(0) > '0'){
                                med_buffer = new Meds();
                                med_buffer.setName(m.p);
                                med_buffer.time = "Abends";
                                med_buffer.quantity=m.v + quantity_mod;
                                med_buffer.days="Täglich";
                                medlist.add(med_buffer);

                            }
                            //zur nacht
                            if(m.h.charAt(0) > '0'){
                                med_buffer = new Meds();
                                med_buffer.setName(m.p);
                                med_buffer.time = "Zur Nacht";
                                med_buffer.quantity=m.h + quantity_mod;
                                med_buffer.days="Täglich";
                                medlist.add(med_buffer);

                            }
                            //mittags
                            if(m.d.charAt(0) > '0'){
                                med_buffer = new Meds();
                                med_buffer.setName(m.p);
                                med_buffer.time = "Mittags";
                                med_buffer.quantity=m.d + quantity_mod;
                                med_buffer.days="Täglich";
                                medlist.add(med_buffer);
                            }
                            //if freeform dosage days is set do other stuff yoo
                            if(!m.t.equals("")){
                                med_buffer = new Meds();
                                med_buffer.setName(m.p);
                                med_buffer.setTime("Mittags");
                                //med_buffer.setDays(m.t);
                                med_buffer.setQuantity(m.t +" "+quantity_mod);
                                medlist.add(med_buffer);
                            }

                        }
                        if (Meds.listAll(Meds.class).size()>0) {
                            for(Meds m: Meds.listAll(Meds.class)){
                                if (!m.getDays().equals("")) {
                                    cancelAlarm(m.getId());
                                }
                            }
                        }
                        //save the new meds we got from the datamatrix scan and delete the old ones
                        Meds.deleteAll(Meds.class);
                        //save new meds
                        for (Meds _med : medlist) {
                            _med.save();
                        }
                        for(Meds m: Meds.listAll(Meds.class)){
                            if (!m.getDays().equals("")) {
                                createAlarm(m.getDays(), m.getTime(), m.getId());
                            }
                        }
                        //set misc info
                        Medplaninfo check = Medplaninfo.findById(Medplaninfo.class, (long) 1);

                        if (check == null) {
                            Log.e("CHECK", "no empty object was found somethign must have went wrong(quite badly lol)");
                        } else {
                            check.setDoctor(doctor);
                            check.setMedcount(medlist.size());
                            check.setPatient(vorname+" "+nachname);
                            check.save();
                        }


                    } else {


                        Gson g = new Gson();
                        MedPlan medplan = g.fromJson(medplan_string, MedPlan.class);
                        tvarzt.setText("Austellender Arzt: " + medplan.doctor);
                        tvpatient.setText("Ausgestellt für: " + medplan.patient);

                        List<Meds> medlist = medplan.meds;
                        MedicineListAdapter adapter = new MedicineListAdapter(getApplicationContext(), R.layout.adapter_view_layout, medlist);
                        listView.setAdapter(adapter);
                        builder.setMessage("Medikationsplan erfasst");

                        //save meds from qrcode scan
                        Meds.deleteAll(Meds.class);
                        //save new meds
                        for (Meds _med : medlist) {
                            _med.save();
                        }
                        //set misc info
                        Medplaninfo check = Medplaninfo.findById(Medplaninfo.class, (long) 1);

                        if (check == null) {
                            Log.e("CHECK", "no empty object was found somethign must have went wrong(quite badly lol)");
                        } else {
                            check.setDoctor(medplan.getDoctor());
                            check.setMedcount(medplan.getMedcount());
                            check.setPatient(medplan.getPatient());
                            check.save();
                        }
                    }


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
                        restart();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                try {
                    JSONObject jsonObject = XML.toJSONObject(result.getContents());
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

    //checks the online service for a newer Medplan und replaces the current one with the newer one
    private void update_medplan(final Context context) {
        SharedPreferences pref = getSharedPreferences("user_key", MODE_PRIVATE);
        String url = "https://mobsysbackend.herokuapp.com/request.json";

        RequestQueue queue = Volley.newRequestQueue(this);

        Map<String, String> params = new HashMap<String, String>();
        params.put("uat", pref.getString("user_key", "missing"));
        JSONObject parameters = new JSONObject(params);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, parameters,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        if (Meds.listAll(Meds.class).size()>0) {
                            for(Meds m: Meds.listAll(Meds.class)){
                                if (!m.getDays().equals("")) {
                                    cancelAlarm(m.getId());
                                }
                            }
                        }
                        JSONObject json = (JSONObject) response;
                        Gson g = new Gson();
                        medplan = g.fromJson(json.toString(), MedPlan.class);
                        Log.i("JSONtoString", "onResponse: " + json.toString());
                        tvarzt.setText("Austellender Arzt: " + medplan.getDoctor());
                        tvpatient.setText("Ausgestellt für: " + medplan.getPatient());

                        List<Meds> medlist = medplan.getMeds();
                        MedicineListAdapter adapter = new MedicineListAdapter(context, R.layout.adapter_view_layout, medlist);
                        listView.setAdapter(adapter);


                        //doing some databse stuff
                        //remember record indexes start with 1
                        Medplaninfo check = Medplaninfo.findById(Medplaninfo.class, (long) 1);

                        if (check == null) {
                            Log.e("CHECK", "No empty object was found. something must have went wrong");
                        } else {
                            check.setDoctor(medplan.getDoctor());
                            check.setMedcount(medplan.getMedcount());
                            check.setPatient(medplan.getPatient());
                            //delete old meds
                            Meds.deleteAll(Meds.class);
                            //save new meds
                            for (Meds _med : medplan.meds) {
                                _med.save();
                                if (!_med.getDays().equals("")) {
                                    createAlarm(_med.getDays(), _med.getTime(), _med.getId());
                                }
                            }
                            check.save();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.i("TAG", "onErrorResponse: Something went wrong during the updateing");

                    }
                }
        );
        requestQueue.add(jsonObjReq);
    }

    //creates the alarm
    private void createAlarm(String days, String time, long id) {

        Calendar cal = Calendar.getInstance();
        switch(days){
            case "Montag":
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                break;
            case "Dienstag":
                cal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                break;
            case "Mittwoch":
                cal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                break;
            case "Donnerstag":
                cal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                break;
            case "Freitag":
                cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                break;
            case "Samstag":
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                break;
            case "Sonntag":
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                break;
            default:
                break;
        }

        switch(time){
            case "Morgens":
                time_morgens = time_prefs.getString("time_morgens", getResources().getString(R.string.default_morgens));
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time_morgens.substring(0,2)));
                cal.set(Calendar.MINUTE, Integer.parseInt(time_morgens.substring(3,5)));
                break;
            case "Mittags":
                time_mittags = time_prefs.getString("time_mittags", getResources().getString(R.string.default_mittags));
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time_mittags.substring(0,2)));
                cal.set(Calendar.MINUTE, Integer.parseInt(time_mittags.substring(3,5)));
                break;
            case "Abends":
                time_abends = time_prefs.getString("time_abends", getResources().getString(R.string.default_abends));
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time_abends.substring(0,2)));
                cal.set(Calendar.MINUTE, Integer.parseInt(time_abends.substring(3,5)));
                break;
            case "Zur Nacht":
                time_zur_nacht = time_prefs.getString("time_zur_nacht", getResources().getString(R.string.default_zur_nacht));
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time_zur_nacht.substring(0,2)));
                cal.set(Calendar.MINUTE, Integer.parseInt(time_zur_nacht.substring(3,5)));
                break;
            default:

                break;
        }

        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        //add a day or a week if the cal is in the past right now
        if(cal.before(Calendar.getInstance())){
            if(days.equals("Täglich") || days.equals("Taeglich")){
                cal.add(Calendar.DATE,1);
            } else {
                cal.add(Calendar.DATE,7);
            }

        }
        //determine the alarm intervalls
        long intervalMillis;
        if(days.equals("Täglich") || days.equals("Taeglich")){
            intervalMillis = 86400000; //daily
        } else {
            intervalMillis = 604800000; //weekly
        }
        //Intent with id (for the notification later on)
        Intent intent = new Intent(this, Alarm.class);
        intent.putExtra("med_id", id);
//PendingIntent uses the id as the request code
        PendingIntent pi = PendingIntent.getBroadcast(this, (int)id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //set repeating alarm
        AlarmManager mng = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mng.setRepeating(AlarmManager.RTC_WAKEUP,  cal.getTimeInMillis(), intervalMillis, pi);
    }

    private void cancelAlarm(long id) {
        Intent intent = new Intent(this, Alarm.class);
//cancel the pending intent, wich was specified for a Medicine (id as request code)
        PendingIntent pi = PendingIntent.getBroadcast(this, (int) id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager mng = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mng.cancel(pi);
    }

    private void startSettingsActivity() {
        //create intent for the SettingsActivity
        Intent intent = new Intent(this, SettingsActivity.class);
        /*start*/
        startActivity(intent);
    }

    /*realizes changed time settings
    cancels the affected alarms and creates new ones
     */
    private void renewTimes() {
        String time_morgens_new = time_prefs.getString("time_morgens", getResources().getString(R.string.default_morgens));
        String time_mittags_new = time_prefs.getString("time_mittags", getResources().getString(R.string.default_mittags));
        String time_abends_new = time_prefs.getString("time_abends", getResources().getString(R.string.default_abends));
        String time_zur_nacht_new = time_prefs.getString("time_zur_nacht", getResources().getString(R.string.default_morgens));
        /*comparison of class olcal time variblaes and new Preferences
        deletes alarms affected medicines and creates new alarms for them
         */

        if (!time_morgens.equals(time_morgens_new)) {
            //list all affected Meds and iterate for each
            List<Meds> meds = Meds.find(Meds.class, "time = ?", "Morgens");
            for (Meds m : meds) {
                if (!m.getDays().equals("")) {
                    cancelAlarm(m.getId());
                }
            }

            for(Meds m: meds){
                if (!m.getDays().equals("")) {
                    createAlarm(m.getDays(), m.getTime(), m.getId());
                }
            }
        }

        if (!time_mittags.equals(time_mittags_new)) {
            List<Meds> meds = Meds.find(Meds.class, "time = ?", "Mittags");
            for (Meds m : meds) {
                if (!m.getDays().equals("")) {
                    cancelAlarm(m.getId());
                }
            }

            for(Meds m: meds){
                if (!m.getDays().equals("")) {
                    createAlarm(m.getDays(), m.getTime(), m.getId());
                }
            }
        }
        if (!time_abends.equals(time_abends_new)) {
            List<Meds> meds = Meds.find(Meds.class, "time = ?", "Abends");
            for (Meds m : meds) {
                if (!m.getDays().equals("")) {
                    cancelAlarm(m.getId());
                }
            }

            for(Meds m: meds){
                if (!m.getDays().equals("")) {
                    createAlarm(m.getDays(), m.getTime(), m.getId());
                }
            }
        }
        if (!time_zur_nacht.equals(time_zur_nacht_new)) {
            List<Meds> meds = Meds.find(Meds.class, "time = ?", "Morgens");
            for (Meds m : meds) {
                if (!m.getDays().equals("")) {
                    cancelAlarm(m.getId());
                }
            }

            for(Meds m: meds){
                if (!m.getDays().equals("")) {
                    createAlarm(m.getDays(), m.getTime(), m.getId());
                }
            }
        }
    }
    private void restart() {
        Intent intent = new Intent(this, MedplanActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}

