package david.itimerecycler;

import android.util.Log;
import android.widget.Scroller;

/**
 * Created by Paul on 11/5/17.
 */

public class ScrollHelper {


    public static int[] calculateScrollDistance(Scroller scroller, int velocityX, int velocityY){
        int[] outDist = new int[2];

        scroller.fling(0, 0, velocityX, velocityY,
                Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
        outDist[0] = scroller.getFinalX()/2;
        outDist[1] = scroller.getFinalY()/2;

        return outDist;
    }


    public static float calculateScrollTime(float velocity){
        return (float) (0.2 + velocity/1000);
    }

    public static float calculateAccelerator(float dis, float time){
        return ( 2 * dis )/ (time * time);
    }

    public static float getCurrentDistance(float accelerator, float time){
        return (float) (0.5 * accelerator * time * time);
    }


    public static float findRightPosition(float distance, float offset,float unitLength){
        int i = 0;
        int posOrNeg = distance==0? 0:(int) (distance/Math.abs(distance));

        while(true){
            float possibleLength = unitLength * i + offset;
            if (Math.abs(distance) <= possibleLength){
                return possibleLength * posOrNeg;
            }
            i++;
        }
    }

}
