package com.example.mrr.final_project_mobile_programming.Calendar;

/**
 * Created by Mrr on 2017-05-29.
 */

public class NotificationInfo {

    private int notificationHour;
    private int notificationMinute;
    private boolean hasNotification;

    public NotificationInfo() {

    }

    public NotificationInfo(int notificationHour, int notificationMinute) {

        this.notificationHour = notificationHour;
        this.notificationMinute = notificationMinute;
        hasNotification = (notificationHour != 0 || notificationMinute != 0);
    }


    public boolean isHasNotification() {
        return hasNotification;
    }

    public void setHasNotification(boolean hasNotification) {
        this.hasNotification = hasNotification;
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
