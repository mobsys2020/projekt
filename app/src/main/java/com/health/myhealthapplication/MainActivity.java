package com.health.myhealthapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

/**
 * ...
 * @author Sam Wolter
 * @author Ole Hannemann
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

ListView listView;
Button btnNeuerPlan;
private ArrayAdapter<String> adapter =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listMain);
        btnNeuerPlan= (Button) findViewById(R.id.btnNeuerPlan);
        btnNeuerPlan.setOnClickListener(this);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        getItems();
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

    public void getItems() {
        String cars = "Bisoprolol";
        adapter.add(cars);
        //Übergabe des Adapters an die List View
        listView.setAdapter(adapter);
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
