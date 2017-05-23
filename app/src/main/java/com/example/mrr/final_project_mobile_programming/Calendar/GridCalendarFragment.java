package com.example.mrr.final_project_mobile_programming.Calendar;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.mrr.final_project_mobile_programming.Activity.Communicator;
import com.example.mrr.final_project_mobile_programming.R;
import com.example.mrr.final_project_mobile_programming.Utilities.EventHandler;

import java.util.Calendar;


public class GridCalendarFragment extends Fragment implements View.OnClickListener {

    GridView gridCalendar;
    GridCalendarAdapter adapter;
    Button bLeftMonth;
    Button bRightMonth;
    TextView tMonth;
    int currentMonthSelected;
    int currentYearSelected;
    EventHandler eventHandler = null;
    Communicator communicator;

    String[] months = {

            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.grid_calendar_layout, container, false);

        currentMonthSelected = Calendar.getInstance().get(Calendar.MONTH);
        currentYearSelected = Calendar.getInstance().get(Calendar.YEAR);

        eventHandler = new EventHandler(getActivity(), null, null, 1);

        gridCalendar = (GridView) view.findViewById(R.id.gridCalendar);
        adapter = new GridCalendarAdapter(getActivity(), currentMonthSelected, currentYearSelected, communicator.getEvents());
        gridCalendar.setAdapter(adapter);

        bLeftMonth = (Button) view.findViewById(R.id.bLeftMonth);
        bRightMonth = (Button) view.findViewById(R.id.bRightMonth);
        bLeftMonth.setOnClickListener(this);
        bRightMonth.setOnClickListener(this);

        tMonth = (TextView) view.findViewById(R.id.tMonth);
        tMonth.setText(months[currentMonthSelected] + " " + currentYearSelected);

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

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if(id == R.id.bLeftMonth) {

            if(currentMonthSelected == 0) {

                currentMonthSelected = 11;
                currentYearSelected--;
            }

            else {

                currentMonthSelected--;
            }

            changeMonth();
        }

        else if(id == R.id.bRightMonth) {

            if(currentMonthSelected == 11) {

                currentMonthSelected = 0;
                currentYearSelected++;
            }

            else {

                currentMonthSelected++;
            }

            changeMonth();
        }
    }

    public void addNewEvent(Event event) {

        adapter.addNewEvent(event);
    }

    private void changeMonth() {

        adapter = new GridCalendarAdapter(getActivity(), currentMonthSelected, currentYearSelected, communicator.getEvents());
        gridCalendar.setAdapter(adapter);
        tMonth.setText(months[currentMonthSelected] + " " + currentYearSelected);
    }
}

