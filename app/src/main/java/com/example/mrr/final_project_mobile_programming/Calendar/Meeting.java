package com.example.mrr.final_project_mobile_programming.Calendar;

import com.example.mrr.final_project_mobile_programming.Contacts.ContactModel;

import java.util.ArrayList;
import java.util.Date;

public class Meeting extends Event {

    ArrayList<ContactModel> contacts;

    public Meeting(String title, String description, Date date, int notificationHour, int notificationMinute) {
        super(title, description, date, notificationHour, notificationMinute);

        contacts = new ArrayList<>();
    }

    public void addContactToEvent(ContactModel contact) {

        contacts.add(contact);
    }

    public void removeContactFromEvent(int position) {

        contacts.remove(position);
    }

    public ArrayList<ContactModel> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<ContactModel> contacts) {
        this.contacts = contacts;
    }
}
