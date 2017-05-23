package com.example.mrr.final_project_mobile_programming.Utilities;

import android.database.Cursor;

import com.example.mrr.final_project_mobile_programming.Calendar.Event;
import com.example.mrr.final_project_mobile_programming.Calendar.Meeting;
import com.example.mrr.final_project_mobile_programming.Contacts.ContactModel;

import java.util.ArrayList;
import java.util.Date;


public class CursorsFetcher {

    public static ArrayList<Event> getMeetingsFromDatabaseAsList(Cursor cursorMeetings, Cursor cursorContacts) {

        ArrayList<Event> meetings = new ArrayList<>();

        if(cursorMeetings != null && cursorMeetings.getCount() > 0) {

            cursorMeetings.moveToFirst();

            do {

                String title = cursorMeetings.getString(cursorMeetings.getColumnIndex(EventHandler.COLUMN_TITLE));
                String description = cursorMeetings.getString(cursorMeetings.getColumnIndex(EventHandler.COLUMN_DESCRIPTION));
                long date = (long) cursorMeetings.getDouble(cursorMeetings.getColumnIndex(EventHandler.COLUMN_DATE));
                int notificationHour = cursorMeetings.getInt(cursorMeetings.getColumnIndex(EventHandler.COLUMN_NOTIFICATION_HOUR));
                int notificationMinute = cursorMeetings.getInt(cursorMeetings.getColumnIndex(EventHandler.COLUMN_NOTIFICATION_MINUTE));
                int meetingId = cursorMeetings.getInt(cursorMeetings.getColumnIndex(EventHandler.COLUMN_ID));

                Meeting meeting = new Meeting(title, description, new Date(date), notificationHour, notificationMinute);

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
                    meeting.setContacts(contacts);
                    cursorContacts.moveToFirst();
                }

                meetings.add(meeting);
            }
            while(cursorMeetings.moveToNext());
        }

        return meetings;
    }
}
