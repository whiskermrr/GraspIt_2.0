package com.example.mrr.final_project_mobile_programming.Calendar;

import java.util.Date;

public class TaskToDo extends Event {

    int imageId;

    public TaskToDo(String title, String description, Date date, int notificationHour, int notificationMinute, int imageId) {

        super(title, description, date, notificationHour, notificationMinute);

        this.imageId = imageId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
