package david.itimecalendar.calendar.listeners;

import java.util.List;
import java.util.Map;

/**
 * Created by yuhaoliu on 22/11/16.
 */
public interface ITimeEventPackageInterface {
    Map<Long, List<ITimeEventInterface>> getRegularEventDayMap();
    Map<Long, List<ITimeEventInterface>> getRepeatedEventDayMap();
    void clearPackage();
}
