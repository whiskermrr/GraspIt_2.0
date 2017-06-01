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
    String userUID;

    @Override
    public boolean onStartJob(JobParameters params) {

        int id = params.getExtras().getInt(WORK_TYPE_KEY);
        userUID = params.getExtras().getString(USER_UID, "0");

        eventHandler = new EventHandler(getApplicationContext(), null, null, 1);

        if(id == 0) {

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
            ArrayList<Event> events = CursorsFetcher.getMeetingsFromDatabaseAsList(
                    eventHandler.getCursorOfAllMeetings(userUID),
                    eventHandler.getCursorOfAllContacts());

            for(Event event : events) {

                String unique_id = mDatabase.push().getKey();

                if(!event.isHasFirebaseKey()) {

                    event.setFirebaseKey(unique_id);
                    eventHandler.updateEventFirebaseKey(event.get_id(), unique_id);
                }


                mDatabase.child(userUID).child(event.getFirebaseKey()).setValue(event);
            }
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        return false;
    }
}
