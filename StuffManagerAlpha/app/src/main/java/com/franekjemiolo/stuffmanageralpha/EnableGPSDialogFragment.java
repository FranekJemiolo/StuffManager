package com.franekjemiolo.stuffmanageralpha;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ContextThemeWrapper;

/**
 * Created by frane_000 on 04.09.2015.
 */
public class EnableGPSDialogFragment extends DialogFragment {

    public static final String PREFERENCES_NAME = "com.franekjemiolo.stuffmanageralpha.MyPreferencesFile";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the builder class for convenient dialog construction
        ContextThemeWrapper cw = new ContextThemeWrapper(getActivity(), R.style.EnableGPSDialogTheme );
        AlertDialog.Builder builder = new AlertDialog.Builder( cw );
        builder.setMessage(R.string.dialog_enable_gps)
                .setPositiveButton(R.string.dialog_go_to_settings, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Open settings
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        getActivity().startActivity(intent);
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_disable_gps, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Don't take the location, save the preference
                        SharedPreferences sharedPref = getActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean(getString(R.string.location_enabled), false);
                        editor.commit();
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
