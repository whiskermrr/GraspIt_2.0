package com.example.mrr.final_project_mobile_programming.Calendar;

import java.util.Calendar;
import java.util.Date;

public abstract class Event {

    private String title;
    private String description;
    private Date date;
    private int notificationHour;
    private int notificationMinute;

    public Event(String title, String description, Date date, int notificationHour, int notificationMinute) {

        this.title = title;
        this.description = description;
        this.date = date;
        this.notificationHour = notificationHour;
        this.notificationMinute = notificationMinute;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
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

    public int getYear() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.YEAR);
    }


    public int getMonth() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.MONTH);
    }

    public int getDay() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getHour() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    public long getDateAsLong() {

        return date.getTime();
    }

    public void setDate(long time) {

        date = new Date(time * 1000);
    }

    public String getHoursAsString() {

        if(this.getMinute() > 9) {

            return this.getHour() + ":" + this.getMinute();
        }

        else return this.getHour() + ":0" + this.getMinute();
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
}

