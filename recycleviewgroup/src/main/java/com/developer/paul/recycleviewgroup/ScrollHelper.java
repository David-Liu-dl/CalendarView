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


    public static int[] calculateScrollDistance(Scroller scroller, int velocityX, int velocityY, int maxDis, int alreadyMoveDis){
        int[] outDist = new int[2];

        scroller.fling(0, 0, velocityX, velocityY,
                Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
        int disX = Math.abs(scroller.getFinalX()/2) + Math.abs(alreadyMoveDis)> maxDis?
                (maxDis-Math.abs(alreadyMoveDis)) * scroller.getFinalX()/Math.abs(scroller.getFinalX()) : scroller.getFinalX()/2;
        outDist[0] = disX;
        outDist[1] = scroller.getFinalY()/2;

        return outDist;
    }


    public static int calculateScrollTime(int velocity){
        int scrollTime = 1 + Math.abs(velocity)/2000;
        return scrollTime > 2? 2 : scrollTime;
    }

    public static int calculateVerticalScrollTime(int velocity){
        return 2;
    }

    public static int calculateAccelerator(int dis, int time){
        return ( 2 * dis )/ (time * time);
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