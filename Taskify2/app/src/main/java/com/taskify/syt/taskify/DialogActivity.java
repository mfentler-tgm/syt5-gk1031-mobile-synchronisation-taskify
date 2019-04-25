package com.taskify.syt.taskify;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DialogActivity extends DialogFragment {

    public EditText taskTitle;
    public Spinner spinner;


    public DialogActivity() {

    }

    public static DialogActivity newInstance() {
        DialogActivity da = new DialogActivity();
        return da;
    }
    /**
     * Removed dublicate method
     * Method is in Tasks.java
    public void populateData(Task t) throws ParseException {
        //tDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(getCurrentUserID() != null) {
            db.collection("user").document(getCurrentUserID()).collection("tasks").add(t);
        }
    }
     */

    public String getCurrentUserID(){
        //Get userID of current user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return mAuth.getUid();
    }

    public Date getCurrentDate() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String newdateString = dateFormat.format(date);
        Date newdate = dateFormat.parse(newdateString);
        return newdate;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_activity, null);
        View view2 = inflater.inflate(R.layout.activity_tasks,null);
        taskTitle = view.findViewById(R.id.taskTitle);
        spinner = view.findViewById(R.id.spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.planets_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        builder.setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            Task task = new Task(taskTitle.getText().toString(), getCurrentDate(), getCurrentUserID(), spinner.getSelectedItem().toString(), "unactive");

                            EmailPasswordActivity.getInstance().prepareTasksForExit();
                            Tasks.getInstance().populateData(task);
                            Tasks.getInstance().bindArrayAdapter();
                            Tasks.getInstance().setListeners();

                        } catch(Exception e) {
                            Log.d("dialog",e.getMessage());
                        }
                        DialogActivity.this.getDialog().cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DialogActivity.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
