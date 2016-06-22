package com.example.udit1806.stormy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by Udit1806 on 20/06/16.
 */
public class AlertDialogFragment extends DialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle(R.string.error_title)
                    .setMessage(R.string.error_Title)
                    .setPositiveButton(R.string.error_ok_button,null);

                AlertDialog dialog = builder.create();
                     return dialog;
    }
}
