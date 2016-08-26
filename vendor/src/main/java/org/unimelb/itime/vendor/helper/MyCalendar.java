package org.unimelb.itime.vendor.helper;

import java.util.Calendar;

/**
 * Created by yuhaoliu on 6/08/16.
 */
public class MyCalendar {
    int year;
    int month;
    int day;
    
    private String TAG="MyAPPCalendar";

    public MyCalendar(Calendar calendar) {
        this.cloneFromCalendar(calendar);
    }

    public MyCalendar(MyCalendar myCalendar){
        this.cloneFromMyCalendar(myCalendar);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void cloneFromCalendar(Calendar calendar){
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public void cloneFromMyCalendar(MyCalendar myCalendar){
        this.year = myCalendar.getYear();
        this.month = myCalendar.getMonth();
        this.day = myCalendar.getDay();
    }

    public void setOffset(int offset){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + offset);
        cloneFromCalendar(calendar);
    }

    public Calendar getCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,this.getYear());
        calendar.set(Calendar.MONTH,this.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH,this.getDay());

        return calendar;
    }

    public boolean isToday(){
        Calendar calendar = Calendar.getInstance();
        boolean year = (this.getYear() == calendar.get(Calendar.YEAR));
        boolean month = (this.getMonth() == calendar.get(Calendar.MONTH));
        boolean day = (this.getDay() == calendar.get(Calendar.DAY_OF_MONTH));

        return  year && month && day;
    }
}
