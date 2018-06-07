package com.example.jessi.pictovent;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class EventDialog extends DialogFragment {
    String eventDate;
    public static EventDialog newInstance(String event) {
        EventDialog dialog = new EventDialog();
        Bundle args = new Bundle();
        args.putString("eventDate", event);
        dialog.setArguments(args);
        return dialog;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        eventDate = "";
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            eventDate = getArguments().getString("event");
        }

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Event created on:" + " " + eventDate)//TODO: Figure out why string not coming in
                .setTitle(R.string.app_name)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton(R.string.dialog_retry, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
