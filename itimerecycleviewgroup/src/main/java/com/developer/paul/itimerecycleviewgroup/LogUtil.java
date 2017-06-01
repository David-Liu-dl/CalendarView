package com.developer.paul.itimerecycleviewgroup;

import android.util.Log;

import java.util.List;

/**
 * Created by Paul on 30/5/17.
 */

public class LogUtil {
    private static String TAG = "LogUtil";
    public static boolean debug = false;

    public static void log(String tag,String logString){
        if (debug) {
            Log.i(tag, "log: " + logString);
        }
    }

    public static void logAwesomes(List<AwesomeViewGroup> awesomeViewGroups){
        if (!debug){
            return;
        }
        Log.i(TAG, "logAwesomes: ");
        for (AwesomeViewGroup awesomeViewGroup : awesomeViewGroups){
            Log.i(TAG, "logAwesomes: " + awesomeViewGroup);
        }
        Log.i(TAG, "logAwesomes: ");
    }

    public static void logError(String error){
        if (!debug){
            return;
        }
        Log.i(TAG, "logError: " + error);
    }

    public static void logFirstAwesome(List<AwesomeViewGroup> awesomeViewGroups){
        if (!debug){
            return;
        }
        Log.i(TAG, "logFirstAwesome: ");
        Log.i(TAG, "logFirstAwesome: " + awesomeViewGroups.get(0));
        Log.i(TAG, "logFirstAwesome: ");
    }
}
