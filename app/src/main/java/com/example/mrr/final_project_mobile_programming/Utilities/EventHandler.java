package com.example.mrr.final_project_mobile_programming.Utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mrr.final_project_mobile_programming.Calendar.Event;
import com.example.mrr.final_project_mobile_programming.Calendar.Meeting;
import com.example.mrr.final_project_mobile_programming.Calendar.TaskToDo;
import com.example.mrr.final_project_mobile_programming.Contacts.ContactModel;

import java.util.ArrayList;

public class EventHandler extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "events.db";
    public static final String TABLE_MEETINGS = "meetings";
    public static final String TABLE_CONTACTS = "contacts";

    //Event variables
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TYPE_OF_EVENT = "type_of_event";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_MONTH = "month";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_HOUR = "hour";
    public static final String COLUMN_MINUTE = "minute";
    public static final String COLUMN_NOTIFICATION_HOUR = "notification_hour";
    public static final String COLUMN_NOTIFICATION_MINUTE = "notification_minute";
    public static final String COLUMN_TASK_ICON = "task_icon";
    public static final String COLUMN_FIREBASE_KEY = "firebase_key";

    //Meeting variables
    public static final String COLUMN_CONTACT_EVENT_ID = "contact_event_id";
    public static final String COLUMN_CONTACT_NAME = "contact_name";
    public static final String COLUMN_CONTACT_ID = "contact_id";
    public static final String COLUMN_CONTACT_NUMBER = "contact_number";
    public static final String COLUMN_CONTACT_PHOTO = "contact_photo";
    public static final String COLUMN_CONTACT_EMAIL = "contact_email";

    SQLiteDatabase db;


    public EventHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {

        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String queryEvent = "CREATE TABLE " + TABLE_MEETINGS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TYPE_OF_EVENT + " INTEGER NOT NULL," +
                COLUMN_TITLE + " TEXT NOT NULL," +
                COLUMN_DESCRIPTION + " TEXT NOT NULL," +
                COLUMN_DATE + " REAL NOT NULL," +
                COLUMN_YEAR + " INTEGER NOT NULL," +
                COLUMN_MONTH + " INTEGER NOT NULL," +
                COLUMN_DAY + " INTEGER NOT NULL," +
                COLUMN_HOUR + " INTEGER NOT NULL," +
                COLUMN_MINUTE + " INTEGER NOT NULL," +
                COLUMN_NOTIFICATION_HOUR + " INTEGER NOT NULL," +
                COLUMN_NOTIFICATION_MINUTE + " INTEGER NOT NULL," +
                COLUMN_TASK_ICON + " INTEGER NOT NULL," +
                COLUMN_FIREBASE_KEY + " TEXT" +
                ");";

        String queryContact = "CREATE TABLE " + TABLE_CONTACTS + "(" +
                COLUMN_CONTACT_EVENT_ID + " INTEGER NOT NULL," +
                COLUMN_CONTACT_NAME + " TEXT," +
                COLUMN_CONTACT_ID + " TEXT," +
                COLUMN_CONTACT_NUMBER + " TEXT," +
                COLUMN_CONTACT_PHOTO  + " TEXT," +
                COLUMN_CONTACT_EMAIL + " TEXT" +
                ")";

        db.execSQL(queryEvent);
        db.execSQL(queryContact);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEETINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public boolean isEventInDatabase(String firebaseKey) {

        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_FIREBASE_KEY + " FROM " + TABLE_MEETINGS + " WHERE " +
        COLUMN_FIREBASE_KEY + " = ?", new String[] {firebaseKey});

        boolean hasEvent = (cursor.getCount() > 0);

        cursor.close();
        db.close();

        return hasEvent;
    }

    public void updateEventFirebaseKey(String id, String firebaseKey) {

        db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIREBASE_KEY, firebaseKey);
        db.update(TABLE_MEETINGS, values, "_id = " + id, null);
        db.close();
    }

    public void addEvent(Event event) {

        ContentValues values = prepareValues(event);
        db = getWritableDatabase();
        db.insert(TABLE_MEETINGS, null, values);
        db.close();
    }

    public Cursor getEventById(int id) {

        db = getReadableDatabase();

        return db.rawQuery("SELECT * FROM " + TABLE_MEETINGS + " WHERE " + COLUMN_ID + " = ?",
                new String[] {Integer.toString(id)});
    }

    public Cursor getCursorOfAllMeetings() {

        db = getReadableDatabase();

        return db.rawQuery("SELECT * FROM " + TABLE_MEETINGS, null);
    }

    public Cursor getCursorOfAllContacts() {

        return db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);
    }

    public Cursor getCursorOfMeetingsBySelectedDate(int year, int month, int day) {

        db = getReadableDatabase();

        return db.rawQuery("SELECT * FROM " + TABLE_MEETINGS + " WHERE " +
                COLUMN_YEAR + " = ? AND " + COLUMN_MONTH + " = ? AND " + COLUMN_DAY + " = ?",
                new String[] {Integer.toString(year), Integer.toString(month), Integer.toString(day)});
    }

    public Cursor getCursorOfContactsByMeetingId(int id) {

        db = getReadableDatabase();

        return db.rawQuery("SELECT * FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_CONTACT_EVENT_ID + " = ?",
                new String[] {Integer.toString(id)});
    }

    private ContentValues prepareValues(Event event) {

        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, event.getTitle());
        values.put(COLUMN_DESCRIPTION, event.getDescription());
        values.put(COLUMN_DATE, event.DateAsLong());
        values.put(COLUMN_YEAR, event.Year());
        values.put(COLUMN_MONTH, event.Month());
        values.put(COLUMN_DAY, event.Day());
        values.put(COLUMN_HOUR, event.Hour());
        values.put(COLUMN_MINUTE, event.Minute());
        values.put(COLUMN_NOTIFICATION_HOUR, event.getNotificationHour());
        values.put(COLUMN_NOTIFICATION_MINUTE, event.getNotificationMinute());
        values.put(COLUMN_TYPE_OF_EVENT, event.getTypeId());
        values.put(COLUMN_FIREBASE_KEY, event.getFirebaseKey());

        int imageValue = -1;

        if(event.getClass() == TaskToDo.class) {

            TaskToDo task = (TaskToDo) event;
            imageValue = task.getImageId();
        }

        if(event.getClass() == Meeting.class) {

            Meeting meeting = (Meeting) event;
            insertContact(meeting);
        }

        values.put(COLUMN_TASK_ICON, imageValue);

        return values;
    }

    private void insertContact(Meeting meeting) {

        int lastMeetingId = getLastAddedMeetingId() + 1;
        db = getWritableDatabase();
        ArrayList<ContactModel> contacts = meeting.getContacts();

        for(int i = 0; i < meeting.getContacts().size(); i++) {

            ContentValues values = new ContentValues();
            values.put(COLUMN_CONTACT_EVENT_ID, lastMeetingId);
            values.put(COLUMN_CONTACT_NAME, contacts.get(i).getName());
            values.put(COLUMN_CONTACT_ID, contacts.get(i).getContactId());
            values.put(COLUMN_CONTACT_NUMBER, contacts.get(i).getPhoneNumber());
            values.put(COLUMN_CONTACT_PHOTO, contacts.get(i).getPhoto());
            values.put(COLUMN_CONTACT_EMAIL, contacts.get(i).getEmail());
            db.insert(TABLE_CONTACTS, null, values);
        }

        db.close();
    }

    public int getLastAddedMeetingId() {

        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_MEETINGS, null);
        cursor.moveToLast();

        int id = 0;

        if(cursor.getCount() > 0)
            id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));

        cursor.close();
        db.close();

        return id;
    }

    public void closeDatabase() {

        if(db.isOpen()) {

            db.close();
        }
    }
}
