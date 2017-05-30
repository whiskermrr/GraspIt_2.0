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

import com.example.mrr.final_project_mobile_programming.Adapters.SwitchTabAdapter;
import com.example.mrr.final_project_mobile_programming.Calendar.Event;
import com.example.mrr.final_project_mobile_programming.Fragments.GridCalendarFragment;
import com.example.mrr.final_project_mobile_programming.Calendar.Meeting;
import com.example.mrr.final_project_mobile_programming.Calendar.TaskToDo;
import com.example.mrr.final_project_mobile_programming.Contacts.ContactModel;
import com.example.mrr.final_project_mobile_programming.Fragments.ContactsFragment;
import com.example.mrr.final_project_mobile_programming.FCM.NotificationReceiver;
import com.example.mrr.final_project_mobile_programming.FCM.myJobService;
import com.example.mrr.final_project_mobile_programming.Fragments.AddEventFragment;
import com.example.mrr.final_project_mobile_programming.Fragments.DayFragment;
import com.example.mrr.final_project_mobile_programming.Fragments.EventFragment;
import com.example.mrr.final_project_mobile_programming.R;
import com.example.mrr.final_project_mobile_programming.Utilities.CalendarUtility;
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

    private static int SCHEDULE_ALARMS = 1;
    private static int SCHEDULE_FIREBASE_UPDATE = 0;

    ViewPager viewPager = null;
    EventHandler eventHandler = null;
    Event mEvent = null;
    private ComponentName myServiceComponent;
    private int jobId = 0;
    FirebaseUser user = null;
    DatabaseReference mDatabase = null;

    private int currentYear;
    private int currentMonth;
    private int currentDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkFirebaseUser();
        setCurrentDate();

        eventHandler = new EventHandler(this, null, null, 1);
        //eventHandler.onUpgrade(eventHandler.getWritableDatabase(), 1, 1);

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                long typeId = (long) dataSnapshot.child("typeId").getValue();

                if(typeId == 0) {

                    Meeting meeting = dataSnapshot.getValue(Meeting.class);
                    if(!eventHandler.isEventInDatabase(meeting.getFirebaseKey())) {

                        addEventToDatabase(meeting);
                        updateDayFragment(currentYear, currentMonth, currentDay);
                        setAlarm(meeting);
                    }
                }

                else if(typeId == 1) {

                    TaskToDo task = dataSnapshot.getValue(TaskToDo.class);
                    if(!eventHandler.isEventInDatabase(task.getFirebaseKey())) {

                        addEventToDatabase(task);
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
        viewPager.setCurrentItem(0);

        IntentFilter filter = new IntentFilter("com.example.mrr.Action1");
        NotificationReceiver receiver = new NotificationReceiver();
        registerReceiver(receiver, filter);

        myServiceComponent = new ComponentName(this, myJobService.class);

        scheduleJob(SCHEDULE_ALARMS);
        scheduleJob(SCHEDULE_FIREBASE_UPDATE);
    }

    private void checkFirebaseUser() {

        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null) {

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
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

        Cursor cursorEvents = eventHandler.getCursorOfAllMeetings(user.getUid());
        cursorEvents.moveToFirst();
        Cursor cursorContacts = eventHandler.getCursorOfAllContacts();

        ArrayList<Event> events = CursorsFetcher.getMeetingsFromDatabaseAsList(cursorEvents, cursorContacts);

        eventHandler.closeDatabase();

        return events;
    }


    @Override
    public ArrayList<Event> getEvents(int year, int month, int day) {

        ArrayList<Event> events = new ArrayList<>();

        Cursor cursorEvents = eventHandler.getCursorOfMeetingsBySelectedDate(year, month, day, user.getUid());

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

        eventHandler.addEvent(event, user.getUid());

        ViewPager pager = (ViewPager) findViewById(R.id.activity_main_pager);

        SwitchTabAdapter adapter = (SwitchTabAdapter) pager.getAdapter();
        GridCalendarFragment fragmentCalendar = (GridCalendarFragment) adapter.instantiateItem(pager, 1);
        fragmentCalendar.addNewEvent(event);
        scheduleJob(0);
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

    @Override
    public int getLastAddedMeetingId() {

        return eventHandler.getLastAddedMeetingId();
    }

    public void scheduleJob(int workTypeKey) {

        JobInfo.Builder builder = new JobInfo.Builder(jobId++, myServiceComponent);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NOT_ROAMING);
        builder.setPersisted(false);

        PersistableBundle extras = new PersistableBundle();
        extras.putInt(myJobService.WORK_TYPE_KEY, workTypeKey);
        extras.putString(myJobService.USER_UID, user.getUid());

        builder.setExtras(extras);

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }

    public void setCurrentDate() {

        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH);
        currentDay = calendar.get(Calendar.DAY_OF_MONTH);
    }
}