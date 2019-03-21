package com.taskify.syt.taskify;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

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

        Task currentTask = getItem(position);


        TextView taskStatus = (TextView)listItem.findViewById(R.id.taskStatus);
        TextView taskTag = (TextView)listItem.findViewById(R.id.taskTag);
        TextView taskDescription = (TextView)listItem.findViewById(R.id.taskDescription);

        taskStatus.setText(currentTask.getState());
        taskTag.setText(currentTask.getTaskTag());
        taskDescription.setText(currentTask.getDescription());

        return listItem;
    }
}
