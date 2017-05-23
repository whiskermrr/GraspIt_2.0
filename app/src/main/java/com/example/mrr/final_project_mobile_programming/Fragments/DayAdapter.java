package com.example.mrr.final_project_mobile_programming.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mrr.final_project_mobile_programming.Calendar.Event;
import com.example.mrr.final_project_mobile_programming.Calendar.Meeting;
import com.example.mrr.final_project_mobile_programming.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class DayAdapter extends ArrayAdapter<Event> {

    private Context mContext;
    private ArrayList<Event> mEvents;

    public DayAdapter(Context context, ArrayList<Event> events) {
        super(context, R.layout.day_row_layout);

        mContext = context;
        this.mEvents = events;
    }

    private class DayMeetingViewHandler {

        ImageView imageMeeting;
        TextView tMeetingTitle;
        TextView tMeetingDescription;
        TextView tMeetingTime;

        private DayMeetingViewHandler(View view) {

            imageMeeting = (ImageView) view.findViewById(R.id.imageMeeting);
            tMeetingTitle = (TextView) view.findViewById(R.id.tMeetingTitle);
            tMeetingDescription = (TextView) view.findViewById(R.id.tMeetingDescription);
            tMeetingTime = (TextView) view.findViewById(R.id.tMeetingTime);
        }
    }

    public void setData(ArrayList<Event> events) {

        mEvents = events;
    }


    @Override
    public int getCount() {

        return mEvents.size();
    }

    @Override
    public Event getItem(int position) {

        return mEvents.get(position);
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    public void addEventToList(Event event) {

        mEvents.add(event);
    }

    //need selection between meetings and taskToDo
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        DayMeetingViewHandler holder = null;

        if(view == null) {

            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.day_row_layout, parent, false);
            holder = new DayMeetingViewHandler(view);
            view.setTag(holder);
        }

        else
            holder = (DayMeetingViewHandler) view.getTag();

        Meeting meeting = (Meeting) mEvents.get(position);

        holder.tMeetingTime.setText(meeting.getHoursAsString());
        holder.tMeetingTitle.setText(meeting.getTitle());
        holder.tMeetingDescription.setText(meeting.getDescription());

        if(meeting.getContacts() != null && meeting.getContacts().size() > 0) {

            String photo = meeting.getContacts().get(0).getPhoto();

            if(photo != null) {

                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(photo));
                    holder.imageMeeting.setImageBitmap(bitmap);
                }

                catch (FileNotFoundException e) {

                    e.printStackTrace();
                }

                catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }

        else
            holder.imageMeeting.setImageResource(R.drawable.ic_people);

        return view;
    }
}
