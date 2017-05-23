package com.example.mrr.final_project_mobile_programming.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.example.mrr.final_project_mobile_programming.Activity.Communicator;
import com.example.mrr.final_project_mobile_programming.Calendar.Event;
import com.example.mrr.final_project_mobile_programming.Calendar.Meeting;
import com.example.mrr.final_project_mobile_programming.R;
import com.example.mrr.final_project_mobile_programming.Utilities.UtilityListViewHeight;

import java.util.ArrayList;
import java.util.Calendar;

public class DayFragment extends Fragment {

    ListView meetingList;
    ListView dayToDoList;
    CheckedTextView checkTextViewMeetings;
    CheckedTextView checkTextViewToDo;
    Communicator communicator;
    DayAdapter adapterMeetings;
    DayAdapter adapterTaskToDo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.day_fragment_layout, container, false);

        meetingList = (ListView) view.findViewById(R.id.dayMeetingsList);
        checkTextViewMeetings = (CheckedTextView) view.findViewById(R.id.checkTextViewMeetings);
        checkTextViewMeetings.setChecked(true);
        checkTextViewMeetings.setCheckMarkDrawable(R.drawable.ic_expand_less);
        checkTextViewMeetings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkTextViewMeetings.isChecked()) {

                    checkTextViewMeetings.setChecked(false);
                    checkTextViewMeetings.setCheckMarkDrawable(R.drawable.ic_expand_more);
                    meetingList.setVisibility(View.GONE);
                }

                else {

                    checkTextViewMeetings.setChecked(true);
                    checkTextViewMeetings.setCheckMarkDrawable(R.drawable.ic_expand_less);
                    meetingList.setVisibility(View.VISIBLE);
                }

            }
        });

        dayToDoList = (ListView) view.findViewById(R.id.dayToDoList);
        checkTextViewToDo = (CheckedTextView) view.findViewById(R.id.checkTextViewToDo);
        checkTextViewToDo.setChecked(true);
        checkTextViewToDo.setCheckMarkDrawable(R.drawable.ic_expand_less);
        checkTextViewToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkTextViewToDo.isChecked()) {

                    checkTextViewToDo.setChecked(false);
                    checkTextViewToDo.setCheckMarkDrawable(R.drawable.ic_expand_more);
                    dayToDoList.setVisibility(View.GONE);
                }

                else {

                    checkTextViewToDo.setChecked(true);
                    checkTextViewToDo.setCheckMarkDrawable(R.drawable.ic_expand_less);
                    dayToDoList.setVisibility(View.VISIBLE);
                }
            }
        });

        Calendar calendar = Calendar.getInstance();

        updateAdapter(communicator.getEvents(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {

            communicator = (Communicator) context;
        }

        catch (ClassCastException e) {

            e.printStackTrace();
        }
    }

    public void addNewEvent(Event event) {

        adapterMeetings.addEventToList(event);
        adapterMeetings.notifyDataSetChanged();
    }

    public void updateAdapter(ArrayList<Event> events) {

        adapterMeetings = new DayAdapter(getActivity(), new ArrayList<Event>());
        adapterTaskToDo = new DayAdapter(getActivity(), new ArrayList<Event>());

        for(Event event : events) {

            if(event.getClass() == Meeting.class) {

                adapterMeetings.addEventToList(event);
                adapterTaskToDo.addEventToList(event);
            }

        }

        meetingList.setAdapter(adapterMeetings);
        dayToDoList.setAdapter(adapterTaskToDo);
        UtilityListViewHeight.setListViewHeightBasedOnItemsInList(meetingList);
        UtilityListViewHeight.setListViewHeightBasedOnItemsInList(dayToDoList);
    }
}
