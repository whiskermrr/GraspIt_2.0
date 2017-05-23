package com.example.mrr.final_project_mobile_programming.Activity;

import android.Manifest;
import android.content.Context;
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
import com.example.mrr.final_project_mobile_programming.Fragments.AddEventFragment;
import com.example.mrr.final_project_mobile_programming.Fragments.DayFragment;
import com.example.mrr.final_project_mobile_programming.R;
import com.example.mrr.final_project_mobile_programming.Utilities.CursorsFetcher;
import com.example.mrr.final_project_mobile_programming.Utilities.EventHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements Communicator {

    public static final int PERMISSION_ALL = 1;
    public static final String[] PERMISSION = {

            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.CALL_PHONE
    };

    ViewPager viewPager = null;
    EventHandler eventHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!hasPermission(this, PERMISSION)) {

            ActivityCompat.requestPermissions(this, PERMISSION, PERMISSION_ALL);
        }

        eventHandler = new EventHandler(this, null, null, 1);

        viewPager = (ViewPager) findViewById(R.id.activity_main_pager);
        FragmentManager manager = getSupportFragmentManager();
        viewPager.setAdapter(new SwitchTabAdapter(manager));
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
    public ArrayList<Event> getEvents() {

        Cursor cursorMeetings = eventHandler.getCursorOfAllMeetings();
        cursorMeetings.moveToFirst();
        Cursor cursorContacts = eventHandler.getCursorOfAllContacts();

        ArrayList<Event> events = CursorsFetcher.getMeetingsFromDatabaseAsList(cursorMeetings, cursorContacts);

        eventHandler.closeDatabase();

        return events;
    }

    //need selection between meetings and taskToDo
    @Override
    public ArrayList<Event> getEvents(int year, int month, int day) {

        ArrayList<Event> events = new ArrayList<>();

        Cursor cursorMeetings = eventHandler.getCursorOfMeetingsBySelectedDate(year, month, day);

        if(cursorMeetings != null && cursorMeetings.getCount() > 0) {

            cursorMeetings.moveToFirst();
            Cursor cursorContact = eventHandler.getCursorOfAllContacts();

            events = CursorsFetcher.getMeetingsFromDatabaseAsList(cursorMeetings, cursorContact);
            cursorContact.close();
            cursorMeetings.close();
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
        DayFragment fragment = (DayFragment) adapter.instantiateItem(pager, 1);


        if(fragment != null) {

            fragment.updateAdapter(events);
        }

        viewPager.setCurrentItem(1);
    }

    @Override
    public void addEventToDatabase(Event event) {

        eventHandler.addEvent(event);

        ViewPager pager = (ViewPager) findViewById(R.id.activity_main_pager);

        SwitchTabAdapter adapter = (SwitchTabAdapter) pager.getAdapter();
        GridCalendarFragment fragmentCalendar = (GridCalendarFragment) adapter.instantiateItem(pager, 2);
        fragmentCalendar.addNewEvent(event);

        DayFragment fragmentDay = (DayFragment) adapter.instantiateItem(pager, 1);
        fragmentDay.addNewEvent(event);
    }

    @Override
    public void showContactsFragment() {

        ContactsFragment fragment = new ContactsFragment();
        FragmentManager manager = getSupportFragmentManager();
        fragment.show(manager, "AA");
    }

    @Override
    public void updateAddEventFragment(ContactModel contact) {

        ViewPager pager = (ViewPager) findViewById(R.id.activity_main_pager);

        SwitchTabAdapter adapter = (SwitchTabAdapter) pager.getAdapter();
        AddEventFragment fragment = (AddEventFragment) adapter.instantiateItem(pager, 3);
        fragment.addChosenContact(contact);
    }
}