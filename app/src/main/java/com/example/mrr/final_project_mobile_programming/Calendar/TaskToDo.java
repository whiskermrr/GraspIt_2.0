package com.example.mrr.final_project_mobile_programming.Calendar;

import java.util.Date;

public class TaskToDo extends Event {

    private int imageId;

    public TaskToDo() {


    }

    public TaskToDo(String title, String description, Date date, int notificationHour, int notificationMinute, int typeId, int imageId) {

        super(title, description, date, notificationHour, notificationMinute, typeId);

        this.imageId = imageId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
