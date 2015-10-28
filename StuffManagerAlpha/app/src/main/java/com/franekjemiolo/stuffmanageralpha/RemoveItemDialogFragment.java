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
 * Created by frane_000 on 05.09.2015.
 */
public class RemoveItemDialogFragment extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the builder class for convenient dialog construction
        ContextThemeWrapper cw = new ContextThemeWrapper(getActivity(), R.style.EnableGPSDialogTheme );
        AlertDialog.Builder builder = new AlertDialog.Builder( cw );
        builder.setMessage(R.string.dialog_remove_item)
                .setPositiveButton(R.string.dialog_remove_item_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Remove the item
                        ((DetailedContainerViewActivity)getActivity()).removeThisItem();
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_remove_item_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing
                        dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
