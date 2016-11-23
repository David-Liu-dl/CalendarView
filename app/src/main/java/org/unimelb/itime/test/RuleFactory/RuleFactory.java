package org.unimelb.itime.test.RuleFactory;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by yuhaoliu on 21/11/16.
 */
public class RuleFactory {
    private static final String TAG = "Rule";
    private static RuleFactory ourInstance = new RuleFactory();

    private SimpleDateFormat format = new SimpleDateFormat("yyyymmdd");

    public static RuleFactory getInstance() {
        return ourInstance;
    }

    private RuleFactory() {
    }

    public RuleModel getRuleModel(long startTime, long endTime, String[] recurrence){
        RuleModel rule = new RuleModel();
        rule.setStartTime(startTime);
        rule.setEndTime(endTime);

        for (int i = 0; i < recurrence.length; i++) {
            String value = recurrence[i];
            valueAnalyzer(value, rule);
        }
        return rule;
    }

    private void valueAnalyzer(String value, RuleModel rule){
        String fstWord = value.split("[^\\w']+")[0];

        switch (fstWord){
            case "EXDATE":
                exDateAnalyzer(value, rule);
                break;
            case "RDATE":
                rDATEAnalyzer(value, rule);
                break;
            case "RRULE":
                rRULEAnalyzer(value, rule);
                break;
            default:
                break;
        }
    }

    private void exDateAnalyzer(String exDate, RuleModel rule){
        if (exDate.contains(":")){
            List<String> exDatesStr;
            if (exDate.contains(",")){
                exDatesStr = Arrays.asList((exDate.split(":")[1].split(",")));
            }else{
                exDatesStr = Arrays.asList(exDate.split(":")[1]);
            }

            ArrayList<Date> exDates = new ArrayList<>();

            for (String anExDatesStr : exDatesStr) {
                try {
                    exDates.add(format.parse(anExDatesStr));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            rule.setEXDates(exDates);
        }else {
            Log.w(TAG, "EXDATE NOT match analyzing RULE");
        }
    }

    private void rDATEAnalyzer(String rDate, RuleModel rule){
        if (rDate.contains(":")){
            List<String> rDatesStr;
            if (rDate.contains(",")){
                rDatesStr = Arrays.asList(rDate.split(":")[1].split(","));
            }else{
                rDatesStr = Arrays.asList(rDate.split(":")[1]);
            }

            ArrayList<Date> rDates = new ArrayList<>();

            for (String anRDatesStr : rDatesStr) {
                try {
                    rDates.add(format.parse(anRDatesStr));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            rule.setRDates(rDates);
        }else {
            Log.w(TAG, "RDATE NOT match analyzing RULE");
        }
    }

    private void rRULEAnalyzer(String rRule, RuleModel rule){
        rRule = rRule.split(":")[1];

        if (rRule.contains("FREQ")){
            String[] fields = rRule.split(";");
            for (String field:fields
                 ) {
                String attr = field.split("=")[0];
                String value = field.split("=")[1];

                switch (attr){
                    case "FREQ":
                        rule.setFrequencyEnum(FrequencyEnum.valueOf(value));
                        break;
                    case "INTERVAL":
                        rule.setInterval(Integer.valueOf(value));
                        break;
                    case "UNTIL":
                        try {
                            rule.setUntil(format.parse(value));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "BYDAY":
                        List<String> byDays;

                        if (value.contains(",")){
                            byDays = Arrays.asList(value.split(","));
                        }else{
                            byDays = Arrays.asList(value);
                        }

                        for (String day:byDays
                             ) {
                            rule.getBy_BYDAY().add(WeekDayEnum.valueOf(day));
                        }
                        break;
                    //1-31 or -1-(-31)
                    case "BYMONTHDAY":
                        break;
                    //1-365 or -1-(-365)
                    case "BYYEARDAY":
                        break;
                    //the nth week of year
                    case "BYWEEKNO":
                        break;
                    case "BYMONTH":
                        List<Integer> byMonths = new ArrayList<>();

                        if (value.contains(",")){
                            String[] strNum= value.split(",");
                            for (String num:strNum
                                 ) {
                                byMonths.add(Integer.parseInt(num));
                            }
                        }else{
                            byMonths.add(Integer.parseInt(value));
                        }

                        rule.setBy_BYMONTH(byMonths);
                        break;
                    case "BYSETPOS":
                        break;
                    case "WKST":
                        rule.setWKST(value);
                        break;
                    case "COUNT":
                        rule.setCount(Integer.parseInt(value));
                    default:
                        Log.i(TAG, "rRULEAnalyzer: " + "Attribute CANNOT be analyzed -- " + attr);
                        break;
                }
            }
        }else {
            Log.w(TAG, "RRULE NOT match analyzing RULE");
        }
    }

}
