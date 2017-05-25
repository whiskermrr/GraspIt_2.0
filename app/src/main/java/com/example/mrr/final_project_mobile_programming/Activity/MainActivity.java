package com.example.mrr.final_project_mobile_programming.Activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mrr.final_project_mobile_programming.Calendar.Event;
import com.example.mrr.final_project_mobile_programming.Calendar.GridCalendarFragment;
import com.example.mrr.final_project_mobile_programming.Contacts.ContactModel;
import com.example.mrr.final_project_mobile_programming.Contacts.ContactsFragment;
import com.example.mrr.final_project_mobile_programming.FCM.NotificationReceiver;
import com.example.mrr.final_project_mobile_programming.Fragments.AddEventFragment;
import com.example.mrr.final_project_mobile_programming.Fragments.DayFragment;
import com.example.mrr.final_project_mobile_programming.Fragments.EventFragment;
import com.example.mrr.final_project_mobile_programming.R;
import com.example.mrr.final_project_mobile_programming.Utilities.CursorsFetcher;
import com.example.mrr.final_project_mobile_programming.Utilities.EventHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements Communicator {

    public static final int PERMISSION_ALL = 1;
    public static final String[] PERMISSION = {

            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.INTERNET
    };

    ViewPager viewPager = null;
    EventHandler eventHandler = null;
    Event mEvent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!hasPermission(this, PERMISSION)) {

            ActivityCompat.requestPermissions(this, PERMISSION, PERMISSION_ALL);
        }

        eventHandler = new EventHandler(this, null, null, 1);
        //eventHandler.onUpgrade(eventHandler.getWritableDatabase(), 1, 1);

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
    }

    public static boolean hasPermission(Context context, String[] permissions) {

        for(String permission : permissions) {

            if(ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }

        return true;
    }

    public void onRequestPermissionsResult(int requestCode, int grantResult) {

        switch (requestCode) {

            case PERMISSION_ALL:

                if(grantResult == PackageManager.PERMISSION_GRANTED) {

                    System.out.println("PERMISSION GRANTED!");
                }

                else
                    System.out.println("PERMISSION DENIED");
        }
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

        if(event.getHour() - event.getNotificationHour() < hour)
            return;

        if(event.getHour() - event.getNotificationHour() == hour
                && event.getMinute() - event.getNotificationMinute() <= minute)
            return;

        calendar = prepareCalendarForNotification(event);

        Intent intent = new Intent("com.example.mrr.Action1");
        intent.putExtra(NotificationReceiver.NOTIFICATION_TITLE, event.getTitle());
        intent.putExtra(NotificationReceiver.NOTIFICATION_TYPE, event.getTypeId());
        intent.putExtra(NotificationReceiver.NOTIFICATION_HOURS, event.getHoursAsString());

        final int id = (int) System.currentTimeMillis();

        intent.putExtra(NotificationReceiver.NOTIFICATION_ID, id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, id, intent,
                PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager) getApplicationContext()
                .getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }


    private Calendar prepareCalendarForNotification(Event event) {

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, event.getYear());
        calendar.set(Calendar.MONTH, event.getMonth());

        if(event.getHour() - event.getNotificationHour() < 0) {

            calendar.set(Calendar.DAY_OF_MONTH, event.getDay() - 1);
            calendar.set(Calendar.HOUR_OF_DAY, 24 - (event.getHour() - event.getNotificationHour()));

        }

        else {

            calendar.set(Calendar.DAY_OF_MONTH, event.getDay());
            calendar.set(Calendar.HOUR_OF_DAY, event.getHour() - event.getNotificationHour());
        }

        if(event.getMinute() - event.getNotificationMinute() < 0) {

            calendar.set(Calendar.HOUR_OF_DAY, event.getHour() - 1);
            calendar.set(Calendar.MINUTE, 60 - (event.getMinute() - event.getNotificationMinute()));
        }

        else {

            calendar.set(Calendar.MINUTE, event.getMinute() - event.getNotificationMinute());
        }

        calendar.set(Calendar.SECOND, 0);

        return calendar;
    }


}