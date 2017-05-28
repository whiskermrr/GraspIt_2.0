package com.example.mrr.final_project_mobile_programming.Utilities;

import com.example.mrr.final_project_mobile_programming.Calendar.Event;

import java.util.Calendar;


public class CalendarUtility {

    public static Calendar prepareCalendarForNotification(Event event) {

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, event.getCustomDate().Year());
        calendar.set(Calendar.MONTH, event.getCustomDate().Month());

        if(event.getCustomDate().Hour() - event.getNotification().getNotificationHour() < 0) {

            calendar.set(Calendar.DAY_OF_MONTH, event.getCustomDate().Day() - 1);
            calendar.set(Calendar.HOUR_OF_DAY, 24 - (event.getCustomDate().Hour() - event.getNotification().getNotificationHour()));

        }

        else {

            calendar.set(Calendar.DAY_OF_MONTH, event.getCustomDate().Day());
            calendar.set(Calendar.HOUR_OF_DAY, event.getCustomDate().Hour() - event.getNotification().getNotificationHour());
        }

        if(event.getCustomDate().Minute() - event.getNotification().getNotificationMinute() < 0) {

            calendar.set(Calendar.HOUR_OF_DAY, event.getCustomDate().Hour() - 1);
            calendar.set(Calendar.MINUTE, 60 - (event.getCustomDate().Minute() - event.getNotification().getNotificationMinute()));
        }

        else {

            calendar.set(Calendar.MINUTE, event.getCustomDate().Minute() - event.getNotification().getNotificationMinute());
        }

        calendar.set(Calendar.SECOND, 0);

        return calendar;
    }
}
