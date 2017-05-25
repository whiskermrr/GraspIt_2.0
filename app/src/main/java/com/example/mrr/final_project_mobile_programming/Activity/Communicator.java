package com.example.mrr.final_project_mobile_programming.Activity;

import com.example.mrr.final_project_mobile_programming.Calendar.Event;
import com.example.mrr.final_project_mobile_programming.Contacts.ContactModel;

import java.util.ArrayList;

public interface Communicator {

    ArrayList<Event> getEvents();
    ArrayList<Event> getEvents(int year, int month, int day);
    void updateDayFragment(int year, int month, int day);
    void addEventToDatabase(Event event);
    void showContactsFragment();
    void showEventFragment();
    void updateAddEventFragment(ContactModel contact);
    void setEvent(Event event);
    Event getEvent();
}