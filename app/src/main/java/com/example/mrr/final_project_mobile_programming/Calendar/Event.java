package com.example.mrr.final_project_mobile_programming.Calendar;

import java.util.Calendar;
import java.util.Date;

public class Event {

    private String title;
    private String description;
    private Date date;
    private int notificationHour;
    private int notificationMinute;
    private int typeId;
    boolean hasNotification;
    String _id;
    String firebaseKey;
    boolean hasFirebaseKey;

    public Event() {


    }

    public Event(String title, String description, Date date, int notificationHour, int notificationMinute, int typeId) {

        this.title = title;
        this.description = description;
        this.date = date;
        this.notificationHour = notificationHour;
        this.notificationMinute = notificationMinute;
        this.typeId = typeId;
        hasNotification = (notificationHour != 0 || notificationMinute != 0);
        firebaseKey = null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int Year() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.YEAR);
    }

    public boolean isHasNotification() {
        return hasNotification;
    }

    public void setHasNotification(boolean hasNotification) {
        this.hasNotification = hasNotification;
    }

    public int Month() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.MONTH);
    }

    public int Day() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int Hour() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int Minute() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    public long DateAsLong() {

        return date.getTime();
    }

    public String HourAsString() {

        if(this.Minute() > 9) {

            return this.Hour() + ":" + this.Minute();
        }

        else return this.Hour() + ":0" + this.Minute();
    }

    public int getNotificationHour() {

        return notificationHour;
    }

    public void setNotificationHour(int notificationHour) {

        this.notificationHour = notificationHour;
    }

    public int getNotificationMinute() {

        return notificationMinute;
    }

    public void setNotificationMinute(int notificationMinute) {

        this.notificationMinute = notificationMinute;
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
}

