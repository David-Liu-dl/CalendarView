package david.itimecalendar.calendar.listeners;

import java.util.List;
import java.util.Map;

/**
 * Created by David Liu on 22/11/16.
 * ${COMPANY}
 * lyhmelbourne@gmail.com
 */
public interface ITimeEventPackageInterface {
    Map<Long, List<ITimeEventInterface>> getRegularEventDayMap();
    Map<Long, List<ITimeEventInterface>> getRepeatedEventDayMap();
    void clearPackage();
}
