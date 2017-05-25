package com.example.mrr.final_project_mobile_programming.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mrr.final_project_mobile_programming.Activity.Communicator;
import com.example.mrr.final_project_mobile_programming.Calendar.Event;
import com.example.mrr.final_project_mobile_programming.Calendar.Meeting;
import com.example.mrr.final_project_mobile_programming.Calendar.TaskToDo;
import com.example.mrr.final_project_mobile_programming.Contacts.ContactsAdapter;
import com.example.mrr.final_project_mobile_programming.R;

import java.io.FileNotFoundException;
import java.io.IOException;


public class EventFragment extends DialogFragment implements AdapterView.OnItemClickListener {

    Communicator communicator;

    ImageView imageEvent;
    EditText etTitle;
    EditText etDescription;
    TextView tDate;
    TextView tContacts;
    ListView contactsList;
    Button bOK;

    String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct","Nov", "Dec" };

    ContactsAdapter adapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.event_fragment_layout, container, false);

        imageEvent = (ImageView) view.findViewById(R.id.imageEvent);
        etTitle = (EditText) view.findViewById(R.id.etTitle);
        etDescription = (EditText) view.findViewById(R.id.etDescription);
        tDate = (TextView) view.findViewById(R.id.tDate);
        contactsList = (ListView) view.findViewById(R.id.contactsList);
        contactsList.setOnItemClickListener(this);
        tContacts = (TextView) view.findViewById(R.id.tContacts);
        bOK = (Button) view.findViewById(R.id.bOK);
        bOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        prepareGUI();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        communicator = (Communicator) context;
    }

    private void prepareGUI() {

        Event event = communicator.getEvent();

        etTitle.setText(event.getTitle());
        etDescription.setText(event.getDescription());
        tDate.setText(event.getDay() + " " + months[event.getMonth()] + " " + event.getYear());

        if(event.getClass() == Meeting.class) {

            Meeting meeting = (Meeting) event;
            adapter = new ContactsAdapter(getActivity(), meeting.getContacts());
            contactsList.setAdapter(adapter);

            String photo = null;

            if(meeting.getContacts().size() > 0)
                photo = meeting.getContacts().get(0).getPhoto();

            if(photo != null) {

                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(photo));
                    imageEvent.setImageBitmap(bitmap);
                }

                catch (FileNotFoundException e) {

                    e.printStackTrace();
                }

                catch (IOException e) {

                    e.printStackTrace();
                }
            }

            else
                imageEvent.setImageResource(R.drawable.ic_people);
        }

        else if(event.getClass() == TaskToDo.class) {

            TaskToDo task = (TaskToDo) event;
            contactsList.setVisibility(LinearLayout.GONE);
            tContacts.setVisibility(LinearLayout.GONE);
            imageEvent.setImageResource(task.getImageId());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getDialog().setCanceledOnTouchOutside(true);

        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        int height = (displayMetrics.heightPixels);
        int width = (displayMetrics.widthPixels);

        getDialog().getWindow().setLayout(width, height);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + adapter.getNumber(position)));
        startActivity(callIntent);
    }
}
