package com.taskify.syt.taskify;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class CustomTaskAdapter extends ArrayAdapter<Task> {

    public CustomTaskAdapter(@NonNull Activity context, ArrayList<Task> al){
        super(context,0,al);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if(listItem == null)
            listItem = LayoutInflater.from(getContext()).inflate(
                    R.layout.custom_taskview,parent,false);

        final Task currentTask = getItem(position);


        TextView taskStatus = (TextView)listItem.findViewById(R.id.taskStatus);
        TextView taskTag = (TextView)listItem.findViewById(R.id.taskTag);
        TextView taskDescription = (TextView)listItem.findViewById(R.id.taskDescription);
        Button startstopButton = listItem.findViewById(R.id.startStopButton);
        startstopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tasks.getInstance().startTaskTimer((LinearLayout)v.getParent().getParent().getParent(), currentTask);
            }
        });
        Button finishButton = listItem.findViewById(R.id.finishButton);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tasks.getInstance().stopTaskTimer((LinearLayout)v.getParent().getParent().getParent(), currentTask);
            }
        });
        taskStatus.setText(currentTask.getState());
        taskTag.setText(currentTask.getTaskTag());
        taskDescription.setText(currentTask.getDescription());


        return listItem;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }
}
