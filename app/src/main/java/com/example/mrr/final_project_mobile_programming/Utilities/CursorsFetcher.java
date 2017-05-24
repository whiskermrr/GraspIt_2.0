package com.example.mrr.final_project_mobile_programming.Utilities;

import android.database.Cursor;

import com.example.mrr.final_project_mobile_programming.Calendar.Event;
import com.example.mrr.final_project_mobile_programming.Calendar.Meeting;
import com.example.mrr.final_project_mobile_programming.Calendar.TaskToDo;
import com.example.mrr.final_project_mobile_programming.Contacts.ContactModel;

import java.util.ArrayList;
import java.util.Date;


public class CursorsFetcher {

    public static ArrayList<Event> getMeetingsFromDatabaseAsList(Cursor cursorEvents, Cursor cursorContacts) {

        ArrayList<Event> events = new ArrayList<>();

        if(cursorEvents != null && cursorEvents.getCount() > 0) {

            cursorEvents.moveToFirst();

            do {

                String title = cursorEvents.getString(cursorEvents.getColumnIndex(EventHandler.COLUMN_TITLE));
                String description = cursorEvents.getString(cursorEvents.getColumnIndex(EventHandler.COLUMN_DESCRIPTION));
                long date = (long) cursorEvents.getDouble(cursorEvents.getColumnIndex(EventHandler.COLUMN_DATE));
                int notificationHour = cursorEvents.getInt(cursorEvents.getColumnIndex(EventHandler.COLUMN_NOTIFICATION_HOUR));
                int notificationMinute = cursorEvents.getInt(cursorEvents.getColumnIndex(EventHandler.COLUMN_NOTIFICATION_MINUTE));
                int meetingId = cursorEvents.getInt(cursorEvents.getColumnIndex(EventHandler.COLUMN_ID));
                int typeId = cursorEvents.getInt(cursorEvents.getColumnIndex(EventHandler.COLUMN_TYPE_OF_EVENT));
                int imageId = cursorEvents.getInt(cursorEvents.getColumnIndex(EventHandler.COLUMN_TASK_ICON));


                if(typeId == 0) {

                    Meeting event = new Meeting(title, description, new Date(date), notificationHour, notificationMinute, typeId);

                    if(cursorContacts != null) {

                        ArrayList<ContactModel> contacts = new ArrayList<>();

                        while (cursorContacts.moveToNext()) {

                            int contactEventId = cursorContacts.getInt(cursorContacts.getColumnIndex(EventHandler.COLUMN_CONTACT_EVENT_ID));

                            if(meetingId == contactEventId) {

                                String contactName = cursorContacts.getString(cursorContacts.getColumnIndex(EventHandler.COLUMN_CONTACT_NAME));
                                String contactId = cursorContacts.getString(cursorContacts.getColumnIndex(EventHandler.COLUMN_CONTACT_ID));
                                String contactPhoto = cursorContacts.getString(cursorContacts.getColumnIndex(EventHandler.COLUMN_CONTACT_PHOTO));
                                String contactNumber = cursorContacts.getString(cursorContacts.getColumnIndex(EventHandler.COLUMN_CONTACT_NUMBER));
                                String contactEmail = cursorContacts.getString(cursorContacts.getColumnIndex(EventHandler.COLUMN_CONTACT_EMAIL));

                                ContactModel contact = new ContactModel(contactName, contactId, contactPhoto, contactNumber, contactEmail);
                                contacts.add(contact);
                            }
                        }
                        event.setContacts(contacts);
                        cursorContacts.moveToFirst();
                    }

                    events.add(event);
                }

                else if(typeId == 1) {

                    TaskToDo event = new TaskToDo(title, description, new Date(date), notificationHour, notificationMinute, typeId, imageId);
                    events.add(event);
                }
            }
            while(cursorEvents.moveToNext());
        }

        return events;
    }
}
