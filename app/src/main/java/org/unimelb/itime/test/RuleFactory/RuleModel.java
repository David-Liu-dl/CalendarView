package org.unimelb.itime.test.RuleFactory;

import android.util.Log;

import org.antlr.v4.tool.Rule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Set;

/**
 * Created by yuhaoliu on 20/11/16.
 */
public class RuleModel {
    private static final String TAG = "Rule";

    private SimpleDateFormat format = new SimpleDateFormat("yyyymmdd");
    private long dayLongM = 86400000;

    //Recurrence Rule
    private FrequencyEnum frequencyEnum = null;
    private Date until;
    private int count;
    private int interval = 1;

    private ArrayList<Date> EXDates = new ArrayList<>();
    private ArrayList<Date> RDates = new ArrayList<>();

    private List<Integer> by_BYSECOND = new ArrayList<>();
    private List<Integer> by_BYMINUTE = new ArrayList<>();
    private List<Integer> by_BYHOUR = new ArrayList<>();
    private List<WeekDayEnum> by_BYDAY = new ArrayList<>();
    private List<Integer> by_BYMONTHDAY = new ArrayList<>();
    private List<Integer> by_BYYEARDAY = new ArrayList<>();
    private List<Integer> by_BYMONTH = new ArrayList<>();
    private List<Integer> by_BYWEEKNO = new ArrayList<>();
    private List<Integer> by_BYSETPOS = new ArrayList<>();

    private String WKST = "SU";

    public List<Integer> getBy_BYMONTH() {
        return by_BYMONTH;
    }

    private RuleInterface ruleInterface;

    public RuleModel(RuleInterface ruleInterface){
        this.ruleInterface = ruleInterface;
    }


    public void setBy_BYMONTH(List<Integer> by_BYMONTH) {
        this.by_BYMONTH = by_BYMONTH;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Integer> getBy_BYSECOND() {
        return by_BYSECOND;
    }

    public void setBy_BYSECOND(List<Integer> by_BYSECOND) {
        this.by_BYSECOND = by_BYSECOND;
    }

    public List<Integer> getBy_BYMINUTE() {
        return by_BYMINUTE;
    }

    public void setBy_BYMINUTE(List<Integer> by_BYMINUTE) {
        this.by_BYMINUTE = by_BYMINUTE;
    }

    public List<Integer> getBy_BYHOUR() {
        return by_BYHOUR;
    }

    public void setBy_BYHOUR(List<Integer> by_BYHOUR) {
        this.by_BYHOUR = by_BYHOUR;
    }

    public List<WeekDayEnum> getBy_BYDAY() {
        return by_BYDAY;
    }

    public void setBy_BYDAY(List<WeekDayEnum> by_BYDAY) {
        this.by_BYDAY = by_BYDAY;
    }

    public List<Integer> getBy_BYMONTHDAY() {
        return by_BYMONTHDAY;
    }

    public void setBy_BYMONTHDAY(List<Integer> by_BYMONTHDAY) {
        this.by_BYMONTHDAY = by_BYMONTHDAY;
    }

    public List<Integer> getBy_BYYEARDAY() {
        return by_BYYEARDAY;
    }

    public void setBy_BYYEARDAY(List<Integer> by_BYYEARDAY) {
        this.by_BYYEARDAY = by_BYYEARDAY;
    }

    public List<Integer> getBy_BYWEEKNO() {
        return by_BYWEEKNO;
    }

    public void setBy_BYWEEKNO(List<Integer> by_BYWEEKNO) {
        this.by_BYWEEKNO = by_BYWEEKNO;
    }

    public List<Integer> getBy_BYSETPOS() {
        return by_BYSETPOS;
    }

    public void setBy_BYSETPOS(List<Integer> by_BYSETPOS) {
        this.by_BYSETPOS = by_BYSETPOS;
    }

    public String getWKST() {
        return WKST;
    }

    public void setWKST(String WKST) {
        this.WKST = WKST;
    }

    public FrequencyEnum getFrequencyEnum() {
        return frequencyEnum;
    }

    public void setFrequencyEnum(FrequencyEnum frequencyEnum) {
        this.frequencyEnum = frequencyEnum;
    }

    public Date getUntil() {
        return until;
    }

    public void setUntil(Date until) {
        this.until = until;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public ArrayList<Date> getEXDates() {
        return EXDates;
    }

    public void setEXDates(ArrayList<Date> EXDates) {
        this.EXDates = EXDates;
    }

    public void addEXDate(Date date){
        this.EXDates.add(date);
    }

    public void removeEXDate(Date date){
        this.EXDates.remove(date);
    }

    public ArrayList<Date> getRDates() {
        return RDates;
    }

    public void setRDates(ArrayList<Date> RDates) {
        this.RDates = RDates;
    }

    public void addRDate(Date date){
        this.RDates.add(date);
    }

    public void removerDate(Date date){
        this.RDates.remove(date);
    }


    public boolean isInclude(long dateM){
        Date compareDate = new Date(dateM);

        long startTime = this.ruleInterface.getStartTime();
        //cmpDate less then start of creating
        if (dateM < startTime){
            return false;
        }

        if (until != null && !isInUntil(compareDate)){
            return false;
        }

        if (isInRDate(compareDate)){
            return true;
        }else if(isInEXDate(compareDate)){
            return false;
        }else{
            if (startTime == 0 || frequencyEnum == null){
                Log.i(TAG, "isInclude: " + "Need 'startTime' and 'frequencyEnum'.");
                return false;
            }else {
                switch (frequencyEnum){
                    case DAILY:
                        return dailyCheck(dateM);
                    case WEEKLY:
                        return weeklyCheck(dateM);
                    case MONTHLY:
                        return monthlyCheck(dateM);
                    case YEARLY:
                        return yearlyCheck(dateM);
                    default:
                        Log.i(TAG, "isInclude: " + frequencyEnum);
                        break;
                }

                Log.i(TAG, "isInclude: " + frequencyEnum);
            }
        }

        return false;
    }

    private boolean isInEXDate(Date date){
        for (Date exD:EXDates
             ) {
            if (checkDatesEqual(exD,date)){
                return true;
            }
        }
        return false;
    }

    private boolean isInRDate(Date date){
        for (Date rD:RDates
                ) {
            if (checkDatesEqual(rD,date)){
                return true;
            }
        }
        return false;
    }

    private boolean isInUntil(Date date){
        return (date.getTime() < until.getTime());
    }

    private boolean dailyCheck(long dateM){
        int dayDiffer = getDayDiffer(dateM);
        int nowCount = dayDiffer/interval;

        if (this.interval == 1){
            if (count != 0){
                return nowCount < count;
            }else{
                return true;
            }
        }else{
            int remains = dayDiffer%interval;

            //interval && count
            if (count != 0){
                return remains == 1 && nowCount < count;
            }else{
                return remains == 1;
            }
        }
    }

    //ensure compareDateM's hour minutes seconds equals 0
    private int getDayDiffer(long compareDateM){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(this.ruleInterface.getStartTime());
        setCalToDayBegin(cal);
        long differM = compareDateM - cal.getTimeInMillis();

        return (int) (differM/dayLongM);
    }

    private void setCalToDayBegin(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private boolean weeklyCheck(long dateM){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateM);
        //default start of sunday, sunday = 1;
        int cmpDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        if (interval == 1){
            for (WeekDayEnum day:this.by_BYDAY
                    ) {
                int dayOfWeek = day.getIndex();
                if (cmpDayOfWeek == dayOfWeek){
                    return true;
                }
            }

            int dayDiffer = getDayDiffer(dateM);
            if (count != 0){
                return dayDiffer % 7 == 0 && dayDiffer/7 < count;
            }else{
                return dayDiffer % 7 == 0;
            }
        }else{
            //not handle BYDAY attr yet
            int dayDiffer = getDayDiffer(dateM);
//            int remains = dayDiffer % (7 * interval);

            if (count != 0){
                return dayDiffer % (7 * interval) == 0 && dayDiffer/(7*interval) < count;
            }else{
                return dayDiffer % (7 * interval) == 0;
            }
        }
    }

    private boolean monthlyCheck(long dateM){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateM);

        Calendar orCal = Calendar.getInstance();
        orCal.setTimeInMillis(this.ruleInterface.getStartTime());

        int orgDayOfMonth = orCal.get(Calendar.DAY_OF_MONTH);
        int compareDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        if (interval == 1){
            return orgDayOfMonth == compareDayOfMonth;
        }else{
            int diffYear = cal.get(Calendar.YEAR) - orCal.get(Calendar.YEAR);
            int diffMonth = diffYear * 12 + cal.get(Calendar.MONTH) - orCal.get(Calendar.MONTH);

            return orgDayOfMonth == compareDayOfMonth && diffMonth % interval == 0;
        }

//        return false;
    }

    private boolean yearlyCheck(long dateM){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(dateM);

        Calendar orCal = Calendar.getInstance();
        orCal.setTimeInMillis(this.ruleInterface.getStartTime());

        int orMonthOfYear =  orCal.get(Calendar.MONTH);
        int calMonthOfYear =  cal.get(Calendar.MONTH);

        int orDayOfMonth = orCal.get(Calendar.DAY_OF_MONTH);
        int calDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        if (interval == 1){
            return orMonthOfYear == calMonthOfYear
                    && orDayOfMonth == calDayOfMonth;
        }else{
            int orgYear = orCal.get(Calendar.YEAR);
            int calYear = cal.get(Calendar.YEAR);

            return (calYear - orgYear)%interval == 0
                    && orMonthOfYear == calMonthOfYear
                    && orDayOfMonth == calDayOfMonth;
        }

//        return false;
    }

    private boolean checkDatesEqual(Date d1, Date d2){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(d1);
        cal2.setTime(d2);
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

        return sameDay;
    }

    //range: [startRange, endRange)
    public ArrayList<Long> getOccurenceDates(long startRange, long endRange){
        ArrayList<Long> availableDates = new ArrayList<>();
        long startTime = this.ruleInterface.getStartTime();

        if (startTime > endRange){
            return  availableDates;
        }else if (until != null && (startRange > until.getTime())){
            return  availableDates;
        }else{
            if (startRange < startTime){
                startRange = startTime;
            }
            int dayDiffer = getDayDiffer(startRange);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(startRange);

            switch (frequencyEnum){
                case DAILY: {
                    //compute first available date
                    int remains = dayDiffer % interval;
                    cal.add(Calendar.DATE, remains);

                    long currentAvailableDate = cal.getTimeInMillis();
                    while(currentAvailableDate < endRange){
                        currentAvailableDate = cal.getTimeInMillis();
                        if (currentAvailableDate >= startRange){
                            availableDates.add(currentAvailableDate);
                        }
                        cal.add(Calendar.DATE, interval);
                    }
                    break;
                }
                case WEEKLY:{
                    //compute first available date
                    int remains = dayDiffer % (interval * 7);
                    cal.add(Calendar.DATE, remains);

                    long currentAvailableDate = cal.getTimeInMillis();
                    while(currentAvailableDate < endRange){
                        currentAvailableDate = cal.getTimeInMillis();
                        if (currentAvailableDate >= startRange){
                            availableDates.add(currentAvailableDate);
                        }
                        cal.add(Calendar.DATE, interval * 7);
                    }
                }
                case MONTHLY:{
                    //compute first available date
                    Calendar orCal = Calendar.getInstance();
                    orCal.setTimeInMillis(startTime);

                    int orgDayOfMonth = orCal.get(Calendar.DAY_OF_MONTH);
                    int compareDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

                    int diffYear = cal.get(Calendar.YEAR) - orCal.get(Calendar.YEAR);
                    int diffMonth = diffYear * 12 + cal.get(Calendar.MONTH) - orCal.get(Calendar.MONTH);

                    int remainsDay = orgDayOfMonth - compareDayOfMonth;
                    int remainsMonth = diffMonth % interval;

                    cal.add(Calendar.MONTH, remainsMonth);
                    cal.add(Calendar.DATE, remainsDay);

                    long currentAvailableDate = cal.getTimeInMillis();
                    while(currentAvailableDate < endRange){
                        currentAvailableDate = cal.getTimeInMillis();
                        if (currentAvailableDate >= startRange){
                            availableDates.add(currentAvailableDate);
                        }
                        cal.add(Calendar.MONTH, interval);
                    }
                }
                case YEARLY:{
                    //compute first available date
                    Calendar orCal = Calendar.getInstance();
                    orCal.setTimeInMillis(startTime);

                    int orgDayOfMonth = orCal.get(Calendar.DAY_OF_MONTH);
                    int compareDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

                    int orgMonthOfYear = orCal.get(Calendar.MONTH);
                    int compareMonthOfYear = cal.get(Calendar.MONTH);

                    int diffYear = cal.get(Calendar.YEAR) - orCal.get(Calendar.YEAR);

                    int remainsYear = diffYear%interval;
                    int remainsMonth = orgMonthOfYear - compareMonthOfYear;
                    int remainsDay = orgDayOfMonth - compareDayOfMonth;

                    cal.add(Calendar.YEAR, remainsYear);
                    cal.add(Calendar.MONTH, remainsMonth);
                    cal.add(Calendar.DATE, remainsDay);

                    long currentAvailableDate = cal.getTimeInMillis();
                    while(currentAvailableDate < endRange){
                        currentAvailableDate = cal.getTimeInMillis();
                        if (currentAvailableDate >= startRange){
                            availableDates.add(currentAvailableDate);
                        }
                        cal.add(Calendar.YEAR, interval);
                    }
                }

                default:
                    Log.i(TAG, "isInclude: " + frequencyEnum);
                    break;
            }
        }

        return availableDates;
    }


    public List<String> getRecurrence(){

        String exDate = "EXDATE;VALUE=DATE:";
        for (int i = 0; i < EXDates.size(); i++) {
            exDate += format.format(EXDates.get(i)) + (i == (EXDates.size() -1) ? "":",");
        }

        String rDate = "RDATE;VALUE=DATE:";
        for (int i = 0; i < RDates.size(); i++) {
            rDate += format.format(RDates.get(i)) + (i == (RDates.size() -1) ? "":",");
        }

        List<String> result = new ArrayList<>();
        if (EXDates.size() != 0){
            result.add(exDate);
        }
        if (RDates.size() != 0){
            result.add(rDate);
        }

        if (frequencyEnum != null){
            String rRule = "RRULE:";
            rRule += ("FREQ=" + frequencyEnum.getValue())
                    + (until == null ? "":(";UNTIL=" + format.format(until)
                    + ";")) + (interval == 1 ? "": ";INTERVAL=" + interval);
            result.add(rRule);
        }

        return result;
    }


}
