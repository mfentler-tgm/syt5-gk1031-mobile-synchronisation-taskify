package com.taskify.syt.taskify;

import java.util.Date;

public class Task {

    public String description;
    public Date createdOn;
    public String user_id;

    public Task(String description, Date createdOn, String user_id) {
        this.description = description;
        this.createdOn = createdOn;
        this.user_id = user_id;
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
}
