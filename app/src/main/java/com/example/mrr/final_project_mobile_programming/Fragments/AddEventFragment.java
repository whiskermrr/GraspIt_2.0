package com.example.mrr.final_project_mobile_programming.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.mrr.final_project_mobile_programming.Activity.Communicator;
import com.example.mrr.final_project_mobile_programming.Calendar.Meeting;
import com.example.mrr.final_project_mobile_programming.Calendar.TaskToDo;
import com.example.mrr.final_project_mobile_programming.Contacts.ContactModel;
import com.example.mrr.final_project_mobile_programming.Contacts.ContactsAdapter;
import com.example.mrr.final_project_mobile_programming.R;
import com.example.mrr.final_project_mobile_programming.Utilities.UtilityListViewHeight;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AddEventFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    Communicator communicator;
    ContactsAdapter adapter;
    Calendar calendar = Calendar.getInstance();

    LinearLayout notificationLayout;
    LinearLayout iconLayout;
    LinearLayout contactLayout;
    ListView contactsList;
    Switch notificationSwitch;
    ImageButton bAddContact;
    Spinner spinnerEvent;

    EditText etTitle;
    EditText etDescription;
    EditText etDate;
    EditText eventHour;
    EditText eventMinute;
    EditText notificationHour;
    EditText notificationMinute;

    CustomImageView iBrain;
    CustomImageView iBook;
    CustomImageView iWorkout;

    Button bAddEvent;

    int idOfChosenImage;
    boolean clickedIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.add_event_fragment_layout, container, false);

        adapter = null;
        clickedIcon = false;
        idOfChosenImage = -1;

        contactLayout = (LinearLayout) view.findViewById(R.id.contactLayout);
        notificationLayout = (LinearLayout) view.findViewById(R.id.notificationLayout);
        notificationLayout.setVisibility(LinearLayout.GONE);
        iconLayout = (LinearLayout) view.findViewById(R.id.iconLayout);
        iconLayout.setVisibility(LinearLayout.GONE);
        contactsList = (ListView) view.findViewById(R.id.addContactList);
        notificationSwitch = (Switch) view.findViewById(R.id.notificationSwitch);
        bAddContact = (ImageButton) view.findViewById(R.id.bAddContact);
        etTitle = (EditText) view.findViewById(R.id.etTitle);
        etDescription = (EditText) view.findViewById(R.id.etDescription);
        etDate = (EditText) view.findViewById(R.id.etDate);
        eventHour = (EditText) view.findViewById(R.id.etHour);
        eventMinute = (EditText) view.findViewById(R.id.etMinute);
        notificationHour = (EditText) view.findViewById(R.id.etNotificationHours);
        notificationMinute = (EditText) view.findViewById(R.id.etNotificationMinutes);
        bAddEvent = (Button) view.findViewById(R.id.bAddEvent);
        iBook = (CustomImageView) view.findViewById(R.id.iBook);
        iBrain = (CustomImageView) view.findViewById(R.id.iBrain);
        iWorkout = (CustomImageView) view.findViewById(R.id.iWorkout);
        spinnerEvent = (Spinner) view.findViewById(R.id.spinnerEvent);
        spinnerEvent.setOnItemSelectedListener(this);

        iBook.setClickable(true);
        iBrain.setClickable(true);
        iWorkout.setClickable(true);

        iBook.setOnClickListener(this);
        iBrain.setOnClickListener(this);
        iWorkout.setOnClickListener(this);

        notificationSwitch.setOnCheckedChangeListener(this);
        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                adapter.removeContactFromList(position);
                UtilityListViewHeight.setListViewHeightBasedOnItemsInList(contactsList);
            }
        });

        bAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                communicator.showContactsFragment();
            }
        });

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(getActivity(), date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });

        bAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addEventFromInput();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        communicator = (Communicator) context;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if(isChecked)
            notificationLayout.setVisibility(LinearLayout.VISIBLE);

        else
            notificationLayout.setVisibility(LinearLayout.GONE);

    }

    public void addChosenContact(ContactModel contact) {

        if(adapter == null) {

            ArrayList<ContactModel> contacts = new ArrayList<>();
            contacts.add(contact);

            adapter = new ContactsAdapter(getActivity(), contacts);
            contactsList.setAdapter(adapter);
        }

        else
            adapter.addContactToList(contact);
        UtilityListViewHeight.setListViewHeightBasedOnItemsInList(contactsList);
    }

    private void addEventFromInput() {

        if(!etTitle.getText().toString().equals("") && !etDescription.getText().toString().equals("")
                && !etDate.getText().toString().equals("") && !eventHour.getText().toString().equals("")
                && !eventMinute.getText().toString().equals("")) {

            String title = etTitle.getText().toString();
            String description = etDescription.getText().toString();
            String dateAsString = etDate.getText().toString();
            Date date = convertStringDateToDateType(dateAsString);
            int hour = Integer.parseInt(eventHour.getText().toString());
            int minute = Integer.parseInt(eventMinute.getText().toString());
            int notifiHour = 0;
            int notifiMinute = 0;

            if(date == null) {

                Toast.makeText(getActivity(), "Wrong date!", Toast.LENGTH_SHORT).show();
                return;
            }

            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            date = calendar.getTime();

            if(notificationSwitch.isChecked()) {

                if(!notificationHour.getText().toString().equals("") && !notificationMinute.getText().toString().equals("")) {

                    notifiHour = Integer.parseInt(notificationHour.getText().toString());
                    notifiMinute = Integer.parseInt(notificationMinute.getText().toString());
                }

                else {

                    Toast.makeText(getActivity(), "Notification fields!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            int typeId = spinnerEvent.getSelectedItemPosition();
            int _id = communicator.getLastAddedMeetingId();

            if(typeId == 0) {

                Meeting meeting = new Meeting(title, description, date, notifiHour, notifiMinute, typeId);
                meeting.set_id(Integer.toString(_id + 1));

                if(adapter != null) {

                    ArrayList<ContactModel> contacts = adapter.getAllContacts();
                    meeting.setContacts(contacts);
                    adapter.clearContactList();
                    UtilityListViewHeight.setListViewHeightBasedOnItemsInList(contactsList);
                }

                communicator.addEventToDatabase(meeting);
            }

            else if(typeId == 1 && clickedIcon) {

                TaskToDo task = new TaskToDo(title, description, date, notifiHour, notifiMinute, typeId, idOfChosenImage);
                task.set_id(Integer.toString(_id + 1));
                communicator.addEventToDatabase(task);
            }

            etTitle.setText("");
            etDescription.setText("");
            etDate.setText("");
            eventHour.setText("");
            eventMinute.setText("");
            notificationHour.setText("");
            notificationMinute.setText("");
            notificationSwitch.setChecked(false);

            Toast.makeText(getActivity(), "Event Added", Toast.LENGTH_SHORT).show();
        }

        else
            Toast.makeText(getActivity(), "Empty Fields!", Toast.LENGTH_SHORT).show();
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    private void updateLabel() {

        String format = "dd/MM/yy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.GERMANY);

        etDate.setText(simpleDateFormat.format(calendar.getTime()));
    }

    private Date convertStringDateToDateType(String dateAsString) {

        Date date = null;

        String format = "dd/MM/yy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.GERMANY);

        try {

            date = simpleDateFormat.parse(dateAsString);
        }
        catch (ParseException e) {

            Toast.makeText(getActivity(), "Wrong date!", Toast.LENGTH_SHORT).show();
        }

        return date;
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {

            case R.id.iBook:
                idOfChosenImage = R.drawable.book_icon;
                iBook.changeImageSize(70);
                iBrain.changeImageSize(50);
                iWorkout.changeImageSize(50);
                clickedIcon = true;
                break;

            case R.id.iBrain:
                idOfChosenImage = R.drawable.brain_icon;
                iBook.changeImageSize(50);
                iBrain.changeImageSize(70);
                iWorkout.changeImageSize(50);
                clickedIcon = true;
                break;

            case R.id.iWorkout:
                idOfChosenImage = R.drawable.workout_icon;
                iBook.changeImageSize(50);
                iBrain.changeImageSize(50);
                iWorkout.changeImageSize(70);
                clickedIcon = true;
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(position == 0) {

            iconLayout.setVisibility(LinearLayout.GONE);
            contactLayout.setVisibility(LinearLayout.VISIBLE);
        }

        else if(position == 1) {

            iconLayout.setVisibility(LinearLayout.VISIBLE);
            contactLayout.setVisibility(LinearLayout.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
