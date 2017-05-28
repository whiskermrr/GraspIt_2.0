package com.example.mrr.final_project_mobile_programming.Calendar;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mrr on 2017-05-29.
 */

public class CustomDate {

    private Date date;

    public CustomDate() {


    }

    public CustomDate(Date date) {

        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int Year() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.YEAR);
    }

    public int Month() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.MONTH);
    }

    public int Day() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int Hour() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int Minute() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }


    public long DateAsLong() {

        return date.getTime();
    }

    public String HourAsString() {

        if(this.Minute() > 9) {

            return this.Hour() + ":" + this.Minute();
        }

        else return this.Hour() + ":0" + this.Minute();
    }
}
