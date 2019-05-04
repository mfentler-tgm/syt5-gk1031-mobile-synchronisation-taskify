package com.taskify.syt.taskify;

import android.util.Log;

import java.sql.Time;
import java.util.Date;
import java.util.Timer;

public class Task {

    private String description;
    private Date createdOn;
    private String user_id;
    private String taskTag;
    private String state;
    private int taskDuration;
    private String documentID;
    //private Timer timeSpentOnTask;

    public Task(String description, Date createdOn, String user_id, String taskTag, String state) {
        if(description != null)
            this.description = description;
        else{
            throw new IllegalArgumentException("Task needs a description name");
        }

        this.createdOn = createdOn;
        if(user_id != null)
            this.user_id = user_id;
        if(taskTag != null)
            this.taskTag = taskTag;
        //this.timeSpentOnTask = new Timer();
        if(state != null) {
            if (state.equals("active") || state.equals("paused") || state.equals("finished") || state.equals("unactive")) {
                this.state = state;
            } else {
                this.state = "paused";
            }
        }else{
            this.state = "paused";
        }
        this.taskDuration = 0;

    }
    public Task() {
        // Default constructor required for calls to DataSnapshot.getValue(Task.class)
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

        public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTaskTag(){return this.taskTag;}
    public void setTaskTag(String tag){this.taskTag = tag;}

    public String getState(){return this.state;}
    public void setState(String state){this.state = state;}

    public int getTaskDuration(){return this.taskDuration;}
    public void setTaskDuration(int duration){this.taskDuration = duration;}

    public void setDocumentID(String id){this.documentID = id;}
    public String getDocumentID(){return this.documentID;}
}
