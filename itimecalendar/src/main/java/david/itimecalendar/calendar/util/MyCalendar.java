package david.itimecalendar.calendar.util;

import java.util.Calendar;

/**
 * Created by David Liu on 6/08/16.
 * ${COMPANY}
 * lyhmelbourne@gmail.com
 */
public class MyCalendar {
    private int year;
    private int month;
    private int day;

    private int hour;
    private int minute;
    private int millisecond;

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

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getMillisecond() {
        return millisecond;
    }

    public void setMillisecond(int millisecond) {
        this.millisecond = millisecond;
    }

    public int getDayOfWeek(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,this.getYear());
        calendar.set(Calendar.MONTH,this.getMonth());
        calendar.set(Calendar.DATE,this.getDay());

        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public void cloneFromCalendar(Calendar calendar){
        this.year = calendar.get(Calendar.YEAR);
        this.month = calendar.get(Calendar.MONTH);
        this.day = calendar.get(Calendar.DAY_OF_MONTH);
        this.hour = calendar.get(Calendar.HOUR_OF_DAY);
        this.minute = calendar.get(Calendar.MINUTE);
    }

    public void setOffset(int offset){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + offset);
        cloneFromCalendar(calendar);
    }

    public void setOffsetByDate(int offset){
        Calendar calendar = this.getCalendar();
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + offset);
        cloneFromCalendar(calendar);
    }

    public Calendar getCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,this.getYear());
        calendar.set(Calendar.MONTH,this.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH,this.getDay());
        calendar.set(Calendar.HOUR_OF_DAY,this.getHour());
        calendar.set(Calendar.MINUTE, this.getMinute());
        calendar.set(Calendar.MILLISECOND, this.getMillisecond());

        return calendar;
    }

    public boolean isToday(){
        Calendar calendar = Calendar.getInstance();
        boolean year = (this.getYear() == calendar.get(Calendar.YEAR));
        boolean month = (this.getMonth() == calendar.get(Calendar.MONTH));
        boolean day = (this.getDay() == calendar.get(Calendar.DAY_OF_MONTH));

        return  year && month && day;
    }

    public int getHour() {
        return hour;
    }

    public long getBeginOfDayMilliseconds(){
        Calendar calendar = this.getCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return  calendar.getTimeInMillis();
    }

    public long getEndOfDayMilliseconds(){
        Calendar endCal = Calendar.getInstance();
        endCal.setTimeInMillis(getBeginOfDayMilliseconds());
        endCal.set(Calendar.DATE, endCal.get(Calendar.DATE) + 1);

        return  endCal.getTimeInMillis();
    }

    public void setToSameBeginOfDay(MyCalendar target){
        this.hour = target.getHour();
        this.minute = target.getMinute();
        this.millisecond = target.getMillisecond();
    }

    public String toString(){
        int year = this.getYear();
        int month = this.getMonth();
        int day = this.getDay();
        int hour = this.getHour();
        int minutes = this.getMinute();

        return year + "  " + day + "/" + month + "  " + hour + " : " + minutes;
    }

    private void cloneFromMyCalendar(MyCalendar myCalendar){
        this.year = myCalendar.getYear();
        this.month = myCalendar.getMonth();
        this.day = myCalendar.getDay();
        this.hour = myCalendar.getHour();
        this.minute = myCalendar.getMinute();
        this.millisecond = myCalendar.getMillisecond();
    }

    public boolean contains(long time){
        long beginOfDay = getBeginOfDayMilliseconds();
        long endOfDay = getEndOfDayMilliseconds();
        return time>= beginOfDay && time < endOfDay;
    }

}
