package com.example.mrr.final_project_mobile_programming.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mrr.final_project_mobile_programming.Calendar.Event;
import com.example.mrr.final_project_mobile_programming.Calendar.GridCalendarFragment;
import com.example.mrr.final_project_mobile_programming.Calendar.Meeting;
import com.example.mrr.final_project_mobile_programming.Calendar.TaskToDo;
import com.example.mrr.final_project_mobile_programming.Contacts.ContactModel;
import com.example.mrr.final_project_mobile_programming.Contacts.ContactsFragment;
import com.example.mrr.final_project_mobile_programming.FCM.NotificationReceiver;
import com.example.mrr.final_project_mobile_programming.FCM.myJobService;
import com.example.mrr.final_project_mobile_programming.Fragments.AddEventFragment;
import com.example.mrr.final_project_mobile_programming.Fragments.DayFragment;
import com.example.mrr.final_project_mobile_programming.Fragments.EventFragment;
import com.example.mrr.final_project_mobile_programming.R;
import com.example.mrr.final_project_mobile_programming.Utilities.CursorsFetcher;
import com.example.mrr.final_project_mobile_programming.Utilities.EventHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements Communicator {

    ViewPager viewPager = null;
    EventHandler eventHandler = null;
    Event mEvent = null;
    private ComponentName myServiceComponent;
    private int jobId = 0;
    FirebaseUser user = null;
    DatabaseReference mDatabase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkFirebaseUser();

        eventHandler = new EventHandler(this, null, null, 1);
        //eventHandler.onUpgrade(eventHandler.getWritableDatabase(), 1, 1);



        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                long typeId = (long) dataSnapshot.child("typeId").getValue();

                if(typeId == 0) {

                    Meeting meeting = dataSnapshot.getValue(Meeting.class);
                    if(!eventHandler.isEventInDatabase(meeting.getFirebaseKey())) {

                        eventHandler.addEvent(meeting);
                        setAlarm(meeting);

                    }

                }

                else if(typeId == 1) {

                    TaskToDo task = dataSnapshot.getValue(TaskToDo.class);
                    if(!eventHandler.isEventInDatabase(task.getFirebaseKey())) {

                        eventHandler.addEvent(task);
                        setAlarm(task);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        viewPager = (ViewPager) findViewById(R.id.activity_main_pager);
        FragmentManager manager = getSupportFragmentManager();
        viewPager.setAdapter(new SwitchTabAdapter(manager));
        viewPager.setCurrentItem(1);

        IntentFilter filter = new IntentFilter("com.example.mrr.Action1");
        NotificationReceiver receiver = new NotificationReceiver();
        registerReceiver(receiver, filter);

        Calendar calendar = Calendar.getInstance();

        setAlarmsForEvents(getEvents(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));

        myServiceComponent = new ComponentName(this, myJobService.class);
    }

    private void checkFirebaseUser() {

        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null) {

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Events").child(user.getUid());
        System.out.println(user.getUid());
    }

    @Override
    public void setEvent(Event event) {

        mEvent = event;
    }

    @Override
    public Event getEvent() {

        return mEvent;
    }

    @Override
    public ArrayList<Event> getEvents() {

        Cursor cursorEvents = eventHandler.getCursorOfAllMeetings();
        cursorEvents.moveToFirst();
        Cursor cursorContacts = eventHandler.getCursorOfAllContacts();

        ArrayList<Event> events = CursorsFetcher.getMeetingsFromDatabaseAsList(cursorEvents, cursorContacts);

        eventHandler.closeDatabase();

        return events;
    }


    @Override
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

                return o1.getDate().compareTo(o2.getDate());
            }
        });

        return events;
    }

    @Override
    public void updateDayFragment(int year, int month, int day) {

        ArrayList<Event> events = getEvents(year, month, day);

        ViewPager pager = (ViewPager) findViewById(R.id.activity_main_pager);

        SwitchTabAdapter adapter = (SwitchTabAdapter) pager.getAdapter();
        DayFragment fragment = (DayFragment) adapter.instantiateItem(pager, 0);


        if(fragment != null) {

            fragment.updateAdapter(events);
        }

        viewPager.setCurrentItem(0);
    }

    @Override
    public void addEventToDatabase(Event event) {

        eventHandler.addEvent(event);

        ViewPager pager = (ViewPager) findViewById(R.id.activity_main_pager);

        SwitchTabAdapter adapter = (SwitchTabAdapter) pager.getAdapter();
        GridCalendarFragment fragmentCalendar = (GridCalendarFragment) adapter.instantiateItem(pager, 1);
        fragmentCalendar.addNewEvent(event);

        DayFragment fragmentDay = (DayFragment) adapter.instantiateItem(pager, 0);
        fragmentDay.addNewEvent(event);
        setAlarm(event);
        updateFirebaseDatabase();
    }

    @Override
    public void showContactsFragment() {

        ContactsFragment fragment = new ContactsFragment();
        FragmentManager manager = getSupportFragmentManager();
        fragment.show(manager, "AA");
    }

    @Override
    public void showEventFragment() {

        EventFragment fragment = new EventFragment();
        FragmentManager manager = getSupportFragmentManager();
        fragment.show(manager, "AA");

    }

    @Override
    public void updateAddEventFragment(ContactModel contact) {

        ViewPager pager = (ViewPager) findViewById(R.id.activity_main_pager);

        SwitchTabAdapter adapter = (SwitchTabAdapter) pager.getAdapter();
        AddEventFragment fragment = (AddEventFragment) adapter.instantiateItem(pager, 2);
        fragment.addChosenContact(contact);
    }

    private void setAlarmsForEvents(ArrayList<Event> events) {

        for(Event event : events)
            setAlarm(event);
    }

    private void setAlarm(Event event) {

        if(!event.isHasNotification())
            return;

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        if(event.Hour() - event.getNotificationHour() < hour)
            return;

        if(event.Hour() - event.getNotificationHour() == hour
                && event.Minute() - event.getNotificationMinute() <= minute)
            return;

        calendar = prepareCalendarForNotification(event);

        Intent intent = new Intent("com.example.mrr.Action1");
        intent.putExtra(NotificationReceiver.NOTIFICATION_TITLE, event.getTitle());
        intent.putExtra(NotificationReceiver.NOTIFICATION_TYPE, event.getTypeId());
        intent.putExtra(NotificationReceiver.NOTIFICATION_HOURS, event.HourAsString());

        final int id = (int) System.currentTimeMillis();

        intent.putExtra(NotificationReceiver.NOTIFICATION_ID, id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, id, intent,
                PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getApplicationContext()
                .getSystemService(ALARM_SERVICE);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }


    private Calendar prepareCalendarForNotification(Event event) {

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, event.Year());
        calendar.set(Calendar.MONTH, event.Month());

        if(event.Hour() - event.getNotificationHour() < 0) {

            calendar.set(Calendar.DAY_OF_MONTH, event.Day() - 1);
            calendar.set(Calendar.HOUR_OF_DAY, 24 - (event.Hour() - event.getNotificationHour()));

        }

        else {

            calendar.set(Calendar.DAY_OF_MONTH, event.Day());
            calendar.set(Calendar.HOUR_OF_DAY, event.Hour() - event.getNotificationHour());
        }

        if(event.Minute() - event.getNotificationMinute() < 0) {

            calendar.set(Calendar.HOUR_OF_DAY, event.Hour() - 1);
            calendar.set(Calendar.MINUTE, 60 - (event.Minute() - event.getNotificationMinute()));
        }

        else {

            calendar.set(Calendar.MINUTE, event.Minute() - event.getNotificationMinute());
        }

        calendar.set(Calendar.SECOND, 0);

        return calendar;
    }

    @Override
    public int getLastAddedMeetingId() {

        return eventHandler.getLastAddedMeetingId();
    }

    public void updateFirebaseDatabase() {

        JobInfo.Builder builder = new JobInfo.Builder(jobId++, myServiceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NOT_ROAMING);
        builder.setPersisted(false);

        PersistableBundle extras = new PersistableBundle();
        extras.putInt(myJobService.WORK_TYPE_KEY, 0);
        extras.putString(myJobService.USER_UID, user.getUid());

        builder.setExtras(extras);

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }
}