package com.developer.paul.itimerecycleviewgroup;

import android.util.Log;
import android.widget.Scroller;

/**
 * Created by David Liu on 30/5/17.
 * ${COMPANY}
 * lyhmelbourne@gmail.com
 */

public class ScrollHelper {
    public static int[] calculateScrollDistance(Scroller scroller, int velocityX, int velocityY,
                                                int maxDis, int alreadyMoveDis, int unitLength){
        int[] outDist = new int[2];
        scroller.fling(0, 0, velocityX, velocityY,
                Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
        int disX = scroller.getFinalX()/2;
        if (Math.abs(disX + alreadyMoveDis) > Math.abs(maxDis)){
            // make sure this is less than maxDis
            disX =  (maxDis-Math.abs(alreadyMoveDis)) * scroller.getFinalX()/Math.abs(scroller.getFinalX());
        }

        int reminder = (disX + alreadyMoveDis) % unitLength;
        Log.i("new", "calculateScrollDistance: " + " disX: " + disX + " alreadyMove : " + alreadyMoveDis +
                " reminder " + reminder + " unilen : " + unitLength);
        if (Math.abs(disX) < Math.abs(reminder)){
            disX = disX < 0 ? disX - unitLength : disX + unitLength;
        }
        disX = disX - reminder;
        // make sure this disX is a integer*unitlength and less than maxDis
        outDist[0] = disX;
        outDist[1] = scroller.getFinalY()/2;
        return outDist;
    }

    public static float calculateScrollTime(float velocity){
        float scrollTime = 0.5f + Math.abs(velocity)/1000.0f;
        return scrollTime > 1.2f? 1.2f : scrollTime;
    }

    public static boolean shouldFling(float v){
        return Math.abs(v) > 400;
    }
}
