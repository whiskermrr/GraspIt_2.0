package com.example.mrr.final_project_mobile_programming.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.example.mrr.final_project_mobile_programming.Activity.Communicator;
import com.example.mrr.final_project_mobile_programming.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class GridCalendarAdapter extends BaseAdapter {

    private final List<String> list;
    private static final int DAY_OFFSET = 1;
    private String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct","Nov", "Dec" };
    private final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    private int daysInMonth;
    private int currentDayOfMonth;
    private int currentWeekDay;
    private int currentMonth;
    private int currentYear;
    private int selectedMonth;
    private Button gridDay;
    private Context context;
    private ArrayList<Event> events;

    Communicator communicator;


    public GridCalendarAdapter(Context context, int month, int year, ArrayList<Event> events) {

        this.context = context;
        communicator = (Communicator) context;

        this.list = new ArrayList<>();
        this.events = events;
        Calendar calendar = Calendar.getInstance();
        setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
        setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
        setCurrentMonth(calendar.get(Calendar.MONTH));
        setCurrentYear(calendar.get(Calendar.YEAR));

        showMonth(month, year);
    }

    private void showMonth(int month, int year) {

        int trailingSpaces;
        int daysInPrevMonth;
        int prevMonth;
        int prevYear;
        int nextMonth;
        int nextYear;

        selectedMonth = month;
        daysInMonth = getNumberOfDaysOfMonth(selectedMonth);

        GregorianCalendar calendar = new GregorianCalendar(year, selectedMonth, 1);

        if(selectedMonth == 11) {

            prevMonth = selectedMonth - 1;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            nextMonth = 0;
            prevYear = year;
            nextYear = year + 1;
        }

        else if(selectedMonth == 0) {

            prevMonth = 11;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            nextMonth = selectedMonth + 1;
            prevYear = year - 1;
            nextYear = year;
        }

        else {

            prevMonth = selectedMonth - 1;
            nextMonth = selectedMonth + 1;
            nextYear = year;
            prevYear = year;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
        }

        trailingSpaces = calendar.get(Calendar.DAY_OF_WEEK) - 2;


        if(calendar.isLeapYear(calendar.get(Calendar.YEAR))) {

            if(month == 2) {

                daysInMonth++;
            }

            else if(month == 3) {

                daysInPrevMonth++;
            }
        }

        for(int i = 0; i < trailingSpaces; i++) {

            list.add((String.valueOf(daysInPrevMonth - trailingSpaces + DAY_OFFSET + i)) + "-GREY-" + getMonthAsString(prevMonth) + "-" + prevYear);
        }

        for(int i = 1; i <= daysInMonth; i++) {

            if(i == getCurrentDayOfMonth() && selectedMonth == getCurrentMonth() && year == getCurrentYear()) {

                list.add(String.valueOf(i) + "-GREEN-" + getMonthAsString(selectedMonth) + "-" + year);
            }

            else {

                list.add(String.valueOf(i) + "-WHITE-" + getMonthAsString(selectedMonth) + "-" + year);
            }
        }

        for(int i = 1; i < list.size() % 7; i++) {

            list.add(String.valueOf(i) + "-GREY-" + getMonthAsString(nextMonth) + "-" + nextYear);
        }


    }

    public void addNewEvent(Event event) {

        events.add(event);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View view = convertView;

        if(view == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.grid_calendar_cell_layout, parent, false);
        }

        gridDay = (Button) view.findViewById(R.id.buttonCalendar);
        String[] dayInfo = list.get(position).split("-");
        String day = dayInfo[0].trim();
        String color = dayInfo[1].trim();
        String month = dayInfo[2].trim();
        String year = dayInfo[3].trim();

        gridDay.setText(day);

        Calendar calendar = Calendar.getInstance();

        calendar.set(Integer.parseInt(year), convertMonthToInt(month), Integer.parseInt(day));

        if(color.equals("GREY")) {

            gridDay.setBackgroundResource(R.drawable.circular_button_grey);
        }

        if(color.equals("WHITE")) {

            gridDay.setBackgroundResource(R.drawable.circular_button_white);
        }

        if(color.equals("GREEN")) {

            gridDay.setBackgroundResource(R.drawable.circular_button_green);
        }

        if(position % 7 == 6) {

            gridDay.setTextColor(Color.parseColor("#FF0000"));
        }

        String tag = "";

        for(int i = 0; i < events.size(); i++) {

            if(events.get(i).getYear() == Integer.parseInt(year)
                    && events.get(i).getMonth() == convertMonthToInt(month)
                    && events.get(i).getDay() == Integer.parseInt(day)) {

                int parseColor = Color.parseColor("#03BB3F");

                if(currentDayOfMonth == events.get(i).getDay())
                    parseColor = Color.parseColor("#FFCA3A");

                gridDay.setTextColor(parseColor);
                tag += i + "-";
            }
        }

        gridDay.setTag(tag);

        gridDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.getId() == R.id.buttonCalendar) {

                    if (!v.getTag().equals("")) {

                        String[] tag = v.getTag().toString().split("-");
                        int[] positions = new int[tag.length];

                        String[] dayInfo = list.get(position).split("-");
                        String day = dayInfo[0].trim();
                        String month = dayInfo[2].trim();
                        String year = dayInfo[3].trim();

                        communicator.updateDayFragment(Integer.parseInt(year), convertMonthToInt(month), Integer.parseInt(day));

                        for(int i = 0; i < positions.length; i++) {

                            positions[i] = Integer.parseInt(tag[i]);
                        }

                        for(int i = 0; i < positions.length; i++) {


                            if(events.get(positions[i]).getTypeId() == 0) {

                                Meeting event = (Meeting) events.get(positions[i]);

                                System.out.println("TITLE: " + event.getTitle());
                                System.out.println("DESCRIPTION: " + event.getDescription());
                                System.out.println("YEAR: " + event.getYear());
                                System.out.println("MONTH: " + event.getMonth());
                                System.out.println("DAY: " + event.getDay());
                                System.out.println("TYPE: " +event.getTypeId());
                                System.out.println("----------------");

                                for (int j = 0; j < event.getContacts().size(); j++) {

                                    System.out.println(event.getContacts().get(j).getName());
                                    System.out.println(event.getContacts().get(j).getPhoneNumber());
                                    System.out.println(event.getContacts().get(j).getEmail());
                                    //System.out.println(Uri.parse(event.getContacts().get(j).getPhoto()));
                                    System.out.println("----------------");
                                }
                            }

                            else if(events.get(positions[i]).getTypeId() == 1) {

                                TaskToDo event = (TaskToDo) events.get(positions[i]);

                                System.out.println("TITLE: " + event.getTitle());
                                System.out.println("DESCRIPTION: " + event.getDescription());
                                System.out.println("YEAR: " + event.getYear());
                                System.out.println("MONTH: " + event.getMonth());
                                System.out.println("DAY: " + event.getDay());
                                System.out.println("TYPE: " + event.getTypeId());
                                System.out.println("IMAGE: " + event.getImageId());
                                System.out.println("----------------");
                            }
                        }
                    }
                }
            }
        });

        return view;
    }





    private String getMonthAsString(int i)
    {
        return months[i];
    }

    private int getNumberOfDaysOfMonth(int i)
    {
        return daysOfMonth[i];
    }

    private int getCurrentDayOfMonth()
    {
        return currentDayOfMonth;
    }

    private void setCurrentDayOfMonth(int currentDayOfMonth)
    {
        this.currentDayOfMonth = currentDayOfMonth;
    }

    private void setCurrentWeekDay(int currentWeekDay)
    {
        this.currentWeekDay = currentWeekDay;
    }


    private int convertMonthToInt(String month) {

        for(int i = 0; i < months.length; i++) {

            if(month.equals(months[i]))
                return i;
        }

        return -1;
    }

    private void setCurrentMonth(int currentMonth) {

        this.currentMonth = currentMonth;
    }

    private int getCurrentMonth() {

        return currentMonth;
    }

    private void setCurrentYear(int currentYear) {

        this.currentYear = currentYear;
    }

    private int getCurrentYear() {

        return currentYear;
    }

}
