package com.example.mrr.final_project_mobile_programming.Calendar;

import java.util.Calendar;
import java.util.Date;

public class Event {

    private String title;
    private String description;
    private CustomDate customDate;
    private NotificationInfo notification;
    private int typeId;
    String _id;
    String firebaseKey;
    boolean hasFirebaseKey;

    public Event() {


    }

    public Event(String title, String description, Date date, int notificationHour, int notificationMinute, int typeId) {

        this.title = title;
        this.description = description;
        this.customDate = new CustomDate(date);
        this.notification = new NotificationInfo(notificationHour, notificationMinute);
        this.typeId = typeId;
        firebaseKey = null;
    }

    public boolean isHasFirebaseKey() {

        return (firebaseKey != null);
    }

    public void setHasFirebaseKey(boolean hasFirebaseKey) {

        this.hasFirebaseKey = hasFirebaseKey;
    }

    public String getFirebaseKey() {

        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {

        this.firebaseKey = firebaseKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public int getTypeId() {

        return typeId;
    }

    public void setTypeId(int typeId) {

        this.typeId = typeId;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public CustomDate getCustomDate() {
        return customDate;
    }

    public void setCustomDate(CustomDate date) {
        this.customDate = date;
    }

    public NotificationInfo getNotification() {
        return notification;
    }

    public void setNotification(NotificationInfo notification) {
        this.notification = notification;
    }
}

