package com.developer.paul.recycleviewgroup;

/**
 * Created by Paul on 22/5/17.
 */

import android.util.Log;
import android.widget.Scroller;

/**
 * Created by Paul on 11/5/17.
 */

public class ScrollHelper {


    public static int[] calculateScrollDistance(Scroller scroller, int velocityX, int velocityY,
                                                int maxDis, int alreadyMoveDis, int unitLength){
        int[] outDist = new int[2];

        scroller.fling(0, 0, velocityX, velocityY,
                Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
//        int disX = Math.abs(scroller.getFinalX()/2) + Math.abs(alreadyMoveDis)> maxDis?
//                (maxDis-Math.abs(alreadyMoveDis)) * scroller.getFinalX()/Math.abs(scroller.getFinalX()) : scroller.getFinalX()/2;
        int disX = scroller.getFinalX()/2;
        if (Math.abs(disX + alreadyMoveDis) > Math.abs(maxDis)){
            // make sure this is less than maxDis
            disX =  (maxDis-Math.abs(alreadyMoveDis)) * scroller.getFinalX()/Math.abs(scroller.getFinalX());
        }

        int reminder = (disX + alreadyMoveDis)%unitLength;
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


    public static float calculateScrollTime(int velocity){
        float scrollTime = 1.0f + Math.abs(velocity)/1000.0f;
        return scrollTime > 1.5f? 1.5f : scrollTime;
    }

    public static int calculateVerticalScrollTime(int velocity){
        return 2;
    }

    public static int calculateAccelerator(int dis, float time){
        return (int) (( 2 * dis )/ (time * time));
    }

    public static int getCurrentDistance(float accelerator, float time){
//        Log.i("scrollHelper", "getCurrentDistance: " + accelerator + " " + time);
        return (int) (0.5 * accelerator * time * time);
    }


    public static int findRightPosition(int distance, int offset,int unitLength){
        int i = 0;
        int posOrNeg = distance==0? 0:distance/Math.abs(distance);

        while(true){
            int possibleLength = offset < 0 ? unitLength * i + offset : unitLength * i - offset;
//            Log.i("find", "findRightPosition: "  + possibleLength +  " , " + Math.abs(distance));
            if (Math.abs(distance) <= Math.ceil(possibleLength)){
                return possibleLength * posOrNeg;
            }
            i++;
        }
    }

}