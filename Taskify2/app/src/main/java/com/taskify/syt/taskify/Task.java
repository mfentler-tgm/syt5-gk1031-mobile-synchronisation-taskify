package com.taskify.syt.taskify;

import java.util.Date;

public class Task {

    private String description;
    private Date createdOn;
    private String user_id;
    private String taskTag;

    public Task(String description, Date createdOn, String user_id, String taskTag) {
        this.description = description;
        this.createdOn = createdOn;
        this.user_id = user_id;
        this.taskTag = taskTag;
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
}
