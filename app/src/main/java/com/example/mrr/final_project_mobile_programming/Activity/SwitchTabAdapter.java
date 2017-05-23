package com.example.mrr.final_project_mobile_programming.Activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.mrr.final_project_mobile_programming.Calendar.GridCalendarFragment;
import com.example.mrr.final_project_mobile_programming.Contacts.ContactsFragment;
import com.example.mrr.final_project_mobile_programming.Fragments.AddEventFragment;
import com.example.mrr.final_project_mobile_programming.Fragments.DayFragment;

public class SwitchTabAdapter extends FragmentPagerAdapter {


    public SwitchTabAdapter(FragmentManager fm) {

        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        if(position == 0) {

            fragment = new DayFragment();
        }

        else if(position == 1) {

            fragment = new GridCalendarFragment();
        }

        else if(position == 2) {

            fragment = new AddEventFragment();
        }

        return fragment;
    }

    @Override
    public int getCount() {

        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if(position == 0) {

            return "Day";
        }

        else if(position == 1) {

            return "Calendar";
        }

        else if(position == 2) {

            return "Add Event";
        }

        return null;
    }
}
