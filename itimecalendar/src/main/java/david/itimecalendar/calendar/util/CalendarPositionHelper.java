package david.itimecalendar.calendar.util;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by David Liu on 15/8/17.
 * ${COMPANY}
 * lyhmelbourne@gmail.com
 */

public class CalendarPositionHelper {
    public TreeMap<Integer, String> positionToTimeTreeMap = new TreeMap<>();
    public TreeMap<Integer, String> positionToTimeQuarterTreeMap = new TreeMap<>();
    public TreeMap<Float, Integer> timeToPositionTreeMap = new TreeMap<>();

    /**
     *
     * @param positionY
     * @return
     */
    public int nearestQuarterTimeSlotKey(int positionY) {
        int key = positionY;
        Map.Entry<Integer, String> low = positionToTimeQuarterTreeMap.floorEntry(key);
        Map.Entry<Integer, String> high = positionToTimeQuarterTreeMap.ceilingEntry(key);
        if (low != null && high != null) {
            return Math.abs(key - low.getKey()) < Math.abs(key - high.getKey())
                    ? low.getKey()
                    : high.getKey();
        } else if (low != null || high != null) {
            return low != null ? low.getKey() : high.getKey();
        }

        return -1;
    }

    /**
     * get y position bases on time
     * @param time
     * @return nearest position
     */
    public int nearestTimeSlotValue(float time) {
        float key = time;
        Map.Entry<Float, Integer> low = timeToPositionTreeMap.floorEntry(key);
        Map.Entry<Float, Integer> high = timeToPositionTreeMap.ceilingEntry(key);
        if (low != null && high != null) {
            return Math.abs(key - low.getKey()) < Math.abs(key - high.getKey())
                    ? low.getValue()
                    : high.getValue();
        } else if (low != null || high != null) {
            return low != null ? low.getValue() : high.getValue();
        }

        return -1;
    }
}
