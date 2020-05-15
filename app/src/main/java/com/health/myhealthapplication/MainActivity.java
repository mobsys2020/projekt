package com.health.myhealthapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * ...
 * @author Sam Wolter
 * @author Ole Hannemann
 */
public class MainActivity extends AppCompatActivity {

ListView listView;
private ArrayAdapter<String> adapter =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listMain);
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
                startScanner();
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
        try {

            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "DATA_MATRIX"); // "PRODUCT_MODE for bar codes

            startActivityForResult(intent, 0);

        } catch (Exception e) {
            Log.i("Fehler", "startScanner: ");

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
