package com.example.mrr.final_project_mobile_programming.FCM;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

import com.example.mrr.final_project_mobile_programming.Calendar.Event;
import com.example.mrr.final_project_mobile_programming.Utilities.CalendarUtility;
import com.example.mrr.final_project_mobile_programming.Utilities.CursorsFetcher;
import com.example.mrr.final_project_mobile_programming.Utilities.EventHandler;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class myJobService extends JobService {

    public static final String WORK_TYPE_KEY = "workType";
    public static final String USER_UID = "user_uid";
    EventHandler eventHandler = null;

    @Override
    public boolean onStartJob(JobParameters params) {

        int id = params.getExtras().getInt(WORK_TYPE_KEY);
        String userUID = params.getExtras().getString(USER_UID, "0");
        System.out.println("user UID: " + userUID);

        eventHandler = new EventHandler(getApplicationContext(), null, null, 1);

        if(id == 0) {

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Events");
            ArrayList<Event> events = CursorsFetcher.getMeetingsFromDatabaseAsList(
                    eventHandler.getCursorOfAllMeetings(),
                    eventHandler.getCursorOfAllContacts());

            for(Event event : events) {

                String unique_id = mDatabase.push().getKey();

                if(!event.isHasFirebaseKey()) {

                    event.setFirebaseKey(unique_id);
                    eventHandler.updateEventFirebaseKey(event.get_id(), unique_id);
                }


                mDatabase.child(userUID).child(event.getFirebaseKey()).setValue(event);
            }

            Toast.makeText(getApplicationContext(), "Firebase database updated!", Toast.LENGTH_SHORT).show();
        }

        if(id == 1) {

            Calendar calendar = Calendar.getInstance();

            setAlarmsForEvents(getEvents(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));

            System.out.println("ALARMS!!!");
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        Toast.makeText(getApplicationContext(), "Sync DONE", Toast.LENGTH_SHORT).show();
        return false;
    }

    private void setAlarmsForEvents(ArrayList<Event> events) {

        for(Event event : events)
            setAlarm(event);
    }

    private void setAlarm(Event event) {

        if(!event.getNotification().isHasNotification())
            return;

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if(event.getCustomDate().Hour() - event.getNotification().getNotificationHour() < hour)
            return;

        if(event.getCustomDate().Hour() - event.getNotification().getNotificationHour() == hour
                && event.getCustomDate().Minute() - event.getNotification().getNotificationMinute() <= minute)
            return;

        calendar = CalendarUtility.prepareCalendarForNotification(event);

        Intent intent = new Intent("com.example.mrr.Action1");
        intent.putExtra(NotificationReceiver.NOTIFICATION_TITLE, event.getTitle());
        intent.putExtra(NotificationReceiver.NOTIFICATION_TYPE, event.getTypeId());
        intent.putExtra(NotificationReceiver.NOTIFICATION_HOURS, event.getCustomDate().HourAsString());

        final int id = (int) System.currentTimeMillis();

        intent.putExtra(NotificationReceiver.NOTIFICATION_ID, id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, id, intent,
                PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getApplicationContext()
                .getSystemService(ALARM_SERVICE);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public ArrayList<Event> getEvents(int year, int month, int day) {

        ArrayList<Event> events = new ArrayList<>();

        Cursor cursorEvents = eventHandler.getCursorOfMeetingsBySelectedDate(year, month, day);

        if(cursorEvents != null && cursorEvents.getCount() > 0) {

            cursorEvents.moveToFirst();
            Cursor cursorContact = eventHandler.getCursorOfAllContacts();

            events = CursorsFetcher.getMeetingsFromDatabaseAsList(cursorEvents, cursorContact);
            cursorContact.close();
            cursorEvents.close();
        }

        eventHandler.closeDatabase();

        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {

                return o1.getCustomDate().getDate().compareTo(o2.getCustomDate().getDate());
            }
        });

        return events;
    }
}
