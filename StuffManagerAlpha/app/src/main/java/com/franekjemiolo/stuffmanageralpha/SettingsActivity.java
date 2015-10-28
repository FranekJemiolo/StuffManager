package com.franekjemiolo.stuffmanageralpha;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.audiofx.BassBoost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREFERENCES_NAME = "com.franekjemiolo.stuffmanageralpha.MyPreferencesFile";

    // The switch that enabled location
    Switch autoLocationSwitch;

    // Should we take location
    Boolean takeLocation;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        autoLocationSwitch = (Switch) findViewById(R.id.autoLocationSwitch);
        loadPreferences();
        setTitle("Settings");
        autoLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Auto location is enabled
                   SettingsActivity.this.savePreferences(isChecked);
                }
                else {
                    // Auto location is disabled
                    SettingsActivity.this.savePreferences(isChecked);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoLocationSwitch = (Switch) findViewById(R.id.autoLocationSwitch);
        loadPreferences();
        setTitle("Settings");
        autoLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Auto location is enabled
                    SettingsActivity.this.savePreferences(isChecked);
                } else {
                    // Auto location is disabled
                    SettingsActivity.this.savePreferences(isChecked);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    public void savePreferences(boolean location_enabled) {
        SharedPreferences sharedPref = this.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(getString(R.string.location_enabled), location_enabled);
        editor.commit();
        Log.d("tag", "Saving preferences to " + location_enabled);
    }

    public void loadPreferences() {
        SharedPreferences sharedPref = this.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        boolean defaultValue = true;
        takeLocation = sharedPref.getBoolean(getString(R.string.location_enabled), defaultValue);
        autoLocationSwitch.setChecked(takeLocation);
    }
}