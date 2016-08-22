package org.unimelb.itime.vendor.helper;

import java.util.Calendar;

/**
 * Created by Paul on 22/08/2016.
 */
public class MyCalendar {
    private int year;
    int month;
    int day;
    int hour;
    int minute;

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

    public int getHour(){
        return this.hour;
    }

    public void setHour(int hour){
        this.hour = hour;
    }

    public int getMinute(){
        return this.minute;
    }

    public void setMinute(int minute){
        this.minute = minute;
    }


    public void cloneFromCalendar(Calendar calendar){
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DATE);
        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.minute = calendar.get(Calendar.MINUTE);
    }

    public void cloneFromMyCalendar(MyCalendar myCalendar){
        this.year = myCalendar.getYear();
        this.month = myCalendar.getMonth();
        this.day = myCalendar.getDay();
        this.hour = myCalendar.getHour();
        this.minute = myCalendar.getMinute();
    }

    public void setOffset(int offset){
        Calendar calendar = this.getCalendar();
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + offset);
        cloneFromCalendar(calendar);
    }

    private Calendar getCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,this.getYear());
        calendar.set(Calendar.MONTH,this.getMonth());
        calendar.set(Calendar.DATE,this.getDay());
        calendar.set(Calendar.HOUR_OF_DAY,this.getHour());
        calendar.set(Calendar.MINUTE, this.getMinute());

        return calendar;
    }

    public int getDayOfWeek(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,this.getYear());
        calendar.set(Calendar.MONTH,this.getMonth());
        calendar.set(Calendar.DATE,this.getDay());

        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    @Override
    public String toString() {
        return "MyCalendar{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", minute=" + minute +
                '}';
    }
}
